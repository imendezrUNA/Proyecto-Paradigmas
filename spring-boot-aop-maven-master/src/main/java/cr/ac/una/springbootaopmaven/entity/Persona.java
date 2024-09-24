package cr.ac.una.springbootaopmaven.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Generated;

@Entity
@Data
public class Persona {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;
    String nombre;
    Integer  edad;
}
