package com.example.foodplanner.presentation.search.view.ingredients;


import java.util.ArrayList;
import java.util.List;

public class PaginationManager<T> {

    private List<T> allItems;
    private int pageSize;
    private int currentPage;

    public PaginationManager(int pageSize) {
        this.pageSize = pageSize;
        this.allItems = new ArrayList<>();
        this.currentPage = 0;
    }

    public void setAllItems(List<T> items) {
        this.allItems = new ArrayList<>(items);
        this.currentPage = 0;
    }

    public List<T> getNextPage() {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allItems.size());

        if (start >= allItems.size()) {
            return new ArrayList<>();
        }

        currentPage++;
        return new ArrayList<>(allItems.subList(start, end));
    }

    public boolean hasMorePages() {
        return currentPage * pageSize < allItems.size();
    }

    public void reset() {
        currentPage = 0;
    }

    public int getTotalItems() {
        return allItems.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLoadedItemsCount() {
        return currentPage * pageSize;
    }
}
