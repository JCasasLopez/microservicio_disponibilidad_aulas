package init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import init.dao.AulasDao;
import init.dao.ReservasDao;
import init.dao.UsuariosDao;
import init.entities.Aula;
import init.entities.Reserva;
import init.entities.Usuario;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DaoTest {
	
	@Autowired
    private AulasDao aulasDao;
	@Autowired
	private UsuariosDao usuariosDao;
	@Autowired
	private ReservasDao reservasDao;
	
	//Estos 3 métodos se usan para reducir la cantidad de código de creación de objetos
	private Aula crearAula() {
		return new Aula(0, "aula1", 20, false, false);
	}
	
	private Usuario crearUsuario() {
		return new Usuario(0, "aaa@gmail.com", "Jorge", "Casas López", LocalDate.of(1978, 11, 26), "Vindel39!");
	}
	
	private List<Reserva> crearReservas(Aula aula, Usuario usuario){
		List<Reserva> reservas = new ArrayList<>();
		Reserva reserva1 = new Reserva(0, LocalDateTime.of(2024, 11, 8, 14, 0), LocalDateTime.of(2024, 11, 8, 15, 0), 
				aula, usuario); 
		Reserva reserva2 = new Reserva(0, LocalDateTime.of(2024, 11, 8, 11, 0), LocalDateTime.of(2024, 11, 8, 12, 30), 
				aula, usuario);
		Reserva reserva3 = new Reserva(0, LocalDateTime.of(2024, 11, 8, 13, 30), LocalDateTime.of(2024, 11, 8, 14, 0), 
				aula, usuario);
		reservas.add(reserva1);
	    reservas.add(reserva2);
	    reservas.add(reserva3);
		return reservas;
	}
	
	//Este método sirve como fuente de datos para al test aula1DeberiaEstarDisponible
	private static Stream<Arguments> reservasPosibles(){
		return Stream.of(
				Arguments.of(LocalDateTime.of(2024, 11, 8, 9, 0), LocalDateTime.of(2024, 11, 8, 10, 0)),
				Arguments.of(LocalDateTime.of(2024, 11, 8, 10, 0), LocalDateTime.of(2024, 11, 8, 11, 0)),
				Arguments.of(LocalDateTime.of(2024, 11, 8, 12, 30), LocalDateTime.of(2024, 11, 8, 13, 30)),
				Arguments.of(LocalDateTime.of(2024, 11, 8, 15, 0), LocalDateTime.of(2024, 11, 8, 15, 30)),
				Arguments.of(LocalDateTime.of(2024, 11, 8, 16, 30), LocalDateTime.of(2024, 11, 8, 17, 30))
				);
	}
	
	//Este método sirve como fuente de datos para al test noDeberiaHaberAulasDisponibles
	private static Stream<Arguments> reservasNoPosibles(){
		return Stream.of(
				Arguments.of(LocalDateTime.of(2024, 11, 8, 11, 30), LocalDateTime.of(2024, 11, 8, 12, 0)),
				Arguments.of(LocalDateTime.of(2024, 11, 8, 14, 0), LocalDateTime.of(2024, 11, 8, 15, 30))
				);
	}
	
	@Test
	@DisplayName("Persiste entidad Aula correctamente")
	void debePersistirEntidadAula() {
		//Arrange
		Aula aula1 = crearAula();

		//Act
		Aula aulaBD = aulasDao.save(aula1);
		
		//Assert
		Assertions.assertEquals("aula1", aulaBD.getNombre(),
				"El nombre del aula no se persistió correctamente en la base de datos. " +
			    "Valor esperado: 'aula1', Valor obtenido: '" + aulaBD.getNombre() + "'");
	}
	
	@Test
	@DisplayName("Persiste entidad Usuario correctamente")
	void debePersistirEntidadUsuario() {
		//Arrange
		Usuario usuario1 = crearUsuario();
		//Act
		Usuario usuarioBD = usuariosDao.save(usuario1);
		
		//Assert
		Assertions.assertEquals("aaa@gmail.com", usuarioBD.getEmail(), 
				"El email no se persistió correctamente en la base de datos. " +
			    "Valor esperado: 'aaa@gmail.com', Valor obtenido: '" + usuarioBD.getEmail() + "'");
	}
	
	@Test
	@DisplayName("Persiste entidad Reserva correctamente")
	void debePersistirEntidadReserva() {
		//Arrange
		Aula aulaBD = aulasDao.save(crearAula());
		Usuario usuarioBD = usuariosDao.save(crearUsuario());
		Reserva reserva1 = crearReservas(aulaBD, usuarioBD).get(0);
		
		//Act
		Reserva reservaBD = reservasDao.save(reserva1);
		
		//Assert
		Assertions.assertEquals(reserva1.getHoraInicio(), reservaBD.getHoraInicio(),
				 	"La hora de inicio de la reserva no coincide con la esperada.");
	}
	
	@Test
	@DisplayName("Devuelve la lista correcta de reservas de cada aula")
	void deberiaDevolverReservasCorrectasPorAula() {
		//Arrange
		Aula aulaBD = aulasDao.save(crearAula());
		Usuario usuarioBD = usuariosDao.save(crearUsuario());
		List<Reserva> reservas = crearReservas(aulaBD, usuarioBD);
		reservas.forEach(reservasDao::save);
		
		//Act
		List<Reserva> listaReservas = reservasDao.findByAula(aulaBD.getIdAula());

		//Assert
		Assertions.assertEquals(reservas.size(), listaReservas.size(), 
				"La lista de reservas devuelta no tiene el tamaño esperado de " + reservas.size());
	}
	
	@Test
	@DisplayName("Devuelve la lista correcta de reservas de cada usuario")
	void deberiaDevolverReservasCorrectasPorUsuario() {
		//Arrange
		Aula aulaBD = aulasDao.save(crearAula());
		Usuario usuarioBD = usuariosDao.save(crearUsuario());
		List<Reserva> reservas = crearReservas(aulaBD, usuarioBD);
		reservas.forEach(reservasDao::save);

		
		//Act
		List<Reserva> listaReservas = reservasDao.findByUsuario(usuarioBD.getIdUsuario());

		//Assert
		Assertions.assertEquals(reservas.size(), listaReservas.size(), 
				"La lista de reservas devuelta no tiene el tamaño esperado de " + reservas.size());
	}
	
	@Test
	@DisplayName("Lanza una excepcion si se intenta peristir aulas con nombre duplicado")
	void noDeberiaPersistirAulasConNombreDuplicado() {
		// Arrange
		Aula aulaBD = crearAula();
		Aula aulaDuplicada = new Aula(0, aulaBD.getNombre(), 30, true, true);

		// Act
		aulasDao.save(aulaBD);

		// Assert
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			aulasDao.save(aulaDuplicada);
			},
		"Se esperaba una excepción de violación de restricción de unicidad al intentar persistir un "
		+ "aula con el nombre duplicado.");
	}
	
	@DisplayName("Lanza una excepcion si se intenta peristir usuarios con email duplicado")
	void noDeberiaPersistirUsuariosConEmailDuplicado() {
		// Arrange
		Usuario usuarioBD = crearUsuario();
		Usuario usuarioDuplicado = new Usuario(0, usuarioBD.getEmail(), "Luis", "García", LocalDate.of(1978, 11, 27), "Vindel49!");

		// Act
		usuariosDao.save(usuarioBD);

		// Assert
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			usuariosDao.save(usuariosDao.save(usuariosDao.save(usuarioDuplicado)));
			},
		"Se esperaba una excepción de violación de restricción de unicidad al intentar persistir "
		+ "un usuario con el email duplicado.");
	}
	
	@ParameterizedTest
	@MethodSource("reservasPosibles")
	@DisplayName("Verifica Aula1 se devuelve como disponible")
	void aula1DeberiaEstarDisponible(LocalDateTime horaInicio, LocalDateTime horaFin) {
		//Arrange
		Aula aulaBD = aulasDao.save(crearAula());
		Usuario usuarioBD = usuariosDao.save(crearUsuario());
		List<Reserva> reservas = crearReservas(aulaBD, usuarioBD);
		reservas.forEach(r -> reservasDao.save(r));
		reservas.forEach(reservasDao::save);
		
		//Act
		List<Aula> aulasDisponibles = aulasDao.findAulasDisponiblesPorHorario(horaInicio, horaFin);
		
		//Assert
		Assertions.assertTrue(aulasDisponibles.contains(aulaBD), 
				"No hay aulas disponibles para esos horarios, cuando Aula 1 debería estarlo");
	}
	
	@ParameterizedTest
	@MethodSource("reservasNoPosibles")
	@DisplayName("Verifica que ningún aula se devuelve como disponible")
	void noDeberiaHaberAulasDisponibles(LocalDateTime horaInicio, LocalDateTime horaFin) {
		//Arrange
		Aula aulaBD = aulasDao.save(crearAula());
		Usuario usuarioBD = usuariosDao.save(crearUsuario());
		List<Reserva> reservas = crearReservas(aulaBD, usuarioBD);
		reservas.forEach(reservasDao::save);
		
		//Act
		List<Aula> aulasDisponibles = aulasDao.findAulasDisponiblesPorHorario(horaInicio, horaFin);
		
		//Assert
		Assertions.assertEquals(0, aulasDisponibles.size(), 
				"Hay aulas disponibles para esos horarios, pero no debería haber ninguna");
	}
	
}
