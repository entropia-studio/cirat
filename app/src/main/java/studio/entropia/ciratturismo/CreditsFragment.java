package studio.entropia.ciratturismo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreditsFragment extends Fragment {

    private final String htmlText = "<p>Aplicación desarrollada por<br/><br/><strong>Entropia Studio</strong><br/>\n" +
            "C/San Antonio 18 Bajo<br/>46110 - Godella (Valencia)<br/>\n" +
            "<a href=\"mailto:info@entropia.studio\">info@entropia.studio</a> - Tel.: 963.565.55<br/></p>\n" +
            "<p>Toda la información sobre las rutas e imágenes mostradas han sido recopiladas por <strong>José Tomás Izquierdo</strong><br/>\n" +
            "<a href=\"http://fotorutascomunidadvalenciana.blogspot.com.es\">fotorutascomunidadvalenciana.blogspot.com.es</a></p>\n" +
            "<p>Información sobre fiestas y eventos facilitada por <strong>Daniel Ponce</strong>\n" +
            "<a href=\"http://danielponcenadador.blogspot.com.es/\">danielponcenadador.blogspot.com.es</a><br/>\n" +
            "</p>";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.credits_content, container, false);

        TextView description = (TextView) v.findViewById(R.id.description);
        description.setText(Html.fromHtml(htmlText));
        return v;
    }
}
