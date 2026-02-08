package com.example.foodplanner.presentation.meals_by_category.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealsByCategoryAdapter extends RecyclerView.Adapter<MealsByCategoryAdapter.MealsByCategoryViewHolder> {

    private List<MealsByCategory> mealsByCategoriesList;
    private OnMealByCategoryClick listener;
    private Set<String> favoriteMealIds = new HashSet<>();

    public MealsByCategoryAdapter(OnMealByCategoryClick listener) {
        this.mealsByCategoriesList = new ArrayList<>();
        this.listener = listener;
    }

    public void setMeals(List<MealsByCategory> meals) {
        this.mealsByCategoriesList = meals;
        notifyDataSetChanged();
    }

    public void setFavoriteMealIds(Set<String> favoriteIds) {
        this.favoriteMealIds = favoriteIds != null ? new HashSet<>(favoriteIds) : new HashSet<>();
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

        // Set initial favorite state
        boolean isFavorite = favoriteMealIds.contains(meal.getMealId());
        updateFavoriteIcon(holder.favoriteIcon, isFavorite);

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

        if (holder.favoriteCard != null) {
            holder.favoriteCard.setOnClickListener(v -> {
                boolean currentlyFavorite = favoriteMealIds.contains(meal.getMealId());

                // Animate only when adding to favorites
                if (!currentlyFavorite) {
                    animateHeart(holder.favoriteIcon);
                }


                if (listener != null) {
                    listener.onFavoriteClick(meal);
                }
            });
        }
    }

    private void updateFavoriteIcon(ImageView icon, boolean isFavorite) {
        if (isFavorite) {
            // Use filled heart drawable
            icon.setImageResource(R.drawable.icon_tick);
        } else {
            // Use outline heart drawable
            icon.setImageResource(R.drawable.icon_heart);
        }
    }

    private void animateHeart(ImageView heartIcon) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 1f, 1.3f);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 1.3f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 1.3f, 1f);

        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(scaleDownX, scaleDownY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleUp).before(scaleDown);
        animatorSet.start();
    }

    @Override
    public int getItemCount() {
        return mealsByCategoriesList.size();
    }

    class MealsByCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView mealTitle;
        ImageView mealThumb;
        MaterialCardView btnAddToPlan;
        MaterialCardView favoriteCard;
        ImageView favoriteIcon;

        public MealsByCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.mealTitle_category);
            mealThumb = itemView.findViewById(R.id.mealImage_category);
            btnAddToPlan = itemView.findViewById(R.id.mealPlanCard);
            favoriteCard = itemView.findViewById(R.id.favoriteCard);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }
}