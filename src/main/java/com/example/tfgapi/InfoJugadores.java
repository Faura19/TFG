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

public class InfoJugadores extends AppCompatActivity {

    private Button btnVolverJugador;
    private LinearLayout containerJugadores;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_jugadores);

        btnVolverJugador = findViewById(R.id.btnVolverJugador);
        containerJugadores = findViewById(R.id.containerJugadores);

        // 🔹 Recibir la información desde el intent
        String infoJugadores = getIntent().getStringExtra("info_jugador");

        if (infoJugadores != null && !infoJugadores.isEmpty()) {
            mostrarJugadores(infoJugadores);
        } else {
            TextView noData = new TextView(this);
            noData.setText("No se recibió información de los jugadores");
            containerJugadores.addView(noData);
        }

        // 🔹 Botón para volver al MainActivity
        btnVolverJugador.setOnClickListener(v -> {
            Intent intent = new Intent(InfoJugadores.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Método para dividir el texto y mostrar imágenes + texto por jugador
    private void mostrarJugadores(String infoJugadores) {
        String[] lineas = infoJugadores.split("\\n");
        LayoutInflater inflater = LayoutInflater.from(this);

        int lineasPorJugador = 6; // Cada jugador tiene 5 líneas (por ejemplo)
        int indiceLinea = 0;
        int numeroJugador = 1;

        while (indiceLinea < lineas.length) {
            // Saltar líneas vacías
            if (lineas[indiceLinea].trim().isEmpty()) {
                indiceLinea++;
                continue;
            }

            // 🔹 Contenedor vertical para este jugador
            LinearLayout jugadorLayout = new LinearLayout(this);
            jugadorLayout.setOrientation(LinearLayout.VERTICAL);
            jugadorLayout.setPadding(32, 32, 32, 32);

            // 🔹 Título grande del jugador
            TextView tituloJugador = new TextView(this);
            tituloJugador.setText("Jugador " + numeroJugador);
            tituloJugador.setTextSize(22);
            tituloJugador.setTextColor(getResources().getColor(android.R.color.black));
            tituloJugador.setPadding(0, 0, 0, 16);
            tituloJugador.setTypeface(null, Typeface.BOLD);
            jugadorLayout.addView(tituloJugador);

            // 🔹 Añadir cada línea con su imagen
            for (int j = 0; j < lineasPorJugador && indiceLinea < lineas.length; j++, indiceLinea++) {
                if (lineas[indiceLinea].trim().isEmpty()) break;

                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_perfil, containerJugadores, false);
                ImageView imageJugador = item.findViewById(R.id.imagePerfil);
                TextView textJugadorInfo = item.findViewById(R.id.textPerfilInfo);

                textJugadorInfo.setText(lineas[indiceLinea]);

                // 🔹 Asigna la imagen según el dato
                switch (j) {
                    case 0: imageJugador.setImageResource(R.drawable.ic_edadjugador); break;  // Nombre
                    case 1: imageJugador.setImageResource(R.drawable.ic_dorsal); break;   // Dorsal
                    case 2: imageJugador.setImageResource(R.drawable.ic_valormercado); break; // Posición
                    case 3: imageJugador.setImageResource(R.drawable.ic_posicionfutbol); break;   // Equipo
                    case 4: imageJugador.setImageResource(R.drawable.ic_carne); break;
                    case 5: imageJugador.setImageResource(R.drawable.ic_equipo); break;
                    default: imageJugador.setImageResource(R.drawable.ic_launcher_foreground); break;
                }

                jugadorLayout.addView(item);
            }

            // 🔹 Separador visual entre jugadores
            View separador = new View(this);
            separador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4));
            separador.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            separador.setPadding(0, 16, 0, 16);

            // Añadir el bloque al contenedor principal
            containerJugadores.addView(jugadorLayout);
            containerJugadores.addView(separador);

            numeroJugador++;
        }
    }
}
