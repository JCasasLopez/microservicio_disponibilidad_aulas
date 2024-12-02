package init;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import init.model.AulaDto;
import init.model.SlotDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DisponibilidadControllerIntegrationTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Container
	private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0")
					.withDatabaseName("reservasaulas")
					.withUsername("root")
					.withPassword("Vindel39!");
	
	static {
        mySQLContainer.start();
    }
	
	@Test
	@DisplayName("El contenedor MySQL se ha creado y funciona correctamente")
	void testContainerfunciona() {
		assertTrue(mySQLContainer.isCreated(), "El contenedor MySQL no se ha creado");
		assertTrue(mySQLContainer.isRunning(), "El contenedor MySQL no funciona correctamente");
	}
	
	@Test
	@DisplayName("aulasDisponibles devuelve una respuesta válida")
	void aulasDisponibles_devuelveRespuestaValida() {
		//Arrange
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");	
		
		LocalDateTime horaInicio = LocalDateTime.of(2024, 11, 11, 9, 0);
	    LocalDateTime horaFin = LocalDateTime.of(2024, 11, 11, 19, 0);
	    int capacidad = 0;
	    boolean proyector = false;
	    boolean altavoces = false;

	    String url = UriComponentsBuilder
	        .fromPath("/aulasDisponibles")
	        .queryParam("horaInicio", horaInicio)
	        .queryParam("horaFin", horaFin)
	        .queryParam("capacidad", capacidad)
	        .queryParam("proyector", proyector)
	        .queryParam("altavoces", altavoces)
	        .toUriString();
		
		HttpEntity requestEntity = new HttpEntity(null, headers);
		
		//Act
		ResponseEntity<List<AulaDto>> response = testRestTemplate.exchange(
				url,
				HttpMethod.GET, 
				requestEntity, 
				new ParameterizedTypeReference<List<AulaDto>>() {});
		
		//Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertNotNull(response.getBody());
	    
	    // Verifica que el Content-Type es correcto
	    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
	    
	    // Verifica que la respuesta es una lista (puede estar vacía o no)
	    assertInstanceOf(List.class, response.getBody());
	    
	    // Si la lista no está vacía, verifica que los elementos son del tipo correcto
	    if (!response.getBody().isEmpty()) {
	        assertInstanceOf(AulaDto.class, response.getBody().get(0));
	    }
	}
	
	@Test
	@DisplayName("crearHorarioAula devuelve una respuesta válida")
	void crearHorarioAula_devuelveRespuestaValida() {
		//Arrange
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		
		int idAula = 194;
		LocalDate inicioPeriodo = LocalDate.now();
		LocalDate finalPeriodo = inicioPeriodo.plusDays(1);
		
		String url = UriComponentsBuilder
				.fromPath("/crearHorarioAula")
				.queryParam("idAula", idAula)
				.queryParam("inicioPeriodo", inicioPeriodo)
				.queryParam("finalPeriodo", finalPeriodo)
				.toUriString();
				
		HttpEntity requestEntity = new HttpEntity(null, headers);
		
		//Act
		ResponseEntity<List<SlotDto>> response = testRestTemplate.exchange(
				url, 
				HttpMethod.GET,
				requestEntity,
				new ParameterizedTypeReference<List<SlotDto>>() {});
		
		System.out.println("Respuesta del servidor: " + response.getBody());
		
		//Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertInstanceOf(List.class, response.getBody());
		if (!response.getBody().isEmpty()) {
	        assertInstanceOf(SlotDto.class, response.getBody().get(0));
	    }
		
	}

}
