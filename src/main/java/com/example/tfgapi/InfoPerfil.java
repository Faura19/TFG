package com.example.tfgapi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoPerfil extends AppCompatActivity {

    private Button btnVolverProfile;
    private LinearLayout containerPerfil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_perfil);

        btnVolverProfile = findViewById(R.id.btnVolverProfile);
        containerPerfil = findViewById(R.id.containerPerfil);

        String infoPerfil = getIntent().getStringExtra("info_perfil");

        if (infoPerfil != null && !infoPerfil.isEmpty()) {
            mostrarPerfiles(infoPerfil);
        } else {
            TextView noData = new TextView(this);
            noData.setText("No se recibió información de los perfiles");
            containerPerfil.addView(noData);
        }

        btnVolverProfile.setOnClickListener(v -> {
            Intent intent = new Intent(InfoPerfil.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Método para dividir el texto y mostrar imágenes + texto
    private void mostrarPerfiles(String infoPerfil) {
        String[] lineas = infoPerfil.split("\\n");
        LayoutInflater inflater = LayoutInflater.from(this);

        int lineasPorPerfil = 5; // Cada perfil tiene 5 líneas de info
        int indiceLinea = 0;
        int numeroPerfil = 1; // Contador de perfiles

        while (indiceLinea < lineas.length) {
            // Saltar líneas vacías
            if (lineas[indiceLinea].trim().isEmpty()) {
                indiceLinea++;
                continue;
            }

            // 🔹 Contenedor vertical para este perfil
            LinearLayout perfilLayout = new LinearLayout(this);
            perfilLayout.setOrientation(LinearLayout.VERTICAL);
            perfilLayout.setPadding(32, 32, 32, 32);

            // 🔹 Añadir título del perfil
            TextView tituloPerfil = new TextView(this);
            tituloPerfil.setText("Perfil " + numeroPerfil);
            tituloPerfil.setTextSize(22);
            tituloPerfil.setTextColor(getResources().getColor(android.R.color.black));
            tituloPerfil.setPadding(0, 0, 0, 16);
            tituloPerfil.setTypeface(null, android.graphics.Typeface.BOLD);
            perfilLayout.addView(tituloPerfil);

            // 🔹 Añadir las líneas (info del perfil)
            for (int j = 0; j < lineasPorPerfil && indiceLinea < lineas.length; j++, indiceLinea++) {
                if (lineas[indiceLinea].trim().isEmpty()) break;

                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_perfil, containerPerfil, false);
                ImageView imagePerfil = item.findViewById(R.id.imagePerfil);
                TextView textPerfilInfo = item.findViewById(R.id.textPerfilInfo);

                textPerfilInfo.setText(lineas[indiceLinea]);

                // 🔹 Asigna la imagen según la línea dentro del perfil
                switch (j) {
                    case 0: imagePerfil.setImageResource(R.drawable.ic_calendario); break;  // Año fundación
                    case 1: imagePerfil.setImageResource(R.drawable.ic_estadio); break;      // Capacidad estadio
                    case 2: imagePerfil.setImageResource(R.drawable.ic_trofeo); break;       // Títulos
                    case 3: imagePerfil.setImageResource(R.drawable.ic_entrada); break;      // Entrada partido
                    case 4: imagePerfil.setImageResource(R.drawable.ic_carne); break;        // ID
                    default: imagePerfil.setImageResource(R.drawable.ic_launcher_foreground); break;
                }

                perfilLayout.addView(item);
            }

            // 🔹 Separador visual entre perfiles
            View separador = new View(this);
            separador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4));
            separador.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            separador.setPadding(0, 16, 0, 16);

            // Añadir todo al contenedor principal
            containerPerfil.addView(perfilLayout);
            containerPerfil.addView(separador);

            numeroPerfil++; // Incrementar el número de perfil
        }
    }


}
