package init;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import init.model.SlotDto;
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
	
	//Métodos auxiliares para reducir la cantidad de código "boilerplate"
	private RequestBuilder requestBuilderAulas(LocalDateTime inicioPeriodo, LocalDateTime finalPeriodo,
			int capacidad, boolean proyector, boolean altavoces) {
		return MockMvcRequestBuilders.get("/aulasDisponibles")
				.param("horaInicio", inicioPeriodo.toString())  
				.param("horaFin", finalPeriodo.toString())
				.param("capacidad", String.valueOf(capacidad))
				.param("proyector", String.valueOf(proyector))
				.param("altavoces", String.valueOf(altavoces))
				.accept(MediaType.APPLICATION_JSON);
	}
	
	private RequestBuilder requestBuilderHorario(int idAula, LocalDate inicioPeriodo,
			LocalDate finalPeriodo) {
		return MockMvcRequestBuilders.get("/crearHorarioAula")
				.param("idAula", String.valueOf(idAula))
				.param("inicioPeriodo", inicioPeriodo.toString())  
				.param("finalPeriodo", finalPeriodo.toString())  
				.accept(MediaType.APPLICATION_JSON);
	}
	
	//TESTS PROPIAMENTE DICHOS
	@Test
	@DisplayName("Devuelve la lista de aulas disponibles")
	void aulasDisponibles() throws Exception {
		//Arrange
		LocalDateTime inicioPeriodo = LocalDateTime.of(2024, 11, 11, 10, 0); 
		LocalDateTime finalPeriodo = inicioPeriodo.plusHours(1);
		when(disponibilidadService.aulasDisponibles(inicioPeriodo, finalPeriodo, 10, false, false))
											.thenReturn(List.of(new AulaDto()));
		RequestBuilder requestBuilder = requestBuilderAulas(inicioPeriodo, finalPeriodo, 
				10, false, false);
		
		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		Assertions.assertEquals(HttpStatus.OK.value(), mcvResult.getResponse().getStatus(), 
				"El estatus HTTP devuelto es incorrecto");
		
		String responseContent = mcvResult.getResponse().getContentAsString();
		//TypeReference es necesario porque Java pierde la información de tipos genéricos en 
		//tiempo de ejecución (por el borrado de tipos). TypeReference le dice a Jackson exactamente 
		//qué tipo de objeto debe crear: una Lista de AulaDto.
		List<AulaDto> responseAulas = objectMapper.readValue(responseContent, 
	            new TypeReference<List<AulaDto>>() {});
		Assertions.assertEquals(1, responseAulas.size(), 
				"La lista no contiene el número de aulas esperada");
	}
	
	@ParameterizedTest
	@CsvSource ({"12, 11",  
		    "21, 21",   
		    })
	@DisplayName("El inicio no puede ser anterior a la finalización")
	void aulasDisponibles_lanzaExcepcionSiHorasNoSonCorrectas(int horaInicio, int horaFinal) throws Exception {
		//Arrange
		LocalDateTime inicioPeriodo = LocalDateTime.of(2024, 11, 11, horaInicio, 0);
		LocalDateTime finalPeriodo = LocalDateTime.of(2024, 11, 11, horaFinal, 0); 
		when(disponibilidadService.aulasDisponibles(inicioPeriodo, finalPeriodo, 10, false, false))
													.thenReturn(List.of(new AulaDto()));
				
		RequestBuilder requestBuilder = requestBuilderAulas(inicioPeriodo, finalPeriodo, 
				10, false, false);
				
		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mcvResult.getResponse().getStatus(), 
				"El estatus HTTP devuelto es incorrecto");
	}
	
	@Test
	@DisplayName("La capacidad no puede ser negativa")
	void aulasDisponibles_lanzaExcepcionSiCapacidadNegativa() throws Exception {
		//Arrange
		LocalDateTime inicioPeriodo = LocalDateTime.of(2024, 11, 11, 11, 0);
		LocalDateTime finalPeriodo = inicioPeriodo.plusHours(1);
		when(disponibilidadService.aulasDisponibles(inicioPeriodo, finalPeriodo, -10, false, false))
													.thenReturn(List.of(new AulaDto()));
				
		RequestBuilder requestBuilder = requestBuilderAulas(inicioPeriodo, finalPeriodo, -10, 
				false, false);
				
		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mcvResult.getResponse().getStatus(), 
				"El estatus HTTP devuelto es incorrecto");
	}
	
	@Test
	@DisplayName("Devuelve el horario actualizado del aula")
	void crearHorarioAula_horarioActualizado() throws Exception{
		//Arrange
		int idAula = 1;
		LocalDate inicioPeriodo = LocalDate.of(2024, 11, 11);
		LocalDate finalPeriodo = inicioPeriodo.plusDays(1);		
		when(disponibilidadService.crearHorarioAula(idAula, 
				inicioPeriodo.atTime(configuracionHoraria.getHoraApertura(), 0), 
				finalPeriodo.atTime(configuracionHoraria.getHoraCierre(), 0)))
										.thenReturn(List.of(new SlotDto()));
		RequestBuilder requestBuilder = requestBuilderHorario(idAula, inicioPeriodo, finalPeriodo);
		
		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		String responseContent = mcvResult.getResponse().getContentAsString();
		List<SlotDto> reponseSlots = objectMapper.readValue(responseContent, 
				new TypeReference<List<SlotDto>>() {});
		Assertions.assertEquals(HttpStatus.OK.value(), mcvResult.getResponse().getStatus(), 
				"El estatus HTTP devuelto es incorrecto");
		Assertions.assertEquals(1, reponseSlots.size(), 
				"La lista no contiene el número de slots esperado");
	}
	
	@ParameterizedTest
	@CsvSource ({"11, 10",  
		    "21, 21",   
		    })
	@DisplayName("El día inicio no puede ser anterior al de finalización")
	void crearHorarioAula_lanzaExcepcionSiFechasNoSonCorrectas(int diaInicio, int diaFinal) 
			throws Exception {
		//Arrange
		int idAula = 1;
		LocalDate inicioPeriodo = LocalDate.of(2024, 11, diaInicio);
		LocalDate finalPeriodo = LocalDate.of(2024, 11, diaFinal);
		when(disponibilidadService.crearHorarioAula(idAula, 
				inicioPeriodo.atTime(configuracionHoraria.getHoraApertura(), 0), 
				finalPeriodo.atTime(configuracionHoraria.getHoraCierre(), 0)))
							.thenReturn(List.of(new SlotDto()));
		RequestBuilder requestBuilder = requestBuilderHorario(idAula, inicioPeriodo, finalPeriodo);

		//Act
		MvcResult mcvResult = mockMvc.perform(requestBuilder).andReturn();
		
		//Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mcvResult.getResponse().getStatus());
	}
}
