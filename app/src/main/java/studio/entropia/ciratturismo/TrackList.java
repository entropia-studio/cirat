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
import java.util.List;

import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.data.cTrack;

public class TrackList extends Fragment {

    private ArrayList<cTrack> cTracks = new ArrayList<>();
    private ListView mListView;
    private Boolean debug = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.listview, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Extraemos la carpeta de resolución para las imágenes
        AppConstant.REMOTE_RESOLUTION_FOLDER = prefs.getString("resolution_folder","");

        openAndQueryDatabase();

        mListView = (ListView) v.findViewById(R.id.mListView);

        myAdapter adapter = new myAdapter(getActivity(), cTracks);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), TrackDetail.class);
                intent.putExtra("id_track", cTracks.get(position).getId_track());
                startActivity(intent);
            }
        });

        return v;

    }

    public class myAdapter extends ArrayAdapter<cTrack> {

        public myAdapter(Context context, ArrayList<cTrack> cTracks){
            super(context,R.layout.track_list_item,cTracks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.track_list_item,null);

            TextView name_cover = (TextView) view.findViewById(R.id.track_name);
            name_cover.setText(cTracks.get(position).getmTracksLang().get(0).getName());

            ImageView img_track = (ImageView) view.findViewById(R.id.img_track);

            if (debug){
                Log.i(getClass().getSimpleName(),"img_track: "+AppConstant.URL_IMAGES_TRACKS+cTracks.get(position).getImage_cover());
            }

            Picasso.with(getContext())
                    .load(AppConstant.URL_IMAGES_TRACKS+
                          AppConstant.REMOTE_RESOLUTION_FOLDER+
                          cTracks.get(position).getImage_cover())
                    .error(R.mipmap.error_image)
                    .into(img_track);

            TextView track_distance = (TextView) view.findViewById(R.id.track_distance);
            track_distance.setText(cTracks.get(position).getDistance()+" km");

            TextView is_circular = (TextView) view.findViewById(R.id.is_circular);
            if (cTracks.get(position).getIs_circular() == 1){
                is_circular.setText(getString(R.string.si));
            }else{
                is_circular.setText(getString(R.string.no));
            }
            return (view);
        }
    }

    private void openAndQueryDatabase() {

        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getActivity());

        try {
            String SELECT_SQL = "SELECT t._id as id_track,name,distance,is_circular,total_time,file \n" +
                                "FROM track t \n" +
                                "INNER JOIN track_lang tl ON t._id = tl.id_track \n" +
                                "INNER JOIN track_image ti ON tl.id_track = ti.id_track \n" +
                                "INNER JOIN image i ON i._id = ti.id_image \n" +
                                "WHERE tl.id_lang = 2 AND ti.cover = 1 AND t.active = 1 \n" +
                                "ORDER BY t.position\n";
            if (debug){
                Log.i(getClass().getSimpleName(),"SQL: "+SELECT_SQL);
            }

            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                Log.i(getClass().getSimpleName(),"line 126");
                if  (c.moveToFirst()) {
                    Log.i(getClass().getSimpleName(),"line 128");
                    do {
                        Log.i(getClass().getSimpleName(),"line 130");
                        int _id = c.getInt(c.getColumnIndex("id_track"));
                        String name = c.getString(c.getColumnIndex("name"));
                        String distance = c.getString(c.getColumnIndex("distance"));
                        int is_circular = c.getInt(c.getColumnIndex("is_circular"));
                        String total_time = c.getString(c.getColumnIndex("total_time"));
                        String image_cover = c.getString(c.getColumnIndex("file"));

                        //Creamos nuevo objeto track
                        cTrack mTrack = new cTrack();
                        mTrack.setId_track(_id);

                        mTrack.setDistance(distance);
                        mTrack.setIs_circular(is_circular);
                        mTrack.setTotal_time(total_time);
                        mTrack.setImage_cover(image_cover);

                        List<cObjectLang> mObjectLangList = new ArrayList<>();
                        cObjectLang mObjectLang = new cObjectLang();
                        mObjectLang.setName(name);

                        mObjectLangList.add(mObjectLang);
                        mTrack.setmTracksLang(mObjectLangList);

                        cTracks.add(mTrack);
                        if (debug){
                            Log.i(getClass().getSimpleName(),"id_track: "+_id+". image: "+image_cover);
                        }

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
}
