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
import com.example.foodplanner.data.model.search.area.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



    public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

        private List<Area> countries;
        private OnCountryClick listener;

        // Map of country names to flag emojis
        private static final Map<String, String> COUNTRY_FLAGS = new HashMap<String, String>() {{
            put("Algerian", "🇩🇿");
            put("American", "🇺🇸");
            put("Argentinian", "🇦🇷");
            put("Australian", "🇦🇺");
            put("British", "🇬🇧");
            put("Canadian", "🇨🇦");
            put("Chinese", "🇨🇳");
            put("Croatian", "🇭🇷");
            put("Dutch", "🇳🇱");
            put("Egyptian", "🇪🇬");
            put("Filipino", "🇵🇭");
            put("French", "🇫🇷");
            put("Greek", "🇬🇷");
            put("Indian", "🇮🇳");
            put("Irish", "🇮🇪");
            put("Italian", "🇮🇹");
            put("Jamaican", "🇯🇲");
            put("Japanese", "🇯🇵");
            put("Kenyan", "🇰🇪");
            put("Malaysian", "🇲🇾");
            put("Mexican", "🇲🇽");
            put("Moroccan", "🇲🇦");
            put("Norwegian", "🇳🇴");
            put("Polish", "🇵🇱");
            put("Portuguese", "🇵🇹");
            put("Russian", "🇷🇺");
            put("Saudi Arabian", "🇸🇦");
            put("Slovakian", "🇸🇰");
            put("Spanish", "🇪🇸");
            put("Syrian", "🇸🇾");
            put("Thai", "🇹🇭");
            put("Tunisian", "🇹🇳");
            put("Turkish", "🇹🇷");
            put("Ukrainian", "🇺🇦");
            put("Uruguayan", "🇺🇾");
            put("Vietnamese", "🇻🇳");
            put("Venezuelan", "🇻🇪");
        }};

        public interface OnCountryClickListener {
            void onCountryClick(Area area);
        }

        public CountryAdapter(OnCountryClick listener) {
            this.countries = new ArrayList<>();
            this.listener = listener;
        }

        public void setCountries(List<Area> countryList) {
            this.countries = countryList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_country, parent, false);
            return new CountryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
            Area country = countries.get(position);
            holder.countryName.setText(country.getStrArea());

            // Get flag emoji for the country
            String flag = COUNTRY_FLAGS.getOrDefault(country.getStrArea(), "🌍");
            holder.countryFlag.setText(flag);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCountryClick(country);
                }
            });
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        static class CountryViewHolder extends RecyclerView.ViewHolder {
            TextView countryFlag;
            TextView countryName;

            public CountryViewHolder(@NonNull View itemView) {
                super(itemView);
                countryFlag = itemView.findViewById(R.id.countryFlag);
                countryName = itemView.findViewById(R.id.countryName);
            }
        }
    }

