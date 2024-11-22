package config;

public class ConfiguracionHoraria {
	private final int horaApertura;
    private final int horaCierre;

    public ConfiguracionHoraria(int horaApertura, int horaCierre) {
    
    	if (horaApertura >= horaCierre) {
            throw new IllegalArgumentException(
                "La hora de apertura (" + horaApertura + ") debe ser anterior " +
                "a la hora de cierre (" + horaCierre + ")");
        }
    	
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public int getHoraApertura() {
        return horaApertura;
    }

    public int getHoraCierre() {
        return horaCierre;
    }

}
