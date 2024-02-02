package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smoothBottomBar = findViewById(R.id.bottombar);

        // Отображаем HomeFragment при запуске приложения
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new HomeFragment());
        fragmentTransaction.commit();

        // менять фрагменты
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {

                // home
                if (i == 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new HomeFragment());
                    fragmentTransaction.commit();
                }

                // profile
                if (i == 1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new ProfileFragment());
                    fragmentTransaction.commit();
                }

                // map
                if (i == 2) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new MapFragment());
                    fragmentTransaction.commit();
                }

                // messenger
                if (i == 3) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new MessengerFragment());
                    fragmentTransaction.commit();
                }
                return false;
            }
        });
    }
}
