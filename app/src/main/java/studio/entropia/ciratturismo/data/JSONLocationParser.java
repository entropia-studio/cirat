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
public class JSONLocationParser {
    private final static String LOG_TAG = JSONLocationParser.class.getSimpleName();

    public List<cLocation> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readLocationsArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<cLocation> readLocationsArray(JsonReader reader) throws IOException {
        List<cLocation> locations = new ArrayList<cLocation>();
        reader.beginArray();
        while (reader.hasNext()) {
            locations.add(readLocation(reader));
        }
        reader.endArray();
        Log.i(LOG_TAG,"return:readLocationsArray");
        return locations;
    }

    public cLocation readLocation(JsonReader reader) throws IOException {

        cLocation mLocation = new cLocation();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id_location")){
                mLocation.setId_location(reader.nextInt());
            } else if (name.equals("id_category")){
                mLocation.setId_category(reader.nextInt());
            } else if (name.equals("coordinates")){
                mLocation.setCoordinates(reader.nextString());
            } else if (name.equals("phone") && reader.peek() != JsonToken.NULL){
                mLocation.setPhone(reader.nextString());
            } else if (name.equals("mobile") && reader.peek() != JsonToken.NULL){
                mLocation.setMobile(reader.nextString());
            } else if (name.equals("url") && reader.peek() != JsonToken.NULL){
                mLocation.setUrl(reader.nextString());
            } else if (name.equals("email") && reader.peek() != JsonToken.NULL){
                mLocation.setEmail(reader.nextString());
            } else if (name.equals("address") && reader.peek() != JsonToken.NULL){
                mLocation.setAddress(reader.nextString());
            } else if (name.equals("active")) {
                mLocation.setActive(reader.nextInt());
            } else if (name.equals("location_lang")){
                    mLocation.setmLocationsLang(readLocationsByLangArray(reader));
            } else if (name.equals("location_image") && reader.peek() != JsonToken.NULL){
                mLocation.setmImages(readImagesArray(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mLocation;
    }

    public List<cObjectLang> readLocationsByLangArray(JsonReader reader) throws IOException {
        List<cObjectLang> locations_lang = new ArrayList<>();

        reader.beginArray();

        while (reader.hasNext()) {
            locations_lang.add(readLocationByLang(reader));
        }

        reader.endArray();
        return locations_lang;
    }

    public cObjectLang readLocationByLang(JsonReader reader) throws IOException{

        cObjectLang mLocationLang = new cObjectLang();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("_id")) {
                mLocationLang.set_id(reader.nextInt());
            } else if (name.equals("id_lang")) {
                mLocationLang.setId_lang(reader.nextInt());
            } else if (name.equals("name")) {
                mLocationLang.setName(reader.nextString());
            } else if (name.equals("description")) {
                mLocationLang.setDescription(reader.nextString());
            } else if (name.equals("name_cover")) {
                mLocationLang.setName_cover(reader.nextString());
            } else if (name.equals("hint_cover")) {
                mLocationLang.setHint_cover(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mLocationLang;
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
