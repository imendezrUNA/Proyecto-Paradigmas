package cr.ac.una.springbootaopmaven.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PersonaAspect {

    // Usamos SLF4J para el log
    private static final Logger logger = LoggerFactory.getLogger(PersonaAspect.class);

    // Log antes de ejecutar el metodo getPersonas
    @Before("execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.getPersonas(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Solicitud iniciada: " + joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.getPersonas(..))", returning = "result")
    public void logAftergetPersonas(Object result) {
        if (result instanceof ResponseEntity<?>) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;

            if (response.getStatusCode().is4xxClientError()) {
                logger.error("Error 400 - BAD REQUEST: " + response.getBody());
            }
            else if (response.getStatusCode().is5xxServerError()) {
                logger.error("Error 500 - INTERNAL SERVER ERROR: " + response.getBody());
            }
            else if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Respuesta exitosa: " + response.getBody());
            }
        }
    }

    // Log antes de ejecutar el método savePersona
    @Before("execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.savePersona*(..))")
    public void logAntesDeGuardar(JoinPoint joinPoint) {
        logger.info("Iniciando método: " + joinPoint.getSignature().getName() + " en PersonaController");
    }

    // Log luego de ejecutar el metodo savePersona
    @AfterReturning(pointcut = "execution(* cr.ac.una.springbootaopmaven.controller.PersonaController.savePersona(..))", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof ResponseEntity<?>) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                logger.error("Error 400 - BAD REQUEST: " + response.getBody());
            }
            if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                logger.error("Error 500 - INTERNAL SERVER ERROR: " + response.getBody());
            }else {
                logger.info("Respuesta exitosa: " + response.getBody());
            }
        }
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
