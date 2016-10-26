package studio.entropia.ciratturismo.helper;

import android.content.Context;
import android.location.Location;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by javier on 2/06/16.
 */
public class Utility {
    /**
     ** Calcula la distancia en km entre dos coordenadas
     * Formato: latitud, longitud (39.5167187,-0.4219455)
     */

    public static Float calculateDistance(String location_coordinates, String user_coordinates){
        float distance = 0;
        //Comprobamos si las coordenadas tienen la , de separación
        if (location_coordinates.indexOf(",") >= 0)
        {
            //Coordenadas del punto
            double location_latitude = getLatitude(location_coordinates);
            double location_longitude = getLongitude(location_coordinates);

            //Coordenadas del usuario
            String[] user_latLong = user_coordinates.split(",");
            double user_latitude = getLatitude(user_coordinates);
            double user_longitude = getLongitude(user_coordinates);

            //Creamos objeto con las coordenadas del cLocation
            Location point_location = new Location("Location");
            //Asignamos latitud, longitud
            point_location.setLatitude(location_latitude);
            point_location.setLongitude(location_longitude);

            //Ubicación del usuario
            Location user_location = new Location("User cLocation");
            user_location.setLatitude(user_latitude);
            user_location.setLongitude(user_longitude);

            //Distancia en kilómetros
            distance = user_location.distanceTo(point_location)/1000;
        }

        return distance;
    }

    public static String formatDistance(Float distance){
        //Formateamos los kilómetros de distancia 0.15 km
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return (formatter.format(distance));
    }


    /*
    * Obtiene la latitud a partir de una cadena
    * latitud,longitud 40.055215, -0.463084
     */
    public static double getLatitude(String LatLong){
        String[] location_latLong = LatLong.split(",");
        double location_latitude = Double.parseDouble(location_latLong[0]);
        return location_latitude;
    }
    /*
    * Obtiene la longitud a partir de una cadena
    * latitud,longitud 40.055215, -0.463084
     */
    public static double getLongitude(String LatLong){
        String[] location_latLong = LatLong.split(",");
        double location_longitude = Double.parseDouble(location_latLong[1]);
        return location_longitude;
    }

    /**
     * A partir de un número de dp nos devuelve los px para est densidad de pantalla
     * @param mContext
     * @param dpValue
     * @return
     */
    public static int getDpFromPx(Context mContext, int dpValue){
        float dimension = mContext.getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * dimension);
        return margin;
    }

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    /**
     *
     * @param str
     * @return una palabra capitalizada
     */
    public static String capitalize(String str){
        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        return cap;
    }



}
