package com.example.foodplanner.presentation.filter_results.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;


import java.util.ArrayList;
import java.util.List;

public class FilterResultsAdapter extends RecyclerView.Adapter<FilterResultsAdapter.MealViewHolder> {


    private List<RecipeDetails> meals;
   private OnMealClick listener;


    public FilterResultsAdapter( OnMealClick listener) {

        this.meals = new ArrayList<>();
         this.listener = listener;
    }

    public void setMeals(List<RecipeDetails> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        RecipeDetails meal = meals.get(position);
        holder.mealName.setText(meal.getMealName());
        Glide.with(holder.itemView)
                .load(meal.getStrMealThumbnail())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.mealThumb);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMealClicked(meal);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealThumb;
        private TextView mealName;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealThumb = itemView.findViewById(R.id.mealThumb);
            mealName = itemView.findViewById(R.id.mealName);

        }


    }
}