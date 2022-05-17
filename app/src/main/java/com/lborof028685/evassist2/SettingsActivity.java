package com.lborof028685.evassist2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    /**
     * Displays the settings page for the app
     */
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        // get the app bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // set its attributes
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("Settings");
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // select the correct nav item
        bottomNavigationView.setSelectedItemId(R.id.settingSelector);

        // assign listeners to nav
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingSelector:
                        return true;
                    case R.id.guideSelector:
                        startActivity(new Intent(getApplicationContext(), GuideActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.chargingSelector:
                        startActivity(new Intent(getApplicationContext(), ChargingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.newsSelector:
                        startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        /**
         * Inner class that provides the settings fragment and uses SharedPreferences to store the preferences
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences;
        // See: https://developer.android.com/training/basics/intents/result
        private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        onSignInResult(result);
                    }
                }
        );

        private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
            IdpResponse response = result.getIdpResponse();
            if (result.getResultCode() == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                setAccountBtn();
                // with firebase it's not necessary to store the user in SharedPreferences
            }
        }

        private void setLogInBtn(Preference accountPreference) {
            // they're logged out or have an anonymous account
            accountPreference.setTitle("Log In");
            accountPreference.setSummary("Tap here to log in/register");
            accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    // Create and launch sign-in intent
                    Intent signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build();
                    signInLauncher.launch(signInIntent);

                    return true;
                }
            });
        };

        private void setLogOutBtn(Preference accountPreference) {
            // they're logged in
            accountPreference.setTitle("Log Out");
            accountPreference.setSummary("Account: "+user.getEmail());
            accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FirebaseAuth.getInstance().signOut();
                    setLogInBtn(accountPreference);
                    return true;
                }
            });
        }
        private void setAccountBtn() {
            Preference accountPreference = findPreference("loginOutBtn");
            if (user == null) {
                setLogInBtn(accountPreference);

            } else{
                setLogOutBtn(accountPreference);
            }
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.root_preferences);

            // get the SharedPreferences
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            // when Car Model changes
            onSharedPreferenceChanged(sharedPreferences,getString(R.string.car_model));


            // decide what the account button should do
            setAccountBtn();

            // get the user guide button
            Preference button = findPreference(getString(R.string.user_guide));

            // assign a listener
            button.setOnPreferenceClickListener(preference -> {

                // create intent
                Intent intent = new Intent(getActivity(), WebViewActivity.class);

                // add user guide link
                intent.putExtra("link","file:///android_asset/home.html");

                // start the intent
                startActivity(intent);
                return true;
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // when this shared preference is changed
            Preference preference = findPreference(key);

            // prepare space for a new sumamryProvider
            Preference.SummaryProvider summaryProvider;
            CharSequence newSummary = "";

            // if it's a list preference
            if (preference instanceof ListPreference) {

                // get it
                ListPreference listPref = (ListPreference) preference;

                // get the index
                int pIndex = listPref.findIndexOfValue(sharedPreferences.getString(key,""));

                // if it's at least 0
                if (pIndex >= 0) {
                    // get the chosen index value
                    summaryProvider = preference1 -> listPref.getEntries()[pIndex];
                } else {
                    // get the current value if not
                    summaryProvider = preference1 -> sharedPreferences.getString(key, "");

                }

                // set the new summary provider
                preference.setSummaryProvider(summaryProvider);

            }
        }

        @Override
        public void onResume() {
            //reregister the listener
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
        @Override
        public void onPause(){
            super.onPause();
            // unregister
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}