package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomDialogFragment extends DialogFragment {
    private String title;
    private String message;
    private boolean isFavorited = false;

    // Constructor to pass title and message
    public CustomDialogFragment(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_history, null); // Assuming dialog_history.xml is your custom dialog layout

        ImageButton cancelButton = view.findViewById(R.id.cancelID);
        Button starButton = view.findViewById(R.id.star_button); // The ID of your button
        TextView titleTextView = view.findViewById(R.id.tvAlertTitle);
        TextView messageTextView = view.findViewById(R.id.tvAlertMessage);

        // Set title and message
        titleTextView.setText(title);
        messageTextView.setText(message);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        starButton.setBackgroundResource(isFavorited ? R.drawable.my_favorite_place : R.drawable.my_not_favorite_place);

        // Set up the click listener to toggle the drawable
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the state
                isFavorited = !isFavorited;

                // Change the button's background drawable based on the toggled state
                starButton.setBackgroundResource(isFavorited ? R.drawable.my_favorite_place : R.drawable.my_not_favorite_place);
            }
        });

        return dialog;
    }

}
