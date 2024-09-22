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
                        .description("API para procesar y analizar logs de una aplicación Spring Boot")
                );
    }
}
