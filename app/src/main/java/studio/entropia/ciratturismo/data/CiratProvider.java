package studio.entropia.ciratturismo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by javier on 27/07/16.
 */
public class CiratProvider extends ContentProvider {

    private static final String LOG_TAG = CiratProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CiratDBHelper mOpenHelper;

    static final int LOCATION = 100;
    static final int LOCATION_ID = 101;
    static final int LOCATION_CATEGORY = 102;
    static final int LOCATION_LANG = 103;
    static final int LOCATION_IMAGE = 104;
    static final int TRACK = 200;
    static final int TRACK_ID = 201;
    static final int TRACK_IMAGE = 202;
    static final int TRACK_LANG = 203;
    static final int CELEBRATION = 300;
    static final int CELEBRATION_ID = 301;
    static final int CELEBRATION_IMAGE = 302;
    static final int CELEBRATION_LANG = 303;
    static final int IMAGE = 500;

    private static final SQLiteQueryBuilder sLocationByIdQueryBuilder;
    private static final SQLiteQueryBuilder sLocationsByCategory;
    private static final SQLiteQueryBuilder sTracks;
    private static final SQLiteQueryBuilder sCelebrations;

    static{
        sLocationByIdQueryBuilder = new SQLiteQueryBuilder();
        sLocationByIdQueryBuilder.setTables(
            CiratContract.LocationEntry.TABLE_NAME+ " INNER JOIN " +
            CiratContract.LocationLangEntry.TABLE_NAME +
            " ON " + CiratContract.LocationEntry.TABLE_NAME + "." + CiratContract.LocationEntry._ID + " = " +
            CiratContract.LocationLangEntry.TABLE_NAME + "." + CiratContract.LocationLangEntry.COLUMN_LOCATION_LANG_LOCATION_KEY +
            " INNER JOIN " + CiratContract.LocationImageEntry.TABLE_NAME + " ON " +
            CiratContract.LocationLangEntry.TABLE_NAME + "." + CiratContract.LocationLangEntry.COLUMN_LOCATION_LANG_LOCATION_KEY +
            " = " + CiratContract.LocationImageEntry.TABLE_NAME + "." + CiratContract.LocationImageEntry.COLUMN_LOCATION_IMAGE_LOCATION_KEY +
            " INNER JOIN " + CiratContract.ImageEntry.TABLE_NAME + " ON " +
            CiratContract.ImageEntry.TABLE_NAME + "." + CiratContract.ImageEntry._ID + " = " +
            CiratContract.LocationImageEntry.TABLE_NAME + "." + CiratContract.LocationImageEntry.COLUMN_LOCATION_IMAGE_IMAGE_KEY
        );
    }

    static{
        sLocationsByCategory = new SQLiteQueryBuilder();
        sLocationsByCategory.setTables("location INNER JOIN location_lang ON location._id = location_lang.id_location " +
                "INNER JOIN image ON image._id = location_image.id_image " +
                "INNER JOIN location_image ON location_image.id_location = location._id ");
    }

    //location._id = ?
    private static final String sCategorySelection =
            CiratContract.LocationEntry.COLUMN_CATEGORY_KEY + " = ? AND " +
                    CiratContract.LocationLangEntry.COLUMN_LOCATION_LANG_LANG_KEY + " = 2 AND " +
                    CiratContract.LocationImageEntry.COLUMN_LOCATION_IMAGE_COVER + " = 1 AND " +
                    CiratContract.LocationEntry.TABLE_NAME+"."+CiratContract.LocationEntry.COLUMN_LOCATION_ACTIVE + " = 1";

