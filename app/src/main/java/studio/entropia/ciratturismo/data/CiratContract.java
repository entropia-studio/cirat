package studio.entropia.ciratturismo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by javier on 25/07/16.
 */
public class CiratContract {
    public static final String CONTENT_AUTHORITY = "studio.entropia.ciratturismo.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATION_LANG = "location_lang";
    public static final String PATH_LOCATION_IMAGE = "location_image";
    public static final String PATH_CELEBRATION = "celebration";
    public static final String PATH_CELEBRATION_LANG = "celebration_lang";
    public static final String PATH_CELEBRATION_IMAGE = "celebration_image";
    public static final String PATH_TRACK = "track";
    public static final String PATH_TRACK_LANG = "track_lang";
    public static final String PATH_TRACK_IMAGE = "track_image";

    public static final String PATH_IMAGE = "image";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the location table */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "location";

        // Definimos todas las columnas de la tabla excepto _id
        public static final String COLUMN_CATEGORY_KEY = "id_category";
        public static final String COLUMN_LOCATION_COORDINATES = "coordinates";
        public static final String COLUMN_LOCATION_ADDRESS = "address";
        public static final String COLUMN_LOCATION_PHONE = "phone";
        public static final String COLUMN_LOCATION_MOBILE = "mobile";
        public static final String COLUMN_LOCATION_URL = "url";
        public static final String COLUMN_LOCATION_EMAIL = "email";
        public static final String COLUMN_LOCATION_ACTIVE = "active";
        public static final String COLUMN_LOCATION_UPDATED = "updated";

        public static Uri buildLocationUri(long id_location) {
            return ContentUris.withAppendedId(CONTENT_URI, id_location);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class LocationLangEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_LANG).build();

        public static final String TABLE_NAME = "location_lang";
        // Definimos todas las columnas de la tabla excepto _id
        public static final String COLUMN_LOCATION_LANG_LOCATION_KEY = "id_location";
        public static final String COLUMN_LOCATION_LANG_LANG_KEY = "id_lang";
        public static final String COLUMN_LOCATION_LANG_NAME = "name";
        public static final String COLUMN_LOCATION_LANG_DESCRIPTION = "description";
        public static final String COLUMN_LOCATION_LANG_NAME_COVER= "name_cover";
        public static final String COLUMN_LOCATION_LANG_NAME_ACTIVE= "active";
        public static final String COLUMN_LOCATION_LANG_UPDATED = "updated";

