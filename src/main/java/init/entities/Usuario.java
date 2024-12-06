package init.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="usuarios")
public class Usuario {
	//No hace falta establecer la relación entre Usuario y Reserva (no va a haber búsquedas
	//que relacionen ambas, ni se va a persistir ninguna entidad)

	//Aunque no sería estrictamente necesario para la lógica de negocio del microservicio declarar
	//la relación de dependencia en ambas direcciones, si que es conveniente para los tests.
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idUsuario;
    @Column(unique = true)
	private String email;
	private String nombre;
	private String apellidos;
	private LocalDate fechaNacimiento;
	private String password;
	private boolean isAdmin;
	@OneToMany(mappedBy="usuario")
	private List<Reserva> reservas;
	
	public Usuario(int idUsuario, String email, String nombre, String apellidos, LocalDate fechaNacimiento,
			String password, boolean isAdmin) {
		this.idUsuario = idUsuario;
		this.email = email;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fechaNacimiento = fechaNacimiento;
		this.password = password;
		this.isAdmin = isAdmin;
	}

	public Usuario() {
		super();
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}
	
}
