package com.vivemedellin.gestion_usuarios.service;

import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.entity.*;
import com.vivemedellin.gestion_usuarios.repository.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final MunicipioRepository municipioRepository;

    private final InteresRepository interesRepository;

    private final InteresXUsuarioRepository interesXUsuarioRepository;

    private final TokenVerificacionRepository tokenVerificacionRepository;

    private final CorreoService correoService;

    private static final List<String> PALABRAS_INAPROPIADAS = List.of("xxx", "puta", "mierda","pendiente");

    public UsuarioService(UsuarioRepository usuarioRepository, MunicipioRepository municipioRepository, InteresRepository interesRepository, InteresXUsuarioRepository interesXUsuarioRepository, TokenVerificacionRepository tokenVerificacionRepository, CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
        this.interesRepository = interesRepository;
        this.interesXUsuarioRepository = interesXUsuarioRepository;
        this.tokenVerificacionRepository = tokenVerificacionRepository;
        this.correoService = correoService;
    }

    public Usuario autenticarOCrearUsuarioDesdeGoogle(String email) {
        return usuarioRepository.findByCorreoElectronico(email)
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario();
                    Municipio municipio = new Municipio();
                    municipio.setId(1);
                    nuevoUsuario.setCorreoElectronico(email);
                    nuevoUsuario.setNombre("PENDIENTE");
                    nuevoUsuario.setApellido("PENDIENTE");
                    nuevoUsuario.setApodo(UUID.randomUUID().toString().substring(0, 8));
                    nuevoUsuario.setContraseña(encriptarPassword("Pendiente"));
                    nuevoUsuario.setFechaNacimiento(LocalDate.of(1900, 1, 1));
                    nuevoUsuario.setCorreoVerificado(true);
                    nuevoUsuario.setRegistradoManual(false);
                    nuevoUsuario.setMunicipio(municipio);
                    return usuarioRepository.save(nuevoUsuario);
                });
    }   

    public void registrarUsuario(RegistroUsuarioDTO dto) {

        if (usuarioRepository.existsByApodo(dto.getApodo())) {
            throw new IllegalArgumentException("El apodo ya está en uso.");
        }

        for (String palabra : PALABRAS_INAPROPIADAS) {
            if (dto.getApodo().toLowerCase().contains(palabra)) {
                throw new IllegalArgumentException("El apodo contiene palabras inapropiadas.");
            }
        }

        // Validar correo
        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        // Validar contraseña
        String password = dto.getContraseña();
        if (!password.equals(dto.getConfirmarContraseña())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        if (!validarPassword(password)) {
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos.");
        }

        // Crear entidad Usuario
        Usuario u = new Usuario();
        u.setApodo(dto.getApodo());
        u.setNombre(dto.getNombre());
        u.setSegundoNombre(dto.getSegundoNombre());
        u.setApellido(dto.getApellido());
        u.setSegundoApellido(dto.getSegundoApellido());
        u.setCorreoElectronico(dto.getCorreoElectronico());
        u.setContraseña(encriptarPassword(password));
        u.setFotoPerfil(dto.getFotoPerfil());
        u.setBiografia(dto.getBiografia());
        u.setTelefono(dto.getTelefono());
        u.setFechaNacimiento(dto.getFechaNacimiento());

        // Asociar municipio
        Municipio municipio = municipioRepository.findById(dto.getIdMunicipio())
            .orElseThrow(() -> new IllegalArgumentException("Municipio no válido"));
        u.setMunicipio(municipio);

        usuarioRepository.save(u); // Guarda primero el usuario para tener su apodo como FK

        // Asociar interés
        for (Integer idInteres : dto.getIdsIntereses()) {
            Interes interes = interesRepository.findById(idInteres)
                .orElseThrow(() -> new IllegalArgumentException("Interés con ID " + idInteres + " no válido"));

            InteresXUsuario ixu = new InteresXUsuario();
            ixu.setUsuario(u);
            ixu.setInteres(interes);

            interesXUsuarioRepository.save(ixu);
        }
        String token = UUID.randomUUID().toString();
        TokenVerificacion tv = new TokenVerificacion();
        tv.setToken(token);
        tv.setUsuario(u);
        tv.setExpiracion(LocalDateTime.now().plusHours(24)); // válido por 24h

        tokenVerificacionRepository.save(tv);

        // Enviar correo
        String link = "http://localhost:8080/api/usuarios/verificar?token=" + token;
        correoService.enviarCorreoVerificacion(u.getCorreoElectronico(), link);
    }

    private boolean validarPassword(String password) {
    if (password.length() < 8) {
        return false;
    }

    boolean tieneMayuscula = false;
    int cantidadNumeros = 0;
    boolean tieneEspecial = false;

    for (char c : password.toCharArray()) {
        if (Character.isUpperCase(c)) {
            tieneMayuscula = true;
        } else if (Character.isDigit(c)) {
            cantidadNumeros++;
        } else if ("!@#$%^&*()_+-={}[]:;\"'<>,.?/\\|".contains(String.valueOf(c))) {
            tieneEspecial = true;
        }
    }

    return tieneMayuscula && cantidadNumeros >= 3 && tieneEspecial;
}


    private String encriptarPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }
}