package init.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import init.config.ConfiguracionHoraria;
import init.entities.Reserva;
import init.exceptions.SlotNotFoundException;
import init.model.SlotDto;

public class GestorSlotsService {
	//Los métodos de esta clase son auxiliares de los métodos principales en DisponibilidadServiceImpl
	
	ConfiguracionHoraria configHoraria;
	
	public GestorSlotsService(ConfiguracionHoraria configHoraria) {
		this.configHoraria = configHoraria;
	}

	public List<SlotDto> crearSlots(int idAula, LocalDateTime inicioSemana) {
		//Crea la lista de slots para la semana, desde la fecha de inicioSemana
		//hasta el viernes a la hora definida como de cierre.
		List<SlotDto> listaSlots = new ArrayList<>();
		for(LocalDateTime slot = inicioSemana; slot.getDayOfWeek().getValue() < 6; 
				slot = slot.plusDays(1).withHour(configHoraria.getHoraApertura()).withMinute(0)) {
			for(; slot.getHour() < configHoraria.getHoraCierre(); slot = slot.plusMinutes(30)) {
				listaSlots.add(new SlotDto(idAula, slot, slot.plusMinutes(30)));
			}
		}
		return listaSlots;
	}
	
	public List<SlotDto> actualizarDisponibilidad(List<Reserva> reservasSemana, List<SlotDto> slotsSemana){
		//A partir de las reservas de un aula para una semana dada y la lista de slots sin actualizar 
		//actualiza la disponibilidad de esos slots y devuelve una lista de los mismos para mostrarla en el front
		for(Reserva r:reservasSemana) {
			for(LocalDateTime hora = r.getHoraInicio(); hora.isBefore(r.getHoraFin()); 
																hora = hora.plusMinutes(30)) {
				//Para hacer creer al compilador que la variable es efectiva final
				LocalDateTime horaActual = hora;
				SlotDto slot = slotsSemana.stream()
											.filter(s -> s.getHoraInicio().isEqual(horaActual))
											.findFirst()
											.orElseThrow(() -> new SlotNotFoundException(
												        "No se encontró slot para la hora: " + horaActual));
				slot.setDisponible(false);
			}
		}
		return slotsSemana;
	}

}
