package com.example.tfgapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnAccion;
    private Switch switchModo;
    private TextView tvModo;
    private RequestQueue requestQueue;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 100;
    private boolean esLogin = true; // por defecto login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        // Comprobar si ya hay token guardado
        String token = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("jwt", null);

        if (token != null) {
            // Usuario ya logueado → ir directo a MainActivity
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/

        setContentView(R.layout.activity_auth);

        // Configurar opciones de inicio de sesión de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Inicializar cliente de Google Sign-In
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Botón de Google
        findViewById(R.id.btnGoogle).setOnClickListener(v -> iniciarSesionGoogle());


        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnAccion = findViewById(R.id.btnAccion);
        switchModo = findViewById(R.id.switchModo);
        tvModo = findViewById(R.id.tvModo);

        requestQueue = Volley.newRequestQueue(this);

        // Listener del switch para cambiar entre login y registro
        switchModo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            esLogin = !isChecked;
            actualizarModo();
        });

        // Botón principal: login o registro según el modo
        btnAccion.setOnClickListener(v -> {
            if (esLogin) {
                hacerLogin();
            } else {
                hacerRegister();
            }
        });

        actualizarModo(); // Actualiza el texto inicial
    }

    private void actualizarModo() {
        if (esLogin) {
            tvModo.setText("Modo: Login");
            btnAccion.setText("Iniciar sesión");
        } else {
            tvModo.setText("Modo: Registro");
            btnAccion.setText("Registrar usuario");
        }
    }

    private void hacerLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiClient.BASE_URL + "auth/login";

        JSONObject body = new JSONObject();
        try {
            body.put("usuario", usuario);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    try {
                        String token = response.getString("token");

                        // ✅ Guardar token
                        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("jwt", token)
                                .apply();

                        Toast.makeText(this, "✅ Login exitoso", Toast.LENGTH_SHORT).show();

                        // ✅ Redirigir a MainActivity
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    int code = (error.networkResponse != null) ? error.networkResponse.statusCode : 0;
                    Toast.makeText(this, "❌ Error de login (" + code + ")", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }


    private void hacerRegister() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiClient.BASE_URL + "auth/register";

        JSONObject body = new JSONObject();
        try {
            body.put("usuario", usuario);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    Toast.makeText(this, "✅ Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    // Cambiar automáticamente a modo login
                    switchModo.setChecked(false);
                    esLogin = true;
                    actualizarModo();
                },
                error -> {
                    int code = (error.networkResponse != null) ? error.networkResponse.statusCode : 0;
                    Toast.makeText(this, "❌ Error de registro (" + code + ")", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void iniciarSesionGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            manejarResultadoGoogle(task);
        }
    }

    private void manejarResultadoGoogle(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount cuenta = task.getResult(ApiException.class);

            if (cuenta != null) {
                String email = cuenta.getEmail();
                String nombre = cuenta.getDisplayName();
                String idToken = cuenta.getIdToken(); // si lo pediste con requestIdToken()

                Toast.makeText(this, "✅ Sesión con Google: " + nombre, Toast.LENGTH_SHORT).show();

                // Aquí podrías enviar el idToken a tu backend para validarlo con Google
                // Ejemplo:
                // enviarTokenAGoogleBackend(idToken);

                // Ir a la siguiente actividad
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (ApiException e) {
            Toast.makeText(this, "❌ Error al iniciar con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}


