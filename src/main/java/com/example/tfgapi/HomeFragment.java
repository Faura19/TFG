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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView textView;
    private EditText editTextEquipo, editTextPuntos;
    private EditText editTextIdEliminar;
    private EditText editTextIdActualizar, editTextEquipoActualizar, editTextPuntosActualizar;
    private Button btnEnviar, btnEliminar, btnActualizar,infoEquipo;
    private RequestQueue queue;
    private final String url = "http://10.0.2.2:8080/liga";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textView = view.findViewById(R.id.textView);
        editTextEquipo = view.findViewById(R.id.editTextEquipo);
        editTextPuntos = view.findViewById(R.id.editTextPuntos);
        editTextIdEliminar = view.findViewById(R.id.editTextIdEliminar);
        editTextIdActualizar = view.findViewById(R.id.editTextIdActualizar);
        editTextEquipoActualizar = view.findViewById(R.id.editTextEquipoActualizar);
        editTextPuntosActualizar = view.findViewById(R.id.editTextPuntosActualizar);
        btnEnviar = view.findViewById(R.id.btnEnviar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnActualizar = view.findViewById(R.id.btnActualizar);
        infoEquipo=view.findViewById(R.id.infoEquipo);

        queue = Volley.newRequestQueue(requireContext());

        obtenerDatos();

        infoEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), InfoEquipo.class);
                    intent.putExtra("info_equipos", textView.getText().toString());
                    startActivity(intent);
                }
            }
        });
        btnEnviar.setOnClickListener(v -> enviarDatos());
        btnEliminar.setOnClickListener(v -> eliminarEquipo());
        btnActualizar.setOnClickListener(v -> actualizarEquipo());

        return view;
    }

    // 🔹 MÉTODO GET - Obtener lista de equipos
    private void obtenerDatos() {
        String token = getToken();
        if (token == null) {
            textView.setText("⚠️ No hay token guardado. Inicia sesión primero.");
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String equipo = obj.getString("nombreEquipo");
                            int puntos = obj.getInt("puntosEquipo");

                            builder.append("\nEquipo: ").append(equipo)
                                    .append("\nPuntos: ").append(puntos)
                                    .append("\n\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    textView.setText(builder.toString());
                },
                error -> {
                    textView.setText("❌ Error al obtener datos: " + error.toString());
                    Log.e("Volley", "Error al obtener datos", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders(token);
            }
        };

        queue.add(jsonArrayRequest);
    }

    // 🔹 MÉTODO POST - Insertar nuevo equipo
    private void enviarDatos() {
        String equipo = editTextEquipo.getText().toString().trim();
        String puntos = editTextPuntos.getText().toString().trim();
        String token = getToken();

        if (token == null) {
            textView.setText("⚠️ Inicia sesión antes de enviar datos.");
            return;
        }

        if (equipo.isEmpty() || puntos.isEmpty()) {
            textView.setText("Por favor, completa todos los campos antes de enviar.");
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nombreEquipo", equipo);
            jsonBody.put("puntosEquipo", Integer.parseInt(puntos));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        textView.setText("✅ Insertado correctamente");
                        editTextEquipo.setText("");
                        editTextPuntos.setText("");
                        obtenerDatos();
                    },
                    error -> {
                        textView.setText("❌ Error al insertar: " + error.toString());
                        Log.e("Volley", "Error POST", error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = getAuthHeaders(token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 🔹 MÉTODO DELETE - Eliminar equipo por ID
    private void eliminarEquipo() {
        String idStr = editTextIdEliminar.getText().toString().trim();
        String token = getToken();

        if (token == null) {
            textView.setText("⚠️ Inicia sesión antes de eliminar.");
            return;
        }

        if (idStr.isEmpty()) {
            textView.setText("Introduce un ID para eliminar.");
            return;
        }

        String deleteUrl = url + "/" + idStr;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                response -> {
                    textView.setText("🗑️ Equipo eliminado correctamente");
                    editTextIdEliminar.setText("");
                    obtenerDatos();
                },
                error -> {
                    textView.setText("❌ Error al eliminar: " + error.toString());
                    Log.e("Volley", "Error DELETE", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders(token);
            }
        };

        queue.add(deleteRequest);
    }

    // 🔹 MÉTODO PUT - Actualizar equipo
    private void actualizarEquipo() {
        String idStr = editTextIdActualizar.getText().toString().trim();
        String equipo = editTextEquipoActualizar.getText().toString().trim();
        String puntos = editTextPuntosActualizar.getText().toString().trim();
        String token = getToken();

        if (token == null) {
            textView.setText("⚠️ Inicia sesión antes de actualizar.");
            return;
        }

        if (idStr.isEmpty() || equipo.isEmpty() || puntos.isEmpty()) {
            textView.setText("Completa todos los campos para actualizar.");
            return;
        }

        String putUrl = url + "/" + idStr;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nombreEquipo", equipo);
            jsonBody.put("puntosEquipo", Integer.parseInt(puntos));

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> {
                        textView.setText("✏️ Equipo actualizado correctamente");
                        editTextIdActualizar.setText("");
                        editTextEquipoActualizar.setText("");
                        editTextPuntosActualizar.setText("");
                        obtenerDatos();
                    },
                    error -> {
                        textView.setText("❌ Error al actualizar: " + error.toString());
                        Log.e("Volley", "Error PUT", error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = getAuthHeaders(token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(putRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            textView.setText("Error al crear JSON: " + e.getMessage());
        }
    }

    // 🔸 Recuperar el token guardado
    private String getToken() {
        return requireActivity()
                .getSharedPreferences("MyPrefs", 0)
                .getString("jwt", null);
    }

    // 🔸 Cabeceras de autenticación
    private Map<String, String> getAuthHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}

