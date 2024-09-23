package cr.ac.una.springbootaopmaven;

import cr.ac.una.springbootaopmaven.entity.Persona;
import cr.ac.una.springbootaopmaven.repository.PersonaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootAopMavenApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAopMavenApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(PersonaRepository personaRepository) {
        return args -> {
            Persona p1 = new Persona();
            p1.setNombre("Juan");
            p1.setEdad(30);
            personaRepository.save(p1);

            Persona p2 = new Persona();
            p2.setNombre("Maria");
            p2.setEdad(25);
            personaRepository.save(p2);

            System.out.println("Datos de prueba cargados en memoria");
        };
    }
}
