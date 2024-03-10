package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SmoothBottomBar smoothBottomBar;
    final Fragment homeFragment = new HomeFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment mapFragment = new MapFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smoothBottomBar = findViewById(R.id.bottombar);

        // Отображаем HomeFragment при запуске приложения
        fragmentManager.beginTransaction().add(R.id.framelayout, homeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.framelayout, profileFragment).hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.framelayout, mapFragment).hide(mapFragment).commit();

        // менять фрагменты
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Fragment selectedFragment = null;
                switch (i) {
                    case 0:
                        selectedFragment = homeFragment;
                        break;
                    case 1:
                        selectedFragment = profileFragment;
                        break;
                    case 2:
                        selectedFragment = mapFragment;
                        break;
                }

                fragmentManager.beginTransaction().hide(activeFragment).show(selectedFragment).commit();
                activeFragment = selectedFragment;

                return true;
            }
        });
    }

    public void setActiveFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) { // Check if the new fragment is already added
            transaction.add(R.id.framelayout, fragment);
        }
        transaction.hide(activeFragment); // Hide the currently active fragment
        transaction.show(fragment); // Show the new fragment
        transaction.commit(); // Commit the transaction

        activeFragment = fragment; // Update the active fragment reference
    }

    public void showHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment);
        if (!homeFragment.isAdded()) {
            transaction.add(R.id.framelayout, homeFragment);
        }
        transaction.show(homeFragment);
        transaction.commit();

        activeFragment = homeFragment;
    }
}
