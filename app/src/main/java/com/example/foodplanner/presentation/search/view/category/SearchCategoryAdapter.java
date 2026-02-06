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

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private List<Category> selectedCategories;
    private OnCategoryClickListener listener;

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

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.search_category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
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
            categoryName.setText(category.getCategoryName());
            View checkIconContainer = itemView.findViewById(R.id.checkIconContainer);



            // Load category image using Glide
            Glide.with(context)
                    .load(category.getCategoryThumbnail())
                    .into(categoryImage);

            // Update selection state
            boolean isSelected = selectedCategories.contains(category);
            cardView.setSelected(isSelected);
            checkIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            checkIconContainer.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            // Handle click
            cardView.setOnClickListener(v -> {
                boolean nowSelected = !isSelected;

                if (nowSelected) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }

                notifyItemChanged(getAdapterPosition());

                if (listener != null) {
                    listener.onCategoryClicked(category, nowSelected);
                }
            });
        }
    }
}