package studio.entropia.ciratturismo;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import studio.entropia.ciratturismo.helper.Utility;


public class TrackMap extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private int id_track;
    private String center_coordinates;
    private String kml;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public Location mLastLocation;
    private Boolean debug = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Configuramos al action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_location);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //El listener tiene que ser configurado después de setSupportActionBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //Tipo de vista del mapa (Carretera, Híbrido, etc)
        Spinner mapTypeView = (Spinner) findViewById(R.id.mapTypeView);

        ArrayAdapter<String> adapter02 = new ArrayAdapter<String>(
                getSupportActionBar().getThemedContext(),
                R.layout.appbar_filter_title,
                new String[]{getString(R.string.car_mode),
                        getString(R.string.hybrid_mode),
                        getString(R.string.satellite_mode),
                        getString(R.string.terrain_mode)}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);

                return view;
            }
        };


        adapter02.setDropDownViewResource(R.layout.appbar_filter_list);

        mapTypeView.setAdapter(adapter02);

        //Asignamos el tipo de visualización
        mapTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case (0):
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case (1):
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case (2):
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case (3):
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //... Acciones al no existir ningún elemento seleccionado
            }
        });

        Bundle extras = getIntent().getExtras();
        id_track = extras.getInt("id_track");
        center_coordinates = extras.getString("center_coordinates");
        kml = extras.getString("kml");
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Boolean setMyLocation = true;

        //Eliminamos los botones de indicaciones al marcador
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //Comprobación de permisos
        //Botón de ubicación del usuario
        if (android.os.Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                setMyLocation = false;
            }
        }
        //Mostramos el botón de ubicación del usuario,
        //con padding ya que hemos definido la App Bar
        if (setMyLocation){
            mMap.setMyLocationEnabled(true);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            int idResource = getResources().getIdentifier(kml,"raw",getPackageName());
            KmlLayer layer = new KmlLayer(mMap,idResource, getApplicationContext());
            layer.addLayerToMap();
            moveCameraToKml();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void moveCameraToKml() {

        String[] location_latLong = center_coordinates.split(",");
        double location_latitude = Double.parseDouble(location_latLong[0]);
        double location_longitude = Double.parseDouble(location_latLong[1]);

        LatLng mapLocation = new LatLng(location_latitude,location_longitude);

        CameraPosition camPos = new CameraPosition.Builder()
                .target(mapLocation)   //Centramos el mapa en el cLocation
                .zoom(15)         //Establecemos el zoom
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(0)         //Bajamos el punto de vista de la cámara
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);
    }


    //Conectamos el cliente API para poder obtener la distancia a los puntos geográficos
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (debug){
            Log.i(getClass().getSimpleName(), "Location connection suspended.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Boolean hasPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //Permisos de ubicación
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }
        if (hasPermission){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            if (debug){
                Log.i(getClass().getSimpleName(), "Location services connection failed with code " + connectionResult.getErrorCode());
            }
        }
    }
}
