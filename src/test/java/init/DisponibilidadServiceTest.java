package init;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import init.config.ConfiguracionHoraria;
import init.dao.AulasDao;
import init.dao.ReservasDao;
import init.entities.Aula;
import init.model.AulaDto;
import init.service.DisponibilidadServiceImpl;
import init.service.GestorSlotsService;
import init.utilidades.Mapeador;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
public class DisponibilidadServiceTest {
	
	@Value("${horario.apertura}")
    private int horaApertura;
    
    @Value("${horario.cierre}")
    private int horaCierre;
    
	@Mock
	ReservasDao reservasDao;
	@Mock	
	AulasDao aulasDao;
	@Mock
	ConfiguracionHoraria configHoraria;
	@Mock
	GestorSlotsService gestorSlotsService;
	@Mock
	Mapeador mapeador;
	@InjectMocks
    DisponibilidadServiceImpl disponibilidadService;
	
	@Test
	@DisplayName("El flujo de llamadas a los servicios es correcto")
	void crearHorarioAula_deberiaLlamarServiciosEnOrdenCorrecto() {
	    //Arrange
		int idAula = 1;
		LocalDateTime inicioPeriodo = LocalDateTime.of(2024, 11, 11, horaApertura, 0); 
		LocalDateTime finalPeriodo = LocalDateTime.of(2024, 11, 15, horaCierre, 0);
	    
	    //Act
	    disponibilidadService.crearHorarioAula(idAula, inicioPeriodo, finalPeriodo);    

	    //Assert
	    var inOrder = inOrder(reservasDao, gestorSlotsService);
	    inOrder.verify(reservasDao).findByAulaAndFechas(idAula, inicioPeriodo, finalPeriodo);
	    inOrder.verify(gestorSlotsService).crearSlots(idAula, inicioPeriodo, finalPeriodo);
	    inOrder.verify(gestorSlotsService).actualizarDisponibilidad(any(), any());
	}
	
	@ParameterizedTest
	@CsvSource({
	    // capacidad, proyector, altavoces, aulas en la lista
	    "20,  false, true,  2",    
	    "0,   false, false, 6",    
	    "30,  false, false, 1",    
	    "15,  true,  false, 3",    
	    "10,  true,  true,  2",
	    "40,  true, true, 0"})
	@DisplayName("Devuelve correctamente las aulas que cumplen las condiciones")
	void aulasDisponibles_filtraCorrectamentePorCondiciones(int capacidad, 
													        boolean proyector, 
													        boolean altavoces, 
													        int listaSize) {
		//Arrange
		LocalDateTime inicio = LocalDateTime.of(2024, 11, 11, 12, 0);
		LocalDateTime fin = LocalDateTime.of(2024, 11, 11, 13, 30);
		Aula aula1 = new Aula(0, "aula1", 18, false, false);
		Aula aula2 = new Aula(1, "aula2", 20, true, true); 
		Aula aula3 = new Aula(2, "aula3", 15, true, true);
		Aula aula4 = new Aula(3, "aula4", 30, true, false);
		Aula aula5 = new Aula(4, "aula5", 10, true, false);
		Aula aula6 = new Aula(5, "aula6", 25, false, true); 
		List<Aula> aulasDisponiblesPorHorario = new ArrayList<>(List.of(aula1, aula2, aula3, aula4, aula5, aula6));		
		//Devolvemos una lista fija de aulas disponible por horario para que sean posteriormente
		//filtradas por aulasDisponibles()
		when(aulasDao.findAulasDisponiblesPorHorario(inicio, fin)).thenReturn(aulasDisponiblesPorHorario);
		
		//Act
		//Queremos obtener las aulas que, estando disponibles entre inicio y fin, tengan una 
		//capacidad de al menos 20 personas y estén equipadas con altavoces.
		List<AulaDto> aulasDisponibles = disponibilidadService.aulasDisponibles(inicio, fin, 
																	capacidad, proyector, altavoces);
		
		//Assert
		Assertions.assertEquals(listaSize, aulasDisponibles.size());
	}
	
}
