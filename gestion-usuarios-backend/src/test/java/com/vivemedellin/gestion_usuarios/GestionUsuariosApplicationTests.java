package com.vivemedellin.gestion_usuarios;


import com.vivemedellin.gestion_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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


	@Test
	void testRegistroUsuario() throws Exception {
		String json = """
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

		mockMvc.perform(post("/api/usuarios/registrar")
						.contentType("application/json")
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().string("Usuario registrado exitosamente."));
	}

	@AfterEach
	void tearDown() {
		usuarioRepository.findByApodo("juan123")
				.ifPresent(usuarioRepository::delete);
	}

}
