package com.example.foodplanner.ui.auth.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpFragment extends Fragment {

    private TextInputLayout usernameTextInput;
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputLayout confirmPasswordTextInput;
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameTextInput = view.findViewById(R.id.username_text_input_signup);
        emailTextInput = view.findViewById(R.id.email_text_input_signup);
        passwordTextInput = view.findViewById(R.id.password_text_input_signup);
        confirmPasswordTextInput = view.findViewById(R.id.confirm_password_text_input_signup);

        MaterialButton signUpButton = view.findViewById(R.id.signupBtn);

        usernameInput = (TextInputEditText) usernameTextInput.getEditText();
        emailInput = (TextInputEditText) emailTextInput.getEditText();
        passwordInput = (TextInputEditText) passwordTextInput.getEditText();
        confirmPasswordInput = (TextInputEditText) confirmPasswordTextInput.getEditText();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });
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
    private void handleSignUp() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        usernameTextInput.setError(null);
        emailTextInput.setError(null);
        passwordTextInput.setError(null);
        confirmPasswordTextInput.setError(null);

        if (username.isEmpty()) {
            hideLoading();
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            hideLoading();
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordTextInput.setError("Please confirm your password");
            confirmPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordTextInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }

        checkUsernameExistsAndCreateUser(username, email, password);
        Toast.makeText(requireContext(), "Sign Up clicked", Toast.LENGTH_SHORT).show();
    }

    private void checkUsernameExistsAndCreateUser(String username , String email , String password){
        showLoading();
        CollectionReference usersRef = db.collection("users");
        Query usernameQuery = usersRef.whereEqualTo("username", username).limit(1);
        usernameQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        public void onSuccess(QuerySnapshot querySnapshot) {
            if (!querySnapshot.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Username already taken",
                        Toast.LENGTH_SHORT).show();
            } else {
                createFirebaseUser(username, email, password);
            }
        }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),
                        "Error checking username: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
    private void createFirebaseUser(String username, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {

                            User user = new User(username, email);
                            saveUserToFirestore(firebaseUser.getUid(), user);
                        }
                        else {
                            hideLoading();
                        }
                        Toast.makeText(requireContext(),
                                "Sign up successful ",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        hideLoading();
                        Toast.makeText(requireContext(),
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, User user) {
        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoading();
                        Toast.makeText(requireContext(), "Account created ", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_authFragment_to_startFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                        Toast.makeText(requireContext(),
                                "Error saving user: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}