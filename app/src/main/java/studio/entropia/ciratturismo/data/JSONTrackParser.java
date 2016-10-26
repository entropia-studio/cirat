package studio.entropia.ciratturismo.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier on 28/07/16.
 */
public class JSONTrackParser {
    private final static String LOG_TAG = JSONTrackParser.class.getSimpleName();

    public List<cTrack> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readTracksArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<cTrack> readTracksArray(JsonReader reader) throws IOException {
        List<cTrack> tracks = new ArrayList<cTrack>();
        reader.beginArray();
        while (reader.hasNext()) {
            tracks.add(readTrack(reader));
        }
        reader.endArray();        
        return tracks;
    }

    public cTrack readTrack(JsonReader reader) throws IOException {

        cTrack mTrack = new cTrack();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id_track")){
                mTrack.setId_track(reader.nextInt());
            } else if (name.equals("distance")){
                mTrack.setDistance(reader.nextString());
            } else if (name.equals("is_circular")){
                mTrack.setIs_circular(reader.nextInt());
            } else if (name.equals("total_time")){
                mTrack.setTotal_time(reader.nextString());
            } else if (name.equals("kml")){
                mTrack.setKml(reader.nextString());
            } else if (name.equals("position")){
                mTrack.setPosition(reader.nextInt());
            } else if (name.equals("positive_gradient") && reader.peek() != JsonToken.NULL){
                mTrack.setPositive_gradient(reader.nextInt());
            } else if (name.equals("negative_gradient") && reader.peek() != JsonToken.NULL){
                mTrack.setNegative_gradient(reader.nextInt());
            } else if (name.equals("max_altitude") && reader.peek() != JsonToken.NULL){
                mTrack.setMax_altitude(reader.nextInt());
            } else if (name.equals("min_altitude") && reader.peek() != JsonToken.NULL){
                mTrack.setMin_altitude(reader.nextInt());
            } else if (name.equals("center_coordinates")){
                mTrack.setCenter_coordinates(reader.nextString());
            } else if (name.equals("active")) {
                mTrack.setActive(reader.nextInt());
            } else if (name.equals("track_lang")){
                    mTrack.setmTracksLang(readTracksByLangArray(reader));
            } else if (name.equals("track_image") && reader.peek() != JsonToken.NULL){
                mTrack.setmImages(readImagesArray(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mTrack;
    }

    public List<cObjectLang> readTracksByLangArray(JsonReader reader) throws IOException {
        List<cObjectLang> tracks_lang = new ArrayList<>();

        reader.beginArray();

        while (reader.hasNext()) {
            tracks_lang.add(readTrackByLang(reader));
        }

        reader.endArray();
        return tracks_lang;
    }

    public cObjectLang readTrackByLang(JsonReader reader) throws IOException{

        cObjectLang mTrackLang = new cObjectLang();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("_id")) {
                mTrackLang.set_id(reader.nextInt());
            } else if (name.equals("id_lang")) {
                mTrackLang.setId_lang(reader.nextInt());
            } else if (name.equals("name")) {
                mTrackLang.setName(reader.nextString());
            } else if (name.equals("description")) {
                mTrackLang.setDescription(reader.nextString());
            } else if (name.equals("name_cover")) {
                mTrackLang.setName_cover(reader.nextString());
            } else if (name.equals("hint_cover")) {
                mTrackLang.setHint_cover(reader.nextString());
            } else if (name.equals("active")) {
                mTrackLang.setActive(reader.nextInt());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return mTrackLang;
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
