package com.example.foodplanner.presentation.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MEAL = 0;
    private static final int VIEW_TYPE_LOAD_MORE = 1;

    private Context context;
    private List<MealsByCategory> meals;
    private OnMealClickListener onMealClickListener;
    private OnLoadMoreClickListener onLoadMoreClickListener;
    private boolean showLoadMore = false;
    private int totalResults = 0;

    public interface OnMealClickListener {
        void onMealClicked(MealsByCategory meal);
    }

    public interface OnLoadMoreClickListener {
        void onLoadMoreClicked();
    }

    public SearchResultsAdapter(Context context, OnMealClickListener listener) {
        this.context = context;
        this.meals = new ArrayList<>();
        this.onMealClickListener = listener;
    }

    public void setOnLoadMoreClickListener(OnLoadMoreClickListener listener) {
        this.onLoadMoreClickListener = listener;
    }

    public void setMeals(List<MealsByCategory> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
        notifyDataSetChanged();
    }

    public void addMeals(List<MealsByCategory> newMeals) {
        int startPosition = meals.size();
        this.meals.addAll(newMeals);
        notifyItemRangeInserted(startPosition, newMeals.size());
    }

    public void clearMeals() {
        this.meals.clear();
        this.showLoadMore = false;
        this.totalResults = 0;
        notifyDataSetChanged();
    }

    public void setLoadMoreVisible(boolean visible, int total) {
        this.showLoadMore = visible;
        this.totalResults = total;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == meals.size() && showLoadMore) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return VIEW_TYPE_MEAL;
    }

    @Override
    public int getItemCount() {
        return meals.size() + (showLoadMore ? 1 : 0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false);
            return new LoadMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
            return new SearchResultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchResultViewHolder) {
            MealsByCategory meal = meals.get(position);
            ((SearchResultViewHolder) holder).bind(meal);
        } else if (holder instanceof LoadMoreViewHolder) {
            ((LoadMoreViewHolder) holder).bind(meals.size(), totalResults);
        }
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

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private Button loadMoreButton;
        private TextView loadMoreInfo;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            loadMoreButton = itemView.findViewById(R.id.loadMoreButton);
            loadMoreInfo = itemView.findViewById(R.id.loadMoreInfo);
        }

        public void bind(int loaded, int total) {
            loadMoreInfo.setText("Showing " + loaded + " of " + total + " results");

            loadMoreButton.setOnClickListener(v -> {
                if (onLoadMoreClickListener != null) {
                    onLoadMoreClickListener.onLoadMoreClicked();
                }
            });
        }
    }
}