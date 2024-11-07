package init.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import init.entities.Aula;

public interface DisponibilidadDao extends JpaRepository<Aula, Integer> {
	
}
