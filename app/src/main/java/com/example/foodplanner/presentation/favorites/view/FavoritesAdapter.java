package com.example.foodplanner.presentation.favorites.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<FavoriteMeal> favoritesList;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onFavoriteClick(FavoriteMeal favoriteMeal);
        void onRemoveFavoriteClick(FavoriteMeal favoriteMeal);
    }

    public FavoritesAdapter(OnFavoriteActionListener listener) {
        this.favoritesList = new ArrayList<>();
        this.listener = listener;
    }

    public void setFavorites(List<FavoriteMeal> favorites) {
        this.favoritesList = favorites != null ? favorites : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_meal, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteMeal favorite = favoritesList.get(position);

        holder.mealTitle.setText(favorite.getMealName());

        if (favorite.getMealCategory() != null && !favorite.getMealCategory().isEmpty()) {
            holder.mealCategory.setText(favorite.getMealCategory().toUpperCase());
            holder.mealCategory.setVisibility(View.VISIBLE);
        } else {
            holder.mealCategory.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(favorite.getMealThumbnail())
                .placeholder(R.drawable.splash_bg)
                .error(R.drawable.splash_bg)
                .centerCrop()
                .into(holder.mealImage);

        holder.removeFavoriteIcon.setImageResource(R.drawable.icon_heart_filled);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(favorite);
            }
        });

        holder.removeFavoriteCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveFavoriteClick(favorite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        ImageView removeFavoriteIcon;
        TextView mealTitle;
        TextView mealCategory;
        MaterialCardView removeFavoriteCard;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.favMealImage);
            mealTitle = itemView.findViewById(R.id.favMealTitle);
            mealCategory = itemView.findViewById(R.id.favMealCategory);
            removeFavoriteCard = itemView.findViewById(R.id.removeFavoriteCard);
            removeFavoriteIcon = itemView.findViewById(R.id.removeFavoriteIcon);
        }
    }
}