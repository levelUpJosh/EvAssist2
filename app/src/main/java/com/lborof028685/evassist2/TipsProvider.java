package com.lborof028685.evassist2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class TipsProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.lborof028685.evassist2.TipsProvider";
    static final String URL = "content://"+PROVIDER_NAME+"/tips";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String TITLE = "title";
    static final String CONTENT = "content";
    static final String PARENT_ID = "parent_id";

    private static HashMap<String,String> TIPS_PROJECTION_MAP;

    static final int TIPS = 1;
    static final int TIP_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(PROVIDER_NAME,"tips",TIPS);
        uriMatcher.addURI(PROVIDER_NAME,"tips/#",TIP_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Guide";
    static final String TIPS_TABLE_NAME = "tips";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            "CREATE TABLE "+TIPS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    " title TEXT NOT NULL, "+
                    " content TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TIPS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper((context));

        // Create database if doesn't already exist
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] args, @Nullable String order) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TIPS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case TIPS:
                queryBuilder.setProjectionMap(TIPS_PROJECTION_MAP);
                break;
            case TIP_ID:
                queryBuilder.appendWhere(_ID+"="+uri.getPathSegments().get(1));
                break;

            default:

        }
        if (order == null || order == "") {
            // By default, sort on tip titles
            order = TITLE;
        }

        Cursor cursor= queryBuilder.query(db,projection,selection,args,null,null,order);
        // watch for changes
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)){
            //get all records
            case TIPS:
                return "vnd.android.cursor.item/vnd.lborof028685.tips";

            // particular tip
            case TIP_ID:
                return "vnd.android.cursor.item/vnd.lboro028685.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: "+ uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Add a new tip record
        long rowID = db.insert(TIPS_TABLE_NAME,"",contentValues);

        //if record is added
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("Failed to add record into "+uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] args) {
        int count = 0 ;
        switch (uriMatcher.match(uri)) {
            case TIPS:
                count = db.delete(TIPS_TABLE_NAME,selection,args);
                break;
            case TIP_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TIPS_TABLE_NAME,_ID+"="+id+(!TextUtils.isEmpty(selection) ? "" +
                        "AND ("+selection+')' : ""),args);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] args) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case TIPS:
                count = db.update(TIPS_TABLE_NAME,contentValues,selection,args);
                break;
            case TIP_ID:
                count = db.update(TIPS_TABLE_NAME,contentValues,_ID+"+"+uri.getPathSegments().get(1)+(!TextUtils.isEmpty(selection)? "" +
                        "AND ("+selection+')' : ""),args);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
