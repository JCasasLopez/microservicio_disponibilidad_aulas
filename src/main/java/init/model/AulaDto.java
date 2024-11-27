package init.model;

public class AulaDto {
	private int id;
	private String nombre;
	private int capacidad;
	private boolean proyector;
	private boolean altavoces;
	
	public AulaDto(int id, String nombre, int capacidad, boolean proyector, boolean altavoces) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.proyector = proyector;
		this.altavoces = altavoces;
	}

	public AulaDto() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public boolean isProyector() {
		return proyector;
	}

	public void setProyector(boolean proyector) {
		this.proyector = proyector;
	}

	public boolean isAltavoces() {
		return altavoces;
	}

	public void setAltavoces(boolean altavoces) {
		this.altavoces = altavoces;
	}

}
