package studio.entropia.ciratturismo;


import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.helper.Utility;
import studio.entropia.ciratturismo.data.cMarker;

public class LocationMap extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private Marker myMarker;
    private Boolean debug = false;
    //Array de marcadores, almacenamos todos los creados
    //para poder generar las ventanas de información correctamente
    private ArrayList<cMarker> markersArray = new ArrayList<>();
    private int markerIdCount = 0;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public Location mLastLocation;
    private String location_coordinates;
    private String user_coordinates;
    private List<Marker> markerList = new ArrayList<Marker>();
    private static String TAG = LocationMap.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

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

        //Recogemos la información enviada desde LocationDetail
        Bundle extras = getIntent().getExtras();
        //Extraemos las coordenadas y las convertimos al formato correcto
        String coordinates = extras.getString("coordinates");

        //Creamos un nuevo objeto marcador para almacenar todos los datos necesarios
        cMarker mMarker = new cMarker();

        //Marcador enviado tiene una id 0 siempre
        mMarker.setId(markerIdCount);
        markerIdCount++;
        mMarker.setLatitude(Utility.getLatitude(coordinates));
        mMarker.setLongitude(Utility.getLongitude(coordinates));
        mMarker.setName(extras.getString("name"));
        mMarker.setAddress(extras.getString("address"));
        mMarker.setImage_cover(extras.getString("image_cover"));
        mMarker.setId_location(extras.getInt("id_location"));
        mMarker.setId_category(extras.getInt("id_category"));
        markersArray.add(mMarker);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    /**
     *
     * @param id_category
     * Elimina del mapa todos los marcadores de la categoría
     */
    public void removeMarkers(int id_category){
        int i;
        //Recorremos todos los marcadores
        for (i=1;i<markersArray.size();i++){
            //La categoría coincide
            if (markersArray.get(i).getId_category() == id_category){
                markerList.get(i).remove();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Obtenemos el nombre del recurso tipo R.id.show_category_1
        CharSequence item_title = getResources().getResourceName(item.getItemId());
        //Tomamos el último caracter que es la id de la categoría
        item_title = item_title.subSequence(item_title.length()-1,item_title.length());
        //Parseamos a integer
        int id_categoria = Integer.parseInt(item_title.toString());

        if (debug){
            Log.i(TAG, "id_categoria: "+id_categoria);
        }

        showMarkers(id_categoria);

        if (item.isChecked()){
            item.setChecked(false);
            removeMarkers(id_categoria);
        }else{
            item.setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }

    //Conectamos el cliente API para poder obtener la distancia a los puntos geográficos
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
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
            if (debug){
                Log.i(getClass().getSimpleName(),"mLastLocation connected");
            }
            myMarker.showInfoWindow();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Boolean setMyLocation = true;
        //Activamos la gastion de los clics en marcador
        //Llama a onMarkerClick
        mMap.setOnMarkerClickListener(this);

        //Eliminamos los botones de indicaciones al marcador
        mMap.getUiSettings().setMapToolbarEnabled(false);

        double latitude = markersArray.get(0).getLatitude();
        double longitude = markersArray.get(0).getLongitude();


        // Add a marker and move the camera
        LatLng location = new LatLng(latitude, longitude);

        CameraPosition camPos = new CameraPosition.Builder()
                .target(location)   //Centramos el mapa en el cLocation
                .zoom(17)         //Establecemos el zoom
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(0)         //Bajamos el punto de vista de la cámara
                .build();

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

        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

        mMap.animateCamera(camUpd3);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int id_marker = Integer.parseInt(marker.getId().substring(1));
                Intent intent = new Intent(LocationMap.this,LocationDetail.class);

                if (debug){
                    Log.i(getClass().getSimpleName(),"id_category: "+markersArray.get(id_marker).getId_category()+". id_location:"+markersArray.get(id_marker).getId_location());
                }
                intent.putExtra("id_category",markersArray.get(id_marker).getId_category());
                intent.putExtra("id_location",markersArray.get(id_marker).getId_location());
                if (location_coordinates != null && user_coordinates != null){
                    intent.putExtra(LocationDetail.distance,
                            Utility.formatDistance(Utility.calculateDistance(location_coordinates,user_coordinates)));
                    intent.putExtra("user_coordinates",user_coordinates);
                }

                startActivity(intent);
            }
        });


        myMarker = mMap.addMarker(new MarkerOptions().position(location)
                                         .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_current)));

        //Añadimos el marcador a un array para poder manipularlo a posteriori
        markerList.add(myMarker);

        mMap.setInfoWindowAdapter(this);
        myMarker.showInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker myMarker) {
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker){

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.location_info_window,null);
        //Extraemos el id_marker que tiene una formato M1 .. Mn
        int id_marker = Integer.parseInt(marker.getId().substring(1));

        String name_location = markersArray.get(id_marker).getName();
        String address_location = markersArray.get(id_marker).getAddress();
        String image_cover = markersArray.get(id_marker).getImage_cover();

        TextView location_name = (TextView) v.findViewById(R.id.location_name);
        location_name.setText(name_location);

        TextView location_address = (TextView) v.findViewById(R.id.location_address);
        location_address.setText(address_location);

        location_coordinates = markersArray.get(id_marker).getLatitude()+","+markersArray.get(id_marker).getLongitude();

        if (mLastLocation != null){
            //Creamos la cadena con formato latitud, longitud (39.5167187,-0.4219455)
            user_coordinates = mLastLocation.getLatitude() +","+mLastLocation.getLongitude();

            TextView distance_to_user = (TextView) v.findViewById(R.id.distance_to_user);
            distance_to_user.setText(Utility.formatDistance(Utility.calculateDistance(location_coordinates,user_coordinates))+" km");
        }

        ImageView image_info_window = (ImageView) v.findViewById(R.id.image_info_window);

        //Cargamos la imagen llamando al método Callback InfoWindowRefresher
        //Si la imagen no está cargada vuelve a llamarse recursivamente hasta que se carga
        Picasso.with(getApplicationContext())
                .load(AppConstant.URL_IMAGES_LOCATIONS+
                      AppConstant.REMOTE_RESOLUTION_FOLDER+
                      image_cover)
                .error(R.mipmap.error_image)
                .into(image_info_window,new InfoWindowRefresher(marker));

        return v;
    }

    //Función que nos permite que se vean correctamente las imágenes en el info window
    //Es llamada desde prepareInfoView (getInfoContents) al cargar la imagen con Picasso
    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh = null;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }
        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }
        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }
    }

    /**
     * Muestra todos los marcadores dependiendo de la categoría
     * seleccionada en el spinner
     */
    private void showMarkers(int id_category){


        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getBaseContext());

        try {
            String name_location = ((cMarker)markersArray.get(0)).getName();
            //No extraemos el punto actual enviado desde LocationDetail
            String SELECT_SQL = "SELECT l._id as id_location,coordinates,name,file, address,id_category \n" +
                    "FROM location l \n" +
                    "INNER JOIN location_lang ll ON l._id = ll.id_location \n" +
                    "INNER JOIN location_image li ON ll.id_location = li.id_location \n" +
                    "INNER JOIN image i ON i._id = li.id_image \n" +
                    "WHERE id_category = " + id_category + " AND id_lang = 2 \n" +
                    "AND name != '"+name_location+"' AND cover = 1 AND l.active = 1 \n";

            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if (c.moveToFirst()) {
                    do {

                        //Creamos un nuevo objeto marcador para almacenar todos los datos necesarios
                        cMarker mMarker = new cMarker();

                        //Marcador enviado tiene una id 0 siempre
                        mMarker.setId(markerIdCount);

                        mMarker.setLatitude(Utility.getLatitude(c.getString(c.getColumnIndex("coordinates"))));
                        mMarker.setLongitude(Utility.getLongitude(c.getString(c.getColumnIndex("coordinates"))));
                        mMarker.setName(c.getString(c.getColumnIndex("name")));
                        mMarker.setAddress(c.getString(c.getColumnIndex("address")));
                        mMarker.setImage_cover(c.getString(c.getColumnIndex("file")));
                        mMarker.setId_location(c.getInt(c.getColumnIndex("id_location")));
                        mMarker.setId_category(id_category);
                        markersArray.add(mMarker);

                        //Obtenemos el nombre del icono a mostrar dependiendo de la categoría
                        String marker_icon = AppConstant.markers_mipmap[c.getInt(c.getColumnIndex("id_category"))];
                        //Convertimos la cadena en un identificador de recurso
                        int id_resource = getResources().getIdentifier(marker_icon, "mipmap", getPackageName());
                        //Asignamos al objeto LatLng las coordenadas
                        LatLng location = new LatLng(mMarker.getLatitude(), mMarker.getLongitude());
                        Marker myMarker = mMap.addMarker(new MarkerOptions().position(location)
                                .icon(BitmapDescriptorFactory.fromResource(id_resource)));

                        markerList.add(myMarker);

                        //Aumentamos la id de marcador
                        markerIdCount++;
//
                    }while (c.moveToNext());

                }
            }
            c.close();
        }catch (SQLiteException sqle) {
            if (debug){
                Log.e(getClass().getSimpleName(), "No se puede acceder a la DB");
            }

        }
        myDbHelper.close();
    }

}

