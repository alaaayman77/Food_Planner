package com.example.foodplanner.presentation.search.view.ingredients;

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
import com.example.foodplanner.data.model.search.ingredients.Ingredients;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SearchIngredientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SEE_MORE = 1;
    private static final int VIEW_TYPE_SHIMMER = 2;

    private Context context;
    private List<Ingredients> displayedIngredients;
    private List<Ingredients> selectedIngredients;
    private OnIngredientClickListener listener;
    private OnSeeMoreClickListener seeMoreListener;
    private boolean showSeeMoreButton = false;
    private boolean isLoading = true;

    public interface OnIngredientClickListener {
        void onIngredientClicked(Ingredients ingredient, boolean isSelected);
    }

    public interface OnSeeMoreClickListener {
        void onSeeMoreClicked();
    }

    public SearchIngredientsAdapter(Context context, OnIngredientClickListener listener) {
        this.context = context;
        this.displayedIngredients = new ArrayList<>();
        this.selectedIngredients = new ArrayList<>();
        this.listener = listener;
    }

    public void setOnSeeMoreClickListener(OnSeeMoreClickListener listener) {
        this.seeMoreListener = listener;
    }

    public void clearIngredients() {
        displayedIngredients.clear();
        showSeeMoreButton = false;
        notifyDataSetChanged();
    }

    public void addIngredients(List<Ingredients> newIngredients) {
        int startPosition = displayedIngredients.size();
        displayedIngredients.addAll(newIngredients);
        notifyItemRangeInserted(startPosition, newIngredients.size());
    }

    public void setSeeMoreVisible(boolean visible) {
        boolean wasVisible = showSeeMoreButton;
        showSeeMoreButton = visible;

        if (visible && !wasVisible) {
            notifyItemInserted(displayedIngredients.size());
        } else if (!visible && wasVisible) {
            notifyItemRemoved(displayedIngredients.size());
        }
    }

    public List<Ingredients> getSelectedIngredients() {
        return selectedIngredients;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading) return VIEW_TYPE_SHIMMER;

        if (showSeeMoreButton && position == displayedIngredients.size()) {
            return VIEW_TYPE_SEE_MORE;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_SHIMMER) {
            View view = inflater.inflate(R.layout.search_ingredient_card, parent, false);
            return new ShimmerViewHolder(view);
        } else if (viewType == VIEW_TYPE_SEE_MORE) {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new SeeMoreViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.search_ingredient_card, parent, false);
            return new IngredientViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof IngredientViewHolder && !isLoading) {
            Ingredients ingredient = displayedIngredients.get(position);
            ((IngredientViewHolder) holder).bind(ingredient);
        } else if (holder instanceof SeeMoreViewHolder && !isLoading) {
            ((SeeMoreViewHolder) holder).bind();
        }
        // Shimmer holder does not need binding
    }

    @Override
    public int getItemCount() {
        if (isLoading) return 6; // number of shimmer placeholders
        return showSeeMoreButton ? displayedIngredients.size() + 1 : displayedIngredients.size();
    }

    // --------------------- ViewHolders ---------------------

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            // no binding needed for shimmer
        }
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ingredientImage;
        TextView ingredientName;
        TextView ingredientCount;
        ImageView addButton;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.ingredientCard);
            ingredientImage = itemView.findViewById(R.id.ingredientImage);
            ingredientName = itemView.findViewById(R.id.ingredientName);
            ingredientCount = itemView.findViewById(R.id.ingredientCount);
            addButton = itemView.findViewById(R.id.addButton);
        }

        public void bind(Ingredients ingredient) {
            if (ingredient == null) return;

            ingredientName.setText(ingredient.getIngredientName());
            ingredientCount.setText("Available in 500+ recipes");

            Glide.with(context)
                    .load(ingredient.getIngredientThumbnail())
                    .into(ingredientImage);

            boolean isSelected = selectedIngredients.contains(ingredient);
            addButton.setImageResource(isSelected ? R.drawable.icon_tick : R.drawable.icon_add);

            cardView.setOnClickListener(v -> {
                boolean nowSelected = !isSelected;

                if (nowSelected) selectedIngredients.add(ingredient);
                else selectedIngredients.remove(ingredient);

                notifyItemChanged(getAdapterPosition());

                if (listener != null) {
                    listener.onIngredientClicked(ingredient, nowSelected);
                }
            });
        }
    }

    class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        MaterialButton seeMoreButton;

        public SeeMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            seeMoreButton = itemView.findViewById(R.id.seeMoreButton);
        }

        public void bind() {
            seeMoreButton.setOnClickListener(v -> {
                if (seeMoreListener != null) {
                    seeMoreListener.onSeeMoreClicked();
                }
            });
        }
    }
}
