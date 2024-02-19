package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import androidx.core.content.ContextCompat;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.gms.common.api.Status;

import androidx.appcompat.widget.SearchView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int buttonVisBool = 0;
    private AutocompleteSupportFragment autocompleteFragment;
    Marker clickedMarker = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // инициализировать View
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // инициализировать фрагмент карты
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // async map (выполнение задачи без ожидания завершения других задач. Это позволяет программе выполнять другие операции, не блокируя выполнение)

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().getApplicationContext(), "AIzaSyCCmIaUzr43cDsJmXee0li1d1aoq9SffKQ");
        }

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("MapFragment", "An error occurred: " + status);
            }
        });

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;


                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // Permission is granted, enable the My Location button
                    googleMap.setMyLocationEnabled(true);
                }

                googleMap.setMyLocationEnabled(true);

                Button deleteButton = view.findViewById(R.id.button1);
                Button addButton = view.findViewById(R.id.button2);
                deleteButton.setVisibility(View.INVISIBLE);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (clickedMarker == null || !clickedMarker.equals(marker)) {
                            deleteButton.setVisibility(View.VISIBLE);

                            // Get the screen coordinates of the marker
                            Point markerPoint = googleMap.getProjection().toScreenLocation(marker.getPosition());

                            // Adjust the position of the button to be above the marker
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) deleteButton.getLayoutParams();
                            layoutParams.leftMargin = markerPoint.x - (deleteButton.getWidth() / 2);
                            layoutParams.topMargin = markerPoint.y - deleteButton.getHeight() - 95;
                            deleteButton.setLayoutParams(layoutParams);

                            clickedMarker = marker;
                        } else {
                            deleteButton.setVisibility(View.INVISIBLE);
                            clickedMarker = null;
                        }
                        return true;
                    }
                });


                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickedMarker != null) {
                            clickedMarker.remove();
                            deleteButton.setVisibility(View.INVISIBLE);
                            clickedMarker = null;
                        }
                    }
                });

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMarker();
                    }
                });
            }

        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            if (googleMap != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        return view;
    }


        private void clearMarkers() {
        googleMap.clear();
    }

    private void addMarker() {
        if (googleMap != null) {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(latLng.latitude + ":" + latLng.longitude);

                    Marker marker = googleMap.addMarker(markerOptions);

                    googleMap.setOnMapClickListener(null);
                }
            });
        }
    }
}

