package com.example.foodplanner.presentation.multi_filter;

import java.util.List;

public interface MultiFilterPresenter {
    void searchWithFilters(List<String> categories, List<String> areas);
}
