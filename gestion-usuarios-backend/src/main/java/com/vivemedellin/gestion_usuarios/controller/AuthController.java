/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vivemedellin.gestion_usuarios.config.JwtUtil;
import com.vivemedellin.gestion_usuarios.dto.LoginDTO;
import com.vivemedellin.gestion_usuarios.entity.Usuario;
import com.vivemedellin.gestion_usuarios.repository.UsuarioRepository;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author David
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/google")
    public ResponseEntity<Object> autenticarConGoogle(@RequestBody Map<String, String> request) throws IOException {
        String idToken = request.get("idToken");

        Optional<GoogleIdToken.Payload> payloadOptional = verificarTokenGoogle(idToken);

        if (payloadOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        GoogleIdToken.Payload payload = payloadOptional.get();
        if (logger.isInfoEnabled()) {
            logger.info("Payload: {}", payload.toPrettyString());
        }

        String email = payload.getEmail();
        Usuario usuario = usuarioService.autenticarOCrearUsuarioDesdeGoogle(email);
        return ResponseEntity.ok(usuario);

    }
    
    private Optional<GoogleIdToken.Payload> verificarTokenGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("604384290651-21e4ksnnivctu7ved76u9nvv35i9j5tc.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            return Optional.ofNullable(idToken != null ? idToken.getPayload() : null);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByApodo(dto.getApodo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!usuario.isRegistradoManual()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Este usuario debe iniciar sesión con Google");
        }

        if (!new BCryptPasswordEncoder().matches(dto.getContraseña(), usuario.getContraseña())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario.getCorreoElectronico());

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

        @PostMapping("/logout")
        public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
            // No se puede invalidar un JWT sin mantener estado,
            // pero puedes devolver un mensaje para que el frontend elimine el token.
            return ResponseEntity.ok("Logout exitoso. Token eliminado del cliente.");
        }

}

