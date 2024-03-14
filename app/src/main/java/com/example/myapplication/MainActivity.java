package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SmoothBottomBar smoothBottomBar;
    final Fragment homeFragment = new HomeFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment mapFragment = new MapFragment();
    final Fragment loginFragment = new LogInFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private FirebaseAuth.AuthStateListener authStateListener;
    Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the bottom navigation bar
        smoothBottomBar = findViewById(R.id.bottombar);

        // Initial fragment setup: add all fragments, but only display HomeFragment
        setupFragments();

        // Firebase authentication initialization
        setupFirebaseAuth();

        // Bottom bar item selection handling
        setupBottomBarItemSelection();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new HomeFragment()) // Ensure this is your container ID
                    .commit();
        }
    }

    private void setupFragments() {
        fragmentManager.beginTransaction().add(R.id.framelayout, homeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.framelayout, profileFragment).hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.framelayout, mapFragment).hide(mapFragment).commit();
        fragmentManager.beginTransaction().add(R.id.framelayout, loginFragment).hide(loginFragment).commit();
    }

    private void setupFirebaseAuth() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in, switch to profile fragment if necessary
                setActiveFragment(profileFragment);
            } else {
                // User is signed out, switch to login fragment if necessary
                setActiveFragment(loginFragment);
            }
        };
        mAuth.addAuthStateListener(authStateListener);
    }

    private void setupBottomBarItemSelection() {
        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Fragment selectedFragment = null;
            switch (i) {
                case 0:
                    selectedFragment = homeFragment;
                    break;
                case 1:
                    selectedFragment = (currentUser != null) ? profileFragment : loginFragment;
                    break;
                case 2:
                    selectedFragment = mapFragment;
                    break;
            }
            if (selectedFragment != null) {
                switchFragment(selectedFragment);
            }
            return true;
        });
    }

    public void setActiveFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();
        activeFragment = fragment;
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.framelayout, fragment);
        }
        transaction.hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
    }

    public void showHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment); // Assuming 'activeFragment' tracks the currently shown fragment
        if (!homeFragment.isAdded()) {
            transaction.add(R.id.framelayout, homeFragment);
        }
        transaction.show(homeFragment);
        transaction.commit();

        activeFragment = homeFragment;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }
}