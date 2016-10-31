package studio.entropia.ciratturismo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import studio.entropia.ciratturismo.helper.AppConstant;


public class HomeFragment extends Fragment {

    private ArrayList<String> mImages = new ArrayList<>();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private static final Boolean debug = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.home_content, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        TextView home_description = (TextView) v.findViewById(R.id.home_description);
        home_description.setText(Html.fromHtml(AppConstant.htmlTextHome));

        mImages.add("valle_cirat.jpg");
        mImages.add("zona_bano.jpg");
        mImages.add("salto_novia_01.jpg");
        mImages.add("torre_del_conde.jpg");
        mImages.add("castillo.jpg");
        mImages.add("iglesia_san_bernardo.jpg");


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) v.findViewById(R.id.main_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        return v;
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
                    AppConstant.URL_IMAGES_HOME+
                    AppConstant.REMOTE_RESOLUTION_FOLDER+
                    mImages.get(position));

            if (debug){
                Log.i(getClass().getSimpleName(),
                        "image_file:"+AppConstant.URL_IMAGES_HOME+AppConstant.REMOTE_RESOLUTION_FOLDER+mImages.get(position));
            }

            Fragment ScreenSlidePageFragment = new DetailFragment();
            ScreenSlidePageFragment.setArguments(bundle);
            return ScreenSlidePageFragment;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }
    }
}
