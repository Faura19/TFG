package com.example.tfgapi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class JugadorFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    private final String url = BASE_URL + "/jugador";

    private TextView textView;
    private EditText editTextNombreJugador, editTextDorsalJugador, editTextValorJugador,
            editTextPosicionJugador, editTextEdadJugador, editTextNombreEquipo,
            editTextIdEliminar, editTextIdActualizar,
            editTextNombreJugadorActualizar, editTextDorsalJugadorActualizar,
            editTextValorJugadorActualizar, editTextPosicionJugadorActualizar,
            editTextEdadJugadorActualizar, editTextNombreEquipoActualizar;
    private Button enviar, btnEliminar, btnActualizar,infoJugador;
    private Set<String> equiposDisponibles = new HashSet<>();
    private com.android.volley.RequestQueue queue;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jugador, container, false);

        // ⚡ Inicializar vistas
        textView = view.findViewById(R.id.textView);
        editTextNombreJugador = view.findViewById(R.id.editTextNombreJugador);
        editTextDorsalJugador = view.findViewById(R.id.editTextDorsalJugador);
        editTextValorJugador = view.findViewById(R.id.editTextValorJugador);
        editTextPosicionJugador = view.findViewById(R.id.editTextPosicionJugador);
        editTextEdadJugador = view.findViewById(R.id.editTextEdadJugador);
        editTextNombreEquipo = view.findViewById(R.id.editTextNombreEquipo);
        editTextIdEliminar = view.findViewById(R.id.editTextIdEliminar);
        editTextIdActualizar = view.findViewById(R.id.editTextIdActualizar);
        editTextNombreJugadorActualizar = view.findViewById(R.id.editTextNombreJugadorActualizar);
        editTextDorsalJugadorActualizar = view.findViewById(R.id.editTextDorsalJugadorActualizar);
        editTextValorJugadorActualizar = view.findViewById(R.id.editTextValorJugadorActualizar);
        editTextPosicionJugadorActualizar = view.findViewById(R.id.editTextPosicionJugadorActualizar);
        editTextEdadJugadorActualizar = view.findViewById(R.id.editTextEdadJugadorActualizar);
        editTextNombreEquipoActualizar = view.findViewById(R.id.editTextNombreEquipoActualizar);
        enviar = view.findViewById(R.id.enviar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnActualizar = view.findViewById(R.id.btnActualizar);
        infoJugador=view.findViewById(R.id.infoJugador);

        queue = Volley.newRequestQueue(requireContext());

        cargarEquiposDesdeLigas();
        obtenerDatos();

        infoJugador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), InfoJugadores.class);
                intent.putExtra("info_jugador", textView.getText().toString());
                startActivity(intent);
            }
        });
        enviar.setOnClickListener(v -> enviarDatos());
        btnEliminar.setOnClickListener(v -> eliminarJugador());
        btnActualizar.setOnClickListener(v -> actualizarJugador());

        return view;
    }

    // 🔹 Obtener token JWT desde SharedPreferences
    private String getToken() {
        return requireActivity()
                .getSharedPreferences("MyPrefs", 0)
                .getString("jwt", null);
    }

    // 🔹 Cabeceras con JWT
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        String token = getToken();
        if (token != null) headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private void obtenerDatos() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    StringBuilder builder = new StringBuilder();
                    equiposDisponibles.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            String nombreJugador = obj.getString("nombreJugador");
                            int dorsalJugador = obj.getInt("dorsalJugador");
                            int valorJugador = obj.getInt("valorJugador");
                            String posicionJugador = obj.getString("posicionJugador");
                            int edadJugador = obj.getInt("edadJugador");

                            JSONObject equipoObj = obj.optJSONObject("liga");
                            String nombreEquipo = equipoObj != null ? equipoObj.optString("nombreEquipo", "Desconocido") : "Sin equipo";

                            if (!nombreEquipo.equals("Desconocido") && !nombreEquipo.equals("Sin equipo"))
                                equiposDisponibles.add(nombreEquipo);

                            builder.append("Nombre Jugador: ").append(nombreJugador)
                                    .append("\nDorsal: ").append(dorsalJugador)
                                    .append("\nValor: ").append(valorJugador)
                                    .append("\nPosición: ").append(posicionJugador)
                                    .append("\nEdad: ").append(edadJugador)
                                    .append("\nEquipo: ").append(nombreEquipo)
                                    .append("\n\n");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    textView.setText(builder.toString());
                },
                error -> {
                    textView.setText("❌ Error al obtener datos: " + error.toString());
                    Log.e("Volley", "Error GET", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders();
            }
        };

        queue.add(request);
    }

    private void cargarEquiposDesdeLigas() {
        String urlLigas = BASE_URL + "/liga";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                urlLigas,
                null,
                response -> {
                    equiposDisponibles.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject ligaObj = response.getJSONObject(i);
                            String nombreEquipo = ligaObj.optString("nombreEquipo", "");
                            if (!nombreEquipo.isEmpty()) equiposDisponibles.add(nombreEquipo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("Equipos", "✅ Equipos cargados: " + equiposDisponibles);
                },
                error -> {
                    Log.e("Volley", "Error al obtener ligas", error);
                    textView.setText("⚠️ No se pudieron cargar los equipos.");
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders();
            }
        };

        queue.add(request);
    }

    private void enviarDatos() {
        String nombreJugador = editTextNombreJugador.getText().toString().trim();
        String dorsalJugador = editTextDorsalJugador.getText().toString().trim();
        String valorJugador = editTextValorJugador.getText().toString().trim();
        String posicionJugador = editTextPosicionJugador.getText().toString().trim();
        String edadJugador = editTextEdadJugador.getText().toString().trim();
        String nombreEquipo = editTextNombreEquipo.getText().toString().trim();

        if (nombreJugador.isEmpty() || dorsalJugador.isEmpty() || valorJugador.isEmpty() ||
                posicionJugador.isEmpty() || edadJugador.isEmpty() || nombreEquipo.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos antes de enviar.");
            return;
        }

        /*
        if (!equiposDisponibles.contains(nombreEquipo)) {
            textView.setText("❌ El equipo " + nombreEquipo + " no existe.");
            return;
        }*/

        try {
            JSONObject ligaObj = new JSONObject();
            ligaObj.put("nombreEquipo", nombreEquipo);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nombreJugador", nombreJugador);
            jsonBody.put("dorsalJugador", Integer.parseInt(dorsalJugador));
            jsonBody.put("valorJugador", Integer.parseInt(valorJugador));
            jsonBody.put("posicionJugador", posicionJugador);
            jsonBody.put("edadJugador", Integer.parseInt(edadJugador));
            jsonBody.put("liga", ligaObj);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        textView.setText("✅ Jugador insertado correctamente.");
                        editTextNombreJugador.setText("");
                        editTextDorsalJugador.setText("");
                        editTextValorJugador.setText("");
                        editTextPosicionJugador.setText("");
                        editTextEdadJugador.setText("");
                        editTextNombreEquipo.setText("");
                        obtenerDatos();
                    },
                    error -> {
                        textView.setText("❌ Error al insertar: " + error.toString());
                        Log.e("Volley", "Error POST", error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getAuthHeaders();
                }
            };

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void eliminarJugador() {
        String idStr = editTextIdEliminar.getText().toString().trim();
        if (idStr.isEmpty()) {
            textView.setText("⚠️ Introduce un ID para eliminar.");
            return;
        }

        String deleteUrl = url + "/" + idStr;

        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                response -> {
                    textView.setText("🗑️ Jugador eliminado correctamente.");
                    editTextIdEliminar.setText("");
                    obtenerDatos();
                },
                error -> {
                    textView.setText("❌ Error al eliminar: " + error.toString());
                    Log.e("Volley", "Error DELETE", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders();
            }
        };

        queue.add(request);
    }

    private void actualizarJugador() {
        String idStr = editTextIdActualizar.getText().toString().trim();
        String nombreJugador = editTextNombreJugadorActualizar.getText().toString().trim();
        String dorsalJugador = editTextDorsalJugadorActualizar.getText().toString().trim();
        String valorJugador = editTextValorJugadorActualizar.getText().toString().trim();
        String posicionJugador = editTextPosicionJugadorActualizar.getText().toString().trim();
        String edadJugador = editTextEdadJugadorActualizar.getText().toString().trim();
        String nombreEquipo = editTextNombreEquipoActualizar.getText().toString().trim();

        if (idStr.isEmpty() || nombreJugador.isEmpty() || dorsalJugador.isEmpty() || valorJugador.isEmpty() ||
                posicionJugador.isEmpty() || edadJugador.isEmpty() || nombreEquipo.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos para actualizar.");
            return;
        }

        String putUrl = url + "/" + idStr;

        try {
            JSONObject ligaObj = new JSONObject();
            ligaObj.put("nombreEquipo", nombreEquipo);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nombreJugador", nombreJugador);
            jsonBody.put("dorsalJugador", Integer.parseInt(dorsalJugador));
            jsonBody.put("valorJugador", Integer.parseInt(valorJugador));
            jsonBody.put("posicionJugador", posicionJugador);
            jsonBody.put("edadJugador", Integer.parseInt(edadJugador));
            jsonBody.put("liga", ligaObj);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> {
                        textView.setText("✏️ Jugador actualizado correctamente.");
                        editTextIdActualizar.setText("");
                        editTextNombreJugadorActualizar.setText("");
                        editTextDorsalJugadorActualizar.setText("");
                        editTextValorJugadorActualizar.setText("");
                        editTextPosicionJugadorActualizar.setText("");
                        editTextEdadJugadorActualizar.setText("");
                        editTextNombreEquipoActualizar.setText("");
                        obtenerDatos();
                    },
                    error -> {
                        textView.setText("❌ Error al actualizar: " + error.toString());
                        Log.e("Volley", "Error PUT", error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getAuthHeaders();
                }
            };

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            textView.setText("❌ Error al crear JSON: " + e.getMessage());
        }
    }
}