        public static String getLocationIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildLocationLangUri(long id_location) {
            return ContentUris.withAppendedId(CONTENT_URI, id_location);
        }

    }

    public static final class LocationImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_IMAGE).build();

        public static final String TABLE_NAME = "location_image";
        public static final String COLUMN_LOCATION_IMAGE_IMAGE_KEY = "id_image";
        public static final String COLUMN_LOCATION_IMAGE_LOCATION_KEY = "id_location";
        public static final String COLUMN_LOCATION_IMAGE_COVER = "cover";
        public static final String COLUMN_LOCATION_IMAGE_POSITION = "position";
        public static final String COLUMN_LOCATION_IMAGE_ACTIVE = "active";
        public static final String COLUMN_LOCATION_IMAGE_UPDATED = "updated";

        public static Uri buildLocationImageUri(long id_image) {
            return ContentUris.withAppendedId(CONTENT_URI, id_image);
        }

    }

    public static  final class  LangEntry implements BaseColumns {
        public static final String TABLE_NAME = "lang";
        public static final String COLUMN_LANG_NAME = "name";
        public static final String COLUMN_LANG_ISO_CODE = "iso_code";
        public static final String COLUMN_LANG_ACTIVE = "active";
    }


    public static  final class  CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_CATEGORY_NAME = "name";
        public static final String COLUMN_CATEGORY_ACTIVE = "active";
    }

    public static  final class  CelebrationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CELEBRATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CELEBRATION;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CELEBRATION;

        public static final String TABLE_NAME = "celebration";
        public static final String COLUMN_CELEBRATION_DATE_START = "date_start";
        public static final String COLUMN_CELEBRATION_DATE_END = "date_end";
        public static final String COLUMN_CELEBRATION_ACTIVE = "active";
        public static final String COLUMN_CELEBRATION_UPDATED = "updated";

        public static Uri buildCelebrationUri(long id_celebration) {
            return ContentUris.withAppendedId(CONTENT_URI, id_celebration);
        }
    }

    public static  final class  CelebrationLangEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CELEBRATION_LANG).build();

        public static final String TABLE_NAME = "celebration_lang";
        public static final String COLUMN_CELEBRATION_LANG_CELEBRATION_KEY = "id_celebration";
        public static final String COLUMN_CELEBRATION_LANG_LANG_KEY = "id_lang";
        public static final String COLUMN_CELEBRATION_LANG_NAME = "name";
        public static final String COLUMN_CELEBRATION_LANG_DESCRIPTION = "description";
        public static final String COLUMN_CELEBRATION_LANG_ACTIVE = "active";
        public static final String COLUMN_CELEBRATION_LANG_UPDATED = "updated";

        public static Uri buildCelebrationLangUri(long id_celebration) {
            return ContentUris.withAppendedId(CONTENT_URI, id_celebration);
        }
    }

    public static  final class  CelebrationImagesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CELEBRATION_IMAGE).build();

        public static final String TABLE_NAME = "celebration_image";
        public static final String COLUMN_CELEBRATION_IMAGE_IMAGE_KEY = "id_image";
        public static final String COLUMN_CELEBRATION_IMAGE_CELEBRATION_KEY = "id_celebration";
        public static final String COLUMN_CELEBRATION_IMAGE_COVER = "cover";
        public static final String COLUMN_CELEBRATION_IMAGE_POSITION = "position";
        public static final String COLUMN_CELEBRATION_IMAGE_ACTIVE = "active";
        public static final String COLUMN_CELEBRATION_IMAGE_UPDATED = "updated";

        public static Uri buildCelebrationImageUri(long id_image) {
            return ContentUris.withAppendedId(CONTENT_URI, id_image);
        }

    }

    public static  final class  ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build();


        public static final String TABLE_NAME = "image";
        public static final String COLUMN_IMAGE_FILE = "file";
        public static final String COLUMN_IMAGE_ACTIVE = "active";
        public static final String COLUMN_IMAGE_UPDATED = "updated";
    }

    public static  final class  ImageLangEntry implements BaseColumns {
        public static final String TABLE_NAME = "image_lang";
        public static final String COLUMN_IMAGE_LANG_IMAGE_KEY = "id_image";
        public static final String COLUMN_IMAGE_LANG_LANG_KEY = "id_lang";
        public static final String COLUMN_IMAGE_LANG_NAME = "name";
        public static final String COLUMN_IMAGE_LANG_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_LANG_UPDATED = "updated";
    }

    public static  final class  TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String TABLE_NAME = "track";
        public static final String COLUMN_TRACK_DISTANCE = "distance";
        public static final String COLUMN_TRACK_IS_CIRCULAR = "is_circular";
        public static final String COLUMN_TRACK_TOTAL_TIME = "total_time";
        public static final String COLUMN_TRACK_KML = "kml";
        public static final String COLUMN_TRACK_POSITION = "position";
        public static final String COLUMN_TRACK_POSITIVE_GRADIENT = "positive_gradient";
        public static final String COLUMN_TRACK_NEGATIVE_GRADIENT = "negative_gradient";
        public static final String COLUMN_TRACK_MAX_ALTITUDE = "max_altitude";
        public static final String COLUMN_TRACK_MIN_ALTITUDE = "min_altitude";
        public static final String COLUMN_TRACK_CENTER_COORDINATES = "center_coordinates";
        public static final String COLUMN_TRACK_ACTIVE = "active";
        public static final String COLUMN_TRACK_UPDATED = "updated";

        public static Uri buildTrackUri(long id_track) {
            return ContentUris.withAppendedId(CONTENT_URI, id_track);
        }

    }

    public static  final class  TrackLangEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK_LANG).build();

        public static final String TABLE_NAME = "track_lang";
        public static final String COLUMN_TRACK_LANG_TRACK_KEY = "id_track";
        public static final String COLUMN_TRACK_LANG_LANG_KEY = "id_lang";
        public static final String COLUMN_TRACK_LANG_NAME = "name";
        public static final String COLUMN_TRACK_LANG_DESCRIPTION = "description";
        public static final String COLUMN_TRACK_LANG_ACTIVE = "active";
        public static final String COLUMN_TRACK_LANG_UPDATED = "updated";

        public static Uri buildTrackLangUri(long id_track) {
            return ContentUris.withAppendedId(CONTENT_URI, id_track);
        }
    }

    public static  final class  TrackImagesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK_IMAGE).build();

        public static final String TABLE_NAME = "track_image";
        public static final String COLUMN_TRACK_IMAGE_IMAGE_KEY = "id_image";
        public static final String COLUMN_TRACK_IMAGE_TRACK_KEY = "id_track";
        public static final String COLUMN_TRACK_IMAGE_COVER = "cover";
        public static final String COLUMN_TRACK_IMAGE_POSITION = "position";
        public static final String COLUMN_TRACK_IMAGE_ACTIVE = "active";
        public static final String COLUMN_TRACK_IMAGE_UPDATED = "updated";

        public static Uri buildTrackImageUri(long id_mage) {
            return ContentUris.withAppendedId(CONTENT_URI, id_mage);
        }

    }


}



