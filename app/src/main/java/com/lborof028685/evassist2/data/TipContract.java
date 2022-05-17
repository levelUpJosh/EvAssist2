package com.lborof028685.evassist2.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TipContract {
    // Derived from Lab08

    public static final String CONTENT_AUTHORITY = "com.lborof028685.evassist2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TIPS = "tips";


    //Use inner class to define structure of TipsTable
    public static final class TipsTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIPS).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_TIPS;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_TIPS;

        //Table Name
        public static final String TABLE_NAME = "TIPS_TABLE";
        //Column Names
        public static final String COLUMN_TIP_NO = "TIP_NO";
        public static final String COLUMN_TIP_TITLE = "TIP_TITLE";
        public static final String COLUMN_TIP_CONTENT = "TIP_CONTENT";
        public static final String COLUMN_TIP_PARENT = "TIP_PARENT";

        public static Uri buildTipUriWithTipNo(long tip_no){
            return ContentUris.withAppendedId(CONTENT_URI, tip_no);
        }
    }
}
