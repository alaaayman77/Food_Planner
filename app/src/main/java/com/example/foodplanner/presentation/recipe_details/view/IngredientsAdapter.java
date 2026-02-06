package com.example.foodplanner.presentation.recipe_details.view;

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
import com.example.foodplanner.data.model.recipe_details.IngredientWithMeasure;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private List<IngredientWithMeasure> ingredients;
    private Context context;

    public IngredientsAdapter(Context context) {
        this.context = context;
        this.ingredients = new ArrayList<>();
    }

    public void setIngredients(List<IngredientWithMeasure> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ingredientImage;
        TextView ingredientName;
        TextView ingredientMeasure;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientImage = itemView.findViewById(R.id.ingredientImage_Details);
            ingredientName = itemView.findViewById(R.id.ingredientName_Details);
            ingredientMeasure = itemView.findViewById(R.id.ingredientMeasure_Details);
        }

        public void bind(IngredientWithMeasure ingredient) {
            ingredientName.setText(ingredient.getName());
            ingredientMeasure.setText(ingredient.getMeasure().toUpperCase());

            Glide.with(context)
                    .load(ingredient.getImageUrl())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .into(ingredientImage);
        }
    }
}