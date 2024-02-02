package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.util.Calendar;

public class MakeEvent extends Fragment {

    private TextView textView;
    private int number = 2; // Устанавливаем изначальное значение в 2
    private Handler handler;
    private boolean isIncrementing = false;
    private boolean isDecrementing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_event, container, false);

        Button button1 = view.findViewById(R.id.button1);
        Button buttonMinus = view.findViewById(R.id.button2);
        Button buttonPlus = view.findViewById(R.id.button3);
        textView = view.findViewById(R.id.textview1);
        DatePicker datePicker = view.findViewById(R.id.datePicker);

        // Установите текущую дату в DatePicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, dayOfMonth, null);

        handler = new Handler();

        // Установка изначального значения текста
        textView.setText(String.valueOf(number));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        buttonMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isDecrementing = true;
                    decrementNumber();
                    handler.postDelayed(longPressMinusRunnable, 500);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isDecrementing = false;
                    handler.removeCallbacks(longPressMinusRunnable);
                }
                return true;
            }
        });

        buttonPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isIncrementing = true;
                    incrementNumber();
                    handler.postDelayed(longPressPlusRunnable, 500);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isIncrementing = false;
                    handler.removeCallbacks(longPressPlusRunnable);
                }
                return true;
            }
        });

        return view;
    }

    private Runnable longPressMinusRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDecrementing) {
                decrementNumber();
                handler.postDelayed(this, 75); // Измените скорость уменьшения здесь
            }
        }
    };

    private Runnable longPressPlusRunnable = new Runnable() {
        @Override
        public void run() {
            if (isIncrementing) {
                incrementNumber();
                handler.postDelayed(this, 75); // Измените скорость увеличения здесь
            }
        }
    };

    private void decrementNumber() {
        if (number > 2) { // Проверка на минимальное значение
            number--;
            textView.setText(String.valueOf(number));
        }
    }

    private void incrementNumber() {
        if (number < 100) { // Проверка на максимальное значение
            number++;
            textView.setText(String.valueOf(number));
        }
    }
}
