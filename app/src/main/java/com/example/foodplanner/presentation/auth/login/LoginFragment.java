package com.example.foodplanner.presentation.auth.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.User;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LoginFragment extends Fragment {
    private TextInputLayout usernameTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton logInButton;
    private TextView guest_tv;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MealsRepository mealsRepository;

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
        guest_tv = view.findViewById(R.id.guest_mode);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mealsRepository = new MealsRepository(requireContext());

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        guest_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_authFragment_to_startFragment);
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
                        // Sync meal plans from Firestore after successful login
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
                    fetchAndSaveMealPlan(firestorePlan, new SyncCallback() {
                        @Override
                        public void onComplete() {
                            syncedCount[0]++;
                            if (syncedCount[0] == totalPlans) {
                                hideLoading();
                                Toast.makeText(requireContext(),
                                        "Login successful & " + totalPlans + " meals synced",
                                        Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_authFragment_to_startFragment);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDeleteSuccess() {
                // Not used
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