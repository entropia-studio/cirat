package studio.entropia.ciratturismo.data;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import studio.entropia.ciratturismo.data.cForecastDay;

/**
 * Created by javier on 3/07/16.
 */
public class AemetXmlParser {

    private ArrayList<cForecastDay> forecastDays = new ArrayList<>();
    private Boolean debug = false;
    // We don't use namespaces
    private static final String ns = null;

    public ArrayList parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readAEMET(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<cForecastDay> readAEMET(XmlPullParser parser) throws XmlPullParserException, IOException {

        String fecha;
        String prob_precipitacion = "0";
        String estado_cielo;
        String direccion;
        String velocidad;
        String parent_tag = "root";
        int estado_cielo_code;

        cForecastDay forecastDay = new cForecastDay();

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            if (debug){
                Log.i(getClass().getSimpleName(),"name:"+tag);
            }

            switch (parser.getEventType()){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (tag.equals("dia")) {
                        fecha = parser.getAttributeValue(null, "fecha");
                        if (fecha != null){
                            if (debug){
                                Log.i(getClass().getSimpleName(),"getAttributeValue:"+fecha);
                            }
                            if (fechaIsBefore(fecha)){
                                forecastDay.setFecha(formatFecha(fecha,0));
                            }
                            else{
                                skip(parser);
                            }
                        }
                    }else if (tag.equals("prob_precipitacion")) {
                        String periodo = parser.getAttributeValue(null, "periodo");
                        if (periodo != null) {
                            if (periodo.equals("00-24")) {
                                prob_precipitacion = readText(parser);
                                forecastDay.setProb_precipitacion(prob_precipitacion);
                            }
                        }else{
                            prob_precipitacion = readText(parser);
                            forecastDay.setProb_precipitacion(prob_precipitacion);
                        }
                    }else if (tag.equals("estado_cielo")){
                        estado_cielo = parser.getAttributeValue(null, "descripcion");
                        if (estado_cielo != null){
                            // En caso de estar asignado, no lo volvemos a asignar
                            // solo nos interesa el primer item que tenga descripción
                            if (estado_cielo.length() > 1 && forecastDay.getEstado_cielo() == null) {
                                if (debug){
                                    Log.i(getClass().getSimpleName(),"estado_cielo:"+estado_cielo);
                                }

                                forecastDay.setEstado_cielo(estado_cielo);
                                // Obtenemos el código para poder representar la imagen correcta
                                estado_cielo_code = Integer.parseInt(readText(parser));
                                forecastDay.setEstado_cielo_code(estado_cielo_code);
                                // Obtenemos la imagen a través del código retornado de la AEMET
                                forecastDay.setImg_resource(getIconResource(estado_cielo_code));
                            }
                        }
                    }else if (tag.equals("viento")){
                        String periodo = parser.getAttributeValue(null, "periodo");
                        if (periodo != null) {
                            if (periodo.equals("12-24")) {
                                parent_tag = "viento_12_24";
                            }
                        }else{
                            parent_tag = "viento_12_24";
                        }
                    }else if (tag.equals("direccion") && parent_tag.equals("viento_12_24")){
                        direccion = readText(parser);
                        if (direccion != null){
                            forecastDay.setViento_direccion(direccion);
                        }
                    }else if (tag.equals("velocidad") && parent_tag.equals("viento_12_24")){
                        velocidad = readText(parser);
                        if (velocidad != null){
                            forecastDay.setViento_velocidad(velocidad);
                        }
                        parent_tag = "root";
                    }else if (tag.equals("maxima") && forecastDay.getTemperatura_maxima() == null ){
                        forecastDay.setTemperatura_maxima(readText(parser));
                    }else if (tag.equals("minima") && forecastDay.getTemperatura_minima() == null){
                        forecastDay.setTemperatura_minima(readText(parser));
                    }else if (tag.equals("uv_max")) {
                        forecastDay.setUv_max(readText(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equals("dia")){
                        forecastDays.add(forecastDay);
                        //Iniciamos un nuevo objeto vacío
                        forecastDay = new cForecastDay();
                    }
                    break;
            }
            parser.next();
        }
        return forecastDays;
    }

    private String formatFecha(String fecha, int modo){
        String[] listModos = {
                "EEEE, dd MMM",
                "dd-MM-yyyy",
                "dd-MM"
        };

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_formatted = sdf.parse(fecha);
            if (debug){
                Log.i(getClass().getSimpleName(),"fecha_formatted: "+fecha_formatted);
            }
            SimpleDateFormat sdf02 = new SimpleDateFormat(listModos[modo]);
            fecha = sdf02.format(fecha_formatted);
        }catch (Exception e){
            e.printStackTrace();
        }

        return fecha;

    }

    private Boolean fechaIsBefore(String fecha){
        Boolean fecha_is_correct = false;
        try{
            //Comprobamos si la fecha es mayor o igual a hoy
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_xml = sdf.parse(fecha);
            //Si la fecha del XML es anterior a la actual la descartamos
            //Para el igual tomamos el número de día de la semana para evitar errores con las horas
            if (fecha_xml.getDay() == new Date().getDay() || fecha_xml.after(new Date())){
                fecha_is_correct = true;
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha_is_correct;
    }

    // For the tags dia and prob_precipitacion, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String getIconResource(int estado_cielo){

        String estado_cielo_resource;
        int forecas_array_size = forecastDays.size();

        switch (estado_cielo){
            case 11:
                estado_cielo_resource = (forecas_array_size<1) ? "art_clear" : "ic_clear";
                break;
            case 12:
            case 13:
            case 14:
            case 17:
                estado_cielo_resource = (forecas_array_size<1) ? "art_light_clouds" : "ic_light_clouds";
                break;
            case 15:
            case 16:
                estado_cielo_resource = (forecas_array_size<1) ? "art_clouds" : "ic_cloudy";
                break;
            case 43:
            case 44:
            case 45:
            case 46:
                estado_cielo_resource = (forecas_array_size<1) ? "art_light_rain" : "ic_light_rain";
                break;
            case 23:
            case 24:
            case 25:
            case 26:
                estado_cielo_resource = (forecas_array_size<1) ? "art_rain" : "ic_rain";
                break;
            case 71:
            case 72:
            case 73:
                estado_cielo_resource = (forecas_array_size<1) ? "art_light_rain" : "ic_light_rain";
                break;
            case 33:
            case 34:
            case 35:
            case 36:
                estado_cielo_resource = (forecas_array_size<1) ? "art_snow" : "ic_snow";
                break;
            case 51:
            case 52:
            case 53:
            case 54:
            case 61:
            case 62:
            case 63:
            case 64:
                estado_cielo_resource = (forecas_array_size<1) ? "art_storm" : "ic_storm";
                break;
        default:
            estado_cielo_resource = (forecas_array_size<1) ? "art_clear" : "ic_clear";
        }
        if (debug){
            Log.i(getClass().getSimpleName(),"estado_cielo_resource:"+estado_cielo_resource);
        }
        return estado_cielo_resource;
    }

}
