package studio.entropia.ciratturismo.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import studio.entropia.ciratturismo.R;
import studio.entropia.ciratturismo.helper.AppConstant;

public class ScreenSlidePageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.location_pager_item, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        Bundle bundle = this.getArguments();

        int page = bundle.getInt("page")+1;
        int totalPages = bundle.getInt("totalPages");

        TextView current_image = (TextView) rootView.findViewById(R.id.image_footer);
        current_image.setText(page+" "+getResources().getString(R.string.de)+" "+totalPages);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getContext())
                .load(AppConstant.URL_IMAGES_LOCATIONS+
                      AppConstant.REMOTE_RESOLUTION_FOLDER+
                      bundle.getString("image_file"))
                .into(imageView);

        return rootView;
    }

}
