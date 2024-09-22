package cr.ac.una.springbootaopmaven.service;

import cr.ac.una.springbootaopmaven.serviceInterface.IServicioProcesador;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ServicioProcesadorImpl implements IServicioProcesador {

    private final ProcesadorLog procesadorLog;

    public ServicioProcesadorImpl(ProcesadorLog procesadorLog) {
        this.procesadorLog = procesadorLog;
    }

    @Override
    public Map<String, Object> generarReporteError() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        // Filtrar y agrupar los errores por tipo
        Map<String, Long> conteoErrores = logs.stream()
                .filter(line -> line.contains("ERROR"))
                .collect(Collectors.groupingBy(line -> {
                    if (line.contains("404")) return "404 Not Found";
                    if (line.contains("500")) return "500 Internal Server Error";
                    if (line.contains("NullPointerException")) return "NullPointerException";
                    return "Otros Errores";
                }, Collectors.counting()));

        // Encontrar el error más frecuente
        String errorMasFrecuente = conteoErrores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        // Implementar la lógica para calcular la hora pico de errores
        Map<String, Long> erroresPorHora = logs.stream()
                .filter(line -> line.contains("ERROR"))  // Filtrar solo líneas con errores
                .collect(Collectors.groupingBy(this::extraerHora, Collectors.counting()));  // Agrupar por hora

        // Encontrar la hora con más errores (hora pico)
        String horaPico = erroresPorHora.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No hay errores");

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("TotalErroresPorTipo", conteoErrores);
        reporte.put("ErrorMasFrecuente", errorMasFrecuente);
        reporte.put("HorasPicoErrores", horaPico);  // Hora pico implementada

        return reporte;
    }

    @Override
    public Map<String, Object> generarRespuestaTiempoReporte() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        // Filtrar las líneas que contienen "Tiempo de respuesta"
        List<Long> tiempos = logs.stream()
                .filter(line -> line.contains("Tiempo de respuesta"))
                .map(line -> {
                    Pattern pattern = Pattern.compile("Tiempo de respuesta para el método .*: (\\d+) ms");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return Long.parseLong(matcher.group(1));  // Extraer el tiempo en milisegundos
                    } else {
                        return 0L;  // Si no se encuentra el número, usar 0 como valor por defecto
                    }
                })
                .filter(tiempo -> tiempo > 0)  // Filtrar tiempos inválidos (cero)
                .collect(Collectors.toList());

        if (tiempos.isEmpty()) {
            // Retornar reporte vacío si no se encontraron tiempos
            return Map.of(
                    "PromedioRespuesta", 0,
                    "TiempoMinimo", 0,
                    "TiempoMaximo", 0,
                    "MedianaTiempoRespuesta", 0,
                    "Outliers", Collections.emptyList()
            );
        }

        // Calcular promedio, mínimo, máximo, y mediana de los tiempos
        double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
        long minimo = tiempos.stream().mapToLong(Long::longValue).min().orElse(0);
        long maximo = tiempos.stream().mapToLong(Long::longValue).max().orElse(0);
        long mediana = calcularMediana(tiempos);

        // Detectar outliers (solicitudes lentas)
        List<Long> outliers = detectarSolicitudesLentas(tiempos);

        // Crear el reporte
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("PromedioRespuesta", promedio);
        reporte.put("TiempoMinimo", minimo);
        reporte.put("TiempoMaximo", maximo);
        reporte.put("MedianaTiempoRespuesta", mediana);
        reporte.put("Outliers", outliers);

        return reporte;
    }


    @Override
    public Map<String, Object> generarReporteEndPoint() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        Map<String, Long> conteoEndpoints = logs.stream()
                .filter(line -> line.contains("ENDPOINT"))  // Asegura que la línea contiene un endpoint
                .map(line -> {
                    String[] parts = line.split(" ");
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        return parts[1];  // Extrae el endpoint si está presente
                    } else {
                        return "Indefinido";  // Asigna un valor por defecto si no hay endpoint
                    }
                })
                .collect(Collectors.groupingBy(endpoint -> endpoint, Collectors.counting()));

        String endpointMasUsado = conteoEndpoints.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        String endpointMenosUsado = conteoEndpoints.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("ConteoPorEndpoint", conteoEndpoints);
        reporte.put("EndpointMasUsado", endpointMasUsado);
        reporte.put("EndpointMenosUsado", endpointMenosUsado);

        return reporte;
    }



    @Override
    public Map<String, Object> generarReporteEstatusAplicacion() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        // Contar total de peticiones
        long totalPeticiones = logs.stream()
                .filter(line -> line.contains("REQUEST"))
                .count();

        // Contar total de errores
        long totalErrores = logs.stream()
                .filter(line -> line.contains("ERROR"))
                .count();

        // Filtrar y extraer los tiempos de respuesta
        List<Long> tiemposDeRespuesta = logs.stream()
                .filter(line -> line.contains("Tiempo de respuesta"))  // Filtrar líneas con tiempos de respuesta
                .map(line -> Long.parseLong(line.replaceAll("\\D+", "")))  // Extraer solo los números (milisegundos)
                .collect(Collectors.toList());

        // Calcular el tiempo promedio de respuesta
        double tiempoPromedioRespuesta = tiemposDeRespuesta.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("TotalPeticionesProcesadas", totalPeticiones);
        reporte.put("TotalErrores", totalErrores);
        reporte.put("TiempoPromedioRespuesta", tiempoPromedioRespuesta);  // Añadir el tiempo promedio

        return reporte;
    }

    @Override
    public Map<String, Object> generarReporteErrorCritico() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        // Filtrar líneas con errores críticos o tiempos de respuesta largos
        List<String> eventosCriticos = logs.stream()
                .filter(line -> line.contains("CRITICAL") || line.contains("500") ||
                        line.contains("Timeout") || esTiempoRespuestaLargo(line))
                .collect(Collectors.toList());

        long cantidadEventosCriticos = eventosCriticos.size();

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("EventosCriticos", eventosCriticos);
        reporte.put("CantidadEventosCriticos", cantidadEventosCriticos);

        return reporte;
    }

    // Método para identificar si el tiempo de respuesta es muy largo
    private boolean esTiempoRespuestaLargo(String line) {
        if (line.contains("Tiempo de respuesta")) {
            long tiempo = Long.parseLong(line.replaceAll("\\D+", "")); // Extraer tiempo numérico
            return tiempo > 5000; // Considerar tiempo mayor a 5000ms como crítico
        }
        return false;
    }


    private String extraerHora(String lineaLog) {
        // El formato del timestamp en el log es: "2024-09-22T01:53:47.999-06:00"
        // Extraer la hora "HH" de esa línea.
        try {
            String timestamp = lineaLog.split(" ")[0];  // Obtener la primera parte del log que contiene la fecha y hora
            return timestamp.substring(11, 13) + ":00";  // Devolver solo la hora "HH:00"
        } catch (Exception e) {
            return "Hora desconocida";
        }
    }

    private List<Long> detectarSolicitudesLentas(List<Long> tiempos) {
        long umbral = 5000;  // Definir un umbral (por ejemplo, 5000 ms)
        return tiempos.stream()
                .filter(tiempo -> tiempo > umbral)
                .collect(Collectors.toList());
    }

    private long calcularMediana(List<Long> tiempos) {
        Collections.sort(tiempos);
        int size = tiempos.size();
        if (size % 2 == 0) {
            return (tiempos.get(size / 2 - 1) + tiempos.get(size / 2)) / 2;
        } else {
            return tiempos.get(size / 2);
        }
    }
}
