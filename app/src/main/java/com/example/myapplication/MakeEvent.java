package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MakeEvent extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Ensure you're inflating the correct layout for MakeEvent
        View view = inflater.inflate(R.layout.fragment_make_event, container, false);
        Button backButton = view.findViewById(R.id.button1); // Ensure your button ID is correct

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showHomeFragment(); // Call a method that shows HomeFragment
                }
            }
        });

        return view;
    }
}
