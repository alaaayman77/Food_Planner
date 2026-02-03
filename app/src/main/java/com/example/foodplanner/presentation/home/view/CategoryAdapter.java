package com.example.foodplanner.presentation.home.view;



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


import java.util.ArrayList;
import java.util.List;

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {


        private List<Category> categories;
        private OnCategoryClick listener;



        public CategoryAdapter(OnCategoryClick onCategoryClick) {
            categories= new ArrayList<>();
            listener = onCategoryClick;
        }
        public void setCategories(List<Category> categoryList){
            this.categories = categoryList;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.categoryName.setText(category.getCategoryName());
            Glide.with(holder.itemView)
                    .load(category.getCategoryThumbnail())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(holder.categoryIcon);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setOnCategoryClick(category);
                    }
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

