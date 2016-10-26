package studio.entropia.ciratturismo;


import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.cCelebration;

public class CelebrationDetail extends AppCompatActivity {

    public static final String _ID = "id_celebration";
    public cCelebration mCelebration = new cCelebration();
    public cObjectLang mObjectLang = new cObjectLang();
    public static final String TAG = CelebrationDetail.class.getSimpleName();
    private Boolean debug = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.celebration_detail);

        //Recogemos la información enviada con la id_celebration
        final Bundle extras = getIntent().getExtras();

        //Configuramos al action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_location);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle(R.string.fiestas);

        setSupportActionBar(toolbar);
        //El listener tiene que ser configurado después de setSupportActionBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        //Recogemos la id_celebration enviada desde LocationList
        final int id_celebration = extras.getInt(_ID);
        getCelebrationDetail(id_celebration);

        if (debug){
            Log.i("Cirat","id_celebration:" + extras.getInt(_ID));
        }

        ImageView image_cover = (ImageView) findViewById(R.id.image_cover);

        Picasso.with(this)
                .load(AppConstant.URL_IMAGES_CELEBRATIONS+
                      AppConstant.REMOTE_RESOLUTION_FOLDER+
                      mCelebration.getImage_cover())
                .error(R.mipmap.error_image)
                .into(image_cover);

        TextView name_celebration = (TextView) findViewById(R.id.name_celebration);
        name_celebration.setText(mCelebration.getmCelebrationsLang().get(0).getName());

        TextView date_text = (TextView) findViewById(R.id.date_text);
        String del = getResources().getString(R.string.del);
        String al = getResources().getString(R.string.al);
        String date_start = mCelebration.getDate_start();
        String date_end = mCelebration.getDate_end();
        date_text.setText(del+" "+date_start+" "+al+" "+date_end);


        TextView description_celebration = (TextView) findViewById(R.id.celebration_description);
        description_celebration.setText(Html.fromHtml(mCelebration.getmCelebrationsLang().get(0).getDescription()));
    }


    private void getCelebrationDetail(int id_celebration)
    {
        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getBaseContext());

        try {

            String SELECT_SQL = "SELECT c._id as id_celebration,cl._id,name,description,file,date_start,date_end \n" +
                    "FROM celebration c\n" +
                    "INNER JOIN celebration_lang cl ON c._id = cl.id_celebration\n" +
                    "INNER JOIN celebration_image ci ON cl.id_celebration = ci.id_celebration\n" +
                    "INNER JOIN image i ON i._id = ci.id_image \n" +
                    "WHERE id_lang = 2 AND ci.cover = 1 AND c._id = "+id_celebration;


            if (debug){
                Log.i("Cirat",SELECT_SQL);
            }


            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {

                        String date_start = c.getString(c.getColumnIndex("date_start"));
                        String date_end = c.getString(c.getColumnIndex("date_end"));
                        Date date_start_formatted = null;
                        Date date_end_formatted = null;

                        try{
                            //Comprobamos si la fecha es mayor o igual a hoy
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            date_start_formatted = sdf.parse(date_start);
                            date_end_formatted = sdf.parse(date_end);
                            SimpleDateFormat sdf_start = new SimpleDateFormat("dd");
                            date_start = sdf_start.format(date_start_formatted);
                            SimpleDateFormat sdf_end = new SimpleDateFormat("dd MMMM");
                            date_end = sdf_end.format(date_end_formatted);
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }


                        mCelebration.setDate_start(date_start);
                        mCelebration.setDate_end(date_end);
                        mCelebration.setImage_cover(c.getString(c.getColumnIndex("file")));

                        mObjectLang.setName(c.getString(c.getColumnIndex("name")));
                        mObjectLang.setDescription(c.getString(c.getColumnIndex("description")));

                        List<cObjectLang> mObjectLangList = new ArrayList<>();
                        mObjectLangList.add(mObjectLang);

                        mCelebration.setmCelebrationsLang(mObjectLangList);


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

    private int sumImagesToLocation(Cursor c_images, int id_celebration) {
        int numPhotos = 0;
        //Recorremos el cursor buscando la id_celebration enviada
        if (c_images != null) {
            if (c_images.moveToFirst()){
                do{
                    if (id_celebration == c_images.getInt(c_images.getColumnIndex("id_celebration"))){
                        numPhotos = c_images.getInt(c_images.getColumnIndex("numPhotos"));
                    }
                } while (c_images.moveToNext());
            }
        }
        return (numPhotos);
    }

}
