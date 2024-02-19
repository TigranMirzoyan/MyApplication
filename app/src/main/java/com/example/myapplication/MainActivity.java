package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.HomeFragment;
import com.example.myapplication.MapFragment;
import com.example.myapplication.MessengerFragment;
import com.example.myapplication.ProfileFragment;
import com.example.myapplication.R;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SmoothBottomBar smoothBottomBar;
    final Fragment homeFragment = new HomeFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment mapFragment = new MapFragment();
    final Fragment messengerFragment = new MessengerFragment();
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
        fragmentManager.beginTransaction().add(R.id.framelayout, messengerFragment).hide(messengerFragment).commit();

        // менять фрагменты
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Fragment selectedFragment = null;
                String tag = null;
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
                    case 3:
                        selectedFragment = messengerFragment;
                        break;
                }

                fragmentManager.beginTransaction().hide(activeFragment).show(selectedFragment).commit();
                activeFragment = selectedFragment;

                return true;
            }
        });
    }
}
