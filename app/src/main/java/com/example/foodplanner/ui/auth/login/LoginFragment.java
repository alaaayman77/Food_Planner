package com.example.foodplanner.ui.auth.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodplanner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    private TextInputLayout usernameTextInput;

    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameInput;

    private TextInputEditText passwordInput;
    private MaterialButton logInButton;



    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameTextInput = view.findViewById(R.id.username_text_input_login);
        passwordTextInput = view.findViewById(R.id.password_text_input_login);
        logInButton = view.findViewById(R.id.loginBtn);

        usernameInput = (TextInputEditText) usernameTextInput.getEditText();

        passwordInput = (TextInputEditText) passwordTextInput.getEditText();

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });
    }

    private void handleSignUp() {
        String username = usernameInput.getText().toString().trim();

        String password = passwordInput.getText().toString().trim();


        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }



        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }


        Toast.makeText(requireContext(), "Sign Up clicked", Toast.LENGTH_SHORT).show();
    }
}