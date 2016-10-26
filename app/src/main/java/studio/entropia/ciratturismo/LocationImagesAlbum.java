package studio.entropia.ciratturismo;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import java.util.ArrayList;

import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.menu.ScreenSlidePageFragment;

public class LocationImagesAlbum extends FragmentActivity {

    private ArrayList<String> mFiles = new ArrayList<>();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int id_location;
    private Boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_images_album);

        //Recogemos la información enviada desde LocationDetail
        Bundle extras = getIntent().getExtras();
        id_location = extras.getInt("id_location");
        openAndQueryDatabase(id_location);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
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
            bundle.putInt("totalPages",mFiles.size());
            bundle.putInt("page",position);
            bundle.putString("image_file",mFiles.get(position));
            Fragment ScreenSlidePageFragment = new ScreenSlidePageFragment();
            ScreenSlidePageFragment.setArguments(bundle);
            return ScreenSlidePageFragment;
        }

        @Override
        public int getCount() {
            return mFiles.size();
        }

    }

    private void openAndQueryDatabase(int id_location) {

        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(this);

        try {
            String SELECT_SQL = "SELECT i._id,file \n" +
                    "FROM location_image li \n" +
                    "JOIN image i ON i._id = li.id_image \n" +
                    "WHERE li.id_location = "+id_location+" AND i.active = 1 \n" +
                    "ORDER BY position";
            if (debug){
                Log.i(getClass().getSimpleName(),SELECT_SQL);
            }
            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String location_file = c.getString(c.getColumnIndex("file"));
                        mFiles.add(location_file);
                    }while (c.moveToNext());
                }
            }
            c.close();
        } catch (SQLiteException sqle) {
            if (debug){
                Log.e(getClass().getSimpleName(), "No se puede abrir la base de datos");
            }
        }
    }

}
