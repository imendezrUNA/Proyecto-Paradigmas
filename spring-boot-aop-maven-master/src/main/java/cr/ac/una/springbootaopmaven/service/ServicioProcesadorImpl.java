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

        Map<String, Long> conteoErrores = logs.stream()
                .filter(line -> line.contains("ERROR"))
                .collect(Collectors.groupingBy(line -> {
                    if (line.contains("400")) return "400 Bad Request";
                    if (line.contains("404")) return "404 Not Found";
                    if (line.contains("500")) return "500 Internal Server Error";
                    if (line.contains("NullPointerException")) return "NullPointerException";
                    return "Otros Errores";
                }, Collectors.counting()));

        String errorMasFrecuente = conteoErrores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        Map<String, Long> erroresPorHora = logs.stream()
                .filter(line -> line.contains("ERROR"))
                .collect(Collectors.groupingBy(this::extraerHora, Collectors.counting()));

        String horaPico = erroresPorHora.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No hay errores");

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("Total Errores Por Tipo", conteoErrores);
        reporte.put("Error Mas Frecuente", errorMasFrecuente);
        reporte.put("Horas Pico Errores", horaPico);

        return reporte;
    }

    @Override
    public Map<String, Object> generarRespuestaTiempoReporte() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        List<Long> tiempos = logs.stream()
                .filter(line -> line.contains("Tiempo de respuesta"))
                .map(line -> {
                    Pattern pattern = Pattern.compile("Tiempo de respuesta para el método .*: (\\d+) ms");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return Long.parseLong(matcher.group(1));
                    } else {
                        return 0L;
                    }
                })
                .filter(tiempo -> tiempo > 0)
                .collect(Collectors.toList());

        if (tiempos.isEmpty()) {
            return Map.of(
                    "Promedio Respuesta", 0,
                    "Tiempo Minimo", 0,
                    "Tiempo Maximo", 0,
                    "Mediana Tiempo Respuesta", 0,
                    "Outliers", Collections.emptyList()
            );
        }

        double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
        long minimo = tiempos.stream().mapToLong(Long::longValue).min().orElse(0);
        long maximo = tiempos.stream().mapToLong(Long::longValue).max().orElse(0);
        long mediana = calcularMediana(tiempos);

        List<Long> outliers = detectarSolicitudesLentas(tiempos);

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("Promedio Respuesta", promedio);
        reporte.put("Tiempo Minimo", minimo);
        reporte.put("Tiempo Maximo", maximo);
        reporte.put("Mediana Tiempo Respuesta", mediana);
        reporte.put("Outliers", outliers);

        return reporte;
    }


    @Override
    public Map<String, Object> generarReporteEndPoint() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        Map<String, Map<String, Long>> conteoPorMetodoYEndpoint = logs.stream()
                .filter(line -> line.contains("GET") || line.contains("POST") ||
                        line.contains("PUT") || line.contains("DELETE"))
                .map(line -> {
                    String[] parts = line.split(" ");
                    String metodo = "";
                    String endpoint = "";
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("GET") || parts[i].equals("POST") || parts[i].equals("PUT") || parts[i].equals("DELETE")) {
                            metodo = parts[i];
                            if (i + 1 < parts.length) {
                                endpoint = parts[i + 1];
                                endpoint = endpoint.replaceAll("[\",]", "").trim();
                            }
                            break;
                        }
                    }

                    System.out.println("Método: " + metodo + ", Endpoint: " + endpoint);
                    return new AbstractMap.SimpleEntry<>(metodo, endpoint);
                })
                .filter(entry -> entry.getValue().startsWith("/api/"))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue(),
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.counting()
                        )
                ));

        Map<String, Long> conteoTotalPorEndpoint = conteoPorMetodoYEndpoint.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().values().stream().mapToLong(Long::longValue).sum()
                ));

        String endpointMasUsado = conteoTotalPorEndpoint.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        String endpointMenosUsado = conteoTotalPorEndpoint.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

         Map<String, Object> reporte = new HashMap<>();
        reporte.put("Conteo Por Metodo Y Endpoint", conteoPorMetodoYEndpoint);
        reporte.put("Endpoint Mas Usado", endpointMasUsado);
        reporte.put("Endpoint Menos Usado", endpointMenosUsado);
        reporte.put("Conteo Total Por Endpoint", conteoTotalPorEndpoint);

        return reporte;
    }



    @Override
    public Map<String, Object> generarReporteEstatusAplicacion() {
        List<String> logs = procesadorLog.lectorArchivoLog();

        long totalPeticiones = logs.stream()
                .filter(line -> line.contains("Tiempo de respuesta para el método"))
                .count();

        // Contar total de errores
        long totalErrores = logs.stream()
                .filter(line -> line.contains("ERROR"))
                .count();

        // Filtrar y extraer los tiempos de respuesta
        List<Long> tiemposDeRespuesta = logs.stream()
                .filter(line -> line.contains("Tiempo de respuesta"))
                .map(line -> {
                    try {
                        Pattern pattern = Pattern.compile("Tiempo de respuesta para el método .*: (\\d+) ms");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            return Long.parseLong(matcher.group(1));
                        }
                    } catch (NumberFormatException e) {

                    }
                    return 0L;
                })
                .filter(tiempo -> tiempo > 0)
                .collect(Collectors.toList());

        // Calcular el tiempo promedio de respuesta
        double tiempoPromedioRespuesta = tiemposDeRespuesta.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        // Crear el reporte
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("Total Peticiones Procesadas", totalPeticiones);
        reporte.put("Total Errores", totalErrores);
        reporte.put("Tiempo Promedio Respuesta", tiempoPromedioRespuesta);

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

        // Asegúrate de que hay eventos críticos
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("Eventos Criticos", eventosCriticos);
        reporte.put("Cantidad Eventos Criticos", cantidadEventosCriticos);

        return reporte;
    }

    private boolean esTiempoRespuestaLargo(String line) {
        if (line.contains("Tiempo de respuesta")) {
            try {
                long tiempo = Long.parseLong(line.replaceAll("\\D+", ""));
                return tiempo > 5000;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }


    private String extraerHora(String lineaLog) {

        try {
            String timestamp = lineaLog.split(" ")[0];
            return timestamp.substring(11, 13) + ":00";
        } catch (Exception e) {
            return "Hora desconocida";
        }
    }

    private List<Long> detectarSolicitudesLentas(List<Long> tiempos) {
        long umbral = 5000;
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
