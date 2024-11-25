package init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import init.config.ConfiguracionHoraria;
import init.entities.Aula;
import init.entities.Reserva;
import init.entities.Usuario;
import init.exceptions.SlotNotFoundException;
import init.model.SlotDto;
import init.service.GestorSlotsService;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
public class GestorSlotsServiceTest {
	
	@Value("${horario.apertura}")
    private int horaInicio;
    
    @Value("${horario.cierre}")
    private int horaFinalizacion;
    
    //Estos 3 métodos solo valen para reducir la cantidad de código de creación de objetos
    GestorSlotsService configurarHorario(int horaApertura, int horaCierre) {
    	ConfiguracionHoraria configHoraria = new ConfiguracionHoraria(horaApertura, horaCierre);
		return new GestorSlotsService(configHoraria);
    }
    
    Aula crearAula() {
    	return new Aula(0, "aula1", 20, false, false);
    }
    
    Usuario crearUsuario() {
    	return new Usuario(0, "aaa@gmail.com", "Jorge", "Casas López", LocalDate.of(1978, 11, 26), 
    															"Vindel39!");
    }
	
	@ParameterizedTest
	@CsvSource ({"9, 22",  
			"8, 20",    
		    "9, 21",   
		    "8, 21",    
		    "10, 19"})
	@DisplayName("Primer slot: lunes a la hora de apertura. Ultimo: viernes a la hora de cierre")
	void crearSlots_primerSlotYUltimo(int horaApertura, int horaCierre) {
		//Arrange
		GestorSlotsService gestorSlotsService = configurarHorario(horaApertura, horaCierre);
		SlotDto primerSlotSemana = new SlotDto(1, 
								LocalDateTime.of(2024, 11, 11, horaApertura, 0),
								LocalDateTime.of(2024, 11, 11, horaApertura, 30));
		SlotDto ultimoSlotSemana = new SlotDto(1, 
								LocalDateTime.of(2024, 11, 15, horaCierre - 1, 30),
								LocalDateTime.of(2024, 11, 15, horaCierre, 0));

		//Act
		List<SlotDto> listaSlots = gestorSlotsService.crearSlots(1, 
													LocalDateTime.of(2024, 11, 11, horaApertura, 0));
		Collections.sort(listaSlots);

		//Assert
		Assertions.assertTrue(listaSlots.get(0).equals(primerSlotSemana), 
				"Se espera " + primerSlotSemana + ", pero se obtuvo " + listaSlots.get(0));
		Assertions.assertTrue(listaSlots.get(listaSlots.size()-1).equals(ultimoSlotSemana), 
				"Se espera " + ultimoSlotSemana + ", pero se obtuvo " + listaSlots.get(listaSlots.size()-1));
	}
	
	@Test
	@DisplayName("Los slots se crean SOLO hasta el viernes a la hora de cierre")
	void crearSlots_noDeberiaGenerarSlotsEnFinDeSemana() {
		//Arrange
		GestorSlotsService gestorSlotsService = configurarHorario(horaInicio, horaFinalizacion);
	    LocalDateTime inicioSabado = LocalDateTime.of(2024, 11, 16, horaInicio, 0);
	    
	    //Act
	    List<SlotDto> slots = gestorSlotsService.crearSlots(1, inicioSabado);
	    
	    //Assert
	    Assertions.assertTrue(slots.isEmpty(), 
	    							"No deberían generarse slots para el fin de semana");
	}
	
	@ParameterizedTest
	@CsvSource ({"9, 22",  
			"8, 20",    
		    "9, 21",   
		    "8, 21",    
		    "10, 19"})
	@DisplayName("Número total de slots creados correcto")
	void crearSlots_slotsTotalesCreados(int horaApertura, int horaCierre) {
		//Arrange
		GestorSlotsService gestorSlotsService = configurarHorario(horaApertura, horaCierre);
		LocalDateTime horaInicioSemana = LocalDateTime.of(2024, 11, 11, horaApertura, 0);
		//Calcula el número a partir de las horas de cierre, apertura y longitud del intervalo.
		int numeroSlotsTotales = (((horaCierre - horaApertura) * 60)/ 30) * 5;
		
		//Act
		List<SlotDto> listaSlots = gestorSlotsService.crearSlots(1, horaInicioSemana);
				
		//Assert
		Assertions.assertEquals(numeroSlotsTotales, listaSlots.size(), 
				"Valor esperado: " + numeroSlotsTotales + ", pero devuelve: " + listaSlots.size());
	}
	
	@Test
	@DisplayName("Disponibilidad correcta de slots al crearse (pasados NO disponibles)")
	void disponibilidadSlotsAlCrearse() {
		//Arrange
		LocalDateTime ayer = LocalDateTime.now().minusDays(1);
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		
		//Act 
		SlotDto slotPasado = new SlotDto(1, ayer, ayer.plusMinutes(30));
		SlotDto slotFuturo = new SlotDto(1, tomorrow, tomorrow.plusMinutes(30));
		SlotDto slotAhora = new SlotDto(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));

