package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // инициализировать View
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // инициализировать фрагмент карты
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        // async map (выполнение задачи без ожидания завершения других задач. Это позволяет программе выполнять другие операции, не блокируя выполнение)
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {


            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // когда карта загружена
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        // при нажатии на карту инициализируются параметры маркера
                        MarkerOptions markerOptions = new MarkerOptions();

                        // установить положение маркера
                        markerOptions.position(latLng);

                        // установить название маркера
                        markerOptions.title(latLng.latitude + ":" + latLng.longitude);

                        // удалить все маркеры
                        Button button = view.findViewById(R.id.button1);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                googleMap.clear();
                            }
                        });

                        // анимация масштабирования маркера
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        // добавить маркер на карту
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });
        return view;
    }
}