package com.example.foodplanner.data.model.recipe_details;

public class InstructionStep {

        private int stepNumber;

        private String description;

        public InstructionStep(int stepNumber,  String description) {
            this.stepNumber = stepNumber;

            this.description = description;
        }

        public int getStepNumber() {
            return stepNumber;
        }



        public String getDescription() {
            return description;
        }
    }

