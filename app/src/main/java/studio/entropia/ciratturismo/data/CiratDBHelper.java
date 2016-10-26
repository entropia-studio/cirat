package studio.entropia.ciratturismo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import studio.entropia.ciratturismo.data.CiratContract.CategoryEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationImagesEntry;
import studio.entropia.ciratturismo.data.CiratContract.CelebrationLangEntry;
import studio.entropia.ciratturismo.data.CiratContract.ImageEntry;
import studio.entropia.ciratturismo.data.CiratContract.ImageLangEntry;
import studio.entropia.ciratturismo.data.CiratContract.LangEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationImageEntry;
import studio.entropia.ciratturismo.data.CiratContract.LocationLangEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackImagesEntry;
import studio.entropia.ciratturismo.data.CiratContract.TrackLangEntry;


/**
 * Created by javier on 25/07/16.
 */
public class CiratDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "cirat.db";

    public CiratDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CategoryEntry.COLUMN_CATEGORY_NAME + " VARCHAR (100) UNIQUE NOT NULL," +
                CategoryEntry.COLUMN_CATEGORY_ACTIVE + " INT (1) DEFAULT (1)" +
                ");";

        final String SQL_CREATE_CELEBRATION_TABLE = "CREATE TABLE " + CelebrationEntry.TABLE_NAME + " (" +
                CelebrationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CelebrationEntry.COLUMN_CELEBRATION_DATE_START + " DATE," +
                CelebrationEntry.COLUMN_CELEBRATION_DATE_END + " DATE," +
                CelebrationEntry.COLUMN_CELEBRATION_ACTIVE + " INT (1) DEFAULT (1)," +
                CelebrationEntry.COLUMN_CELEBRATION_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_CELEBRATION_LANG_TABLE = "CREATE TABLE " + CelebrationLangEntry.TABLE_NAME + " (" +
                CelebrationLangEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_CELEBRATION_KEY + " INTEGER REFERENCES celebration (_id) NOT NULL," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_LANG_KEY + " INTEGER REFERENCES lang (_id) NOT NULL," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_NAME + " VARCHAR (255) NOT NULL," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_DESCRIPTION + " TEXT NOT NULL," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_ACTIVE + " INT (1) DEFAULT (1)," +
                CelebrationLangEntry.COLUMN_CELEBRATION_LANG_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_CELEBRATION_IMAGE_TABLE = "CREATE TABLE " + CelebrationImagesEntry.TABLE_NAME + " (" +
                CelebrationImagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_IMAGE_KEY + " INTEGER REFERENCES image (_id)," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_CELEBRATION_KEY + " INTEGER REFERENCES celebration (_id)," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_COVER + " INTEGER (1) DEFAULT (0) NOT NULL," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_POSITION + " INTEGER NOT NULL DEFAULT (5)," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_ACTIVE + " INT (1) DEFAULT (1)," +
                CelebrationImagesEntry.COLUMN_CELEBRATION_IMAGE_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_IMAGE_TABLE = "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ImageEntry.COLUMN_IMAGE_ACTIVE + " INT (1) DEFAULT (1)," +
                ImageEntry.COLUMN_IMAGE_FILE + " STRING (255) NOT NULL," +
                ImageEntry.COLUMN_IMAGE_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_IMAGE_LANG_TABLE = "CREATE TABLE " + ImageLangEntry.TABLE_NAME + " (" +
                ImageLangEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ImageLangEntry.COLUMN_IMAGE_LANG_IMAGE_KEY + " INTEGER REFERENCES image (_id) ON DELETE RESTRICT," +
                ImageLangEntry.COLUMN_IMAGE_LANG_LANG_KEY + " INTEGER REFERENCES lang (_id) ON DELETE RESTRICT," +
                ImageLangEntry.COLUMN_IMAGE_LANG_NAME + " VARCHAR (255) NOT NULL," +
                ImageLangEntry.COLUMN_IMAGE_LANG_DESCRIPTION + " VARCHAR (255)," +
                ImageLangEntry.COLUMN_IMAGE_LANG_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_LANG_TABLE = "CREATE TABLE " + LangEntry.TABLE_NAME + " (" +
                LangEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LangEntry.COLUMN_LANG_NAME + " VARCHAR (32) NOT NULL," +
                LangEntry.COLUMN_LANG_ISO_CODE + " CHAR (2) NOT NULL," +
                LangEntry.COLUMN_LANG_ACTIVE + " INT (1) DEFAULT (1)" +
                ");";


        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationEntry.COLUMN_CATEGORY_KEY + " INTEGER REFERENCES category (_id)," +
                LocationEntry.COLUMN_LOCATION_COORDINATES + " VARCHAR (255) NOT NULL," +
                LocationEntry.COLUMN_LOCATION_ADDRESS + " VARCHAR (255)," +
                LocationEntry.COLUMN_LOCATION_PHONE + " VARCHAR (15)," +
                LocationEntry.COLUMN_LOCATION_MOBILE + " VARCHAR (15)," +
                LocationEntry.COLUMN_LOCATION_URL + " VARCHAR (255)," +
                LocationEntry.COLUMN_LOCATION_EMAIL + " VARCHAR (255)," +
                LocationEntry.COLUMN_LOCATION_ACTIVE + " INT (1) DEFAULT (1), " +
                LocationEntry.COLUMN_LOCATION_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_LOCATION_LANG_TABLE = "CREATE TABLE " + LocationLangEntry.TABLE_NAME + " (" +
                LocationLangEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationLangEntry.COLUMN_LOCATION_LANG_LOCATION_KEY + " INTEGER REFERENCES location (_id)," +
                LocationLangEntry.COLUMN_LOCATION_LANG_LANG_KEY + " REFERENCES lang (_id)," +
                LocationLangEntry.COLUMN_LOCATION_LANG_NAME + " VARCHAR (45) NOT NULL," +
                LocationLangEntry.COLUMN_LOCATION_LANG_DESCRIPTION + " TEXT NOT NULL," +
                LocationLangEntry.COLUMN_LOCATION_LANG_NAME_COVER + " CHAR (60)," +
                LocationLangEntry.COLUMN_LOCATION_LANG_NAME_ACTIVE + " INT (1) DEFAULT (1)," +
                LocationLangEntry.COLUMN_LOCATION_LANG_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_LOCATION_IMAGE_TABLE = "CREATE TABLE " + LocationImageEntry.TABLE_NAME + " (" +
                LocationImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_IMAGE_KEY + " INTEGER REFERENCES image (_id)," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_LOCATION_KEY + " INTEGER REFERENCES location (_id)," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_COVER + " INTEGER (1) DEFAULT (0) NOT NULL," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_POSITION + " INTEGER NOT NULL DEFAULT (5)," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_ACTIVE + " INT (1) DEFAULT (1)," +
                LocationImageEntry.COLUMN_LOCATION_IMAGE_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";


        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrackEntry.COLUMN_TRACK_DISTANCE + " DOUBLE NOT NULL," +
                TrackEntry.COLUMN_TRACK_IS_CIRCULAR + " INT (1) DEFAULT (1) NOT NULL," +
                TrackEntry.COLUMN_TRACK_TOTAL_TIME + " DOUBLE NOT NULL," +
                TrackEntry.COLUMN_TRACK_KML + " STRING NOT NULL," +
                TrackEntry.COLUMN_TRACK_POSITION + " INTEGER NOT NULL," +
                TrackEntry.COLUMN_TRACK_POSITIVE_GRADIENT + " INTEGER," +
                TrackEntry.COLUMN_TRACK_NEGATIVE_GRADIENT + " INTEGER," +
                TrackEntry.COLUMN_TRACK_MAX_ALTITUDE + " INTEGER," +
                TrackEntry.COLUMN_TRACK_MIN_ALTITUDE + " INTEGER," +
                TrackEntry.COLUMN_TRACK_CENTER_COORDINATES + " STRING," +
                TrackEntry.COLUMN_TRACK_ACTIVE + " INT (1) DEFAULT (1), " +
                TrackEntry.COLUMN_TRACK_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_TRACK_LANG_TABLE = "CREATE TABLE " + TrackLangEntry.TABLE_NAME + " (" +
                TrackLangEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrackLangEntry.COLUMN_TRACK_LANG_TRACK_KEY + " INTEGER REFERENCES track (_id)," +
                TrackLangEntry.COLUMN_TRACK_LANG_LANG_KEY + " REFERENCES lang (_id)," +
                TrackLangEntry.COLUMN_TRACK_LANG_NAME + " VARCHAR (45) NOT NULL," +
                TrackLangEntry.COLUMN_TRACK_LANG_DESCRIPTION + " TEXT NOT NULL," +
                TrackLangEntry.COLUMN_TRACK_LANG_ACTIVE + " INT (1) DEFAULT (1)," +
                TrackLangEntry.COLUMN_TRACK_LANG_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        final String SQL_CREATE_TRACK_IMAGE_TABLE = "CREATE TABLE " + TrackImagesEntry.TABLE_NAME + " (" +
                TrackImagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_IMAGE_KEY + " INTEGER REFERENCES image (_id)," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_TRACK_KEY + " INTEGER REFERENCES track (_id)," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_COVER + " INTEGER (1) DEFAULT (0) NOT NULL," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_POSITION + " INTEGER NOT NULL DEFAULT (5)," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_ACTIVE + " INT (1) DEFAULT (1)," +
                TrackImagesEntry.COLUMN_TRACK_IMAGE_UPDATED + " DATETIME DEFAULT (CURRENT_TIMESTAMP)" +
                ");";

        //Creamos las tablas
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CELEBRATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CELEBRATION_LANG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CELEBRATION_IMAGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_IMAGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_IMAGE_LANG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LANG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_LANG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_IMAGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_LANG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_IMAGE_TABLE);

        //Idiomas
        String SQL_LANG_INSERT_01 = "INSERT INTO lang (_id, name, iso_code, active) VALUES (1, 'English', 'en', 0);";
        String SQL_LANG_INSERT_02 = "INSERT INTO lang (_id, name, iso_code, active) VALUES (2, 'Spanish', 'es', 1);";
        sqLiteDatabase.execSQL(SQL_LANG_INSERT_01);
        sqLiteDatabase.execSQL(SQL_LANG_INSERT_02);

        //Categorías
        String SQL_LANG_CATEGORY_01 = "INSERT INTO category (_id, name, active) VALUES (1, 'Hostelería', 1);";
        String SQL_LANG_CATEGORY_02 = "INSERT INTO category (_id, name, active) VALUES (2, 'Punto de interés', 1);";
        String SQL_LANG_CATEGORY_03 = "INSERT INTO category (_id, name, active) VALUES (3, 'Dormir', 1);\n";
        String SQL_LANG_CATEGORY_04 = "INSERT INTO category (_id, name, active) VALUES (4, 'Servicios', 1);\n";
        sqLiteDatabase.execSQL(SQL_LANG_CATEGORY_01);
        sqLiteDatabase.execSQL(SQL_LANG_CATEGORY_02);
        sqLiteDatabase.execSQL(SQL_LANG_CATEGORY_03);
        sqLiteDatabase.execSQL(SQL_LANG_CATEGORY_04);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Versiones 1 y 2
        if (i < 3){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CelebrationEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CelebrationLangEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CelebrationImagesEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImageLangEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationLangEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationImageEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackLangEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackImagesEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        db.execSQL("DELETE FROM location;");
//        db.execSQL("DELETE FROM location_lang;");
//        db.execSQL("DELETE FROM image;");
//        db.execSQL("DELETE FROM location_image;");
//        db.execSQL("DELETE FROM track;");
//        db.execSQL("DELETE FROM track_lang;");
//        db.execSQL("VACUUM;");
//    }
}
