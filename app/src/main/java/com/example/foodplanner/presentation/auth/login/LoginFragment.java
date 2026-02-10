package com.example.foodplanner.presentation.auth.login;

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
import com.example.foodplanner.data.MealsRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

public class LoginFragment extends Fragment implements LoginView {

    private static final String TAG = "LoginFragment";


    private TextInputLayout usernameTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton logInButton;
    private MaterialButton googleBtn;
    private MaterialButton twitterBtn;
    private TextView guest_tv;
    private LoginPresenter presenter;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeFirebase();
        initializePresenter();
        setupGoogleSignIn();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        usernameTextInput = view.findViewById(R.id.username_text_input_login);
        passwordTextInput = view.findViewById(R.id.password_text_input_login);
        logInButton = view.findViewById(R.id.signupBtn);
        googleBtn = view.findViewById(R.id.googleBtn);
        twitterBtn = view.findViewById(R.id.facebookBtn);
        guest_tv = view.findViewById(R.id.guest_mode);
        usernameInput = (TextInputEditText) usernameTextInput.getEditText();
        passwordInput = (TextInputEditText) passwordTextInput.getEditText();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializePresenter() {
        MealsRepository mealsRepository = new MealsRepository(requireContext());
        presenter = new LoginPresenterImp(this, mealsRepository, requireContext());
    }

    private void setupGoogleSignIn() {
        try {
            String webClientId = getString(R.string.default_web_client_id);
            Log.d(TAG, "Web Client ID loaded successfully");

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            Log.d(TAG, "GoogleSignInClient initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Google Sign-In", e);
            showError("Google Sign-In setup failed. Check your configuration.");
        }
    }

    private void setupClickListeners() {
        logInButton.setOnClickListener(v -> presenter.handleLogin());

        googleBtn.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            signInWithGoogle();
        });

        twitterBtn.setOnClickListener(v -> {
            Log.d(TAG, "Twitter Sign-In button clicked");
            signInWithTwitter();
        });

        guest_tv.setOnClickListener(v -> presenter.handleGuestMode());
    }

    private void signInWithGoogle() {
        presenter.handleGoogleSignIn();

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
            presenter.onGoogleSignInResult(account.getIdToken());
        } catch (ApiException e) {
            presenter.onGoogleSignInError(e.getStatusCode());
        }
    }

    private void signInWithTwitter() {
        presenter.handleTwitterSignIn();

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();

        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "Twitter sign-in successful (pending)");
                        presenter.onTwitterSignInResult(authResult);
                    })
                    .addOnFailureListener(e -> presenter.onTwitterSignInError(e));
        } else {
            mAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "Twitter sign-in successful");
                        presenter.onTwitterSignInResult(authResult);
                    })
                    .addOnFailureListener(e -> presenter.onTwitterSignInError(e));
        }
    }



    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_authFragment_to_startFragment);
    }

    @Override
    public void setUsernameError(String error) {
        usernameInput.setError(error);
        usernameInput.requestFocus();
    }

    @Override
    public void setPasswordError(String error) {
        passwordInput.setError(error);
        passwordInput.requestFocus();
    }

    @Override
    public void clearErrors() {
        usernameInput.setError(null);
        passwordInput.setError(null);
    }

    @Override
    public String getUsername() {
        return usernameInput.getText() != null ? usernameInput.getText().toString() : "";
    }

    @Override
    public String getPassword() {
        return passwordInput.getText() != null ? passwordInput.getText().toString() : "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}