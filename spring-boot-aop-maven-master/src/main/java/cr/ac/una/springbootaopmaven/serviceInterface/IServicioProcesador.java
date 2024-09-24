package cr.ac.una.springbootaopmaven.serviceInterface;

import java.util.Map;

public interface IServicioProcesador {

    Map<String, Object> generarReporteError();
    Map<String, Object> generarReporteEndPoint();
    Map<String, Object> generarRespuestaTiempoReporte();
    Map<String, Object> generarReporteEstatusAplicacion();
    Map<String, Object> generarReporteErrorCritico();
}
