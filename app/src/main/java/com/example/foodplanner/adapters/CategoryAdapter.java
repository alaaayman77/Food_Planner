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


import java.util.List;

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private Context context;
        private List<Category> categories;
        private OnCategoryClickListener listener;

        public interface OnCategoryClickListener {
            void onCategoryClick(Category category);
        }

        public CategoryAdapter(Context context, List<Category> categories) {
            this.context = context;
            this.categories = categories;
        }

        public void setOnCategoryClickListener(OnCategoryClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.categoryName.setText(category.getStrCategory());
            Glide.with(context)
                    .load(category.getStrCategoryThumb())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(holder.categoryIcon);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {

            ImageView categoryIcon;
            TextView categoryName;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryIcon = itemView.findViewById(R.id.categoryIcon);
                categoryName = itemView.findViewById(R.id.categoryName);
            }




        }
    }

