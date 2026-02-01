package com.example.foodplanner.ui.auth.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {
    private TextInputLayout usernameTextInput;

    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameInput;

    private TextInputEditText passwordInput;
    private MaterialButton logInButton;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

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
        logInButton = view.findViewById(R.id.signupBtn);

        usernameInput = (TextInputEditText) usernameTextInput.getEditText();

        passwordInput = (TextInputEditText) passwordTextInput.getEditText();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleLogin();
            }
        });
    }

    private boolean isEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }
    private void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
    }

    private void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
    }
    private void handleLogin() {
        String input = usernameInput.getText().toString().trim();

        String password = passwordInput.getText().toString().trim();


        if (input.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        showLoading();
        if (isEmail(input)) {

            loginWithEmail(input, password);
        } else {

            findUserByUsername(input, password);
        }



        Toast.makeText(requireContext(), "Sign Up clicked", Toast.LENGTH_SHORT).show();
    }

    private void findUserByUsername(String username, String password) {
        CollectionReference usersRef =  db.collection("users");
        Query usernameQuery = usersRef.whereEqualTo("username", username).limit(1);
       usernameQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot query) {
               if (!query.isEmpty()) {
                   User user = query.getDocuments().get(0).toObject(User.class);
                   if (user != null) {
                       loginWithEmail(user.getEmail(), password);
                   }

                    } else {
                       hideLoading();
                        Toast.makeText(requireContext(),
                                "Username not found",
                                Toast.LENGTH_SHORT).show();
                    }
           }
       }).addOnFailureListener(e ->
                Toast.makeText(requireContext(),
                        "Error fetching user: " + e.getMessage(),
                        Toast.LENGTH_LONG).show()
        );
    }


    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                "Login successful",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Wrong password",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}