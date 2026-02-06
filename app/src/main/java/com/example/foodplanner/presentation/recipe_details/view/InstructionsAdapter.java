package com.example.foodplanner.presentation.recipe_details.view;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.recipe_details.InstructionStep;

import java.util.ArrayList;
import java.util.List;

    public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {

        private List<InstructionStep> instructions;

        public InstructionsAdapter() {
            this.instructions = new ArrayList<>();
        }

        public void setInstructions(List<InstructionStep> instructions) {
            this.instructions = instructions != null ? instructions : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.instruction_item, parent, false);
            return new InstructionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
            holder.bind(instructions.get(position));
        }

        @Override
        public int getItemCount() {
            return instructions.size();
        }

        static class InstructionViewHolder extends RecyclerView.ViewHolder {
            TextView stepNumber;
            TextView stepTitle;
            TextView stepDescription;

            public InstructionViewHolder(@NonNull View itemView) {
                super(itemView);
                stepNumber = itemView.findViewById(R.id.stepNumber);

                stepDescription = itemView.findViewById(R.id.stepDescription);
            }

            public void bind(InstructionStep step) {
                stepNumber.setText(String.valueOf(step.getStepNumber()));

                stepDescription.setText(step.getDescription());
            }
        }
    }

