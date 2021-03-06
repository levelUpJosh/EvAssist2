package com.lborof028685.evassist2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.lborof028685.evassist2.data.TipContract;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Acitivity:
     * Displays the tips in a ListView and contains the TipFragment
     */
    BottomNavigationView bottomNavigationView;
    // Write a message to the database
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    SimpleCursorAdapter adapter;
    Boolean onTablet=false;
    ArrayList<String> tips = new ArrayList<>();


    public void displayListViewOfTips(){
        // initialise a new simpleCursorAdapter

        adapter = new SimpleCursorAdapter(
                getApplicationContext(),
                R.layout.guidelistview,
                null,
                new String[] {TipContract.TipsTable.COLUMN_TIP_TITLE},
                new int[]{R.id.list_item},
                0
        );

        //grab the listview element
        ListView mylv = findViewById(R.id.guideList);

        // bind the adapter
        mylv.setAdapter(adapter);

        // TODO: set up adding favourite on long click
        mylv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                return true;
            }
        });

        // add listener for each item
        mylv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Context context = getApplicationContext();


                Log.v("ID TO GET", String.valueOf(position));

                Log.v("tipToGet", String.valueOf(position+1));

                // instantiate a new TipFragment
                TipFragment fragment = new TipFragment();

                // create the arguments bundle
                Bundle arguments = new Bundle();

                // add the position of the tip in the taple
                arguments.putInt(TipContract.TipsTable._ID,position+1);
                fragment.setArguments(arguments);


                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragmentPlaceholder, fragment);
                ft.addToBackStack(null);
               // then commit
                ft.commit();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ev-assistant-3d3e4-default-rtdb.europe-west1.firebasedatabase.app/");
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(user.getDisplayName());
        if (user == null) {
            // if not logged in then create a temporary account
            auth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInAnonymously:success");
                                user = auth.getCurrentUser();


                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(GuideActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });;
            user = auth.getCurrentUser();
        }


        setContentView(R.layout.activity_guide);

        //startActivity(new Intent(getApplicationContext(),FirebaseLoginUIActivity.class));
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // select the correct nav item
        bottomNavigationView.setSelectedItemId(R.id.guideSelector);

        // assign listeners to nav
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.guideSelector:
                        return true;
                    case R.id.chargingSelector:
                        startActivity(new Intent(getApplicationContext(), ChargingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.newsSelector:
                        startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settingSelector:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        //instantiate Loader
        LoaderManager.getInstance(this).initLoader(0, null, this);

        displayListViewOfTips();

    }
    private static final int TIPS_LOADER = 1;
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        // define the columns for the loader to use
        String[] columns = {
                TipContract.TipsTable._ID,
                TipContract.TipsTable.COLUMN_TIP_TITLE
        };

        // return the Loader
        return new CursorLoader(getApplicationContext(), TipContract.TipsTable.CONTENT_URI,columns,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // swap in the data
        adapter.swapCursor(data);

        // hide the loading circle
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.i("loader","onLoaderReset");
    }
}