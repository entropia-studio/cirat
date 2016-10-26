package studio.entropia.ciratturismo.data;

import java.util.List;

/**
 * Created by javier on 18/05/16.
 */
public class cLocation {

    private int id_location;
    private int id_category;
    private String coordinates;
    private String image_cover;
    private String phone;
    private String mobile;
    private String url;
    private String email;
    private String address;
    private Float distance_to_user;
    private List<cImage> mImages;
    private List<cObjectLang> mLocationsLang;
    private int active;

    public List<cObjectLang> getmLocationsLang() {
        return mLocationsLang;
    }

    public void setmLocationsLang(List<cObjectLang> mLocationsLang) {
        this.mLocationsLang = mLocationsLang;
    }



    public List<cImage> getmImages() {
        return mImages;
    }
    public void setmImages(List<cImage> mImages) {
        this.mImages = mImages;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public int getId_location() {
        return id_location;
    }

    public int getImagesSum() {
        return imagesSum;
    }

    public void setImagesSum(int imagesSum) {
        this.imagesSum = imagesSum;
    }

    //Número de imágenes asociadas del cLocation
    private int imagesSum;

    public Float getDistance_to_user() {
        return distance_to_user;
    }

    public void setDistance_to_user(Float distance_to_user) {
        this.distance_to_user = distance_to_user;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public cLocation() {
    }

    public int get_id_location() {
        return id_location;
    }



    public String getCoordinates() {
        return coordinates;
    }



    public String getImage_cover() {
        return image_cover;
    }





    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }



    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }

    public void setId_location(int id_location) {
        this.id_location = id_location;
    }


}
