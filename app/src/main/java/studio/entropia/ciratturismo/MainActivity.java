package studio.entropia.ciratturismo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String user_coordinates;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private int id_category;
    private NavigationView navigationView;
    private static final Boolean debug = false;
    private static final String HomeFragment = "HomeFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        new SimpleEula(this).show();

        //navigationView es global ya que se utiliza para resaltar la opción de menú
        //desde LaunchFragment
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        saveImagesRemoteFolder();
        LaunchFragment();

        setSupportActionBar(toolbar);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    /**
     ** En caso de ser la primera vez que accedemos a la activity, lanza la presentación
     ** En caso de venir de LocationDetail, reemplaza el fragment, enviando datos y activa su opción de menú
     */
    public void LaunchFragment(){
        Bundle bundle = getIntent().getExtras();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (bundle != null){
            id_category = bundle.containsKey("id_category") ? bundle.getInt("id_category") : 0;
            user_coordinates = bundle.getString("user_coordinates");
            if (id_category>0){
                if (debug){
                    Log.i(getClass().getSimpleName(),"id_category: "+id_category);
                }
                switch (id_category){
                    case 1:
                        toolbar.setTitle(R.string.restauracion);
                        navigationView.getMenu().getItem(2).setChecked(true);
                        break;
                    case 2:
                        toolbar.setTitle(R.string.que_visitar);
                        navigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case 3:
                        toolbar.setTitle(R.string.dormir);
                        navigationView.getMenu().getItem(5).setChecked(true);
                        break;
                }
                LocationList fragment = new LocationList();
                Bundle args = new Bundle();
                args.putInt("id_category",id_category);
                args.putString("user_coordinates", user_coordinates);
                fragment.setArguments(args);
                ft.replace(R.id.content_frame, fragment);
            }
        }else{
            toolbar.setTitle(R.string.app_name);
            ft.replace(R.id.content_frame, new HomeFragment(),HomeFragment);
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            HomeFragment fragmentManagerFragmentById = (HomeFragment) fragmentManager.findFragmentByTag(HomeFragment);
            if (fragmentManagerFragmentById != null){
                if (fragmentManagerFragmentById.isVisible()){
                    Log.i(getClass().getSimpleName(),"HomeFragment visible");
                    super.onBackPressed();
                }
            }
            LaunchFragment();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();

        switch (id){
            case R.id.nav_visitar:
            case R.id.nav_restauracion:
            case R.id.nav_dormir:
            case R.id.nav_servicios:
                if (id == R.id.nav_visitar){
                    toolbar.setTitle(R.string.que_visitar);
                    id_category = 2;
                }else if (id == R.id.nav_restauracion){
                    toolbar.setTitle(R.string.restauracion);
                    id_category = 1;
                }else if (id == R.id.nav_dormir){
                    toolbar.setTitle(R.string.dormir);
                    id_category = 3;
                }else{
                    toolbar.setTitle(R.string.servicios);
                    id_category = 4;
                }

                LocationList fragment = new LocationList();
                Bundle args = new Bundle();
                args.putInt("id_category",id_category);

                if (user_coordinates != null){
                    args.putString("user_coordinates", user_coordinates);
                }

                fragment.setArguments(args);
                ft.replace(R.id.content_frame, fragment);
                break;
            case R.id.nav_rutas:
                toolbar.setTitle(R.string.rutas);
                ft.replace(R.id.content_frame, new TrackList());
                break;
            case R.id.nav_aventura:
                toolbar.setTitle(R.string.aventura);
                ft.replace(R.id.content_frame, new AventuraFragment());
                break;
            case R.id.nav_fiestas:
                toolbar.setTitle(R.string.fiestas);
                ft.replace(R.id.content_frame, new CelebrationList());
                break;
            case R.id.nav_tiempo:
                toolbar.setTitle(R.string.tiempo);
                ft.replace(R.id.content_frame, new ForecastFragment());
                break;
            case R.id.nav_share:
                Intent shareIntent = createShareIntent();
                startActivity(shareIntent);
                break;
            case R.id.nav_credits:
                toolbar.setTitle(R.string.creditos);
                ft.replace(R.id.content_frame, new CreditsFragment());
                break;

        }

        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Crea un intent para compartir socialmente nuestra aplicación
     */

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_application));
        return shareIntent;
    }

    /**
     * Almacena en las preferencias el nombre de la carpeta
     * dependiendo de la resolución.
     * Trabajamos con tres resoluciones (16:9)
     * xhdpi 1440 x 810 px
     * hdpi 720 x 405 px
     * mdpi 320 x 180 px
     */
    public void saveImagesRemoteFolder(){
        String folder;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        if (width > 800) {
            folder = "xhdpi/";
        }else if (width > 400){
            folder = "hdpi/";
        }else{
            folder = "mdpi/";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("resolution_folder",folder);
        editor.commit();
    }

    //La ubicación del usuario está disponible
    @Override
    public void onConnected(Bundle connectionHint) {
        Boolean hasPermission = true;
        //Comprobamos la API ya que a partir de la 23 hay que verificar si los permisos están aprobados
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //Permisos de ubicación
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }

        if (hasPermission){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (debug){
               Log.i(getClass().getSimpleName(),"mLastLocation:"+mLastLocation);
            }
            if (mLastLocation != null){
                user_coordinates = mLastLocation.getLatitude() +","+mLastLocation.getLongitude();
//                Toast.makeText(this,user_coordinates, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (debug){
            Log.i(getClass().getSimpleName(), "Location connection suspended.");
        }
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
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
