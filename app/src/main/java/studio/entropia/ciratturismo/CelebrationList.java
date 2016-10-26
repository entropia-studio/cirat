package studio.entropia.ciratturismo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import studio.entropia.ciratturismo.data.CiratDBHelper;
import studio.entropia.ciratturismo.data.cObjectLang;
import studio.entropia.ciratturismo.helper.AppConstant;
import studio.entropia.ciratturismo.helper.Utility;
import studio.entropia.ciratturismo.data.cCelebration;

public class CelebrationList extends Fragment {

    private ArrayList<cCelebration> cCelebrations = new ArrayList<>();
    private ListView mListView;
    private Boolean debug = false;
    public static final String TAG = CelebrationList.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.listview, container, false);

        openAndQueryDatabase();

        mListView = (ListView) v.findViewById(R.id.mListView);

        myAdapter adapter = new myAdapter(getActivity(),cCelebrations);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), CelebrationDetail.class);
                intent.putExtra("id_celebration", cCelebrations.get(position).getId_celebration());
                startActivity(intent);
            }
        });

        return v;

    }

    public class myAdapter extends ArrayAdapter<cCelebration> {

        public myAdapter(Context context, ArrayList<cCelebration> cCelebrations){
            super(context,R.layout.celebration_list_item,cCelebrations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.celebration_list_item,null);

            TextView celebration_name = (TextView) view.findViewById(R.id.name_celebration);
            celebration_name.setText(cCelebrations.get(position).getmCelebrationsLang().get(0).getName());


            TextView date_text = (TextView) view.findViewById(R.id.date_text);
            String date_start = cCelebrations.get(position).getDate_start();
            String date_end = cCelebrations.get(position).getDate_end();

            if (date_start != null){
                date_text.setText(Utility.capitalize(getResources().getString(R.string.del)) + " "
                        + date_start + " " + getResources().getString(R.string.al) + " "
                        + date_end);
            }else{
                date_text.setText(getResources().getString(R.string.el)
                    + " " + date_end);
            }

            ImageView img_celebration = (ImageView) view.findViewById(R.id.img_celebration);

            Picasso.with(getContext())
                    .load(AppConstant.URL_IMAGES_CELEBRATIONS+
                          AppConstant.REMOTE_RESOLUTION_FOLDER+
                          cCelebrations.get(position).getImage_cover())
                    .error(R.mipmap.error_image)
                    .into(img_celebration);

            return (view);
        }
    }

    private void openAndQueryDatabase() {

        CiratDBHelper myDbHelper;
        myDbHelper = new CiratDBHelper(getActivity());

        try {
            String SELECT_SQL = "SELECT c._id as id_celebration,cl._id,name,description,file,date_start,date_end \n" +
                    "FROM celebration c\n" +
                    "INNER JOIN celebration_lang cl ON c._id = cl.id_celebration\n" +
                    "INNER JOIN celebration_image ci ON cl.id_celebration = ci.id_celebration\n" +
                    "INNER JOIN image i ON i._id = ci.id_image \n" +
                    "WHERE id_lang = 2 AND ci.cover = 1 AND c.date_start >= date('now')\n" +
                    "ORDER BY c.date_start ASC";
            if (debug){
                Log.i(getClass().getSimpleName(),"SQL: "+SELECT_SQL);
            }

            Cursor c = myDbHelper.getReadableDatabase().rawQuery(SELECT_SQL, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        int _id = c.getInt(c.getColumnIndex("id_celebration"));
                        String name = c.getString(c.getColumnIndex("name"));
                        String date_start = c.getString(c.getColumnIndex("date_start"));
                        String date_end = c.getString(c.getColumnIndex("date_end"));
                        String image_cover = c.getString(c.getColumnIndex("file"));
                        Date date_start_formatted = null;
                        Date date_end_formatted = null;

                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            date_start_formatted = sdf.parse(date_start);
                            date_end_formatted = sdf.parse(date_end);

                            //La fiesta solo tiene lugar en un día concreto
                            if (date_start.equals(date_end)){
                                date_start = null;
                            }else{
                                SimpleDateFormat sdf_start = new SimpleDateFormat("dd");
                                date_start = sdf_start.format(date_start_formatted);
                            }
                            //Día
                            SimpleDateFormat sdf_end_day = new SimpleDateFormat("dd");
                            String date_end_day = sdf_end_day.format(date_end_formatted);
                            //Mes
                            SimpleDateFormat sdf_end_month = new SimpleDateFormat("MMMM");
                            String date_end_month = sdf_end_month.format(date_end_formatted);
                            //Año
                            SimpleDateFormat sdf_end_year = new SimpleDateFormat("yyyy");
                            String date_end_year = sdf_end_year.format(date_end_formatted);

                            date_end = date_end_day + " " + getResources().getString(R.string.de) +
                                       " " + date_end_month + " " +
                                       getResources().getString(R.string.de) + " " +
                                       date_end_year;

                        }catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //Creamos nuevo objeto track
                        cCelebration mCelebration = new cCelebration();

                        mCelebration.setId_celebration(_id);

                        mCelebration.setDate_start(date_start);
                        mCelebration.setDate_end(date_end);
                        mCelebration.setImage_cover(image_cover);

                        List<cObjectLang> mObjectLangList = new ArrayList<>();
                        cObjectLang mObjectLang = new cObjectLang();
                        mObjectLang.setName(name);
                        mObjectLangList.add(mObjectLang);
                        mCelebration.setmCelebrationsLang(mObjectLangList);

                        cCelebrations.add(mCelebration);

                        if (debug){
                            Log.i(getClass().getSimpleName(),"Name: "+name+", file: "+image_cover);
                            Log.i(getClass().getSimpleName(),"date_start: "+date_start+" / date_end_formatted:"+date_end);
                        }


                    }while (c.moveToNext());
                }
            }
            c.close();
        } catch (SQLiteException sqle) {
            if (debug) {
                Log.e(getClass().getSimpleName(), "Could not create or Open the database");
            }
        }
        myDbHelper.close();
    }
}

