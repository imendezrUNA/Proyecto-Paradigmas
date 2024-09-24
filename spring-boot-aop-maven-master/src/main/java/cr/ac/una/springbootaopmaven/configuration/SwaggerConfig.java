package cr.ac.una.springbootaopmaven.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Procesamiento de Logs: Paradigmas Proyecto #1")
                        .version("1.0")
                        .description("API diseñada para facilitar la recopilación, procesamiento y análisis de logs generados por una aplicación Spring Boot. Además proporciona un conjunto de endpoints que permite a los desarrolladores y administradores de sistemas acceder a los registros de la aplicación, analizar su rendimiento y detectar errores, contribuyendo así a una mejor comprensión del comportamiento de la aplicación en producción.")
                );
    }
}
