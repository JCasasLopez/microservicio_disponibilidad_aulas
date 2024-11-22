package init.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import init.entities.Reserva;

public interface ReservasDao extends JpaRepository<Reserva, Integer> {
	@Query("SELECT r FROM Reserva r WHERE r.aula.idAula=?1 AND r.horaFin>?2 AND r.horaInicio<?3")
	List<Reserva> findByAulaAndFechas(int idAula, LocalDateTime inicio, LocalDateTime fin);
	
	//Aunque estos dos métodos no se van a utilizar directamente en el microservicio, sus tests permiten
	//verificar que la relación Aula/Reserva y Usuario/Reserva están correctamente configuradas. 
	@Query("SELECT r FROM Reserva r WHERE r.aula.idAula=?1")
	List<Reserva> findByAula(int idAula);
	
	@Query("SELECT r FROM Reserva r WHERE r.usuario.idUsuario=?1")
	List<Reserva> findByUsuario(int idUsuario);
}
