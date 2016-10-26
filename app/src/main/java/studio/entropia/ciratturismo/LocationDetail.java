package studio.entropia.ciratturismo;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.helper.Utility;
import studio.entropia.ciratturismo.data.cLocation;

/**
 * Created by javier on 25/05/16.
 */
public class LocationDetail extends AppCompatActivity {

    public static final String _ID = "id_location";
    public static final String distance = "distance_to_location";
    public cLocation mLocation = new cLocation();
    public cObjectLang mObjectLang = new cObjectLang();
    public static final String TAG = LocationDetail.class.getSimpleName();
    private Boolean debug = false;
    private int id_category;
    private static final int PERMISSIONS_CALL_PHONE = 0;
    private String mtelephone = "";
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_detail);

        mActivity = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        //Recogemos la información enviada con la id_location
        final Bundle extras = getIntent().getExtras();

        //Configuramos al action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_location);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        if (extras != null) {
            id_category = extras.getInt("id_category");

            switch (id_category) {
                case 1:
                    toolbar.setTitle(R.string.restauracion);
                    break;
                case 2:
                    toolbar.setTitle(R.string.que_visitar);
                    break;
                case 3:
                    toolbar.setTitle(R.string.dormir);
                    break;
                case 4:
                    toolbar.setTitle(R.string.servicios);
                    break;
            }
        }

        setSupportActionBar(toolbar);
        //El listener tiene que ser configurado después de setSupportActionBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("id_category",id_category);
                intent.putExtra("user_coordinates",extras.getString("user_coordinates"));
                startActivity(intent);
            }
        });

        //Recogemos la id_location enviada desde LocationList
        final int id_location = extras.getInt(_ID);
        getLocationDetail(id_location);

        if (debug){
            Log.i("Cirat","id_location:" + extras.getInt(_ID));
            Log.i("Cirat","Name cover: "+mLocation.getmLocationsLang().get(0).getName_cover()+
                  ". Description:"+mLocation.getmLocationsLang().get(0).getDescription()+". Image_cover: "+mLocation.getImage_cover());
        }

        ImageView image_cover = (ImageView) findViewById(R.id.location_image_cover);

        Picasso.with(this)
                .load(AppConstant.URL_IMAGES_LOCATIONS+
                      AppConstant.REMOTE_RESOLUTION_FOLDER+
                      mLocation.getImage_cover())
                .error(R.mipmap.error_image)
                .into(image_cover);

        TextView name = (TextView) findViewById(R.id.location_detail_headline);
        name.setText(mLocation.getmLocationsLang().get(0).getName());

        if (mLocation.getmLocationsLang().get(0).getDescription() != null){
            TextView description = (TextView) findViewById(R.id.location_description);
            description.setText(Html.fromHtml(mLocation.getmLocationsLang().get(0).getDescription()));
        }

        TextView images_quantity = (TextView) findViewById(R.id.images_quantity);
        String str_photo = getString(R.string.photos);

        if (mLocation.getImagesSum() > 1){
            str_photo += "s";
        }

        images_quantity.setText(mLocation.getImagesSum()+" "+str_photo);


        TextView distance_to_user = (TextView) findViewById(R.id.distance_to_user);
        if (debug){
            Log.i(TAG,"Address: "+mLocation.getAddress());
        }

        distance_to_user.setText("("+getString(R.string.a)+" "+extras.getString(LocationDetail.distance)+" km)");

        //Mostramos la dirección
        if (mLocation.getAddress() != null)
        {
            TextView location_address = (TextView) findViewById(R.id.location_address);
            location_address.setText(mLocation.getAddress());
            location_address.setVisibility(View.VISIBLE);
        }

        //Añadimos el botón de llamada
        addCallButton();

        ImageButton button_location_photo = (ImageButton) findViewById(R.id.button_location_photo);

        button_location_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_album = new Intent(getApplicationContext(), LocationImagesAlbum.class);
                intent_album.putExtra("id_location",id_location);
                startActivity(intent_album);
            }
        });

    }


    public void openMap(View view){
        Intent intent = new Intent(this, LocationMap.class);
        intent.putExtra("coordinates", mLocation.getCoordinates());
        intent.putExtra("name", mLocation.getmLocationsLang().get(0).getName());
        intent.putExtra("image_cover",mLocation.getImage_cover());
        intent.putExtra("address",mLocation.getAddress());
        intent.putExtra("id_location",mLocation.get_id_location());
        intent.putExtra("id_category",id_category);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LaunchPhoneCall();
                }
            }
        }
    }

    private void LaunchPhoneCall(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mtelephone));
        try{
            startActivity(callIntent);
        }catch (SecurityException se){
            throw se;
        }
    }

    /**
     * Añade el botón de llamada en caso de que el cLocation tenga uno asociado
     * Comprueba si la versión mínima es la 23 para confirmar permisos de llamada asignados
     */

    private void addCallButton(){

        //Confirmamos que esté asignado alguno de los campos de móvil o teléfono
        if (mLocation.getPhone() != null || mLocation.getMobile() != null){
            String telephone = null;
            if (mLocation.getMobile() != null){
                telephone = mLocation.getMobile().length() >= 9 ? mLocation.getMobile() : null;
            }else if (mLocation.getPhone() != null){
                telephone = mLocation.getPhone().length() >= 9 ? mLocation.getPhone() : null;
            }

            if (telephone != null){
                mtelephone = telephone;
                //Insertamos el botón de llamada
                LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
                ImageButton button = new ImageButton(this);
                button.setBackgroundResource(R.drawable.button_call_phone);

                //Instanciamos un objeto para asignar los márgenes al botón
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.gravity = Gravity.CENTER_VERTICAL;
                //Convertimos los márgenes dp a pixels ya que setMargins recibe px
                int dpValue = 8;
                params.setMargins(0, 0, Utility.getDpFromPx(getApplicationContext(),dpValue), 0);

                //Añadimos el número en texto
                TextView telephone_textview = new TextView(this);
                telephone_textview.setText(telephone);
                telephone_textview.setLayoutParams(params);
                telephone_textview.setTextSize(12);
                telephone_textview.setTextColor(getResources().getColor(R.color.colorTextSmall));

                //Añadimos las vistas al container
                buttonContainer.addView(telephone_textview);
                buttonContainer.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT >= 23){
                        //Comprobación de permisos
                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //Solicitamos el permiso para llamar
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    PERMISSIONS_CALL_PHONE);
                        }
                    }else{
                        LaunchPhoneCall();
                    }
                    }
                });
            }
        }
    }

    private void getLocationDetail(int id_location)
    {
        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getBaseContext());

        try {

            //Número de imágenes por cLocation
            String SELECT_SQL_NUM_PHOTOS = "SELECT COUNT(li._id) as numPhotos, li.id_location\n" +
                    "FROM location_image li\n" +
                    "JOIN image i ON i._id = li.id_image\n" +
                    "WHERE i.active = 1\n" +
                    "GROUP BY li.id_location";

            Cursor c_images = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL_NUM_PHOTOS, null);

            String SELECT_SQL = "SELECT l._id as id_location,ll._id,coordinates,name,phone,mobile," +
                    "address,email,url,name_cover, file, description \n" +
                    "FROM location l \n" +
                    "INNER JOIN location_lang ll ON l._id = ll.id_location \n" +
                    "INNER JOIN location_image li ON ll.id_location = li.id_location \n" +
                    "INNER JOIN image i ON i._id = li.id_image \n" +
                    "WHERE l._id = "+id_location+" AND id_lang = 2 AND cover = 1 AND l.active = 1 \n";

            if (debug){
                Log.i("SELECT_SQL:","SELECT_SQL:"+SELECT_SQL);
            }


            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {

                        mLocation.setId_location(c.getInt(c.getColumnIndex("id_location")));
                        mLocation.setImage_cover(c.getString(c.getColumnIndex("file")));
                        mLocation.setPhone(c.getString(c.getColumnIndex("phone")));
                        mLocation.setMobile(c.getString(c.getColumnIndex("mobile")));
                        mLocation.setAddress(c.getString(c.getColumnIndex("address")));
                        mLocation.setEmail(c.getString(c.getColumnIndex("email")));
                        mLocation.setUrl(c.getString(c.getColumnIndex("url")));
                        mLocation.setCoordinates(c.getString(c.getColumnIndex("coordinates")));
                        mLocation.setAddress(c.getString(c.getColumnIndex("address")));
                        mLocation.setImagesSum(sumImagesToLocation(c_images,c.getInt(c.getColumnIndex("id_location"))));

                        mObjectLang.setName(c.getString(c.getColumnIndex("name")));
                        mObjectLang.setDescription(c.getString(c.getColumnIndex("description")));

                        if (debug){
                            Log.i(getClass().getSimpleName(),"name:"+c.getString(c.getColumnIndex("name")));
                            Log.i(getClass().getSimpleName(),"description:"+c.getString(c.getColumnIndex("description")));
                        }

                        List<cObjectLang> mObjectLangList = new ArrayList<>();
                        mObjectLangList.add(mObjectLang);

                        mLocation.setmLocationsLang(mObjectLangList);

                    }while (c.moveToNext());
                }
            }

            c.close();
        } catch (SQLiteException sqle) {
            if (debug){
                Log.e(getClass().getSimpleName(), "Could not create or Open the database");
            }
        }
        myDbHelper.close();
    }

    /**
     *
     * @param c_images Cursor con el número de imágenes de todos los cLocation
     * @return numPhotos
     */

    private int sumImagesToLocation(Cursor c_images, int id_location) {
        int numPhotos = 0;
        //Recorremos el cursor buscando la id_location enviada
        if (c_images != null) {
            if (c_images.moveToFirst()){
                do{
                    if (id_location == c_images.getInt(c_images.getColumnIndex("id_location"))){
                        numPhotos = c_images.getInt(c_images.getColumnIndex("numPhotos"));
                    }
                } while (c_images.moveToNext());
            }
        }
        return (numPhotos);
    }

}
