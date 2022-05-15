package com.lborof028685.evassist2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.settingSelector);

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
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
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            setAccountBtn();
        }
    }
}