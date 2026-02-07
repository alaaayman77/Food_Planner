package com.example.foodplanner.presentation.mealplanner.view;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;

import com.example.foodplanner.data.model.meal_plan.DateItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DateSelectorAdapter extends RecyclerView.Adapter<DateSelectorAdapter.DateViewHolder> {

    private List<DateItem> dateList = new ArrayList<>();
    private OnDateSelectedListener listener;
    private int selectedPosition = 0;

    public interface OnDateSelectedListener {
        void onDateSelected(DateItem dateItem, int position);
    }

    public DateSelectorAdapter(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void setDates(List<DateItem> dates) {
        this.dateList = dates;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data_selector, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateItem = dateList.get(position);
        holder.bind(dateItem, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int oldPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
                listener.onDateSelected(dateItem, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView dateCard;
        TextView tvDayOfWeek;
        TextView tvDayNumber;
        TextView tvMonthYear;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateCard = itemView.findViewById(R.id.dateCard);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvMonthYear = itemView.findViewById(R.id.tvMonthYear);
        }

        public void bind(DateItem dateItem, boolean isSelected) {
            tvDayOfWeek.setText(dateItem.getDayOfWeek());
            tvDayNumber.setText(dateItem.getDayNumber());
            tvMonthYear.setText(dateItem.getMonthYear());

            if (isSelected) {
                dateCard.setCardBackgroundColor(Color.parseColor("#4A90E2"));
                tvDayOfWeek.setTextColor(Color.WHITE);
                tvDayNumber.setTextColor(Color.WHITE);
                tvMonthYear.setTextColor(Color.parseColor("#E0E0E0"));
                dateCard.setStrokeWidth(0);
            } else {
                dateCard.setCardBackgroundColor(Color.WHITE);
                tvDayOfWeek.setTextColor(Color.parseColor("#999999"));
                tvDayNumber.setTextColor(Color.BLACK);
                tvMonthYear.setTextColor(Color.parseColor("#CCCCCC"));
                dateCard.setStrokeWidth(0);
            }
        }
    }
}
