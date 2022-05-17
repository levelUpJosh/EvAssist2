package com.lborof028685.evassist2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class TipDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 7;

    // set as public for dev
    public static final String DB_NAME = "tips.db";

    public SQLiteDatabase myDB;

    public TipDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        myDB = sqLiteDatabase;
        //define the initial table query
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

        // Define a convenient inner class for adding the Tips to the database
        class Tip {
            String title;
            String content;
            Integer parent; //TODO: Link tips to categories

            Tip (String title, String content, Integer parent) {
                this.title = title;
                this.content = content;
                this.parent = parent;
                insert(); //insert directly into db
            }
            Tip (String title, String content) {
                this.title = title;
                this.content = content;
                insert();
            }

            public ContentValues getValues() {
                ContentValues newValues = new ContentValues();

                // populate a contentValues variable
                newValues.put(TipContract.TipsTable.COLUMN_TIP_TITLE,this.title);
                newValues.put(TipContract.TipsTable.COLUMN_TIP_CONTENT,this.content);
                newValues.put(TipContract.TipsTable.COLUMN_TIP_PARENT,this.parent);

                return newValues;
            }

            public void insert() {
                //define insert function for Tips class
                sqLiteDatabase.insertOrThrow(TipContract.TipsTable.TABLE_NAME,null,getValues());
            }
        }

        // Directly add the Tips to the database at creation of DB (ie only once)
        new Tip("Seek out hotels that offer charging","Look out for hotels that offer overnight charging. There are perhaps more than you expect and no, it won't limit your choice. It'll only make your time easier!");
        new Tip("Only charge your car to 80% in public","DC chargers have a certain \"Charging Curve\" which means that they will charge faster at lower percentages. It's possible that stopping twice on a long journey will actually be shorter for you.");
        new Tip("Preheat your car's battery","Cars such as the Polestar 2 can preheat their battery when a charger is set as a destination");
        new Tip("Charger Etiquette","If you find yourself at a busy DC site, only charge to 80% if you need to. Perhaps put a note in saying when you'll be back but make sure to be aware and come back when the car is ready");





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
