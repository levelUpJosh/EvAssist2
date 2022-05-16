package com.lborof028685.evassist2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class TipDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 3;

    // set as public ffor test
    public static final String DB_NAME = "tips.db";

    public SQLiteDatabase myDB;

    public TipDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        myDB = sqLiteDatabase;

        String query = "CREATE TABLE " +
                TipContract.TipsTable.TABLE_NAME +
                " ( "+ TipContract.TipsTable._ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TipContract.TipsTable.COLUMN_TIP_TITLE+
                " TEXT NOT NULL, "+
                TipContract.TipsTable.COLUMN_TIP_CONTENT+
                " TEXT NOT NULL, "+
                TipContract.TipsTable.COLUMN_TIP_PARENT+
                " INTEGER  );";
        sqLiteDatabase.execSQL(query);
        class Tip {
            String title;
            String content;
            Integer parent;

            Tip (String title, String content, Integer parent) {
                this.title = title;
                this.content = content;
                this.parent = parent;
                insert();
            }
            Tip (String title, String content) {
                this.title = title;
                this.content = content;
                insert();
            }

            public ContentValues getValues() {
                ContentValues newValues = new ContentValues();

                newValues.put(TipContract.TipsTable.COLUMN_TIP_TITLE,this.title);
                newValues.put(TipContract.TipsTable.COLUMN_TIP_CONTENT,this.content);
                newValues.put(TipContract.TipsTable.COLUMN_TIP_PARENT,this.parent);

                return newValues;
            }

            public void insert() {
                sqLiteDatabase.insertOrThrow(TipContract.TipsTable.TABLE_NAME,null,getValues());
            }
        }
        new Tip("Seek out hotels that offer charging","Good Tip here!");
        new Tip("Only charge your car to 80% in public","Good Tip here!");





        Log.i("TipDBHelper","TipDBHelper onCreate()");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TipContract.TipsTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void clearTable(String table_name){
        myDB.execSQL("DELETE FROM "+table_name);
    }
}
