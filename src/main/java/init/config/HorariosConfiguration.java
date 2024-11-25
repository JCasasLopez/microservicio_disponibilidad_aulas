package init.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HorariosConfiguration {
	@Value("${horario.apertura}")
	private int horaApertura;
	
	@Value("${horario.cierre}")
	private int horaCierre;

    @Bean
    ConfiguracionHoraria configuracionHoraria() {
        return new ConfiguracionHoraria(horaApertura, horaCierre);
    }
}
