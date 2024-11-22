package init.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="reservas")
public class Reserva {
<<<<<<< HEAD
	//La relación Aula/Reserva es unidireccional y solo se establece en Aula 
	//(solo es necesario saber las reservas que tiene cada aula)
	//No es necesario establecer la relación entre Reserva y Usuario (no va a haber búsquedas
	//que relacionen ambas, ni se va a persistir ninguna entidad)
=======
	//Aunque no sería estrictamente necesario para la lógica de negocio del microservicio declarar
	//la relación de dependencia en ambas direcciones, si que es conveniente para los tests.
>>>>>>> d080d79 (Acabado Service con tests)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private LocalDateTime inicio;
	private LocalDateTime fin;
	private Aula aula;
	private Usuario usuario;
	
	public Reserva(int id, LocalDateTime inicio, LocalDateTime fin, Aula aula, Usuario usuario) {
		super();
		this.id = id;
		this.inicio = inicio;
		this.fin = fin;
		this.aula = aula;
		this.usuario = usuario;
	}

	public Reserva() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public void setInicio(LocalDateTime inicio) {
		this.inicio = inicio;
	}

	public LocalDateTime getFin() {
		return fin;
	}

	public void setFin(LocalDateTime fin) {
		this.fin = fin;
	}

	public Aula getAula() {
		return aula;
	}

	public void setAula(Aula aula) {
		this.aula = aula;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
