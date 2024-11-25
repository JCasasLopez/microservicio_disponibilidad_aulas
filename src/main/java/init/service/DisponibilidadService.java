package init.service;

import java.time.LocalDateTime;
import java.util.List;
import init.model.AulaDto;
import init.model.SlotDto;

public interface DisponibilidadService {
	List<AulaDto> aulasDisponibles(LocalDateTime horaInicio, LocalDateTime horaFin, int capacidad, 
									boolean proyector, boolean altavoces);
	List<SlotDto> crearHorarioAula(int idAula, LocalDateTime inicioSemana);
}
