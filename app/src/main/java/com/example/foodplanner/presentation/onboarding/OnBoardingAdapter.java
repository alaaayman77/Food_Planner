package com.example.foodplanner.presentation.onboarding;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.onboarding.OnBoarding;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.PageViewHolder> {

    private final List<OnBoarding> pages;

    public OnBoardingAdapter(List<OnBoarding> pages) {
        this.pages = pages;
    }


    static class PageViewHolder extends RecyclerView.ViewHolder {

        LottieAnimationView lottieView;
        TextView            tvTitle;
        TextView            tvDescription;

        PageViewHolder(View itemView) {
            super(itemView);
            lottieView    = itemView.findViewById(R.id.lottieView);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(OnBoarding page) {
            lottieView.setAnimation(page.getLottieRawRes());
            lottieView.playAnimation();

            tvTitle.setText(page.getTitle());
            tvDescription.setText(page.getDescription());
        }
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_onboarding_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}