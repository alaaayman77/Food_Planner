package com.example.foodplanner.presentation.mealplanner.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.ArrayList;
import java.util.List;

public class MealItemAdapter extends RecyclerView.Adapter<MealItemAdapter.MealViewHolder> {

    private List<MealPlan> mealList = new ArrayList<>();
    private OnMealItemClickListener listener;

    public interface OnMealItemClickListener {
        void onMealClick(MealPlan mealPlan);
        void onDeleteClick(MealPlan mealPlan);
    }

    public MealItemAdapter(OnMealItemClickListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<MealPlan> meals) {
        this.mealList = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealPlan meal = mealList.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivMealImage;
        TextView tvMealName;
        TextView tvMealInfo;
        ImageView btnDelete;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvMealItem);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealInfo = itemView.findViewById(R.id.tvMealInfo);
            btnDelete = itemView.findViewById(R.id.btnDeleteMeal);
        }

        public void bind(MealPlan meal) {
            if (meal.getMealName() != null) {
                tvMealName.setText(meal.getMealName());
            } else {
                tvMealName.setText("Meal");
            }

            String info = "";
            if (meal.getMealCategory() != null) {
                info += meal.getMealCategory();
            }
            if (meal.getMealArea() != null) {
                if (!info.isEmpty()) info += " • ";
                info += meal.getMealArea();
            }

            if (!info.isEmpty()) {
                tvMealInfo.setText(info);
                tvMealInfo.setVisibility(View.VISIBLE);
            } else {
                tvMealInfo.setVisibility(View.GONE);
            }

            if (meal.getMealThumbnail() != null && !meal.getMealThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(meal.getMealThumbnail())
                        .placeholder(R.drawable.splash_bg)
                        .error(R.drawable.splash_bg)
                        .centerCrop()
                        .into(ivMealImage);
            }

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(meal);
                }
            });
        }
    }
}