		//Assert
		Assertions.assertAll("Verificación de disponibilidad de slots",
				() -> Assertions.assertFalse(slotPasado.isDisponible(),
						"Un slot pasado no debe estar disponible"),
				() -> Assertions.assertTrue(slotFuturo.isDisponible(),
						"Un slot futuro debe estar disponible"),
				() -> Assertions.assertTrue(slotAhora.isDisponible(),
						"Un slot ahora mismo debe estar disponible"));
	}
	
	@Test
	@DisplayName("Disponibilidad correcta de slots, reflejando las reservas coincidentes")
	void actualizarDisponibilidad_disponibilidadSlotsTrasActualizarse() {
		//Arrange
		GestorSlotsService gestorSlotsService = configurarHorario(horaInicio, horaFinalizacion);
		LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1).withHour(15).withMinute(0);
		LocalDateTime inicioReserva = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0);
		LocalDateTime finReserva = inicioReserva.plusMinutes(30);
		LocalDateTime fechaFuturaSinReservas = LocalDateTime.now().plusDays(2).withHour(15).withMinute(0);
		List<Reserva> reservas = List.of(new Reserva(0, inicioReserva, finReserva, crearAula(), crearUsuario()));
	    
		SlotDto slotPasado = new SlotDto(0, fechaPasada, fechaPasada.plusMinutes(30));
		SlotDto slotAntes = new SlotDto(0, inicioReserva.minusHours(1), finReserva.minusHours(1));
		SlotDto slotJustoAntes = new SlotDto(0, inicioReserva.minusMinutes(30), finReserva.minusMinutes(30));
		SlotDto slotDurante = new SlotDto(0, inicioReserva, finReserva);
		SlotDto slotJustoDespues = new SlotDto(0, inicioReserva.plusMinutes(30), finReserva.plusMinutes(30));
		SlotDto slotDespues = new SlotDto(0, inicioReserva.plusHours(1), finReserva.plusHours(1));
		SlotDto slotOtroDia = new SlotDto(0, fechaFuturaSinReservas, fechaFuturaSinReservas.plusMinutes(30));
		List<SlotDto> slots = new ArrayList<>(List.of(slotPasado, slotAntes, slotJustoAntes, slotDurante, 
								slotJustoDespues, slotDespues, slotOtroDia));
		
		//Act
		gestorSlotsService.actualizarDisponibilidad(reservas, slots);
		
		//Assert
		//Al usar assertAll si falla una aserción el test continuará ejecutando las demás
		//y ves todos los fallos de una vez, no solo el primero
	    Assertions.assertAll("Verificación de disponibilidad de slots",
	        () -> Assertions.assertFalse(slotPasado.isDisponible(), 
	                "Un slot en el pasado no debe estar disponible"),
	        () -> Assertions.assertTrue(slotAntes.isDisponible(), 
	                "Un slot antes de la reserva debe estar disponible"),
	        () -> Assertions.assertTrue(slotJustoAntes.isDisponible(), 
	                "Un slot que termina cuando empieza la reserva debe estar disponible"),
	        () -> Assertions.assertFalse(slotDurante.isDisponible(), 
	                "Un slot durante la reserva debe estar no disponible"),
	        () -> Assertions.assertTrue(slotJustoDespues.isDisponible(), 
	                "Un slot que empieza cuando termina la reserva debe estar disponible"),
	        () -> Assertions.assertTrue(slotDespues.isDisponible(), 
	                "Un slot después de la reserva debe estar disponible"),
	        () -> Assertions.assertTrue(slotOtroDia.isDisponible(), 
	                "Un slot en otro día sin reservas debe estar disponible")
	    );
	}
	
	@Test
	@DisplayName("Lanza excepción si la hora de apertura es posterior a la de cierre")
	void lanzarExcepcionSiHoraAperturaEsPosteriorACierre() {
	    Assertions.assertThrows(IllegalArgumentException.class, 
	        () -> new ConfiguracionHoraria(horaFinalizacion, horaInicio),
	    						"Debería lanzar excepción si hora apertura es posterior a la de hora cierre");
	}
	
	@Test
	@DisplayName("Lanza excepción si no hay un slot coincidente con una reserva")
	void actualizarDisponibilidad_deberiaLanzarExcepcionSiNoSeEncuentraSlot() {
		//Arrange
		GestorSlotsService gestorSlotsService = configurarHorario(horaInicio, horaFinalizacion);
		List<Reserva> reservas = List.of(new Reserva(0, LocalDateTime.of(2024, 11, 11, 9, 0), 
										 				LocalDateTime.of(2024, 11, 11, 9, 30), 
										 				crearAula(), crearUsuario()));
		List<SlotDto> slots = List.of(new SlotDto(0, LocalDateTime.of(2024, 11, 11, 15, 0),
											LocalDateTime.of(2024, 11, 11, 15, 30)));
		//Act & Assert
		Assertions.assertThrows(SlotNotFoundException.class, 
								() -> gestorSlotsService.actualizarDisponibilidad(reservas, slots), 
								"Debería lanzar una excepción si no se encuentran slots"
								+ "para los tramos que coincidan con las reservas");
	}
}
