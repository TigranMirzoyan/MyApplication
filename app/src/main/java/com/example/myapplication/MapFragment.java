package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;


import androidx.appcompat.widget.SearchView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView searchView;
    private int buttonVisBool = 0;


    Marker clickedMarker = null;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // инициализировать View
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // инициализировать SearchView
        searchView = view.findViewById(R.id.searchView);

        // инициализировать фрагмент карты
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // async map (выполнение задачи без ожидания завершения других задач. Это позволяет программе выполнять другие операции, не блокируя выполнение)


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null && !location.equals("")) {
                    Geocoder geocoder = new Geocoder(requireContext());

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                    }
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

