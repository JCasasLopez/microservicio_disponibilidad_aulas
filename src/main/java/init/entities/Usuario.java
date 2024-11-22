package init.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="usuarios")
public class Usuario {
<<<<<<< HEAD
	//No hace falta establecer la relación entre Usuario y Reserva (no va a haber búsquedas
	//que relacionen ambas, ni se va a persistir ninguna entidad)
=======
	//Aunque no sería estrictamente necesario para la lógica de negocio del microservicio declarar
	//la relación de dependencia en ambas direcciones, si que es conveniente para los tests.
>>>>>>> d080d79 (Acabado Service con tests)
	@Id
	private String email;
	private String nombre;
	private String apellidos;
	private LocalDate fechaNacimiento;
	private String password;
	
	public Usuario(String email, String nombre, String apellidos, LocalDate fechaNacimiento, String password) {
		super();
		this.email = email;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fechaNacimiento = fechaNacimiento;
		this.password = password;
	}

	public Usuario() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
