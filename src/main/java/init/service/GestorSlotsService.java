package init.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import init.config.ConfiguracionHoraria;
import init.entities.Reserva;
import init.exceptions.SlotNotFoundException;
import init.model.SlotDto;

@Component
public class GestorSlotsService {
	//Los métodos de esta clase son auxiliares de los métodos principales en DisponibilidadServiceImpl
	
	ConfiguracionHoraria configHoraria;
	
	public GestorSlotsService(ConfiguracionHoraria configHoraria) {
		this.configHoraria = configHoraria;
	}

	public List<SlotDto> crearSlots(int idAula, LocalDateTime inicioPeriodo, LocalDateTime finalPeriodo) {
		List<SlotDto> listaSlots = new ArrayList<>();
		for(LocalDateTime slot = inicioPeriodo; slot.isBefore(finalPeriodo); 
				slot = slot.plusDays(1).withHour(configHoraria.getHoraApertura()).withMinute(0)) {
			for(; slot.getHour() < configHoraria.getHoraCierre(); slot = slot.plusMinutes(30)) {
				listaSlots.add(new SlotDto(idAula, slot, slot.plusMinutes(30)));
			}
		}
		return listaSlots;
	}
	
	public List<SlotDto> actualizarDisponibilidad(List<Reserva> reservasSemana, List<SlotDto> slotsSemana){
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
