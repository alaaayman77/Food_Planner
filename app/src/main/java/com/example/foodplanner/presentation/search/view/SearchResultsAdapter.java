package com.example.foodplanner.presentation.search.view;



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
import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    private Context context;
    private List<MealsByCategory> meals;
    private OnMealClickListener onMealClickListener;

    public interface OnMealClickListener {
        void onMealClicked(MealsByCategory meal);
    }

    public SearchResultsAdapter(Context context, OnMealClickListener listener) {
        this.context = context;
        this.meals = new ArrayList<>();
        this.onMealClickListener = listener;
    }

    public void setMeals(List<MealsByCategory> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void clearMeals() {
        this.meals.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        MealsByCategory meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealThumb;
        private TextView mealName;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            mealThumb = itemView.findViewById(R.id.mealThumb);
            mealName = itemView.findViewById(R.id.mealName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onMealClickListener != null) {
                    onMealClickListener.onMealClicked(meals.get(position));
                }
            });
        }

        public void bind(MealsByCategory meal) {
            mealName.setText(meal.getMealName());

            Glide.with(context)
                    .load(meal.getMealThumbnail())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(mealThumb);
        }
    }
}