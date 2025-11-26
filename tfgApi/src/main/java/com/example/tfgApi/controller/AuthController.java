package com.example.tfgApi.controller;

import com.example.tfgApi.model.Usuario;
import com.example.tfgApi.repository.UsuarioRepository;
import com.example.tfgApi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        String usuario = datos.get("usuario");
        String password = datos.get("password");

        // Verificar si el usuario ya existe
        Optional<Usuario> existingUser = usuarioRepository.findByUsuario(usuario);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body(Map.of("error", "El usuario ya existe"));
        }

        // Cifrar la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(password);
        Usuario nuevoUsuario = new Usuario(usuario, encodedPassword);
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado con éxito"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String password = credenciales.get("password");

        Optional<Usuario> userOpt = usuarioRepository.findByUsuario(usuario);

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = JwtUtil.generarToken(usuario);
                return ResponseEntity.ok(Map.of("token", token));
            }
        }

        return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
    }

    @GetMapping("/public")
    public String publico() {
        return "Ruta pública — sin autenticación.";
    }

    @GetMapping("/private")
    public String privado() {
        return "Ruta privada — autenticación con JWT válida.";
    }
}

