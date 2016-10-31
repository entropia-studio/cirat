package studio.entropia.ciratturismo.data;

/**
 * Created by javier on 2/07/16.
 */
public class cForecastDay {
    private String fecha;
    private String prob_precipitacion;
    private String estado_cielo;
    private int estado_cielo_code;
    private String viento_direccion;
    private String viento_velocidad;
    private String temperatura_maxima;
    private String temperatura_minima;
    private String humedad_relativa;
    private String uv_max;
    private String img_resource;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProb_precipitacion() {
        return prob_precipitacion;
    }

    public void setProb_precipitacion(String prob_precipitacion) {
        this.prob_precipitacion = prob_precipitacion;
    }

    public String getEstado_cielo() {
        return estado_cielo;
    }

    public void setEstado_cielo(String estado_cielo) {
        this.estado_cielo = estado_cielo;
    }

    public String getViento_direccion() {
        return viento_direccion;
    }

    public void setViento_direccion(String viento_direccion) {
        this.viento_direccion = viento_direccion;
    }

    public String getViento_velocidad() {
        return viento_velocidad;
    }

    public void setViento_velocidad(String viento_velocidad) {
        this.viento_velocidad = viento_velocidad;
    }

    public String getTemperatura_maxima() {
        return temperatura_maxima;
    }

    public void setTemperatura_maxima(String temperatura_maxima) {
        this.temperatura_maxima = temperatura_maxima;
    }

    public String getTemperatura_minima() {
        return temperatura_minima;
    }

    public void setTemperatura_minima(String temperatura_minima) {
        this.temperatura_minima = temperatura_minima;
    }

    public String getHumedad_relativa() {
        return humedad_relativa;
    }

    public void setHumedad_relativa(String humedad_relativa) {
        this.humedad_relativa = humedad_relativa;
    }

    public String getUv_max() {
        return uv_max;
    }

    public void setUv_max(String uv_max) {
        this.uv_max = uv_max;
    }

    public int getEstado_cielo_code() {
        return estado_cielo_code;
    }

    public void setEstado_cielo_code(int estado_cielo_code) {
        this.estado_cielo_code = estado_cielo_code;
    }

    public String getImg_resource() {
        return img_resource;
    }

    public void setImg_resource(String img_resource) {
        this.img_resource = img_resource;
    }
}
