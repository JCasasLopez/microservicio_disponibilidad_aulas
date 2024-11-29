package init;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import init.config.ConfiguracionHoraria;
import init.model.AulaDto;
import init.service.DisponibilidadService;

@WebMvcTest
public class DisponibilidadControllerCapaWebTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper; 
	
	@MockBean
	DisponibilidadService disponibilidadService;
	
	@MockBean
	ConfiguracionHoraria configuracionHoraria;


	@Test
	@DisplayName("Devuelve la lista de aulas disponibles")
	void aulasDisponibles() throws Exception {
		//Arrange
		AulaDto aula1 = new AulaDto(0, "aula1", 20, false, false);
		List<AulaDto> aulasDisponibles = List.of(aula1);
		LocalDateTime inicioPeriodo = LocalDateTime.of(2024, 11, 11, 10, 0); 
		LocalDateTime finalPeriodo = LocalDateTime.of(2024, 11, 15, 11, 0);
		when(disponibilidadService.aulasDisponibles(inicioPeriodo, finalPeriodo, 10, false, false))
											.thenReturn(aulasDisponibles);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/aulasDisponibles")
				 .param("horaInicio", inicioPeriodo.toString())  
                 .param("horaFin", finalPeriodo.toString())
                 .param("capacidad", "10")
                 .param("proyector", "false")
                 .param("altavoces", "false")
                 .accept(MediaType.APPLICATION_JSON);
		
		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		Assertions.assertEquals(HttpStatus.OK.value(), mcvResult.getResponse().getStatus());
		
		String responseContent = mcvResult.getResponse().getContentAsString();
		//TypeReference es necesario porque Java pierde la información de tipos genéricos en 
		//tiempo de ejecución (por el borrado de tipos). TypeReference le dice a Jackson exactamente 
		//qué tipo de objeto debe crear: una Lista de AulaDto.
		List<AulaDto> responseAulas = objectMapper.readValue(responseContent, 
	            new TypeReference<List<AulaDto>>() {});
		Assertions.assertEquals(1, responseAulas.size());
		Assertions.assertEquals("aula1", responseAulas.get(0).getNombre());
	}
}
