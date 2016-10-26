package studio.entropia.ciratturismo.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier on 28/07/16.
 */
public class JSONCelebrationParser {
    private final static String LOG_TAG = JSONCelebrationParser.class.getSimpleName();

    public List<cCelebration> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readCelebrationsArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<cCelebration> readCelebrationsArray(JsonReader reader) throws IOException {
        List<cCelebration> celebrations = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            celebrations.add(readCelebration(reader));
        }
        reader.endArray();
        Log.i(LOG_TAG,"return:readLocationsArray");
        return celebrations;
    }

    public cCelebration readCelebration(JsonReader reader) throws IOException {

        cCelebration mCelebration = new cCelebration();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("id_celebration")){
                mCelebration.setId_celebration(reader.nextInt());
            } else if (name.equals("date_start")) {
                mCelebration.setDate_start(reader.nextString());
            } else if (name.equals("date_end")){
                    mCelebration.setDate_end(reader.nextString());
            } else if (name.equals("active")) {
                mCelebration.setActive(reader.nextInt());
            } else if (name.equals("celebration_lang")){
                    mCelebration.setmCelebrationsLang(readCelebrationsByLangArray(reader));
            } else if (name.equals("celebration_image") && reader.peek() != JsonToken.NULL){
                mCelebration.setmImages(readImagesArray(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mCelebration;
    }

    public List<cObjectLang> readCelebrationsByLangArray(JsonReader reader) throws IOException {
        List<cObjectLang> celebrations_lang = new ArrayList<>();

        reader.beginArray();

        while (reader.hasNext()) {
            celebrations_lang.add(readLocationByLang(reader));
        }

        reader.endArray();
        return celebrations_lang;
    }

    public cObjectLang readLocationByLang(JsonReader reader) throws IOException{

        cObjectLang mCelebrationLang = new cObjectLang();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("_id")) {
                mCelebrationLang.set_id(reader.nextInt());
            } else if (name.equals("id_lang")) {
                mCelebrationLang.setId_lang(reader.nextInt());
            } else if (name.equals("name")) {
                mCelebrationLang.setName(reader.nextString());
            } else if (name.equals("description")) {
                mCelebrationLang.setDescription(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mCelebrationLang;
    }

    public List<cImage> readImagesArray(JsonReader reader) throws IOException {
        List<cImage> images = new ArrayList<>();

        reader.beginArray();

        while (reader.hasNext()) {
            images.add(readImage(reader));
        }

        reader.endArray();
        return images;
    }

    public cImage readImage(JsonReader reader) throws IOException{

        cImage mImage = new cImage();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("_id")) {
                mImage.set_id(reader.nextInt());
            }else if (name.equals("id_image")) {
                mImage.setId_image(reader.nextInt());
            } else if (name.equals("file")) {
                mImage.setmFile(reader.nextString());
            } else if (name.equals("active")) {
                mImage.setActive(reader.nextInt());
            } else if (name.equals("cover")) {
                mImage.setCover(reader.nextInt());
            } else if (name.equals("position")) {
                mImage.setPosition(reader.nextInt());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mImage;
    }


}
