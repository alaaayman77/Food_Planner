package com.example.foodplanner.presentation.search.view.category;

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
import com.example.foodplanner.data.model.category.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SearchCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SHIMMER = 0;
    private static final int TYPE_CATEGORY = 1;

    private Context context;
    private List<Category> categories;
    private List<Category> selectedCategories;
    private OnCategoryClickListener listener;
    private boolean isLoading = true;

    public interface OnCategoryClickListener {
        void onCategoryClicked(Category category, boolean isSelected);
    }

    public SearchCategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.selectedCategories = new ArrayList<>();
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading) return TYPE_SHIMMER;
        return TYPE_CATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_SHIMMER) {
            View view = inflater.inflate(R.layout.shimmer_category_card, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.search_category_card, parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder && !isLoading) {
            Category category = categories.get(position);
            ((CategoryViewHolder) holder).bind(category);
        }
        // shimmer holder does not need binding
    }

    @Override
    public int getItemCount() {
        if (isLoading) return 6; // number of shimmer placeholders
        return categories.size();
    }

    // --------------------- ViewHolders ---------------------

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            // no need to reference views for shimmer
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView categoryImage;
        TextView categoryName;
        ImageView checkIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.categoryCard);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
            checkIcon = itemView.findViewById(R.id.checkIcon);
        }

        public void bind(Category category) {
            if (category == null) return;

            categoryName.setText(category.getCategoryName());
            View checkIconContainer = itemView.findViewById(R.id.checkIconContainer);

            Glide.with(context)
                    .load(category.getCategoryThumbnail())
                    .into(categoryImage);

            boolean isSelected = selectedCategories.contains(category);
            cardView.setSelected(isSelected);
            checkIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            checkIconContainer.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            cardView.setOnClickListener(v -> {
                boolean nowSelected = !isSelected;
                if (nowSelected) selectedCategories.add(category);
                else selectedCategories.remove(category);

                notifyItemChanged(getAdapterPosition());

                if (listener != null) {
                    listener.onCategoryClicked(category, nowSelected);
                }
            });
        }
    }
}
