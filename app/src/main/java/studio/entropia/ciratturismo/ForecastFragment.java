package studio.entropia.ciratturismo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import studio.entropia.ciratturismo.data.AemetXmlParser;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.cForecastDay;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment {

    private ArrayList<cForecastDay> forecastDays = new ArrayList<>();

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.forecast_fragment, container, false);
        loadData();
        return rootView;
    }

    public void loadData(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadXmlTask().execute(AppConstant.URL_AMET);
        } else {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.connection_error),
                    Toast.LENGTH_LONG).show();
        }

    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Get a reference to the ListView, and attach this adapter to it.
            ListView listview_aemet = (ListView) getView().findViewById(R.id.listview_aemet);
            AEMETAdapter adapter = new AEMETAdapter(getActivity(), forecastDays);
            listview_aemet.setAdapter(adapter);
        }
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
// HTML markup. Returns HTML string.
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        AemetXmlParser aemetXmlParser = new AemetXmlParser();

        try {
            stream = downloadUrl(urlString);
            forecastDays = aemetXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return "ok";
    }

    public class AEMETAdapter extends ArrayAdapter<cForecastDay> {

        public AEMETAdapter(Context context, ArrayList<cForecastDay> forecastDays){
            super(context,R.layout.list_item_forecast,forecastDays);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int layoutId = -1;
            switch (position){
                case 0:
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                default:
                    layoutId = R.layout.list_item_forecast;
                    break;
            }

            View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

            TextView list_item_forecast_textview = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            list_item_forecast_textview.setText(forecastDays.get(position).getEstado_cielo());


            TextView list_item_date_textview = (TextView) view.findViewById(R.id.list_item_date_textview);
            list_item_date_textview.setText(forecastDays.get(position).getFecha());

            TextView list_item_high_textview = (TextView) view.findViewById(R.id.list_item_high_textview);
            list_item_high_textview.setText(forecastDays.get(position).getTemperatura_maxima()+"ºC");

            TextView list_item_low_textview = (TextView) view.findViewById(R.id.list_item_low_textview);
            list_item_low_textview.setText(forecastDays.get(position).getTemperatura_minima()+"ºC");

            ImageView list_item_icon = (ImageView) view.findViewById(R.id.list_item_icon);
            int idResource = getResources().getIdentifier(forecastDays.get(position).getImg_resource(),"drawable",getActivity().getPackageName());
            list_item_icon.setImageResource(idResource);

            return (view);
        }
    }


    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}