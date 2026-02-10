package com.example.foodplanner.presentation.profile;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;

import com.example.foodplanner.utility.UserPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // Views
    private TextView userNameText;
    private TextView userEmailDisplay;
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton backButton;
    private MaterialButton saveChangesButton;
    private MaterialButton logoutButton;
    private SwitchMaterial darkModeSwitch;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // SharedPreferences
    private UserPrefManager preferencesManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeFirebase();
        initializePreferences();
        initializeViews(view);
        loadUserData();
        setupClickListeners();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initializePreferences() {
        preferencesManager = new UserPrefManager(requireContext());
    }

    private void initializeViews(View view) {
        userNameText = view.findViewById(R.id.userNameText);
        userEmailDisplay = view.findViewById(R.id.userEmailDisplay);
        emailTextInput = view.findViewById(R.id.emailTextInput);
        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        backButton = view.findViewById(R.id.backButton);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
    }

    private void loadUserData() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();

            // Set display name
            if (displayName != null && !displayName.isEmpty()) {
                userNameText.setText(displayName);
            } else if (email != null) {
                // Use email username if no display name
                userNameText.setText(email.split("@")[0]);
            }

            // Set email
            if (email != null) {
                userEmailDisplay.setText(email);
                emailEditText.setText(email);
            }

            // Load user preferences from Firestore
            loadUserPreferences();
        } else {
            // User not logged in, redirect to auth
            Toast.makeText(requireContext(), "Please sign in first", Toast.LENGTH_SHORT).show();
            navigateToAuth();
        }
    }

    private void loadUserPreferences() {
        if (currentUser == null) return;

        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Load dark mode preference
                Boolean darkMode = documentSnapshot.getBoolean("darkMode");
                if (darkMode != null) {
                    darkModeSwitch.setChecked(darkMode);
                }
            }
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        saveChangesButton.setOnClickListener(v -> saveChanges());

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());

        // Save preferences when switches change
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                savePreference("darkMode", isChecked));
    }

    private void saveChanges() {
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        // Clear previous errors
        emailTextInput.setError(null);
        passwordTextInput.setError(null);

        // Validate email
        if (newEmail.isEmpty()) {
            emailTextInput.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailTextInput.setError("Invalid email format");
            return;
        }

        // Check if anything changed
        boolean emailChanged = !newEmail.equals(currentUser.getEmail());
        boolean passwordChanged = !newPassword.isEmpty();

        if (!emailChanged && !passwordChanged) {
            Toast.makeText(requireContext(), "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show re-authentication dialog if email or password is being changed
        if (emailChanged || passwordChanged) {
            showReAuthenticationDialog(newEmail, newPassword, emailChanged, passwordChanged);
        }
    }

    private void showReAuthenticationDialog(String newEmail, String newPassword,
                                            boolean emailChanged, boolean passwordChanged) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_reauth, null);

        TextInputEditText passwordInput = dialogView.findViewById(R.id.currentPasswordInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Your Identity")
                .setMessage("For security, please enter your current password")
                .setView(dialogView)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String currentPassword = passwordInput.getText().toString().trim();

                    if (currentPassword.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Please enter your current password",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    reAuthenticateAndUpdate(currentPassword, newEmail, newPassword,
                            emailChanged, passwordChanged);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void reAuthenticateAndUpdate(String currentPassword, String newEmail,
                                         String newPassword, boolean emailChanged,
                                         boolean passwordChanged) {
        showLoading();

        AuthCredential credential = EmailAuthProvider.getCredential(
                currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Re-authentication successful, now update
                    if (emailChanged) {
                        updateEmail(newEmail, newPassword, passwordChanged);
                    } else if (passwordChanged) {
                        updatePassword(newPassword);
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(requireContext(),
                            "Incorrect password. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateEmail(String newEmail, String newPassword, boolean alsoUpdatePassword) {
        currentUser.updateEmail(newEmail)
                .addOnSuccessListener(aVoid -> {
                    // Update email in Firestore
                    db.collection("users")
                            .document(currentUser.getUid())
                            .update("email", newEmail)
                            .addOnSuccessListener(v -> {
                                userEmailDisplay.setText(newEmail);

                                // Update SharedPreferences
                                preferencesManager.saveUserData(
                                        currentUser.getUid(),
                                        newEmail,
                                        preferencesManager.getUserName()
                                );

                                if (alsoUpdatePassword) {
                                    updatePassword(newPassword);
                                } else {
                                    hideLoading();
                                    Toast.makeText(requireContext(),
                                            "Email updated successfully!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    String errorMessage = "Failed to update email: " + e.getMessage();
                    if (e.getMessage().contains("already in use")) {
                        errorMessage = "This email is already in use by another account";
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void updatePassword(String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    passwordEditText.setText("");
                    Toast.makeText(requireContext(),
                            "Changes saved successfully!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(requireContext(),
                            "Failed to update password: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void savePreference(String key, boolean value) {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .update(key, value)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to save preference",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        showLoading();

        // Sign out from Firebase
        mAuth.signOut();

        // Clear SharedPreferences
        preferencesManager.clearUserData();

        hideLoading();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to auth screen
        navigateToAuth();
    }

    private void navigateToAuth() {
        NavController navController =
                NavHostFragment.findNavController(
                        requireActivity()
                                .getSupportFragmentManager()
                                .findFragmentById(R.id.nav_host_fragment_main)
                );

        navController.navigate(R.id.authFragment);
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
}