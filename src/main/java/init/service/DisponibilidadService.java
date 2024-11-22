package init.service;

import java.time.LocalDateTime;
import java.util.List;

import init.entities.Aula;
import init.model.SlotDto;

public interface DisponibilidadService {
	List<Aula> aulasDisponibles(LocalDateTime horaInicio, LocalDateTime horaFin, int capacidad, 
									boolean proyector, boolean altavoces);
	List<SlotDto> crearHorarioAula(int idAula, LocalDateTime inicioSemana);
}
