package init.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import init.exceptions.BadRequestException;
import init.exceptions.InternalServerException;
import init.model.AulaDto;
import init.service.DisponibilidadService;

@CrossOrigin("*")
@RestController
public class DisponibilidadController {
	DisponibilidadService disponibilidadService;
	
	public DisponibilidadController(DisponibilidadService disponibilidadService) {
		super();
		this.disponibilidadService = disponibilidadService;
	}

	@GetMapping(value="aulasDisponibles", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AulaDto>> aulasDisponibles(
										        @RequestParam LocalDateTime horaInicio,
										        @RequestParam LocalDateTime horaFin,
										        @RequestParam(defaultValue = "0") int capacidad,
										        @RequestParam(defaultValue = "false") boolean proyector,
										        @RequestParam(defaultValue = "false") boolean altavoces){
		try {
			
			if(horaInicio.isAfter(horaFin)) {
	            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin");
			}
			
			if (capacidad < 0) {
	            throw new BadRequestException("La capacidad no puede ser menor de 0");
	        }
			
			List<AulaDto> aulasDisponibles = disponibilidadService.aulasDisponibles(horaInicio, horaFin, 
																	capacidad, proyector, altavoces);
			return new ResponseEntity<>(aulasDisponibles, HttpStatus.OK);  
			
	    } catch (Exception ex) {
	        throw new InternalServerException("Error al acceder a la base de datos", ex);
	    }
	}
}
