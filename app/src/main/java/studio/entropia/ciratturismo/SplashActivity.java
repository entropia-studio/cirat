package studio.entropia.ciratturismo;

import android.Manifest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import studio.entropia.ciratturismo.data.JSONCelebrationParser;
import studio.entropia.ciratturismo.data.JSONLocationParser;
import studio.entropia.ciratturismo.data.JSONTrackParser;
import studio.entropia.ciratturismo.data.cCelebration;
import studio.entropia.ciratturismo.data.cLocation;
import studio.entropia.ciratturismo.data.CiratContract.ImageEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationImageEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationLangEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackImagesEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackLangEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationImagesEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationLangEntry;
import studio.entropia.ciratturismo.data.cTrack;


public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 124;
    private static final int RC_SETTINGS_SCREEN = 125;
    private static final int REQUEST_CODE_GPS_ENABLED = 100;
    private SharedPreferences prefs;
    private String last_updated_date;
    private Boolean debug = false;
    private String TAG = SplashActivity.class.getSimpleName();
    private TextView loading_state;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Recuperamos la fecha de la última actualización si la hay
        last_updated_date = prefs.getString("last_updated_date",null);
        spinner = (ProgressBar) findViewById (R.id.progressBar);
        loading_state = (TextView) findViewById(R.id.description);
        //Actualizamos en cascada Locations->Tracks->Celebrations->Lanzamos MainActivity
        LoadLocations();
    }

    /**
     * Chequea todos los permisos peligrosos necesarios definidos en el manifiesto
     * Obligatorio para el buen funcionamiento a partir API Level 23
     */

    private void permissionCheck(){
        String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            checkGPSState();
        } else {
            // Ask for permission
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale),
                    PERMISSIONS_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (debug){
            Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        }
        checkGPSState();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (debug){
            Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        }

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.permission_denied))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                            LaunchMainActivity();
                        }}
                    )
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }else{
            LaunchMainActivity();
        }


    }

    //Comprueba si el GPS está o no activado
    private void checkGPSState(){
        //Comprobamos si el GPS está activado
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{
            LaunchMainActivity();
        }
    }

    /**
     * Mensaje de alerta si el GPS no está activado
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.GPS_Advice))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),REQUEST_CODE_GPS_ENABLED);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        LaunchMainActivity();
                    }

                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * Recoge el resultado de haber lanzado el intent de ubicación
     * Independientemete de lo que devuelva, lanza la actividad principal
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (debug){
            Log.d(TAG,"onActivityResult: " + requestCode + ":" + resultCode);
        }

        switch (requestCode){
            case REQUEST_CODE_GPS_ENABLED:
                //Usuario no ha activado el GPS
                if (resultCode != 0){
                    Toast.makeText(this,getResources().getString(R.string.GPS_negative_result), Toast.LENGTH_SHORT).show();
                }
                LaunchMainActivity();
                break;
            //Retorno desde los ajustes
            case RC_SETTINGS_SCREEN:
                //Comprobamos si ha activado los permisos de ubicación
                String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    // Have permissions, do the thing!
                    checkGPSState();
                }else{
                    //El usuario no ha activado manualmente los permisos de ubicación
                    LaunchMainActivity();
                }
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length == 0
//                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this,R.string.permission_denied, Toast.LENGTH_LONG).show();
//                    Log.i(TAG,"onRequestPermissionsResult: 01");
//                    LaunchMainActivity();
//                    Log.i(TAG,"onRequestPermissionsResult: 01");
//                }else{
//                    Log.i(TAG,"onRequestPermissionsResult: 03");
//                    LaunchMainActivity();
//                }
//                break;
//            }
//        }
//    }

    private void LaunchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void LoadLocations(){
        String mURLLocations = "http://www.portear.com/ciratapp/getJSONLocations.php";
        spinner.setVisibility(View.VISIBLE);
        loading_state.setText(getResources().getString(R.string.loading_locations));
        new DownloadJsonLocationsTask().execute(mURLLocations);
    }
    public void LoadTracks(){
        String mURLTracks = "http://www.portear.com/ciratapp/getJSONTracks.php";
        loading_state.setText(getResources().getString(R.string.loading_tracks));
        new DownloadJsonTracksTask().execute(mURLTracks);
    }

    public void LoadCelebrations(){
        String mURLCelebrations = "http://www.portear.com/ciratapp/getJSONCelebrations.php";
        loading_state.setText(getResources().getString(R.string.loading_celebrations));
        new DownloadJsonCelebrationsTask().execute(mURLCelebrations);
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadJsonLocationsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (last_updated_date != null){
                //Codificamos a formato URL
                try{
                    last_updated_date = URLEncoder.encode(last_updated_date,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    throw new AssertionError("UTF-8 is unknown");
                }
                urls[0] += "?date="+last_updated_date;
            }
            if (debug){
                Log.i(TAG,"URL locations: "+urls[0]);
            }
            try {
                return loadJsonLocationsFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            LoadTracks();
        }
    }

    // HTML markup. Returns HTML string.
    private String loadJsonLocationsFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        // Instantiate the parser
        JSONLocationParser CiratJsonParser = new JSONLocationParser();
        List<cLocation> locations;

        try {
            stream = downloadUrl(urlString);
            locations = CiratJsonParser.readJsonStream(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        ContentResolver cr = getContentResolver();
        for (int i=0;i<locations.size();i++) {

            //location table values
            ContentValues location_row = new ContentValues();
            location_row.put(LocationEntry._ID.toString(),locations.get(i).get_id_location());
            location_row.put(LocationEntry.COLUMN_CATEGORY_KEY,locations.get(i).getId_category());
            location_row.put(LocationEntry.COLUMN_LOCATION_COORDINATES,locations.get(i).getCoordinates());
            location_row.put(LocationEntry.COLUMN_LOCATION_ADDRESS,locations.get(i).getAddress());
            location_row.put(LocationEntry.COLUMN_LOCATION_PHONE,locations.get(i).getPhone());
            location_row.put(LocationEntry.COLUMN_LOCATION_MOBILE,locations.get(i).getMobile());
            location_row.put(LocationEntry.COLUMN_LOCATION_URL,locations.get(i).getUrl());
            location_row.put(LocationEntry.COLUMN_LOCATION_EMAIL,locations.get(i).getEmail());
            location_row.put(LocationEntry.COLUMN_LOCATION_ACTIVE,locations.get(i).getActive());
            cr.insert(LocationEntry.CONTENT_URI, location_row);

            // Entradas por idioma en las ubicaciones (Multi idioma)
            for (int j =0;j<locations.get(i).getmLocationsLang().size();j++){
                ContentValues location_lang_row = new ContentValues();
                location_lang_row.put(LocationLangEntry._ID.toString(),
                        locations.get(i).getmLocationsLang().get(j).get_id());
                location_lang_row.put(LocationLangEntry.COLUMN_LOCATION_LANG_LOCATION_KEY.toString(),
                        locations.get(i).get_id_location());
                location_lang_row.put(LocationLangEntry.COLUMN_LOCATION_LANG_LANG_KEY.toString(),
                        locations.get(i).getmLocationsLang().get(j).getId_lang());
                location_lang_row.put(LocationLangEntry.COLUMN_LOCATION_LANG_NAME.toString(),
                        locations.get(i).getmLocationsLang().get(j).getName());
                location_lang_row.put(LocationLangEntry.COLUMN_LOCATION_LANG_DESCRIPTION.toString(),
                        locations.get(i).getmLocationsLang().get(j).getDescription());
                location_lang_row.put(LocationLangEntry.COLUMN_LOCATION_LANG_NAME_COVER.toString(),
                        locations.get(i).getmLocationsLang().get(j).getName_cover());
                cr.insert(LocationLangEntry.CONTENT_URI, location_lang_row);
            }

            //Imágenes para las ubicaciones
            for (int j =0;j<locations.get(i).getmImages().size();j++){
                ContentValues location_image_row = new ContentValues();

                location_image_row.put(LocationImageEntry._ID,
                        locations.get(i).getmImages().get(j).get_id());
                location_image_row.put(LocationImageEntry.COLUMN_LOCATION_IMAGE_IMAGE_KEY,
                        locations.get(i).getmImages().get(j).getId_image());
                location_image_row.put(LocationImageEntry.COLUMN_LOCATION_IMAGE_LOCATION_KEY,
                        locations.get(i).get_id_location());
                location_image_row.put(LocationImageEntry.COLUMN_LOCATION_IMAGE_COVER,
                        locations.get(i).getmImages().get(j).getCover());
                location_image_row.put(LocationImageEntry.COLUMN_LOCATION_IMAGE_POSITION,
                        locations.get(i).getmImages().get(j).getPosition());

                cr.insert(LocationImageEntry.CONTENT_URI, location_image_row);

                ContentValues image_row = new ContentValues();
                image_row.put(ImageEntry._ID.toString(),
                        locations.get(i).getmImages().get(j).getId_image());
                image_row.put(ImageEntry.COLUMN_IMAGE_FILE,
                        locations.get(i).getmImages().get(j).getmFile());
                image_row.put(ImageEntry.COLUMN_IMAGE_ACTIVE,
                        locations.get(i).getmImages().get(j).getActive());

                cr.insert(ImageEntry.CONTENT_URI, image_row);
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_updated_date", getDateCurrentTimeZone());
        editor.commit();

        return getDateCurrentTimeZone();

    }

    private class DownloadJsonTracksTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (last_updated_date != null){
                //Codificamos a formato URL
                try{
                    last_updated_date = URLEncoder.encode(last_updated_date,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    throw new AssertionError("UTF-8 is unknown");
                }
                urls[0] += "?date="+last_updated_date;
            }

            if (debug){
                Log.i(TAG,"URL tracks: "+urls[0]);
            }

            try {
                return loadJsonTracksFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            LoadCelebrations();
        }
    }

    // HTML markup. Returns HTML string.
    private String loadJsonTracksFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        // Instantiate the parser
        JSONTrackParser TracksJsonParser = new JSONTrackParser();
        List<cTrack> tracks;

        try {
            stream = downloadUrl(urlString);
            tracks = TracksJsonParser.readJsonStream(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        ContentResolver cr = getContentResolver();

        for (int i=0;i<tracks.size();i++) {

            //Track table values
            ContentValues track_row = new ContentValues();
            track_row.put(TrackEntry._ID.toString(),tracks.get(i).getId_track());
            track_row.put(TrackEntry.COLUMN_TRACK_DISTANCE,tracks.get(i).getDistance());
            track_row.put(TrackEntry.COLUMN_TRACK_IS_CIRCULAR,tracks.get(i).getIs_circular());
            track_row.put(TrackEntry.COLUMN_TRACK_TOTAL_TIME,tracks.get(i).getTotal_time());
            track_row.put(TrackEntry.COLUMN_TRACK_KML,tracks.get(i).getKml());
            track_row.put(TrackEntry.COLUMN_TRACK_POSITION,tracks.get(i).getPosition());
            track_row.put(TrackEntry.COLUMN_TRACK_POSITIVE_GRADIENT,tracks.get(i).getPositive_gradient());
            track_row.put(TrackEntry.COLUMN_TRACK_NEGATIVE_GRADIENT,tracks.get(i).getNegative_gradient());
            track_row.put(TrackEntry.COLUMN_TRACK_MAX_ALTITUDE,tracks.get(i).getMax_altitude());
            track_row.put(TrackEntry.COLUMN_TRACK_MIN_ALTITUDE,tracks.get(i).getMin_altitude());
            track_row.put(TrackEntry.COLUMN_TRACK_CENTER_COORDINATES,tracks.get(i).getCenter_coordinates());
            track_row.put(TrackEntry.COLUMN_TRACK_ACTIVE,tracks.get(i).getActive());
            cr.insert(TrackEntry.CONTENT_URI, track_row);

            for (int j =0;j<tracks.get(i).getmTracksLang().size();j++){

                ContentValues track_lang_row = new ContentValues();
                track_lang_row.put(TrackLangEntry._ID.toString(),
                        tracks.get(i).getmTracksLang().get(j).get_id());
                track_lang_row.put(TrackLangEntry.COLUMN_TRACK_LANG_TRACK_KEY.toString(),
                        tracks.get(i).getId_track());
                track_lang_row.put(TrackLangEntry.COLUMN_TRACK_LANG_LANG_KEY.toString(),
                        tracks.get(i).getmTracksLang().get(j).getId_lang());
                track_lang_row.put(TrackLangEntry.COLUMN_TRACK_LANG_NAME.toString(),
                        tracks.get(i).getmTracksLang().get(j).getName());
                track_lang_row.put(TrackLangEntry.COLUMN_TRACK_LANG_DESCRIPTION.toString(),
                        tracks.get(i).getmTracksLang().get(j).getDescription());
                track_lang_row.put(TrackLangEntry.COLUMN_TRACK_LANG_ACTIVE,
                        tracks.get(i).getmTracksLang().get(j).getActive());
                cr.insert(TrackLangEntry.CONTENT_URI, track_lang_row);
            }

            //Imágenes para los tracks
            for (int j =0;j<tracks.get(i).getmImages().size();j++){

                ContentValues track_image_row = new ContentValues();

                track_image_row.put(TrackImagesEntry._ID,
                        tracks.get(i).getmImages().get(j).get_id());
                track_image_row.put(TrackImagesEntry.COLUMN_TRACK_IMAGE_IMAGE_KEY,
                        tracks.get(i).getmImages().get(j).getId_image());
                track_image_row.put(TrackImagesEntry.COLUMN_TRACK_IMAGE_TRACK_KEY,
                        tracks.get(i).getId_track());
                track_image_row.put(TrackImagesEntry.COLUMN_TRACK_IMAGE_COVER,
                        tracks.get(i).getmImages().get(j).getCover());
                track_image_row.put(TrackImagesEntry.COLUMN_TRACK_IMAGE_POSITION,
                        tracks.get(i).getmImages().get(j).getPosition());

                cr.insert(TrackImagesEntry.CONTENT_URI, track_image_row);

                ContentValues image_row = new ContentValues();
                image_row.put(ImageEntry._ID.toString(),
                        tracks.get(i).getmImages().get(j).getId_image());
                image_row.put(ImageEntry.COLUMN_IMAGE_FILE,
                        tracks.get(i).getmImages().get(j).getmFile());
                image_row.put(ImageEntry.COLUMN_IMAGE_ACTIVE,
                        tracks.get(i).getmImages().get(j).getActive());

                cr.insert(ImageEntry.CONTENT_URI, image_row);
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_updated_date",getDateCurrentTimeZone());
        editor.commit();

        return "tracks";
    }

    private class DownloadJsonCelebrationsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (last_updated_date != null){
                //Codificamos a formato URL
                try{
                    last_updated_date = URLEncoder.encode(last_updated_date,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    throw new AssertionError("UTF-8 is unknown");
                }
                urls[0] += "?date="+last_updated_date;
            }

            if (debug){
                Log.i(TAG,"URL celebrations: "+urls[0]);
            }

            try {
                return loadJsonCelebrationsFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            permissionCheck();
        }
    }

    private String loadJsonCelebrationsFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        // Instantiate the parser
        JSONCelebrationParser CelebrationsJsonParser = new JSONCelebrationParser();
        List<cCelebration> celebrations;

        try {
            stream = downloadUrl(urlString);
            celebrations = CelebrationsJsonParser.readJsonStream(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        ContentResolver cr = getContentResolver();

        for (int i=0;i<celebrations.size();i++) {

            ContentValues celebration_row = new ContentValues();

            celebration_row.put(CelebrationEntry._ID.toString(),celebrations.get(i).getId_celebration());
            celebration_row.put(CelebrationEntry.COLUMN_CELEBRATION_DATE_START,celebrations.get(i).getDate_start());
            celebration_row.put(CelebrationEntry.COLUMN_CELEBRATION_DATE_END,celebrations.get(i).getDate_end());
            celebration_row.put(CelebrationEntry.COLUMN_CELEBRATION_ACTIVE,celebrations.get(i).getActive());

            cr.insert(CelebrationEntry.CONTENT_URI, celebration_row);

            for (int j =0;j<celebrations.get(i).getmCelebrationsLang().size();j++){

                ContentValues celebration_lang_row = new ContentValues();

                celebration_lang_row.put(CelebrationLangEntry._ID.toString(),
                        celebrations.get(i).getmCelebrationsLang().get(j).get_id());
                celebration_lang_row.put(CelebrationLangEntry.COLUMN_CELEBRATION_LANG_CELEBRATION_KEY.toString(),
                        celebrations.get(i).getId_celebration());
                celebration_lang_row.put(CelebrationLangEntry.COLUMN_CELEBRATION_LANG_LANG_KEY.toString(),
                        celebrations.get(i).getmCelebrationsLang().get(j).getId_lang());
                celebration_lang_row.put(CelebrationLangEntry.COLUMN_CELEBRATION_LANG_NAME.toString(),
                        celebrations.get(i).getmCelebrationsLang().get(j).getName());
                celebration_lang_row.put(CelebrationLangEntry.COLUMN_CELEBRATION_LANG_DESCRIPTION.toString(),
                        celebrations.get(i).getmCelebrationsLang().get(j).getDescription());
                celebration_lang_row.put(CelebrationLangEntry.COLUMN_CELEBRATION_LANG_ACTIVE,
                        celebrations.get(i).getmCelebrationsLang().get(j).getActive());

                cr.insert(CelebrationLangEntry.CONTENT_URI, celebration_lang_row);

            }


            //Imágenes
            for (int j =0;j<celebrations.get(i).getmImages().size();j++){

                ContentValues celebration_image_row = new ContentValues();

                celebration_image_row.put(CelebrationImagesEntry._ID,
                        celebrations.get(i).getmImages().get(j).get_id());
                celebration_image_row.put(CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_IMAGE_KEY,
                        celebrations.get(i).getmImages().get(j).getId_image());
                celebration_image_row.put(CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_CELEBRATION_KEY,
                        celebrations.get(i).getId_celebration());
                celebration_image_row.put(CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_COVER,
                        celebrations.get(i).getmImages().get(j).getCover());
                celebration_image_row.put(CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_POSITION,
                        celebrations.get(i).getmImages().get(j).getPosition());

                cr.insert(CelebrationImagesEntry.CONTENT_URI, celebration_image_row);

                ContentValues image_row = new ContentValues();
                image_row.put(ImageEntry._ID.toString(),
                        celebrations.get(i).getmImages().get(j).getId_image());
                image_row.put(ImageEntry.COLUMN_IMAGE_FILE,
                        celebrations.get(i).getmImages().get(j).getmFile());
                image_row.put(ImageEntry.COLUMN_IMAGE_ACTIVE,
                        celebrations.get(i).getmImages().get(j).getActive());

                cr.insert(ImageEntry.CONTENT_URI, image_row);
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_updated_date",getDateCurrentTimeZone());
        editor.commit();

        return "celebrations";
    }



    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
    public  String getDateCurrentTimeZone() {
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

}