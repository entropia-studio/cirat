package studio.entropia.ciratturismo.data;

import java.util.List;

/**
 * Created by javier on 12/08/16.
 */
public class cTrack {
    private int id_track;
    private String distance;
    private int is_circular;
    private String total_time;
    private String kml;
    private int position;
    private int positive_gradient;
    private int negative_gradient;
    private int max_altitude;
    private int min_altitude;
    private String center_coordinates;
    private int active;
    private List<cImage> mImages;
    private List<cObjectLang> mTracksLang;
    private String image_cover;

    public int getId_track() {
        return id_track;
    }

    public void setId_track(int id_track) {
        this.id_track = id_track;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getIs_circular() {
        return is_circular;
    }

    public void setIs_circular(int is_circular) {
        this.is_circular = is_circular;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getKml() {
        return kml;
    }

    public void setKml(String kml) {
        this.kml = kml;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPositive_gradient() {
        return positive_gradient;
    }

    public void setPositive_gradient(int positive_gradient) {
        this.positive_gradient = positive_gradient;
    }

    public int getNegative_gradient() {
        return negative_gradient;
    }

    public void setNegative_gradient(int negative_gradient) {
        this.negative_gradient = negative_gradient;
    }

    public int getMax_altitude() {
        return max_altitude;
    }

    public void setMax_altitude(int max_altitude) {
        this.max_altitude = max_altitude;
    }

    public int getMin_altitude() {
        return min_altitude;
    }

    public void setMin_altitude(int min_altitude) {
        this.min_altitude = min_altitude;
    }

    public String getCenter_coordinates() {
        return center_coordinates;
    }

    public void setCenter_coordinates(String center_coordinates) {
        this.center_coordinates = center_coordinates;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<cImage> getmImages() {
        return mImages;
    }

    public void setmImages(List<cImage> mImages) {
        this.mImages = mImages;
    }

    public List<cObjectLang> getmTracksLang() {
        return mTracksLang;
    }

    public void setmTracksLang(List<cObjectLang> mLocationsLang) {
        this.mTracksLang = mLocationsLang;
    }
    public String getImage_cover() {
        return image_cover;
    }

    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }
}
