package studio.entropia.ciratturismo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import studio.entropia.ciratturismo.helper.AppConstant;

public class AventuraFragment extends Fragment {

    private ArrayList<String> mImages = new ArrayList<>();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.aventura_content, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        TextView home_description = (TextView) v.findViewById(R.id.home_description);
        home_description.setText(Html.fromHtml(AppConstant.htmlTextAventura));

        mImages.add("rafting.jpg");
        mImages.add("hidrospeed.jpg");
        mImages.add("barranco_acuatico.jpg");
        mImages.add("kayak_rio.jpg");
        mImages.add("tirolina.jpg");
        mImages.add("paintball.jpg");
        mImages.add("barranco_seco.jpg");
        mImages.add("espeleologia.jpg");

        // Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) v.findViewById(R.id.main_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstant.URL_AVENTURA));
                startActivity(browserIntent);

            }
        });

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
                    AppConstant.URL_IMAGES_AVENTURA+
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
}
