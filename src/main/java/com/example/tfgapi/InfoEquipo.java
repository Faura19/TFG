package com.example.tfgapi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoEquipo extends AppCompatActivity {

    private Button btnVolverHome;
    private LinearLayout containerEquipos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_equipo);

        btnVolverHome = findViewById(R.id.btnVolverHome);
        containerEquipos = findViewById(R.id.containerEquipos);

        // 🔹 Recuperamos el texto enviado desde HomeFragment
        String infoEquipos = getIntent().getStringExtra("info_equipos");

        if (infoEquipos != null && !infoEquipos.isEmpty()) {
            mostrarEquipos(infoEquipos);
        } else {
            TextView noData = new TextView(this);
            noData.setText("No se recibió información de los equipos.");
            containerEquipos.addView(noData);
        }

        btnVolverHome.setOnClickListener(v -> {
            Intent intent = new Intent(InfoEquipo.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Mostrar cada equipo con su título, imágenes y texto
    private void mostrarEquipos(String infoEquipos) {
        String[] lineas = infoEquipos.split("\\n");
        LayoutInflater inflater = LayoutInflater.from(this);

        int lineasPorEquipo = 5; // Cada equipo tiene 5 líneas (ajústalo si cambia)
        int indiceLinea = 0;
        int numeroEquipo = 1;

        while (indiceLinea < lineas.length) {
            if (lineas[indiceLinea].trim().isEmpty()) {
                indiceLinea++;
                continue;
            }

            // 🔹 Contenedor vertical para un equipo
            LinearLayout equipoLayout = new LinearLayout(this);
            equipoLayout.setOrientation(LinearLayout.VERTICAL);
            equipoLayout.setPadding(32, 32, 32, 32);

            // 🔹 Título del equipo
            TextView tituloEquipo = new TextView(this);
            tituloEquipo.setText("Equipo " + numeroEquipo);
            tituloEquipo.setTextSize(22);
            tituloEquipo.setTextColor(getResources().getColor(android.R.color.black));
            tituloEquipo.setTypeface(null, Typeface.BOLD);
            tituloEquipo.setPadding(0, 0, 0, 16);
            equipoLayout.addView(tituloEquipo);

            // 🔹 Agregar líneas (datos del equipo)
            for (int j = 0; j < lineasPorEquipo && indiceLinea < lineas.length; j++, indiceLinea++) {
                if (lineas[indiceLinea].trim().isEmpty()) break;

                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_perfil, containerEquipos, false);
                ImageView imageEquipo = item.findViewById(R.id.imagePerfil);
                TextView textEquipoInfo = item.findViewById(R.id.textPerfilInfo);

                textEquipoInfo.setText(lineas[indiceLinea]);

                // 🔹 Asignar iconos por línea
                switch (j) {
                    case 0: imageEquipo.setImageResource(R.drawable.ic_equipo); break;
                    case 1: imageEquipo.setImageResource(R.drawable.ic_puntos); break;
                    default: imageEquipo.setImageResource(R.drawable.ic_launcher_foreground); break;
                }

                equipoLayout.addView(item);
            }

            // 🔹 Separador entre equipos
            View separador = new View(this);
            separador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4));
            separador.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            separador.setPadding(0, 16, 0, 16);

            // Añadir al contenedor principal
            containerEquipos.addView(equipoLayout);
            containerEquipos.addView(separador);

            numeroEquipo++;
        }
    }
}
