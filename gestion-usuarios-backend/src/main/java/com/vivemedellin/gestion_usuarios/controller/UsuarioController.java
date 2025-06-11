/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;


import com.vivemedellin.gestion_usuarios.dto.RegistroComplementarioDTO;
import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.entity.*;
import com.vivemedellin.gestion_usuarios.repository.*;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/usuarios") // Esta es la URL base de la API
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final MunicipioRepository municipioRepository;
    private final InteresRepository interesRepository;
    private final InteresXUsuarioRepository interesXUsuarioRepository;
    private final TokenVerificacionRepository tokenVerificacionRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, MunicipioRepository municipioRepository, InteresRepository interesRepository,InteresXUsuarioRepository interesXUsuarioRepository, TokenVerificacionRepository tokenVerificacionRepository){
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
        this.interesRepository = interesRepository;
        this.interesXUsuarioRepository = interesXUsuarioRepository;
        this.tokenVerificacionRepository = tokenVerificacionRepository;
    }
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody @Valid RegistroUsuarioDTO dto) {
        usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
    
    private static final List<String> PALABRAS_INAPROPIADAS = List.of("xxx", "puta", "mierda","pendiente");

    @PostMapping("/registro-complementario")
    public ResponseEntity<String> completarRegistro(@RequestBody RegistroComplementarioDTO dto, @RequestParam String email) {
        logger.info("DTO recibido: {}", dto);
        Usuario usuario = usuarioRepository.findByCor   reoElectronico(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        actualizarCamposUsuario(dto, usuario);
        usuarioRepository.save(usuario);
        guardarIntereses(dto, usuario);

        return ResponseEntity.ok("Registro completado correctamente");
    }

    private void actualizarCamposUsuario(RegistroComplementarioDTO dto, Usuario usuario) {
        validarYAsignarApodo(dto.getApodo(), usuario);

        if (esNoVacio(dto.getNombre())) usuario.setNombre(dto.getNombre());
        if (dto.getSegundoNombre() != null) usuario.setSegundoNombre(dto.getSegundoNombre());
        if (dto.getApellido() != null) usuario.setApellido(dto.getApellido());
        if (dto.getSegundoApellido() != null) usuario.setSegundoApellido(dto.getSegundoApellido());
        if (dto.getTelefono() != null) usuario.setTelefono(dto.getTelefono());
        if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());
        if (dto.getBiografia() != null) usuario.setBiografia(dto.getBiografia());
        if (dto.getFechaNacimiento() != null) usuario.setFechaNacimiento(dto.getFechaNacimiento());

        if (dto.getIdMunicipio() != null) {
            Municipio municipio = municipioRepository.findById(dto.getIdMunicipio())
                    .orElseThrow(() -> new IllegalArgumentException("Municipio no válido"));
            usuario.setMunicipio(municipio);
        }
    }

    private void validarYAsignarApodo(String apodo, Usuario usuario) {
        if (apodo == null) return;

        if (usuarioRepository.existsByApodo(apodo)) {
            throw new IllegalArgumentException("El apodo ya está en uso.");
        }

        for (String palabra : PALABRAS_INAPROPIADAS) {
            if (apodo.toLowerCase().contains(palabra)) {
                throw new IllegalArgumentException("El apodo contiene palabras inapropiadas.");
            }
        }

        usuario.setApodo(apodo);
    }

    private void guardarIntereses(RegistroComplementarioDTO dto, Usuario usuario) {
        if (dto.getIdsIntereses() == null || dto.getIdsIntereses().isEmpty()) return;

        for (Integer idInteres : dto.getIdsIntereses()) {
            Interes interes = interesRepository.findById(idInteres)
                    .orElseThrow(() -> new IllegalArgumentException("Interés con ID " + idInteres + " no válido"));

            InteresXUsuario ixu = new InteresXUsuario();
            ixu.setUsuario(usuario);
            ixu.setInteres(interes);
            interesXUsuarioRepository.save(ixu);
        }
    }

    private boolean esNoVacio(String valor) {
        return valor != null && !valor.isBlank();
    }

    @GetMapping("/verificar")
    public ResponseEntity<String> verificarCorreo(@RequestParam("token") String token) {
        TokenVerificacion tv = tokenVerificacionRepository.findByToken(token);

        if (tv == null || tv.getExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }

        Usuario usuario = tv.getUsuario();
        usuario.setCorreoVerificado(true);
        usuarioRepository.save(usuario);
        tokenVerificacionRepository.delete(tv); // elimina el token tras validarlo

        return ResponseEntity.ok("Correo verificado con éxito.");
    }


}
