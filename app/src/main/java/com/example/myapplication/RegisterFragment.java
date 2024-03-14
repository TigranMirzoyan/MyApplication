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


public class RegisterFragment extends Fragment {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg, buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        buttonReg = view.findViewById(R.id.reg_button);
        progressBar = view.findViewById(R.id.progressbar);
        buttonLogin = view.findViewById(R.id.loginbutton);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveState();
                MainActivity activity = (MainActivity) getActivity();
                Fragment logInFragment = new LogInFragment();
                activity.switchFragment(logInFragment);
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();
                                    MainActivity activity = (MainActivity) getActivity();
                                    Fragment logInFragment = new LogInFragment();
                                    activity.switchFragment(logInFragment);
                                } else {
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
        editor.putString("register_email", editTextEmail.getText().toString());
        editor.putString("register_password", editTextPassword.getText().toString());
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("register_email", "");
        String password = sharedPref.getString("register_password", "");
        if(editTextEmail != null && editTextPassword != null) {
            editTextEmail.setText(email);
            editTextPassword.setText(password);
        }
    }

}
