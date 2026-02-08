package com.example.foodplanner.data.datasource.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MealPlanFirestoreDatasource {
    private static final String TAG = "MealPlanFirestore";
    private static final String COLLECTION_MEAL_PLANS = "meal_plans";

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public MealPlanFirestoreDatasource() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private String getUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    private CollectionReference getUserMealPlansCollection() {
        String userId = getUserId();
        if (userId == null) {
            return null;
        }
        return db.collection("users")
                .document(userId)
                .collection(COLLECTION_MEAL_PLANS);
    }

    public void saveMealPlan(MealPlanFirestore mealPlan, MealPlanFirestoreNetworkResponse callback) {
        CollectionReference collection = getUserMealPlansCollection();
        if (collection == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        collection.add(mealPlan).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Meal plan saved to Firestore");
                callback.onSaveSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error saving meal plan", e);
                callback.onFailure(e.getMessage());
            }
        });

    }

    public void getAllMealPlans(MealPlanFirestoreNetworkResponse callback) {
        CollectionReference collection = getUserMealPlansCollection();
        if (collection == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<MealPlanFirestore> mealPlans = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            MealPlanFirestore mealPlan = document.toObject(MealPlanFirestore.class);
                            mealPlans.add(mealPlan);
                        }
                        callback.onFetchSuccess(mealPlans);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error fetching meal plans", e);
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void deleteMealPlan(String mealId, String dayOfWeek, String mealType, MealPlanFirestoreNetworkResponse callback) {
        CollectionReference collection = getUserMealPlansCollection();
        if (collection == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        collection.whereEqualTo("meal_id", mealId)
                .whereEqualTo("day_of_week", dayOfWeek)
                .whereEqualTo("meal_type", mealType)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        callback.onDeleteSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void deleteAllMealPlans(MealPlanFirestoreNetworkResponse callback) {
        CollectionReference collection = getUserMealPlansCollection();
        if (collection == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        collection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        callback.onDeleteSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

}