package init.entities;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="reservas")
public class Reserva {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idReserva;
	private LocalDateTime horaInicio;
	private LocalDateTime horaFin;
	@ManyToOne()
	@JoinColumn(name="idAula", referencedColumnName="idAula")
	private Aula aula;
	@ManyToOne()
	@JoinColumn(name="idUsuario", referencedColumnName="idUsuario")
	private Usuario usuario;
	
	public Reserva(int idReserva, LocalDateTime horaInicio, LocalDateTime horaFin, Aula aula, Usuario usuario) {
		super();
		this.idReserva = idReserva;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.aula = aula;
		this.usuario = usuario;
	}

	public Reserva() {
		super();
	}

	public int getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public LocalDateTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalDateTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalDateTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalDateTime horaFin) {
		this.horaFin = horaFin;
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
