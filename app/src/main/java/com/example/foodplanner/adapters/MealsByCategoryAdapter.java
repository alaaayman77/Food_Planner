package com.example.foodplanner.adapters;

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
import com.example.foodplanner.model.category.Category;
import com.example.foodplanner.model.category.MealsByCategory;

import java.util.List;

public class MealsByCategoryAdapter extends RecyclerView.Adapter<MealsByCategoryAdapter.MealsByCategoryViewHolder> {

    private Context context;
    private List<MealsByCategory> mealsByCategoriesList;
    private MealsByCategoryAdapter.OnMealsByCategoryClickListener listener;

    public interface OnMealsByCategoryClickListener {
        void onMealsByCategoryClick(MealsByCategory mealsByCategory);
    }

    public void setMeals(List<MealsByCategory> meals) {
        this.mealsByCategoriesList = meals;
        notifyDataSetChanged();
    }

    public MealsByCategoryAdapter(Context context, List<MealsByCategory> meals) {
        this.context = context;
        this.mealsByCategoriesList = meals;
    }

    public void setOnMealsByCategoryClickListener(MealsByCategoryAdapter.OnMealsByCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealsByCategoryAdapter.MealsByCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_meal_category, parent, false);
        return new MealsByCategoryAdapter.MealsByCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealsByCategoryAdapter.MealsByCategoryViewHolder holder, int position) {
        MealsByCategory meal = mealsByCategoriesList.get(position);
        holder.mealTitle.setText(meal.getStrMeal());

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg)
                .centerCrop()
                .into(holder.mealThumb);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealsByCategoryClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealsByCategoriesList.size();
    }

    class MealsByCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView mealTitle;
        ImageView mealThumb;


        public MealsByCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.mealTitle_category);
            mealThumb = itemView.findViewById(R.id.mealImage_category);


        }




    }
}
