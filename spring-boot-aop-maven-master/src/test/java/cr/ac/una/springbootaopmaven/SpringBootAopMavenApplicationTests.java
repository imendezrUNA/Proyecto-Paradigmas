package cr.ac.una.springbootaopmaven;

import cr.ac.una.springbootaopmaven.service.ProcesadorLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class springbootaopmavenapplicationtest {

    @Autowired
    private ProcesadorLog procesadorLog;

    @Test
    public void testCargaArchivoLog() {
        List<String> resultados = procesadorLog.lectorArchivoLog();

        assertNotNull(resultados, "Lista Nula....");
        assertFalse(resultados.isEmpty(), "Lista Vacia....");

        resultados.forEach(line -> assertTrue(line.contains("SPRING-BOOT-AOP-MAVEN"),"la linea necesita contener spring-boot-aop-maven"));
    }

}
