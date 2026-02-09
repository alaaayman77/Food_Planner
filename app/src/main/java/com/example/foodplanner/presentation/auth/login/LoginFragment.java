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
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.User;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private TextInputLayout usernameTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton logInButton;
    private MaterialButton googleBtn;
    private TextView guest_tv;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MealsRepository mealsRepository;
    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        setupGoogleSignIn();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        usernameTextInput = view.findViewById(R.id.username_text_input_login);
        passwordTextInput = view.findViewById(R.id.password_text_input_login);
        logInButton = view.findViewById(R.id.signupBtn);
        googleBtn = view.findViewById(R.id.googleBtn);
        usernameInput = (TextInputEditText) usernameTextInput.getEditText();
        passwordInput = (TextInputEditText) passwordTextInput.getEditText();
        guest_tv = view.findViewById(R.id.guest_mode);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mealsRepository = new MealsRepository(requireContext());
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
            Toast.makeText(requireContext(),
                    "Google Sign-In setup failed. Check your configuration.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        logInButton.setOnClickListener(v -> handleLogin());

        googleBtn.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            signInWithGoogle();
        });

        guest_tv.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_authFragment_to_startFragment)
        );
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
                            // Check if user exists in Firestore, if not create one
                            checkAndCreateUserInFirestore(firebaseUser);
                            // Sync meal plans
                            syncMealPlans();
                        }
                    } else {
                        hideLoading();
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        Toast.makeText(requireContext(),
                                "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndCreateUserInFirestore(FirebaseUser firebaseUser) {
        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // User doesn't exist, create new user
                        String email = firebaseUser.getEmail();
                        String username = firebaseUser.getDisplayName() != null
                                ? firebaseUser.getDisplayName()
                                : email.split("@")[0];

                        User user = new User(username, email);
                        db.collection("users")
                                .document(firebaseUser.getUid())
                                .set(user);
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
    }

    private void findUserByUsername(String username, String password) {
        CollectionReference usersRef = db.collection("users");
        Query usernameQuery = usersRef.whereEqualTo("username", username).limit(1);

        usernameQuery.get().addOnSuccessListener(query -> {
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
        }).addOnFailureListener(e -> {
            hideLoading();
            Toast.makeText(requireContext(),
                    "Error fetching user: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        syncMealPlans();
                    } else {
                        hideLoading();
                        Toast.makeText(requireContext(),
                                "Wrong password",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncMealPlans() {
        mealsRepository.getAllMealPlansFromFirestore(new MealPlanFirestoreNetworkResponse() {
            @Override
            public void onSaveSuccess() {
            }

            @Override
            public void onFetchSuccess(List<MealPlanFirestore> firestorePlans) {
                if (firestorePlans.isEmpty()) {
                    hideLoading();
                    Toast.makeText(requireContext(),
                            "Login successful",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_authFragment_to_startFragment);
                    return;
                }

                int totalPlans = firestorePlans.size();
                final int[] syncedCount = {0};

                for (MealPlanFirestore firestorePlan : firestorePlans) {
                    fetchAndSaveMealPlan(firestorePlan, () -> {
                        syncedCount[0]++;
                        if (syncedCount[0] == totalPlans) {
                            hideLoading();
                            Toast.makeText(requireContext(),
                                    "Login successful & " + totalPlans + " meals synced",
                                    Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_authFragment_to_startFragment);
                        }
                    });
                }
            }

            @Override
            public void onDeleteSuccess() {
            }

            @Override
            public void onFailure(String error) {
                hideLoading();
                Toast.makeText(requireContext(),
                        "Login successful but sync failed: " + error,
                        Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_authFragment_to_startFragment);
            }
        });
    }

    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan, SyncCallback callback) {
        mealsRepository.getRecipeDetails(firestorePlan.getMealId(), new RecipeDetailsNetworkResponse() {
            @Override
            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
                if (!recipeDetailsList.isEmpty()) {
                    RecipeDetails details = recipeDetailsList.get(0);

                    MealPlan mealPlan = new MealPlan(
                            details.getIdMeal(),
                            firestorePlan.getMealType(),
                            firestorePlan.getDayOfWeek(),
                            details.getMealName(),
                            details.getStrMealThumbnail(),
                            details.getMealCategory(),
                            details.getMealArea(),
                            details.getMealInstructions()
                    );
                    mealPlan.setTimestamp(firestorePlan.getTimestamp());

                    mealsRepository.insertMealToMealPlan(mealPlan);
                }
                callback.onComplete();
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onComplete();
            }

            @Override
            public void onServerError(String errorMessage) {
                callback.onComplete();
            }
        });
    }

    private interface SyncCallback {
        void onComplete();
    }
}