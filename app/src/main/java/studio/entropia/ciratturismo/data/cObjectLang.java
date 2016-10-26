package studio.entropia.ciratturismo.data;

/**
 * Created by javier on 10/08/16.
 */
public class cObjectLang {
    private int _id;
    private int id_lang;
    private String name;
    private String name_cover;
    private String hint_cover;
    private String description;
    private int active;

    public int getActive() { return active; }

    public void setActive(int active) { this.active = active;}

    public int get_id() {
        return _id;
    }
    public int getId_lang() {
        return id_lang;
    }

    public String getName() {
        return name;
    }

    public String getName_cover() {
        return name_cover;
    }

    public String getHint_cover() {
        return hint_cover;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setId_lang(int id_lang) {
        this.id_lang = id_lang;
    }
    public void setName_cover(String name_cover) {
        this.name_cover = name_cover;
    }
    public void setHint_cover(String hint_cover) {
        this.hint_cover = hint_cover;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
}
