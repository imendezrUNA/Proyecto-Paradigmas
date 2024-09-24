package cr.ac.una.springbootaopmaven.repository;

import cr.ac.una.springbootaopmaven.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository< Persona, Long> {

}
