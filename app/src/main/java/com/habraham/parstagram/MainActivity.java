package com.habraham.parstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.habraham.parstagram.fragments.ComposeFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.Home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.Compose:
                        Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.Profile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    default:
                        fragment = new ComposeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.Home);
    }
}