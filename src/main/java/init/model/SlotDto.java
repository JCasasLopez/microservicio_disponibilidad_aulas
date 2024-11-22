package init.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SlotDto implements Comparable<SlotDto>{
	private int idAula;
	private LocalDateTime horaInicio;
	private LocalDateTime horaFin;
	private boolean disponible;
	
	public SlotDto(int idAula, LocalDateTime horaInicio, LocalDateTime horaFin) {
		this.idAula = idAula;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		if(this.horaInicio.isBefore(LocalDateTime.now())) {
			this.disponible = false; //Si la hora de inicio ya ha pasado, se declara como NO disponible
		}else {
			this.disponible = true; //Por defecto el slot se declara como disponible
		}
	}

	public SlotDto() {
		super();
	}

	public int getIdAula() {
		return idAula;
	}

	public void setIdAula(int idAula) {
		this.idAula = idAula;
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

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	@Override
	public int compareTo(SlotDto otroSlotDto) {
		return this.horaInicio.compareTo(otroSlotDto.horaInicio);
	}

	@Override
	public boolean equals(Object otroSlotDto) {
		if(this == otroSlotDto) {
			return true;
		}
		
		if(otroSlotDto == null || this.getClass() != otroSlotDto.getClass()) {
			return false;
		}
		
		SlotDto otro = (SlotDto) otroSlotDto; //Hay que castearlo, ya que pertenece a la clase Object
		return this.idAula == otro.idAula &&
				this.horaInicio.equals(otro.horaInicio) &&
				this.horaFin.equals(otro.horaFin) &&
				this.disponible == otro.disponible;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(idAula, horaInicio, horaFin, disponible);
	}

	@Override
	public String toString() {
		return "Aula " + idAula + ". Inicio: " + horaInicio + " Fin: " + horaFin;
	}
	
}
