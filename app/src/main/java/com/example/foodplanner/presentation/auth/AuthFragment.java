package com.example.foodplanner.presentation.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.foodplanner.R;
import com.example.foodplanner.adapters.AuthPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class AuthFragment extends Fragment {
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    AuthPageAdapter authPageAdapter;
    TabLayoutMediator tabMediator;

    public AuthFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager2 = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        authPageAdapter = new AuthPageAdapter(requireActivity());
        viewPager2.setAdapter(authPageAdapter);

         tabMediator = new TabLayoutMediator(
                tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Login");
                                break;
                            case 1:
                                tab.setText("Sign Up");
                                break;
                        }
                    }
                }
        );
         tabMediator.attach();
         setupTabBackground();
    }
    private void setupTabBackground() {
        tabLayout.post(() -> {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    View tabView = tab.view;
                    tabView.setBackgroundResource(R.drawable.tab_selector_background);

                    LinearLayout.LayoutParams params =
                            (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.setMarginStart(4);
                    params.setMarginEnd(4);
                    tabView.setLayoutParams(params);
                }
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager2 = null;
        tabLayout = null;
        authPageAdapter = null;
    }
}