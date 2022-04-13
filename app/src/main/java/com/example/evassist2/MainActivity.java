package com.example.evassist2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.guideFragment);

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.chargingFragment);
        badge.setVisible(true);


    }
    GuideFragment GuideFragment = new GuideFragment();
    NewsFragment NewsFragment = new NewsFragment();
    ChargingFragment ChargingFragment = new ChargingFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.guideFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, GuideFragment).commit();
                return true;

            case R.id.chargingFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ChargingFragment).commit();
                return true;

            case R.id.newsFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, NewsFragment).commit();
                return true;
        }
        return false;
    }
}