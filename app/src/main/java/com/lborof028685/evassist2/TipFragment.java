package com.lborof028685.evassist2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lborof028685.evassist2.data.TipContract;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TipFragment extends Fragment {
    private Context context;
    ContentResolver resolver;
    String[] mProjection = {
            TipContract.TipsTable._ID,
            TipContract.TipsTable.COLUMN_TIP_TITLE,
            TipContract.TipsTable.COLUMN_TIP_CONTENT,
            TipContract.TipsTable.COLUMN_TIP_PARENT
    };

    // Defines a string to contain the selection clause
    String selectionClause = null;

    String sortOrder = null;

    // Initializes an array to contain selection arguments
    String[] selectionArgs = {""};

    Cursor mCursor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TipFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TipFragment newInstance(String param1, String param2) {
        TipFragment fragment = new TipFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        resolver = context.getContentResolver();
        Bundle arguments = getArguments();
        selectionClause = Integer.toString(arguments.getInt(TipContract.TipsTable._ID));
        mCursor = resolver.query(TipContract.TipsTable.CONTENT_URI,mProjection,selectionClause,selectionArgs,sortOrder);
        if (mCursor == null) {
            // some thing went wrong
        } else if(mCursor.getCount()<1) {
            // no results
        } else {
            int index = mCursor.getColumnIndex(TipContract.TipsTable.COLUMN_TIP_TITLE);
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(mCursor));
            while(mCursor.moveToNext()) {
                int id = mCursor.getInt(index);
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip, container, false);
    }
}