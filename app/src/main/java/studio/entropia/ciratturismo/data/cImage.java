package studio.entropia.ciratturismo.data;

/**
 * Created by javier on 8/08/16.
 */
public class cImage {
    public int _id;
    public int id_image;
    public String mFile;
    public int active;
    public int cover;
    public int position;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getId_image() {
        return id_image;
    }

    public void setId_image(int id_image) {
        this.id_image = id_image;
    }

    public String getmFile() {
        return mFile;
    }

    public void setmFile(String mFile) {
        this.mFile = mFile;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
