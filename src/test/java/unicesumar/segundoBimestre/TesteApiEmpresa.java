package unicesumar.segundoBimestre;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

@WebMvcTest
@AutoConfigureMockMvc
public class TesteApiEmpresa {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmpresaRepository repo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void testandoGetByIdComDadoInexistente() throws Exception {
		when(repo.findById("2")).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/api/empresa/2")).andExpect(status().isNotFound());
	}
	
	@Test
	void testandoGetByIdComDadoExistente() throws Exception {
		Empresa existente = new Empresa("1", "Unicesumar", "3263472637", "Av. Guedes", "Maringa", "Paraná", "");
		when(repo.findById("1")).thenReturn(existente);
		
		mockMvc.perform(get("/api/empresa/1"))
		.andExpect(jsonPath("$.id").value("1"))
		.andExpect(jsonPath("$.nomeRazaoSocial").value("Unicesumar"))
		.andExpect(jsonPath("$.cnpj").value("3263472637"))
		.andExpect(jsonPath("$.endereco").value("Av. Guedes"))
		.andExpect(jsonPath("$.cidade").value("Maringa"))
		.andExpect(jsonPath("$.estado").value("Paraná"))
		.andExpect(jsonPath("$.email").value(""))
		.andExpect(status().isOk());
	}
	
	@Test
	void testandoGetAll() throws Exception {
		Empresa unicesumar = new Empresa("1", "Unicesumar", "3263472637", "Av. Guedes", "Maringa", "Paraná", "");
		Empresa tecnospeed = new Empresa("2", "Tecno Speed", "7488347473", "Av. Teste", "Maringa", "Paraná", "");
		when(repo.findAll()).thenReturn(Arrays.asList(unicesumar, tecnospeed));
		
		mockMvc.perform(get("/api/empresa"))
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.[0].id").value("1"))
		.andExpect(jsonPath("$.[1].id").value("2"))
		.andExpect(jsonPath("$.[0].nome").value("Unicesumar"))
		.andExpect(jsonPath("$.[1].nome").value("Tecno Speed"))
		.andExpect(status().isOk());
	}
	@Test
	void testandoPost() throws Exception {
		when(repo.save(ArgumentMatchers.any(Empresa.class))).thenReturn("1");
		
		Map<String, String> empresa = new HashMap<String, String>() {{
		    put("id", "3");
		    put("nome", "Empresa Teste");
		    put("cnpj", "238642346");
		}};
		
		String json = objectMapper.writeValueAsString(empresa);

		mockMvc.perform(post("/api/empresa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated())
		.andExpect(content().string("3"));
	}

	@Test
	void testandoDelete() throws Exception {
		doNothing().when(repo).deleteById("1");
		mockMvc.perform(delete("/api/empresa/1")).andExpect(status().isNoContent());
	}

	@Test
	void testandoPutErrado() throws Exception {
		when(repo.save(ArgumentMatchers.any(Empresa.class))).thenThrow(RuntimeException.class);

		Map<String, String> empresa = new HashMap<String, String>() {{
		    put("id", "3");
		    put("nome", "Empresa Teste");
		    put("cnpj", "238642346");
		}};

		String json = objectMapper.writeValueAsString(empresa);

		mockMvc.perform(put("/api/empresa/3")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testandoPutOk() throws Exception {
		when(repo.save(ArgumentMatchers.any(Empresa.class))).thenReturn("3");

		Map<String, String> empresa = new HashMap<String, String>() {{
		    put("id", "3");
		    put("nome", "Empresa Teste");
		    put("cnpj", "238642346");
		}};

		String json = objectMapper.writeValueAsString(empresa);

		mockMvc.perform(put("/api/empresa/3")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isNoContent());
	}	
}