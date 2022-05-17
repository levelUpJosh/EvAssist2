package com.lborof028685.evassist2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TipProvider extends ContentProvider {
    // derived from lab08, originally by yjp

    // Define match codes
    public static final int TIPS = 100;
    public static final int TIP_WITH_ID = 101;

    // Define helper classes
    private static UriMatcher myUriMatcher = buildUriMatcher();
    public static TipDBHelper myDBHelper;

    private static UriMatcher buildUriMatcher(){
        /**
         * Builds a UriMatcher based on the TipContract
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(TipContract.CONTENT_AUTHORITY,TipContract.PATH_TIPS,TIPS);
        matcher.addURI(TipContract.CONTENT_AUTHORITY,TipContract.PATH_TIPS+"/#",TIP_WITH_ID);

        return matcher;
    }

    public TipProvider() {}

    @Override
    public boolean onCreate() {

        // Get TipDBHelper object
        myDBHelper = new TipDBHelper(getContext(),TipDBHelper.DB_NAME,null,TipDBHelper.DB_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        /**
         * Handles client queries
         */
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch (match_code) {
            case TIPS:
                // get a writeable database of the whole data
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        TipContract.TipsTable.TABLE_NAME, //TABLE TO QUERY
                        projection, // Columns
                        null, // where columns
                        null, // where values
                        null, // group by columns
                        null, // filter by row groups
                        null // sort order
                );
                Log.i("TipProvider","querying TIPS");
                Log.i("TipProvider",Integer.toString(myCursor.getCount()));
                break;
            case TIP_WITH_ID:
                // get a read only form of one particular record
                myCursor = myDBHelper.getReadableDatabase().query(
                        TipContract.TipsTable.TABLE_NAME,
                        projection,
                        TipContract.TipsTable._ID + " = ?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.i("TipProvider","querying TIP with _id "+ContentUris.parseId(uri));
                Log.i("TipProvider",Integer.toString(myCursor.getCount()));
                break;
            default:
                throw new UnsupportedOperationException("Not Supported");

        }

        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        /**
         * Determines the content type
         */
        int match_code = myUriMatcher.match(uri);

        switch (match_code){
            case TIPS:
                return TipContract.TipsTable.CONTENT_TYPE_DIR;
            case TIP_WITH_ID:
                return TipContract.TipsTable.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not Supported");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        /**
         * Inserts data into the database
         */
        // inserts new rows
        int match_code = myUriMatcher.match(uri);
        Uri retUri;

        switch(match_code){
            case TIPS:
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(TipContract.TipsTable.TABLE_NAME,null,contentValues);
                if (_id > 0) {
                    // gets a Uri based on the requested TipNo
                    retUri = TipContract.TipsTable.buildTipUriWithTipNo(_id);
                }
                else
                    throw new SQLException("Failed to insert");
                break;
            default:
                throw new UnsupportedOperationException("Not Supported");

        }
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        /**
         * Delete the TIPs table
         */
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case TIPS:{
                myDBHelper.clearTable(TipContract.TipsTable.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        /**
         * Update rows in the database
         */
        int match_code = myUriMatcher.match(uri);

        // get the db
        SQLiteDatabase db = myDBHelper.getWritableDatabase();

        // create a variable for the number of rows updated
        int rowsUpdated;

        switch(match_code){
            case TIPS:{
                rowsUpdated =  db.update(TipContract.TipsTable.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case TIP_WITH_ID:
                // if selection is not provided then
                if (TextUtils.isEmpty(selection)) {
                    // just use _ID to get the record
                    rowsUpdated = db.update(TipContract.TipsTable.TABLE_NAME,
                            contentValues,
                            TipContract.TipsTable._ID+" = ?",
                            null);
                } else {
                    // else consider the selection criteria also
                    rowsUpdated = db.update(TipContract.TipsTable.TABLE_NAME,
                            contentValues,
                            TipContract.TipsTable._ID + " = ? AND ?",
                            selectionArgs);
                    break;
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }


}
