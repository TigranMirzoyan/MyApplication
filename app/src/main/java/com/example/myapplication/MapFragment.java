package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapFragment extends Fragment {
    private GoogleMap mMap; // Карта Google
    private FusedLocationProviderClient fusedLocationProviderClient; // Поставщик местоположения
    private AutocompleteSupportFragment autocompleteFragment; // Фрагмент автозаполнения для мест
    private Marker clickedMarker = null; // Выбранный маркер
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // Код запроса разрешения на местоположение
    private boolean isUserLocationVisible = false; // Показывается ли местоположение пользователя на карте
    private Button deleteButton; // Кнопка удаления маркера

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity()); // Получение поставщика местоположения

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); // Получение фрагмента карты

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment); // Получение фрагмента автозаполнения

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)); // Установка полей места для автозаполнения

        deleteButton = view.findViewById(R.id.button1);
        deleteButton.setVisibility(View.INVISIBLE); // Скрытие кнопки удаления

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Анимированное перемещение к выбранному месту
                }
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });

        checkLocationPermission(); // Проверка разрешения на местоположение

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().getApplicationContext(), "AIzaSyCCmIaUzr43cDsJmXee0li1d1aoq9SffKQ"); // Инициализация Places API
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                Button addButton = view.findViewById(R.id.button2);

                mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (clickedMarker != null) {
                            deleteButton.setVisibility(View.INVISIBLE);
                            clickedMarker = null;
                        }
                    }
                });

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true); // Включение отображения местоположения пользователя на карте
                }

                View compass = getView().findViewById(Integer.parseInt("1")); // Компас на карте
                if (compass != null) {
                    View locationCompass = ((View) compass.getParent()).findViewById(Integer.parseInt("5")); // Компас местоположения на карте
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationCompass.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    layoutParams.setMargins(30, 0, 0, 100); // Установка отступов для компаса местоположения
                    locationCompass.setLayoutParams(layoutParams);
                }

                mMap.getUiSettings().setMyLocationButtonEnabled(false); // Отключение кнопки местоположения на карте

                if (view != null) {
                    view.findViewById(R.id.MyLocationbtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveToCurrentLocation(); // Перемещение на текущее местоположение пользователя
                        }
                    });
                }

                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        checkIfUserLocationVisibleAndUpdateButton(); // Проверка видимости местоположения пользователя и обновление кнопки
                        moveDeleteButtonOverMarker(clickedMarker); // Перемещение кнопки удаления над маркером
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        clickedMarker = marker;
                        if (clickedMarker != null) {
                            moveDeleteButtonOverMarker(clickedMarker);
                            deleteButton.setVisibility(View.VISIBLE);
                        } else {
                            deleteButton.setVisibility(View.INVISIBLE);
                        }

                        if ("custom".equals(marker.getTag())) {
                            // It's a custom marker, so do not show the delete button.
                            deleteButton.setVisibility(View.INVISIBLE);
                        } else {
                            // It's not a custom marker, handle as before.
                            clickedMarker = marker;
                            moveDeleteButtonOverMarker(clickedMarker);
                            deleteButton.setVisibility(View.VISIBLE);
                        }
                        return true; // Indicate that we have handled the event
                    }
                });

                addButton.setOnClickListener(v -> addMarkerOnMapClick());
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickedMarker != null) {
                            clickedMarker.remove();
                            clickedMarker = null;
                            deleteButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                Marker customMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(40.177114, 44.502224))
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.church_marker))));
                customMarker.setTag("custom");
            }
        });
        return view;
    }

    private void addMarkerOnMapClick() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + ":" + latLng.longitude);
                mMap.addMarker(markerOptions); // Добавление маркера при клике на карту
                mMap.setOnMapClickListener(null);
            }
        });
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Анимированное перемещение на текущее местоположение пользователя
            }
        });
    }

    private void checkIfUserLocationVisibleAndUpdateButton() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                isUserLocationVisible = mMap.getProjection().getVisibleRegion().latLngBounds.contains(currentLatLng); // Проверка видимости местоположения пользователя на карте
                Button myLocationButton = getView().findViewById(R.id.MyLocationbtn);
                if (isUserLocationVisible) {
                    myLocationButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.my_location_visible)); // Обновление внешнего вида кнопки
                } else {
                    myLocationButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.my_location_not_visible));
                }
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Перемещение камеры к последнему известному местоположению пользователя
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE); // Запрос разрешения на местоположение
    }

    private void moveDeleteButtonOverMarker(Marker marker) {
        if (marker != null && mMap != null) {
            Point markerScreenPoint = mMap.getProjection().toScreenLocation(marker.getPosition());

            int translationX = markerScreenPoint.x - (deleteButton.getWidth() / 2);
            int translationY = markerScreenPoint.y - deleteButton.getHeight() - getResources().getDimensionPixelSize(R.dimen.marker_padding);

            deleteButton.setTranslationX(translationX);
            deleteButton.setTranslationY(translationY);
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
