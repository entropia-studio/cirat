package studio.entropia.ciratturismo.data;

import java.util.List;

/**
 * Created by javier on 12/08/16.
 */
public class cCelebration {
    private int id_celebration;
    private String date_start;
    private String date_end;
    private List<cImage> mImages;
    private List<cObjectLang> mCelebrationsLang;
    private int active;
    private String image_cover;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getId_celebration(){
        return id_celebration;
    }

    public void setId_celebration(int id_celebration){
        this.id_celebration = id_celebration;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public List<cImage> getmImages() {
        return mImages;
    }

    public void setmImages(List<cImage> mImages) {
        this.mImages = mImages;
    }

    public List<cObjectLang> getmCelebrationsLang() {
        return mCelebrationsLang;
    }

    public void setmCelebrationsLang(List<cObjectLang> mCelebrationsLang) {
        this.mCelebrationsLang = mCelebrationsLang;
    }
    public String getImage_cover() {
        return image_cover;
    }

    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }
}
