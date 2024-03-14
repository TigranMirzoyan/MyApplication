package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogInFragment extends Fragment {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonToRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            MainActivity activity = (MainActivity) getActivity();
            Fragment profileFragment = new ProfileFragment(); // Replace with the fragment you want to display
            activity.switchFragment(profileFragment);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        buttonLogin = view.findViewById(R.id.btn_login);
        progressBar = view.findViewById(R.id.progressbar);
        buttonToRegister = view.findViewById(R.id.registerbutton);

        buttonToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveState();
                MainActivity activity = (MainActivity) getActivity();
                Fragment registerFragment = new RegisterFragment();
                activity.switchFragment(registerFragment);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trim to remove leading and trailing spaces
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Check for empty fields before showing the ProgressBar and attempting to log in
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method early if email is empty
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method early if password is empty
                }

                // If both email and password fields have been filled, show the ProgressBar and attempt to log in
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Hide the ProgressBar regardless of the task's success or failure
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Inform the user of successful login
                                    Toast.makeText(getActivity().getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                                    // Proceed with switching to the profile fragment
                                    MainActivity activity = (MainActivity) getActivity();
                                    if (activity != null) {
                                        Fragment profileFragment = new ProfileFragment(); // Replace with the fragment you want to display
                                        activity.switchFragment(profileFragment);
                                    }
                                } else {
                                    // Inform the user of failed authentication
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        restoreState();

        return view;
    }

    private void saveState() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        String password = sharedPref.getString("password", "");
        editTextEmail.setText(email);
        editTextPassword.setText(password);
    }


}

