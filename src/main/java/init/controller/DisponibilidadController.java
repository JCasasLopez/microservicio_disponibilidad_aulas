package init.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import init.exceptions.BadRequestException;
import init.model.AulaDto;
import init.model.SlotDto;
import init.service.DisponibilidadService;

@CrossOrigin("*")
@RestController
public class DisponibilidadController {
	
	@Value("${horario.apertura}")
    private int horaInicio;
	
	DisponibilidadService disponibilidadService;
	
	public DisponibilidadController(DisponibilidadService disponibilidadService) {
		this.disponibilidadService = disponibilidadService;
	}

	@GetMapping(value="aulasDisponibles", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AulaDto>> aulasDisponibles(
			@RequestParam LocalDateTime horaInicio,
			@RequestParam LocalDateTime horaFin,
			@RequestParam(defaultValue = "0") int capacidad,
			@RequestParam(defaultValue = "false") boolean proyector,
			@RequestParam(defaultValue = "false") boolean altavoces){

		if(horaInicio.isAfter(horaFin)) {
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
			@RequestParam LocalDateTime inicioSemana){
		
		if(inicioSemana.getDayOfWeek() != DayOfWeek.MONDAY || inicioSemana.getHour() != horaInicio) {
			throw new BadRequestException("La fecha tiene que ser un lunes a la hora de apertura");
		}

		List<SlotDto> horarioAula = disponibilidadService.crearHorarioAula(idAula, inicioSemana);
		return ResponseEntity.ok(horarioAula);
		
	}

}
