package init.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import init.entities.Usuario;

public interface UsuariosDao extends JpaRepository<Usuario, Integer> {
	
}
