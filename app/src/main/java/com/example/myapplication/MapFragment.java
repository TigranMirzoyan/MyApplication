package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutocompleteSupportFragment autocompleteFragment;
    Marker clickedMarker = null; // Для отслеживания выбранного маркера
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // Код запроса разрешения местоположения
    private boolean isUserLocationVisible = false; // Флаг видимости местоположения пользователя

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инициализация View фрагмента
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Настройка клиента местоположения
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Инициализация и настройка фрагмента карты
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Обработчик выбора места через автокомплит
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Перемещение камеры к выбранному месту
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i("MapFragment", " " + status); // Обработка ошибок при выборе места
            }
        });

        // Проверка разрешений на доступ к местоположению при создании фрагмента
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем последнее известное местоположение
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        // Проверяем, получено ли местоположение
                        if (location != null) {
                            // Создаем объект LatLng с координатами местоположения
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Если объект карты не null, перемещаем камеру к текущему местоположению
                            if (mMap != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            }
                        }
                    });
        } else {
            // Если разрешение не получено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Инициализация и настройка фрагмента автокомплита для поиска мест
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().getApplicationContext(), "AIzaSyCCmIaUzr43cDsJmXee0li1d1aoq9SffKQ"); // Инициализация Places API с ключом API
        }

        // Когда карта готова, инициализируем её и настраиваем UI элементы
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                Button deleteButton = view.findViewById(R.id.button1);
                Button addButton = view.findViewById(R.id.button2);
                deleteButton.setVisibility(View.INVISIBLE);

                mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        // If the camera starts moving for any reason, hide the delete button
                        if (clickedMarker != null) {
                            deleteButton.setVisibility(View.INVISIBLE);
                            clickedMarker = null; // Optional: Reset the clicked marker if desired
                        }
                    }
                });

                // находим компас на карте и перенастраиваем его положение
                View view = getView();
                if (view != null && view.findViewById(Integer.parseInt("1")) != null) {
                    View locationCompass = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationCompass.getLayoutParams();

                    // Удаляем верхнее выравнивание и добавляем выравнивание к нижней и левой части экрана
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

                    layoutParams.setMargins(30, 0, 0, 100); // Задаем отступы для компаса
                    locationCompass.setLayoutParams(layoutParams);
                }

                // Проверяем разрешение на доступ к местоположению и, если есть, включаем отображение местоположения пользователя на карте
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                // Отключаем кнопку местоположения по умолчанию, используем свою кнопку для этого
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                // Настройка действия кнопки моего местоположения для перемещения к текущему местоположению пользователя
                if (view != null) {
                    view.findViewById(R.id.MyLocationbtn).setOnClickListener(v -> moveToCurrentLocation());
                }

                // Устанавливаем слушателя на изменение позиции камеры для обновления UI в зависимости от видимости местоположения пользователя
                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        checkIfUserLocationVisibleAndUpdateButton();
                    }
                });

                // Устанавливаем слушателя кликов по маркерам на карте для отображения или скрытия кнопки удаления
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Если кликнутый маркер не выбран ранее или не равен текущему, показываем кнопку удаления
                        if (clickedMarker == null || !clickedMarker.equals(marker)) {
                            deleteButton.setVisibility(View.VISIBLE);

                            // Рассчитываем позицию кнопки удаления относительно маркера
                            Point markerPoint = mMap.getProjection().toScreenLocation(marker.getPosition());
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) deleteButton.getLayoutParams();
                            layoutParams.leftMargin = markerPoint.x - (deleteButton.getWidth() / 2);
                            layoutParams.topMargin = markerPoint.y - deleteButton.getHeight() - 95;
                            deleteButton.setLayoutParams(layoutParams);

                            clickedMarker = marker; // Сохраняем выбранный маркер
                        } else {
                            // Если маркер уже выбран, скрываем кнопку удаления
                            deleteButton.setVisibility(View.INVISIBLE);
                            clickedMarker = null;
                        }
                        return true; // Возвращаем true, чтобы не происходило дополнительных действий по умолчанию
                    }
                });

                // Назначение обработчика нажатия на кнопку удаления маркера
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Проверяем, был ли выбран маркер для удаления
                        if (clickedMarker != null) {
                            clickedMarker.remove(); // Удаляем маркер с карты
                            deleteButton.setVisibility(View.INVISIBLE); // Скрываем кнопку удаления
                            clickedMarker = null; // Сбрасываем выбранный маркер
                        }
                    }
                });

                // Назначение обработчика нажатия на кнопку добавления маркера
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMarker(); // Вызываем метод добавления маркера на карту
                    }
                });
            }
        });
        return view; // Возвращаем view для отображения фрагмента
    }

    // Метод для добавления нового маркера на карту при клике пользователя по карте
    private void addMarker() {
        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    // Создаем новые настройки маркера с координатами клика и заголовком, содержащим эти координаты
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(latLng.latitude + ":" + latLng.longitude);

                    Marker marker = mMap.addMarker(markerOptions);
                    mMap.setOnMapClickListener(null); // Отключаем слушатель после добавления маркера
                }
            });
        }
    }

    // Метод для перемещения камеры к текущему местоположению пользователя
    private void moveToCurrentLocation() {
        // Проверяем наличие разрешений на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Если разрешений нет, выходим из метода
        }
        // Получаем последнее известное местоположение
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) { // Проверяем, что местоположение получено
                // Создаем объект LatLng с координатами местоположения
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                // Перемещаем камеру к текущему местоположению с анимацией
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }

    private void checkIfUserLocationVisibleAndUpdateButton() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Получаем последнее известное местоположение
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null && mMap != null) { // Проверяем, что местоположение и карта доступны
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                // Проверяем, находится ли текущее местоположение в пределах видимой области карты
                isUserLocationVisible = mMap.getProjection().getVisibleRegion().latLngBounds.contains(currentLatLng);

                Button myLocationButton = getView().findViewById(R.id.MyLocationbtn);
                if (isUserLocationVisible) {
                    myLocationButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.my_location_visible));
                } else {
                    myLocationButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.my_location_not_visible));
                }
            }
        });
    }
}
