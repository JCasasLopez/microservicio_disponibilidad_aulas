package init.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;

import config.ConfiguracionHoraria;
import init.dao.AulasDao;
import init.dao.ReservasDao;
import init.entities.Aula;
import init.entities.Reserva;
import init.model.SlotDto;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {

	ReservasDao reservasDao;
	AulasDao aulasDao;
	GestorSlotsService gestorSlotsService;
	ConfiguracionHoraria configHoraria;

	public DisponibilidadServiceImpl(ReservasDao reservasDao, AulasDao aulasDao, 
												GestorSlotsService gestorSlotsService,
												ConfiguracionHoraria configHoraria) {
		this.reservasDao = reservasDao;
		this.aulasDao = aulasDao; 
		this.gestorSlotsService = gestorSlotsService;
		this.configHoraria = configHoraria;
	}

	@Override
	public List<Aula> aulasDisponibles(LocalDateTime horaInicio, LocalDateTime horaFin, int capacidad,
														boolean proyector, boolean altavoces) {
		//Cuando estas condiciones son 'true' significa que el usuario las pone como condición,
		//es decir, quiere un aula con proyector, altavoces o una determinada capacidad.
		boolean condicionProyector = proyector;
		boolean condicionAltavoces = altavoces;
		boolean condicionCapacidad = false;
		if(capacidad > 0) {
			condicionCapacidad = true;
		}
		//Por la restricción de las lambdas de usar variables finales o efectivamente finales
		boolean condicionCapacidadActual = condicionCapacidad;
		return aulasDao.findAulasDisponiblesPorHorario(horaInicio, horaFin).stream()
				//Si condicionCapacidadActual es 'false' significa que al usuario le da igual la capacidad del aula
				//y la interfaz Predicate debería devolver 'true' siempre (dejar pasar todas las aulas).
				//Si es 'true', deja pasar solo aquellas aulas con una capacidad igual o superior a la requerida.
									.filter(a -> condicionCapacidadActual ? a.getCapacidad() >= capacidad : true)
				//Si condicionProyector o condicionAltavoces son 'true', el usuario quiere que el aula tenga
				//proyector y/o altavoces, por tanto, solo deja pasar las que lo tienen. Si es 'false' deja pasar
				//todas las aulas.
									.filter(a -> condicionProyector ? a.isProyector() : true)
									.filter(a -> condicionAltavoces ? a.isAltavoces() : true)
									.toList();
		
	}

	@Override
	public List<SlotDto> crearHorarioAula(int idAula, LocalDateTime inicioSemana){
		//Actualiza la disponibilidad de cada slot para ese aula: comprueba las reservas para esa semana
		//cambiando el estado del slot a NO disponible si hay una reserva activa con ese horario. 
		
		//En el Controller se valida que inicioSemana sea un lunes (hora de apertura). 
		//Por ese motivo, en este método no hay validaciones ni se lanzan ninguna excepción, etc
		//y, por tanto, el único test es para el "happy path" (inicioSemana = lunes)
		LocalDateTime viernesCierre = inicioSemana.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
											    .withHour(configHoraria.getHoraCierre())
											    .withMinute(0);
		List<Reserva> reservasSemana = reservasDao.findByAulaAndFechas(idAula, inicioSemana, viernesCierre);
		List<SlotDto> slotsSemana = gestorSlotsService.crearSlots(idAula, inicioSemana);
		return gestorSlotsService.actualizarDisponibilidad(reservasSemana, slotsSemana);
	}

}
