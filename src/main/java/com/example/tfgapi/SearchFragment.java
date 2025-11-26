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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    private final String url = BASE_URL + "/partidos";

    private TextView textView;
    private RequestQueue queue;
    private EditText editTextGolesFavor, editTextGolesContra, editTextLigaId;
    private EditText editTextIdEliminar, editTextIdActualizar, editTextgolesFavor, editTextgolesContra;
    private Button enviar, btnEliminar, btnActualizar,infoPartidos;
    private List<Long> ligasDisponibles = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        textView = view.findViewById(R.id.textView);
        editTextGolesFavor = view.findViewById(R.id.editTextGolesFavor);
        editTextGolesContra = view.findViewById(R.id.editTextGolesContra);
        editTextLigaId = view.findViewById(R.id.editTextLigaId);
        editTextIdEliminar = view.findViewById(R.id.editTextIdEliminar);
        editTextIdActualizar = view.findViewById(R.id.editTextIdActualizar);
        editTextgolesFavor = view.findViewById(R.id.editTextgolesFavor);
        editTextgolesContra = view.findViewById(R.id.editTextgolesContra);
        enviar = view.findViewById(R.id.enviar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnActualizar = view.findViewById(R.id.btnActualizar);
        infoPartidos=view.findViewById(R.id.infoPartidos);

        queue = Volley.newRequestQueue(requireContext());

        cargarLigas();
        obtenerDatos();

        infoPartidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), InfoPartidos.class);
                intent.putExtra("info_partidos", textView.getText().toString());
                startActivity(intent);
            }
        });
        enviar.setOnClickListener(v -> enviarDatos());
        btnEliminar.setOnClickListener(v -> eliminarPartido());
        btnActualizar.setOnClickListener(v -> actualizarPartido());

        return view;
    }

    // 🔹 Recupera el token directamente de SharedPreferences
    private String getToken() {
        return requireActivity()
                .getSharedPreferences("MyPrefs", 0)
                .getString("jwt", null);
    }

    // 🔹 Devuelve las cabeceras con el JWT
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        String token = getToken();
        if (token != null) {
            headers.put("Authorization", "Bearer " + token);
        }
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private void obtenerDatos() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    StringBuilder builder = new StringBuilder();
                    ligasDisponibles.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int golesFavor = obj.getInt("golesFavor");
                            int golesContra = obj.getInt("golesContra");

                            JSONObject ligaObj = obj.optJSONObject("liga");
                            Long idLiga = ligaObj != null ? ligaObj.optLong("id", 0) : 0;

                            if (idLiga != 0) ligasDisponibles.add(idLiga);

                            builder.append("⚽ Goles a favor: ").append(golesFavor)
                                    .append("\n🥅 Goles en contra: ").append(golesContra)
                                    .append("\n🏆 Liga ID: ").append(idLiga)
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

        queue.add(jsonArrayRequest);
    }

    private void cargarLigas() {
        String urlLigas = BASE_URL + "/liga";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                urlLigas,
                null,
                response -> {
                    ligasDisponibles.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject ligaObj = response.getJSONObject(i);
                            long id = ligaObj.optLong("id", 0);
                            if (id != 0) ligasDisponibles.add(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("Ligas", "✅ Ligas cargadas: " + ligasDisponibles);
                },
                error -> {
                    Log.e("Volley", "Error al obtener ligas", error);
                    textView.setText("⚠️ No se pudieron cargar las ligas.");
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
        String golesFavor = editTextGolesFavor.getText().toString().trim();
        String golesContra = editTextGolesContra.getText().toString().trim();
        String ligaIdStr = editTextLigaId.getText().toString().trim();

        if (golesFavor.isEmpty() || golesContra.isEmpty() || ligaIdStr.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos antes de enviar.");
            return;
        }

        long ligaId;
        try {
            ligaId = Long.parseLong(ligaIdStr);
        } catch (NumberFormatException e) {
            textView.setText("❌ El ID de liga no es válido.");
            return;
        }

        if (!ligasDisponibles.contains(ligaId)) {
            textView.setText("❌ El ID de liga " + ligaId + " no existe.");
            return;
        }

        try {
            JSONObject ligaObj = new JSONObject();
            ligaObj.put("id", ligaId);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("golesFavor", Integer.parseInt(golesFavor));
            jsonBody.put("golesContra", Integer.parseInt(golesContra));
            jsonBody.put("liga", ligaObj);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        textView.setText("✅ Partido insertado correctamente.");
                        editTextGolesFavor.setText("");
                        editTextGolesContra.setText("");
                        editTextLigaId.setText("");
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

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void eliminarPartido() {
        String idStr = editTextIdEliminar.getText().toString().trim();

        if (idStr.isEmpty()) {
            textView.setText("⚠️ Introduce un ID para eliminar.");
            return;
        }

        String deleteUrl = url + "/" + idStr;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                response -> {
                    textView.setText("🗑️ Partido eliminado correctamente.");
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

        queue.add(deleteRequest);
    }

    private void actualizarPartido() {
        String idStr = editTextIdActualizar.getText().toString().trim();
        String golesFavor = editTextgolesFavor.getText().toString().trim();
        String golesContra = editTextgolesContra.getText().toString().trim();

        if (idStr.isEmpty() || golesFavor.isEmpty() || golesContra.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos para actualizar.");
            return;
        }

        String putUrl = url + "/" + idStr;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("golesFavor", Integer.parseInt(golesFavor));
            jsonBody.put("golesContra", Integer.parseInt(golesContra));

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> {
                        textView.setText("✏️ Partido actualizado correctamente.");
                        editTextIdActualizar.setText("");
                        editTextgolesFavor.setText("");
                        editTextgolesContra.setText("");
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

            queue.add(putRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            textView.setText("❌ Error al crear JSON: " + e.getMessage());
        }
    }
}



