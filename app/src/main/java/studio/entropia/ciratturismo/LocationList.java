package studio.entropia.ciratturismo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.helper.Utility;
import studio.entropia.ciratturismo.data.cLocation;

public class LocationList extends Fragment {

    private ArrayList<cLocation> cLocations = new ArrayList<>();
    private ListView list_que_visitar;
    private Context mContext;
    private int _ID;
    private int id_category;
    private String user_coordinates;
    private Boolean debug = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.listview, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        mContext = getActivity();
        id_category = getArguments().getInt("id_category");
        user_coordinates = getArguments().getString("user_coordinates");
        if (debug){
            Log.i(getClass().getSimpleName(),"muser_coordinates: "+user_coordinates);
        }
        //Extraemos los datos de los puntos de interés
        openAndQueryDatabase(id_category);

        list_que_visitar = (ListView) v.findViewById(R.id.mListView);

        LocationAdapter adapter = new LocationAdapter(getActivity(), cLocations);

        list_que_visitar.setAdapter(adapter);

        list_que_visitar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                _ID = ((cLocation)a.getItemAtPosition(position)).get_id_location();

                Intent intent = new Intent(mContext, LocationDetail.class);
                intent.putExtra(LocationDetail._ID, _ID);
                intent.putExtra("id_category", id_category);
                intent.putExtra("user_coordinates",user_coordinates);
                String distance_to_user = Utility.formatDistance(((cLocation)a.getItemAtPosition(position)).getDistance_to_user());
                intent.putExtra(LocationDetail.distance,distance_to_user);
                startActivity(intent);

            }
        });

        return v;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }

    public static LocationList newInstance(Bundle arguments){
        LocationList f = new LocationList();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }


    public class LocationAdapter extends ArrayAdapter<cLocation> {

        public LocationAdapter(Context context, ArrayList<cLocation> cLocations){
            super(context,R.layout.location_list_item, cLocations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.location_list_item,null);

            TextView name_cover = (TextView) view.findViewById(R.id.name_cover);
            name_cover.setText(cLocations.get(position).getmLocationsLang().get(0).getName_cover());

            ImageView img_que_visitar = (ImageView) view.findViewById(R.id.img_location);

            if (debug){
                Log.i(getClass().getSimpleName(),"URL: "+AppConstant.URL_IMAGES+ cLocations.get(position).getImage_cover()+".png");
            }

            Picasso.with(getContext())
                    .load(AppConstant.URL_IMAGES_LOCATIONS+
                          AppConstant.REMOTE_RESOLUTION_FOLDER+
                            cLocations.get(position).getImage_cover())
                    .error(R.mipmap.error_image)
                    .into(img_que_visitar);

            TextView distancia_punto = (TextView) view.findViewById(R.id.distancia_al_punto);
            distancia_punto.setText(Utility.formatDistance(cLocations.get(position).getDistance_to_user())+" km");

            return (view);
        }
    }


    /**
     * Open the database and extract data
     */

    private void openAndQueryDatabase(int id_category) {

        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getActivity());

        try {
            String SELECT_SQL = "SELECT l._id as id_location,ll._id,coordinates,name_cover, file\n" +
                    "FROM location l \n" +
                    "INNER JOIN location_lang ll ON l._id = ll.id_location \n" +
                    "INNER JOIN location_image li ON ll.id_location = li.id_location \n" +
                    "INNER JOIN image i ON i._id = li.id_image \n" +
                    "WHERE id_category = "+id_category+" AND id_lang = 2 AND cover = 1 AND l.active = 1 \n" +
                    "GROUP BY l._id \n" +
                    "ORDER BY l._id";
            if (debug){
                Log.i("Cirat",SELECT_SQL);
            }

            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        int _id = c.getInt(c.getColumnIndex("id_location"));
                        String name_cover = c.getString(c.getColumnIndex("name_cover"));
                        String location_coordinates = c.getString(c.getColumnIndex("coordinates"));
                        String img_que_visitar = c.getString(c.getColumnIndex("file"));

                        //Creamos nuevo objeto cLocation
                        cLocation mLocation = new cLocation();

                        mLocation.setId_location(_id);
                        mLocation.setCoordinates(location_coordinates);
                        mLocation.setImage_cover(img_que_visitar);

                        List<cObjectLang> mObjectLangList = new ArrayList<>();

                        cObjectLang mObjectLang = new cObjectLang();
                        mObjectLang.setName_cover(name_cover);

                        mObjectLangList.add(mObjectLang);

                        mLocation.setmLocationsLang(mObjectLangList);

                        if (user_coordinates == null){
                            user_coordinates = location_coordinates;
                        }

                        //Almacenamos en el objeto la distancia del usuario al punto
                        mLocation.setDistance_to_user(Utility.calculateDistance(location_coordinates,user_coordinates));
                        cLocations.add(mLocation);
                        if (debug){
                            Log.i("cLocations","Name: "+name_cover+", Coordinates: "+location_coordinates);
                        }

                    }while (c.moveToNext());
                    orderLocationsByDistanceToUser(cLocations);
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
     * Ordenamos las locations por distancia al usuario
     * @param cLocations
     */

    public void orderLocationsByDistanceToUser(ArrayList cLocations){
        Collections.sort(cLocations, new Comparator<cLocation>() {
            @Override
            public int compare(cLocation c1, cLocation c2) {
                return Double.compare(c1.getDistance_to_user(), c2.getDistance_to_user());
            }
        });
    }
}

