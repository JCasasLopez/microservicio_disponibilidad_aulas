package init.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import init.config.ConfiguracionHoraria;
import init.dao.AulasDao;
import init.dao.ReservasDao;
import init.entities.Reserva;
import init.exceptions.NoSuchClassroomException;
import init.model.AulaDto;
import init.model.SlotDto;
import init.utilidades.Mapeador;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {
	
	ReservasDao reservasDao;
	AulasDao aulasDao;
	GestorSlotsService gestorSlotsService;
	ConfiguracionHoraria configHoraria;
	Mapeador mapeador;

	public DisponibilidadServiceImpl(ReservasDao reservasDao, AulasDao aulasDao, 
												GestorSlotsService gestorSlotsService,
												ConfiguracionHoraria configHoraria, Mapeador mapeador) {
		this.reservasDao = reservasDao;
		this.aulasDao = aulasDao; 
		this.gestorSlotsService = gestorSlotsService;
		this.configHoraria = configHoraria;
		this.mapeador = mapeador;
	}

	@Override
	public List<AulaDto> aulasDisponibles(LocalDateTime horaInicio, LocalDateTime horaFin, int capacidad,
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
									.map(a -> mapeador.aulaToAulaDto(a))
									.toList();
	}

	@Override
	public List<SlotDto> crearHorarioAula(int idAula, LocalDateTime inicioPeriodo, LocalDateTime finalPeriodo){
		//Crea slots para ese período de tiempo y luego actualiza su disponibilidad a partir de las 
		//reservas para ese período
		if(aulasDao.existsById(idAula)) {
			List<Reserva> reservasSemana = reservasDao.findByAulaAndFechas(idAula, inicioPeriodo, finalPeriodo);
			List<SlotDto> slotsSemana = gestorSlotsService.crearSlots(idAula, inicioPeriodo, finalPeriodo);
			return gestorSlotsService.actualizarDisponibilidad(reservasSemana, slotsSemana);
		}
			throw new NoSuchClassroomException("No existe ningún aula con ese id");
	}

}
