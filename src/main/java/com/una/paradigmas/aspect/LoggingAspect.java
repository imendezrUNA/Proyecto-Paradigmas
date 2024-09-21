package com.una.paradigmas.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Around("execution(* com.una.paradigmas.controller.ProductController.addNewProduct(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            logger.info("Solicitud iniciada: " + joinPoint.getSignature());
            Object result = joinPoint.proceed();

            // Verificar si la respuesta contiene un status 4xx o 5xx
            if (result instanceof ResponseEntity<?>) {
                ResponseEntity<?> response = (ResponseEntity<?>) result;
                if (response.getStatusCode().is4xxClientError()) {
                    logger.warning("Error del cliente: " + response.getStatusCode());
                } else if (response.getStatusCode().is5xxServerError()) {
                    logger.severe("Error del servidor: " + response.getStatusCode());
                } else {
                    logger.info("Respuesta exitosa: " + response.getBody());
                }
            }
            return result;
        } catch (HttpClientErrorException e) {
            logger.warning("Error del cliente (HTTP 400-499): " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error en la solicitud: " + e.getMessage());
            throw e;
        }
    }

}
