package com.example.tfgapi;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    private final String url = BASE_URL + "/equipo";

    private TextView textView;
    private EditText editTextAnoFundacion, editTextCapacidadEstadio, editTexttitulos, editTextEntradaPartido;
    private EditText editTextIdEliminar, editTextIdActualizar, editTextAnoFundacionActualizar,
            editTextCapacidadEstadioActualizar, editTexttitulosActualizar, editTextEntradaPartidoActualizar
            ,editTextIdLiga;
    private Button enviar, eliminar, actualizar,infoPerfil;
    private com.android.volley.RequestQueue queue;
    private List<Long> ligasDisponibles = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ⚡ Inicializar vistas
        editTextIdLiga=view.findViewById(R.id.editTextIdLiga);
        editTextAnoFundacion = view.findViewById(R.id.editTextAnoFundacion);
        editTextCapacidadEstadio = view.findViewById(R.id.editTextCapacidadEstadio);
        editTexttitulos = view.findViewById(R.id.editTexttitulos);
        editTextEntradaPartido = view.findViewById(R.id.editTextEntradaPartido);
        editTextIdActualizar = view.findViewById(R.id.editTextIdActualizar);
        editTextAnoFundacionActualizar = view.findViewById(R.id.editTextAnoFundacionActualizar);
        editTextCapacidadEstadioActualizar = view.findViewById(R.id.editTextCapacidadEstadioActualizar);
        editTexttitulosActualizar = view.findViewById(R.id.editTexttitulosActualizar);
        editTextEntradaPartidoActualizar = view.findViewById(R.id.editTextEntradaPartidoActualizar);
        actualizar = view.findViewById(R.id.actualizar);
        enviar = view.findViewById(R.id.enviar);
        eliminar = view.findViewById(R.id.eliminar);
        infoPerfil=view.findViewById(R.id.infoPerfil);
        textView = view.findViewById(R.id.textView);
        editTextIdEliminar = view.findViewById(R.id.editTextIdEliminar);

        queue = Volley.newRequestQueue(requireContext());

        obtenerLigas();
        obtenerDatos();

        infoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), InfoPerfil.class);
                    intent.putExtra("info_perfil", textView.getText().toString());
                    startActivity(intent);
                }
            }
        });
        enviar.setOnClickListener(v -> enviarDatos());
        eliminar.setOnClickListener(v -> eliminarEquipo());
        actualizar.setOnClickListener(v -> actualizarEquipo());

        return view;
    }

    private void obtenerLigas() {
        String ligasUrl = BASE_URL + "/liga";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                ligasUrl,
                null,
                response -> {
                    ligasDisponibles.clear(); // 👈 Limpia antes de volver a llenar
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            long id = obj.getLong("id");
                            ligasDisponibles.add(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("Ligas", "IDs de ligas disponibles: " + ligasDisponibles);
                },
                error -> {
                    Log.e("Volley", "Error al obtener ligas: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders();
            }
        };

        queue.add(request);
    }


    private String getToken() {
        return requireActivity()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                .getString("jwt", null);
    }

    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        String token = getToken();
        if (token != null && !token.isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        } else {
            Log.w("Auth", "⚠️ No se encontró token JWT en SharedPreferences");
        }
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept", "application/json");
        return headers;
    }


    private void obtenerDatos() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int anoFundacion = obj.getInt("anoFundacion");
                            int capacidadEstadio = obj.getInt("capacidadEstadio");
                            int titulos = obj.getInt("titulos");
                            int entradaPartido = obj.getInt("entradaPartido");

                            JSONObject ligaObj = obj.optJSONObject("liga");
                            Long idLiga = ligaObj != null ? ligaObj.optLong("id", 0) : 0;

                            if (idLiga != 0) ligasDisponibles.add(idLiga);

                            builder.append("\nAño Fundacion: ").append(anoFundacion)
                                    .append("\nCapacidad Estadio: ").append(capacidadEstadio)
                                    .append("\nTítulos: ").append(titulos)
                                    .append("\nEntrada Partido: ").append(entradaPartido)
                                    .append("\n ID: ").append(idLiga)
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

    private void enviarDatos() {
        String anoFundacion = editTextAnoFundacion.getText().toString().trim();
        String capacidadEstadio = editTextCapacidadEstadio.getText().toString().trim();
        String titulos = editTexttitulos.getText().toString().trim();
        String entradaPartido = editTextEntradaPartido.getText().toString().trim();
        String idLiga=editTextIdLiga.getText().toString().trim();

        if (anoFundacion.isEmpty() || capacidadEstadio.isEmpty() || titulos.isEmpty() || entradaPartido.isEmpty()
        || idLiga.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos antes de enviar.");
            return;
        }

        long ligaId;
        try {
            ligaId = Long.parseLong(idLiga);
        } catch (NumberFormatException e) {
            textView.setText("❌ El ID de liga no es válido.");
            return;
        }

        /*
        if (!ligasDisponibles.contains(ligaId)) {
            textView.setText("❌ El ID de liga " + ligaId + " no existe.");
            return;
        }*/

        try {
            JSONObject ligaObj = new JSONObject();
            ligaObj.put("id", ligaId);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("anoFundacion", Integer.parseInt(anoFundacion));
            jsonBody.put("capacidadEstadio", Integer.parseInt(capacidadEstadio));
            jsonBody.put("titulos", Integer.parseInt(titulos));
            jsonBody.put("entradaPartido", Integer.parseInt(entradaPartido));
            jsonBody.put("liga",ligaObj);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        textView.setText("✅ Equipo insertado correctamente.");
                        editTextAnoFundacion.setText("");
                        editTextCapacidadEstadio.setText("");
                        editTexttitulos.setText("");
                        editTextEntradaPartido.setText("");
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

    private void eliminarEquipo() {
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
                    textView.setText("🗑️ Equipo eliminado correctamente.");
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

    private void actualizarEquipo() {
        String idStr = editTextIdActualizar.getText().toString().trim();
        String anoFundacion = editTextAnoFundacionActualizar.getText().toString().trim();
        String capacidadEstadio = editTextCapacidadEstadioActualizar.getText().toString().trim();
        String titulos = editTexttitulosActualizar.getText().toString().trim();
        String entradaPartido = editTextEntradaPartidoActualizar.getText().toString().trim();

        if (idStr.isEmpty() || anoFundacion.isEmpty() || capacidadEstadio.isEmpty() ||
                titulos.isEmpty() || entradaPartido.isEmpty()) {
            textView.setText("⚠️ Completa todos los campos para actualizar.");
            return;
        }

        String putUrl = url + "/" + idStr;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("anoFundacion", Integer.parseInt(anoFundacion));
            jsonBody.put("capacidadEstadio", Integer.parseInt(capacidadEstadio));
            jsonBody.put("titulos", Integer.parseInt(titulos));
            jsonBody.put("entradaPartido", Integer.parseInt(entradaPartido));

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> {
                        textView.setText("✏️ Equipo actualizado correctamente.");
                        editTextIdActualizar.setText("");
                        editTextAnoFundacionActualizar.setText("");
                        editTextCapacidadEstadioActualizar.setText("");
                        editTexttitulosActualizar.setText("");
                        editTextEntradaPartidoActualizar.setText("");
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

