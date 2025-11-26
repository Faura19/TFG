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

public class InfoPartidos extends AppCompatActivity {

    private Button btnVolverPartidos;
    private LinearLayout containerPartidos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_partidos);

        btnVolverPartidos = findViewById(R.id.btnVolverPartidos);
        containerPartidos = findViewById(R.id.containerPartidos);

        // 🔹 Recibir información de los partidos
        String infoPartidos = getIntent().getStringExtra("info_partidos");

        if (infoPartidos != null && !infoPartidos.isEmpty()) {
            mostrarPartidos(infoPartidos);
        } else {
            TextView noData = new TextView(this);
            noData.setText("No se recibió información de los partidos");
            containerPartidos.addView(noData);
        }

        // 🔹 Botón para volver al menú principal
        btnVolverPartidos.setOnClickListener(v -> {
            Intent intent = new Intent(InfoPartidos.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Mostrar cada partido con su título, imágenes y texto
    private void mostrarPartidos(String infoPartidos) {
        String[] lineas = infoPartidos.split("\\n");
        LayoutInflater inflater = LayoutInflater.from(this);

        int lineasPorPartido = 6; // Número de líneas que tiene cada partido (ajústalo según tus datos)
        int indiceLinea = 0;
        int numeroPartido = 1;

        while (indiceLinea < lineas.length) {
            if (lineas[indiceLinea].trim().isEmpty()) {
                indiceLinea++;
                continue;
            }

            // 🔹 Contenedor de un partido (simple, sin fondo ni bordes)
            LinearLayout partidoLayout = new LinearLayout(this);
            partidoLayout.setOrientation(LinearLayout.VERTICAL);
            partidoLayout.setPadding(32, 32, 32, 32);

            // 🔹 Título "Partido X"
            TextView tituloPartido = new TextView(this);
            tituloPartido.setText("Partido " + numeroPartido);
            tituloPartido.setTextSize(22);
            tituloPartido.setTextColor(getResources().getColor(android.R.color.black));
            tituloPartido.setTypeface(null, Typeface.BOLD);
            tituloPartido.setPadding(0, 0, 0, 16);
            partidoLayout.addView(tituloPartido);

            // 🔹 Agregar líneas (datos del partido)
            for (int j = 0; j < lineasPorPartido && indiceLinea < lineas.length; j++, indiceLinea++) {
                if (lineas[indiceLinea].trim().isEmpty()) break;

                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_perfil, containerPartidos, false);
                ImageView imagePartido = item.findViewById(R.id.imagePerfil);
                TextView textPartidoInfo = item.findViewById(R.id.textPerfilInfo);

                textPartidoInfo.setText(lineas[indiceLinea]);

                // 🔹 Asignar iconos por tipo de línea
                switch (j) {
                    case 0: imagePartido.setImageResource(R.drawable.ic_golesfavor); break;   // Goles a favor
                    case 1: imagePartido.setImageResource(R.drawable.ic_golescontra); break;  // Goles en contra
                    case 2: imagePartido.setImageResource(R.drawable.ic_carne); break;        // ID u otro dato
                    default: imagePartido.setImageResource(R.drawable.ic_launcher_foreground); break;
                }

                partidoLayout.addView(item);
            }

            // 🔹 Separador entre partidos
            View separador = new View(this);
            separador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4));
            separador.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            separador.setPadding(0, 16, 0, 16);

            // Añadir el bloque al contenedor principal
            containerPartidos.addView(partidoLayout);
            containerPartidos.addView(separador);

            numeroPartido++;
        }
    }
}

