package studio.entropia.ciratturismo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.cTrack;

public class TrackDetail extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<String> mImages = new ArrayList<>();
    private cTrack mTrack = new cTrack();
    private final Boolean debug = false;
    private int id_track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_track_detail);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        id_track = extras.getInt("id_track");

        getActivityData(id_track);

        TextView track_name = (TextView) findViewById(R.id.track_name);
        TextView track_distance = (TextView) findViewById(R.id.track_distance);
        TextView total_time = (TextView) findViewById(R.id.total_time);
        TextView is_circular = (TextView) findViewById(R.id.is_circular);
        TextView positive_gradient = (TextView) findViewById(R.id.positive_gradient);
        TextView negative_gradient = (TextView) findViewById(R.id.negative_gradient);
        TextView max_altitude = (TextView) findViewById(R.id.max_altitude);
        TextView min_altitude = (TextView) findViewById(R.id.min_altitude);
        TextView track_description = (TextView) findViewById(R.id.track_description);

        track_name.setText(mTrack.getmTracksLang().get(0).getName());
        track_distance.setText(mTrack.getDistance().toString()+" km");
        total_time.setText(mTrack.getTotal_time().toString()+" h");
        if (mTrack.getIs_circular() == 0){
            is_circular.setText(getString(R.string.no));
        }else{
            is_circular.setText(getString(R.string.si));
        }
        positive_gradient.setText(getString(R.string.positive_gradient)+" "+mTrack.getPositive_gradient()+" m");
        negative_gradient.setText(getString(R.string.negative_gradient)+" "+mTrack.getNegative_gradient()+" m");
        max_altitude.setText(getString(R.string.max_altitude)+": "+mTrack.getMax_altitude()+" m");
        min_altitude.setText(getString(R.string.min_altitude)+": "+mTrack.getMin_altitude()+" m");
        track_description.setText(Html.fromHtml(mTrack.getmTracksLang().get(0).getDescription()));
        track_description.setMovementMethod(LinkMovementMethod.getInstance());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TrackMap.class);
                intent.putExtra("id_track",id_track);
                intent.putExtra("center_coordinates",mTrack.getCenter_coordinates());
                intent.putExtra("kml",mTrack.getKml());
                startActivity(intent);

            }
        });

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager_track);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("totalPages",mImages.size());
            bundle.putInt("page",position);
            bundle.putString("image_file",
                    AppConstant.URL_IMAGES_TRACKS+
                    AppConstant.REMOTE_RESOLUTION_FOLDER+
                    mImages.get(position));
            Fragment ScreenSlidePageFragment = new DetailFragment();
            ScreenSlidePageFragment.setArguments(bundle);
            return ScreenSlidePageFragment;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

    }

    private void getActivityData(int _ID)
    {
        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(this);

        try {

            String SELECT_SQL = "SELECT t._id as id_track,name,distance,is_circular,total_time,\n" +
                                "positive_gradient,negative_gradient,max_altitude,min_altitude,description,kml,center_coordinates \n" +
                                "FROM track t \n" +
                                "INNER JOIN track_lang tl ON t._id = tl.id_track \n" +
                                "WHERE id_lang = 2 AND id_track = "+_ID;

            if (debug){
                Log.i(getClass().getSimpleName(),SELECT_SQL);
            }

            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        mTrack.setId_track(c.getInt(c.getColumnIndex("id_track")));
                        mTrack.setDistance(c.getString(c.getColumnIndex("distance")));
                        mTrack.setTotal_time(c.getString(c.getColumnIndex("total_time")));
                        mTrack.setPositive_gradient(c.getInt(c.getColumnIndex("positive_gradient")));
                        mTrack.setNegative_gradient(c.getInt(c.getColumnIndex("negative_gradient")));
                        mTrack.setMax_altitude(c.getInt(c.getColumnIndex("max_altitude")));
                        mTrack.setMin_altitude(c.getInt(c.getColumnIndex("min_altitude")));
                        mTrack.setIs_circular(c.getInt(c.getColumnIndex("is_circular")));
                        mTrack.setKml(c.getString(c.getColumnIndex("kml")));
                        mTrack.setCenter_coordinates(c.getString(c.getColumnIndex("center_coordinates")));

                        List<cObjectLang> mObjectLangList = new ArrayList<>();
                        cObjectLang mObjectLang = new cObjectLang();
                        mObjectLang.setName(c.getString(c.getColumnIndex("name")));
                        mObjectLang.setDescription(c.getString(c.getColumnIndex("description")));

                        mObjectLangList.add(mObjectLang);
                        mTrack.setmTracksLang(mObjectLangList);

                    }while (c.moveToNext());
                }
            }

            //Imáganes para este track
            String SELECT_SQL_IMAGES = "SELECT i._id,file \n" +
                    "FROM track_image ti \n" +
                    "JOIN image i ON i._id = ti.id_image \n" +
                    "WHERE ti.id_track = "+_ID+" AND i.active = 1 \n" +
                    "ORDER BY position";

            if (debug){
                Log.i(getClass().getSimpleName(),SELECT_SQL_IMAGES);
            }

            Cursor c_images = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL_IMAGES, null);

            if (c_images != null) {
                if  (c_images.moveToFirst()) {
                    do {
                        mImages.add(c_images.getString(c_images.getColumnIndex("file")));

                    }while (c_images.moveToNext());
                }
            }
            c_images.close();
            c.close();
        } catch (SQLiteException sqle) {
            if (debug){
                Log.e(getClass().getSimpleName(), "Could not create or Open the database");
            }
        }
        myDbHelper.close();
    }

}
