package com.lborof028685.evassist2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lborof028685.evassist2.data.TipContract;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TipFragment} factory method to
 * create an instance of this fragment.
 */
public class TipFragment extends Fragment {

    // get context and necessary resolver/projection
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

    // prepare for the TextViews
    TextView titleTextView;
    TextView contentTextView;


    public TipFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the view
        View view = inflater.inflate(R.layout.fragment_tip, container, false);

        // get the TextViews
        titleTextView = view.findViewById(R.id.textTitle);
        contentTextView = view.findViewById(R.id.textContent);

        // get the context/resolver/arguments
        context = container.getContext();
        resolver = context.getContentResolver();
        Bundle arguments = getArguments();

        // assign the selectionArgs
        selectionArgs[0] = Integer.toString(arguments.getInt(TipContract.TipsTable._ID));

        // get the relevant Uri
        Uri uri = TipContract.TipsTable.buildTipUriWithTipNo(arguments.getInt(TipContract.TipsTable._ID));

        // query the resolver and get the cursor
        mCursor = resolver.query(uri,mProjection,selectionClause,selectionArgs,sortOrder);


        if (mCursor == null) {
            // some thing went wrong
        } else if(mCursor.getCount()<1) {
            // no results
        } else {
            // while records exist
            while(mCursor.moveToNext()) {
                // get the title and content
                String title = mCursor.getString(mCursor.getColumnIndex(TipContract.TipsTable.COLUMN_TIP_TITLE));
                String content = mCursor.getString(mCursor.getColumnIndex(TipContract.TipsTable.COLUMN_TIP_CONTENT));

                // set the relevant TextViews
                titleTextView.setText(title);
                contentTextView.setText(content);
            }
        }

        // Inflate the layout for this fragment
        return view;
    }
}