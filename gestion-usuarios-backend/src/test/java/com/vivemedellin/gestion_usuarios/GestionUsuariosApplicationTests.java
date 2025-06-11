package com.vivemedellin.gestion_usuarios;


import com.vivemedellin.gestion_usuarios.entity.TokenVerificacion;
import com.vivemedellin.gestion_usuarios.entity.Usuario;
import com.vivemedellin.gestion_usuarios.repository.TokenVerificacionRepository;
import com.vivemedellin.gestion_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GestionUsuariosApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TokenVerificacionRepository tokenVerificacionRepository;

	@AfterEach
	void tearDown() {
		usuarioRepository.findByApodo("juan123")
				.ifPresent(usuarioRepository::delete);
	}

	String jsonRegister = """
		{
			"apodo": "juan123",
			"nombre": "Juan",
			"segundoNombre": "Carlos",
			"apellido": "Pérez",
			"segundoApellido": "Gómez",
			"correoElectronico": "juan.perez@example.com",
			"contraseña": "Secreta123!",
			"confirmarContraseña": "Secreta123!",
			"fotoPerfil": "https://example.com/foto.jpg",
			"biografia": "Soy desarrollador Java.",
			"telefono": "3001234567",
			"fechaNacimiento": "1990-05-15",
			"idMunicipio": 1,
			"idsIntereses": [1]
		}
		""";
	String jsonLogin = """
			{
				"apodo": "juan123",
				"contraseña": "Secreta123!"
			}
		""";

	String jsonLoginBad = """
			{
				"apodo": "juan123",
				"contraseña": "mala"
			}
		""";

	@Test
	void testRegistroUsuario() throws Exception {
		// Registro exitoso
		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonRegister))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));
	}

	@Test
	void testRegistroUsuarioCorreoExistente() throws Exception {
		// Registro exitoso
		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonRegister))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));

		// Intento duplicado (mismo correo)
		String jsonDuplicado = jsonRegister.replace("\"apodo\": \"juan123\"", "\"apodo\": \"juan456\"");

		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonDuplicado))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("El correo ya está registrado."));
	}

	@Test
	public void testLoginExitoso() throws Exception {
		// Registro exitoso
		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonRegister))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonLogin))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	public void testLoginContrasenaIncorrecta() throws Exception {
		// Registro exitoso
		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonRegister))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonLoginBad))
				.andExpect(status().isUnauthorized())
				.andExpect(status().reason("Credenciales inválidas"));
	}

	@Test
	void testVerificarCorreoConTokenValido() throws Exception {
		// Registro exitoso
		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(jsonRegister))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));

		Usuario usuario = usuarioRepository.findByCorreoElectronico("juan.perez@example.com")
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		TokenVerificacion token = tokenVerificacionRepository.findByUsuario(usuario);

		mockMvc.perform(get("/api/usuarios/verificar")
						.param("token", token.getToken()))
				.andExpect(status().isOk())
				.andExpect(content().string("Correo verificado con éxito."));
	}

	@Test
	void testLogoutExitoso() throws Exception {
		mockMvc.perform(post("/api/auth/logout"))
				.andExpect(status().isOk())
				.andExpect(content().string("Logout exitoso. Token eliminado del cliente."));
	}


}