    private Cursor getLocationsByCategory(Uri uri, String[] projection, String sortOrder) {
        String category = CiratContract.LocationEntry.getCategoryFromUri(uri);

        String[] selectionArgs;
        String selection;

//        selection = sCategorySelection;
        selection = "location_lang.id_lang = 2 AND location.active = 1 AND cover = 1 AND id_category = ? ";
        sortOrder = "location_lang.name";

        selectionArgs = new String[]{category};

        Log.i(LOG_TAG,sCategorySelection);

        return sLocationsByCategory.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static{
        sTracks = new SQLiteQueryBuilder();
        sTracks.setTables("track INNER JOIN track_lang ON track._id = track_lang.id_track " +
                "INNER JOIN track_image ON track_image.id_track = track._id " +
                "INNER JOIN image ON image._id = track_image.id_image "
                );
    }

    private Cursor getTracks (Uri uri, String[] projection, String sortOrder) {

        String selection;
        selection = "track_lang.id_lang = 2 AND track.active = 1 AND track_image.cover = 1 ";
        sortOrder = "track.position";

        Log.i(LOG_TAG,sCategorySelection);

        return sTracks.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }

    static{
        sCelebrations = new SQLiteQueryBuilder();
        sCelebrations.setTables("celebration INNER JOIN celebration_lang ON celebration._id = celebration_lang.id_celebration " +
                "INNER JOIN celebration_image ON celebration_image.id_celebration = celebration._id " +
                "INNER JOIN image ON image._id = celebration_image.id_image "
        );
    }

    private Cursor getCelebrations (Uri uri, String[] projection, String sortOrder) {

        String selection;
        selection = "celebration_lang.id_lang = 2 AND celebration.active = 1 " +
                    "AND celebration_image.cover = 1 " +
                    "AND date_start > CURRENT_DATE ";

        sortOrder = "celebration.date_start";

        return sCelebrations.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new CiratDBHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CiratContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, CiratContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, CiratContract.PATH_LOCATION_IMAGE, LOCATION_IMAGE);
        matcher.addURI(authority, CiratContract.PATH_LOCATION + "/*", LOCATION_CATEGORY);
//        matcher.addURI(authority, CiratContract.PATH_LOCATION + "/*", LOCATION_ID);
        matcher.addURI(authority, CiratContract.PATH_LOCATION_LANG, LOCATION_LANG);
        matcher.addURI(authority, CiratContract.PATH_IMAGE, IMAGE);

        matcher.addURI(authority, CiratContract.PATH_TRACK, TRACK);
        matcher.addURI(authority, CiratContract.PATH_TRACK + "/*", TRACK_ID);
        matcher.addURI(authority, CiratContract.PATH_TRACK_IMAGE, TRACK_IMAGE);
        matcher.addURI(authority, CiratContract.PATH_TRACK_LANG, TRACK_LANG);

        matcher.addURI(authority, CiratContract.PATH_CELEBRATION, CELEBRATION);
        matcher.addURI(authority, CiratContract.PATH_CELEBRATION + "/*", CELEBRATION_ID);
        matcher.addURI(authority, CiratContract.PATH_CELEBRATION_IMAGE, CELEBRATION_IMAGE);
        matcher.addURI(authority, CiratContract.PATH_CELEBRATION_LANG, CELEBRATION_LANG);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case LOCATION:
                return CiratContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return CiratContract.LocationEntry.CONTENT_ITEM_TYPE;
            case LOCATION_CATEGORY:
                return CiratContract.LocationEntry.CONTENT_TYPE;
            case TRACK:
                return CiratContract.TrackEntry.CONTENT_TYPE;
            case TRACK_ID:
                return CiratContract.TrackEntry.CONTENT_ITEM_TYPE;
            case CELEBRATION:
                return CiratContract.CelebrationEntry.CONTENT_TYPE;
            case CELEBRATION_ID:
                return CiratContract.CelebrationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "location/"
            case LOCATION:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CiratContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location/#"
            case LOCATION_CATEGORY:
                retCursor = getLocationsByCategory(uri, projection, sortOrder);
                break;
            case TRACK:
            {
                retCursor = getTracks(uri, projection, sortOrder);
                break;
            }
            case CELEBRATION:
            {
                retCursor = getCelebrations(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case LOCATION:
                _id = db.insertWithOnConflict(CiratContract.LocationEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case LOCATION_LANG:
                _id = db.insertWithOnConflict(CiratContract.LocationLangEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.LocationLangEntry.buildLocationLangUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case LOCATION_IMAGE:
                _id = db.insertWithOnConflict(CiratContract.LocationImageEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.LocationImageEntry.buildLocationImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRACK:
                _id = db.insertWithOnConflict(CiratContract.TrackEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRACK_LANG:
                _id = db.insertWithOnConflict(CiratContract.TrackLangEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.TrackLangEntry.buildTrackLangUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRACK_IMAGE:
                _id = db.insertWithOnConflict(CiratContract.TrackImagesEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.TrackImagesEntry.buildTrackImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CELEBRATION:
                _id = db.insertWithOnConflict(CiratContract.CelebrationEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.CelebrationEntry.buildCelebrationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CELEBRATION_LANG:
                _id = db.insertWithOnConflict(CiratContract.CelebrationLangEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.CelebrationLangEntry.buildCelebrationLangUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CELEBRATION_IMAGE:
                _id = db.insertWithOnConflict(CiratContract.CelebrationImagesEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = CiratContract.CelebrationImagesEntry.buildCelebrationImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case IMAGE:
                _id = db.insertWithOnConflict(CiratContract.ImageEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = ContentUris.withAppendedId(CiratContract.ImageEntry.CONTENT_URI, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }
}
