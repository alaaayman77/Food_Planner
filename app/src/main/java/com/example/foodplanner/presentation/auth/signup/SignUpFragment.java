package com.example.foodplanner.presentation.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.User;
import com.example.foodplanner.utility.UserPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    private TextInputLayout usernameTextInput;
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputLayout confirmPasswordTextInput;
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private TextView guest_tv;
    private MaterialButton googleBtn;
    private MaterialButton twitterBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private UserPrefManager preferencesManager;
    OAuthProvider.Builder provider;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Google Sign-In launcher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Google Sign-In result received. Result code: " + result.getResultCode());
                    if (result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    } else {
                        hideLoading();
                        Toast.makeText(requireContext(), "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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

        initializeViews(view);
        initializeFirebase();
        initializePreferences();
        setupGoogleSignIn();
        setupTwitterSignIn();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        usernameTextInput = view.findViewById(R.id.username_text_input_signup);
        emailTextInput = view.findViewById(R.id.email_text_input_signup);
        passwordTextInput = view.findViewById(R.id.password_text_input_signup);
        confirmPasswordTextInput = view.findViewById(R.id.confirm_password_text_input_signup);
        googleBtn = view.findViewById(R.id.googleBtn);
        guest_tv = view.findViewById(R.id.guest_mode);
        twitterBtn = view.findViewById(R.id.facebookBtn);
        usernameInput = (TextInputEditText) usernameTextInput.getEditText();
        emailInput = (TextInputEditText) emailTextInput.getEditText();
        passwordInput = (TextInputEditText) passwordTextInput.getEditText();
        confirmPasswordInput = (TextInputEditText) confirmPasswordTextInput.getEditText();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initializePreferences() {
        preferencesManager = new UserPrefManager(requireContext());
    }

    private void setupTwitterSignIn() {
        provider = OAuthProvider.newBuilder("twitter.com");
    }

    private void setupGoogleSignIn() {
        try {
            String webClientId = getString(R.string.client_id);
            Log.d(TAG, "Web Client ID loaded successfully");

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            Log.d(TAG, "GoogleSignInClient initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Google Sign-In", e);
            Toast.makeText(requireContext(),
                    "Google Sign-In setup failed. Check your configuration.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        MaterialButton signUpButton = requireView().findViewById(R.id.signupBtn);

        signUpButton.setOnClickListener(v -> handleSignUp());

        googleBtn.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            signInWithGoogle();
        });

        guest_tv.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_authFragment_to_startFragment)
        );

        twitterBtn.setOnClickListener(v -> {
            Log.d(TAG, "Twitter Sign-In button clicked");
            signInWithTwitter();
        });
    }

    private void signInWithTwitter() {
        showLoading();
        Log.d(TAG, "Starting Twitter Sign-In flow");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's a pending auth operation
            pendingResultTask
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "Twitter sign-in successful");
                        handleTwitterAuthSuccess(authResult);
                    })
                    .addOnFailureListener(e -> {
                        hideLoading();
                        Log.e(TAG, "Twitter sign-in failed", e);
                        Toast.makeText(requireContext(),
                                "Twitter sign-in failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            // Start new Twitter sign-in flow
            mAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "Twitter sign-in successful");
                        handleTwitterAuthSuccess(authResult);
                    })
                    .addOnFailureListener(e -> {
                        hideLoading();
                        Log.e(TAG, "Twitter sign-in failed", e);
                        Toast.makeText(requireContext(),
                                "Twitter sign-in failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void handleTwitterAuthSuccess(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();

        if (firebaseUser != null) {
            // Check if user exists in Firestore
            checkUserExistsAndSave(firebaseUser);
        } else {
            hideLoading();
            Toast.makeText(requireContext(),
                    "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithGoogle() {
        showLoading();
        Log.d(TAG, "Starting Google Sign-In flow");

        // Sign out first to always show account picker
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Google sign in successful: " + account.getEmail());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            hideLoading();
            Log.e(TAG, "Google sign in failed. Status code: " + e.getStatusCode(), e);

            String errorMessage;
            switch (e.getStatusCode()) {
                case 7:
                    errorMessage = "Network error. Please check your internet connection.";
                    break;
                case 10:
                    errorMessage = "Developer error. Please add SHA-1 certificate to Firebase Console.";
                    break;
                case 12500:
                    errorMessage = "Google Play Services not available. Please update.";
                    break;
                case 12501:
                    errorMessage = "Sign-in cancelled.";
                    break;
                default:
                    errorMessage = "Google sign-in failed. Error code: " + e.getStatusCode();
            }

            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "Authenticating with Firebase using Google token");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Check if user already exists in Firestore
                            checkUserExistsAndSave(firebaseUser);
                        }
                    } else {
                        hideLoading();
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        Toast.makeText(requireContext(),
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserExistsAndSave(FirebaseUser firebaseUser) {
        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String email = firebaseUser.getEmail();
                    String username = firebaseUser.getDisplayName() != null
                            ? firebaseUser.getDisplayName()
                            : email.split("@")[0];

                    if (documentSnapshot.exists()) {
                        // User already exists, get existing username
                        String existingUsername = documentSnapshot.getString("username");
                        if (existingUsername != null) {
                            username = existingUsername;
                        }

                        // Save login state to SharedPreferences
                        preferencesManager.saveUserData(firebaseUser.getUid(), email, username);

                        hideLoading();
                        Toast.makeText(requireContext(),
                                "Already signed in! Redirecting...",
                                Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_authFragment_to_startFragment);
                    } else {
                        // New user, create account
                        User user = new User(username, email);
                        saveUserToFirestore(firebaseUser.getUid(), user);
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Log.e(TAG, "Error checking user existence", e);
                    Toast.makeText(requireContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
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
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
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
    }

    private void checkUsernameExistsAndCreateUser(String username, String email, String password) {
        showLoading();
        CollectionReference usersRef = db.collection("users");
        Query usernameQuery = usersRef.whereEqualTo("username", username).limit(1);

        usernameQuery.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                hideLoading();
                Toast.makeText(requireContext(),
                        "Username already taken",
                        Toast.LENGTH_SHORT).show();
            } else {
                createFirebaseUser(username, email, password);
            }
        }).addOnFailureListener(e -> {
            hideLoading();
            Toast.makeText(requireContext(),
                    "Error checking username: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }

    private void createFirebaseUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            User user = new User(username, email);
                            saveUserToFirestore(firebaseUser.getUid(), user);
                        } else {
                            hideLoading();
                        }
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
                .addOnSuccessListener(aVoid -> {
                    // Save login state to SharedPreferences
                    preferencesManager.saveUserData(uid, user.getEmail(), user.getUsername());

                    hideLoading();
                    Toast.makeText(requireContext(),
                            "Account created successfully!",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_authFragment_to_startFragment);
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(requireContext(),
                            "Error saving user: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}