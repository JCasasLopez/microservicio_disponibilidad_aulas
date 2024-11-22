package init.dao;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import init.entities.Aula;

@Repository
public interface AulasDao extends JpaRepository<Aula, Integer> {
    @Query("SELECT a FROM Aula a WHERE NOT EXISTS "
    	       + "(SELECT r FROM Reserva r WHERE r.aula = a AND r.horaInicio < ?2 AND r.horaFin > ?1)")
	List<Aula> findAulasDisponiblesPorHorario(LocalDateTime horaInicio, LocalDateTime horaFin);
}