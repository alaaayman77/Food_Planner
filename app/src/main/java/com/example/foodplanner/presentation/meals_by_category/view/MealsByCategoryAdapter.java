package com.example.foodplanner.presentation.meals_by_category.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MealsByCategoryAdapter extends RecyclerView.Adapter<MealsByCategoryAdapter.MealsByCategoryViewHolder> {

    private List<MealsByCategory> mealsByCategoriesList;
    private OnMealByCategoryClick listener;


    public MealsByCategoryAdapter(OnMealByCategoryClick listener) {
        this.mealsByCategoriesList = new ArrayList<>();
        this.listener = listener;
    }

    public void setMeals(List<MealsByCategory> meals) {
        this.mealsByCategoriesList = meals;
        notifyDataSetChanged();
    }

    public void setOnMealActionListener(OnMealByCategoryClick listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MealsByCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_meal_category, parent, false);
        return new MealsByCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealsByCategoryViewHolder holder, int position) {
        MealsByCategory meal = mealsByCategoriesList.get(position);
        holder.mealTitle.setText(meal.getMealName());

        Glide.with(holder.itemView)
                .load(meal.getMealThumbnail())
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg)
                .centerCrop()
                .into(holder.mealThumb);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.setOnMealClick(meal);
            }
        });

        if (holder.btnAddToPlan != null) {
            holder.btnAddToPlan.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToPlanClick(meal);
                }

            });
        }


//        if (holder.favoriteCard != null) {
//            holder.favoriteCard.setOnClickListener(v -> {

//            });
//        }
    }

    @Override
    public int getItemCount() {
        return mealsByCategoriesList.size();
    }

    class MealsByCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView mealTitle;
        ImageView mealThumb;
         MaterialCardView btnAddToPlan; // Add this

        public MealsByCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.mealTitle_category);
            mealThumb = itemView.findViewById(R.id.mealImage_category);
            btnAddToPlan = itemView.findViewById(R.id.mealPlanCard); // Add this (if exists in layout)
        }
    }
}