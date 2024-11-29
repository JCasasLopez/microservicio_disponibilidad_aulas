package init.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import init.config.ConfiguracionHoraria;
import init.exceptions.BadRequestException;
import init.model.AulaDto;
import init.model.SlotDto;
import init.service.DisponibilidadService;

@CrossOrigin("*")
@RestController
public class DisponibilidadController {

	DisponibilidadService disponibilidadService;
	ConfiguracionHoraria configuracionHoraria;
	
	public DisponibilidadController(DisponibilidadService disponibilidadService,
			ConfiguracionHoraria configuracionHoraria) {
		this.disponibilidadService = disponibilidadService;
		this.configuracionHoraria = configuracionHoraria;
	}

	@GetMapping(value="aulasDisponibles", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AulaDto>> aulasDisponibles(
			@RequestParam LocalDateTime horaInicio,
			@RequestParam LocalDateTime horaFin,
			@RequestParam(defaultValue = "0") int capacidad,
			@RequestParam(defaultValue = "false") boolean proyector,
			@RequestParam(defaultValue = "false") boolean altavoces){

		if(!horaInicio.isBefore(horaFin)) {
			throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin");
		}

		if (capacidad < 0) {
			throw new BadRequestException("La capacidad no puede ser menor de 0");
		}

		List<AulaDto> aulasDisponibles = disponibilidadService.aulasDisponibles(horaInicio, horaFin, 
				capacidad, proyector, altavoces);
		return ResponseEntity.ok(aulasDisponibles); 

	}
	
	@GetMapping(value="crearHorarioAula", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SlotDto>> crearHorarioAula(@RequestParam int idAula,
			@RequestParam LocalDate inicioPeriodo, 
			@RequestParam LocalDate finalPeriodo){
		
		if(!inicioPeriodo.isBefore(finalPeriodo)) {
			throw new BadRequestException("La fecha de inicio debe ser anterior a la de finalización");
		}
		
		LocalDateTime horaInicioPeriodo = inicioPeriodo.atTime(configuracionHoraria.getHoraApertura(),0 ,0);
		LocalDateTime horaFinalPeriodo = finalPeriodo.atTime(configuracionHoraria.getHoraCierre(), 0, 0);

		List<SlotDto> horarioAula = disponibilidadService.crearHorarioAula(idAula, horaInicioPeriodo, 
				horaFinalPeriodo);
		return ResponseEntity.ok(horarioAula);
	}

}
