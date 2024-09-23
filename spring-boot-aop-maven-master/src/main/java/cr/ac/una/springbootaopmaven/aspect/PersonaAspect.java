package cr.ac.una.springbootaopmaven.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PersonaAspect {

    // Usamos SLF4J para el log
    private static final Logger logger = LoggerFactory.getLogger(PersonaAspect.class);

    // Log antes de ejecutar el método guardarPersona
    @Before("execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.guardarPersona*(..))")
    public void logAntesDeGuardar(JoinPoint joinPoint) {
        logger.info("Iniciando método: " + joinPoint.getSignature().getName() + " en PersonaController");
    }

    // Aspecto para medir el tiempo de respuesta de todos los métodos en PersonaController
    @Around("execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.*(..))")
    public Object medirTiempoRespuesta(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();  // Captura el tiempo de inicio

        // Ejecuta el método
        Object resultado = joinPoint.proceed();

        long tiempoRespuesta = System.currentTimeMillis() - inicio;  // Calcula el tiempo de ejecución en ms
        String metodo = joinPoint.getSignature().getName();

        // Registra el tiempo de respuesta en el log
        logger.info("Tiempo de respuesta para el método {}: {} ms", metodo, tiempoRespuesta);

        return resultado;
    }

    // Log después de ejecutar métodos de generación de reportes
    @After("execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.generarReporte*(..))")
    public void logDespuesDeGenerarReporte(JoinPoint joinPoint) {
        logger.info("Reporte generado por método: " + joinPoint.getSignature().getName() + " en PersonaController");
    }
}
