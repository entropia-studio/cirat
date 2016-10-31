package studio.entropia.ciratturismo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import studio.entropia.ciratturismo.helper.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.viewpager_content, container,false);

        Bundle bundle = this.getArguments();
        int totalPages = bundle.getInt("totalPages");
        int page = bundle.getInt("page");

        ImageView imageView = (ImageView) rootView.findViewById(R.id.track_image);

        Picasso.with(getContext())
                .load(bundle.getString("image_file"))
                .error(R.mipmap.error_image)
                .into(imageView);

        LinearLayout page_indicator = (LinearLayout) rootView.findViewById(R.id.page_indicator);

        //Instanciamos un objeto para asignar los márgenes a las ImageView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER_VERTICAL;
        //Convertimos los márgenes dp a pixels ya que setMargins recibe px
        int dpValue = 6;
        params.setMargins(Utility.getDpFromPx(getActivity(),dpValue), 0, 0 , 0);

        if (totalPages>1){
            for (int i = 0;i<totalPages;i++){
                //Círculos, hay que instarciarlos dentro del bucle
                ImageView dot = new ImageView(getActivity());
                ImageView dot_selected = new ImageView(getActivity());

                dot.setLayoutParams(params);
                dot_selected.setLayoutParams(params);

                //Dependiendo de la versión se hace un tipo de asignación u otra
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dot.setImageDrawable(getResources().getDrawable(R.drawable.dot_non_selected, getContext().getTheme()));
                    dot_selected.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected, getContext().getTheme()));
                }else{
                    dot.setImageDrawable(getResources().getDrawable(R.drawable.dot_non_selected));
                    dot_selected.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
                }
                if (page == i){
                    page_indicator.addView(dot_selected);
                }else{
                    page_indicator.addView(dot);
                }
            }
        }
        return rootView;
    }
}
