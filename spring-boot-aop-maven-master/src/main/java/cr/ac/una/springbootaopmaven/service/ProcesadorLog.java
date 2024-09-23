package cr.ac.una.springbootaopmaven.service;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ProcesadorLog {


    public List<String> lectorArchivoLog() {
        Path path = Paths.get("app.log");

        try (Stream<String> lineas = Files.lines(path)) {
                return lineas
                        .filter(line -> line.contains("spring-boot-aop-maven"))
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());
            }catch(IOException e){
                e.printStackTrace();
                return List.of();
            }
        }
    }

