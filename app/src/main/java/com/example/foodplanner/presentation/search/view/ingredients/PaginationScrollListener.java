package com.example.foodplanner.presentation.search.view.ingredients;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;
    private PaginationListener listener;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int VISIBLE_THRESHOLD = 3;

    public interface PaginationListener {
        void loadMoreItems();
    }

    public PaginationScrollListener(LinearLayoutManager layoutManager, PaginationListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy <= 0) return;

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition + VISIBLE_THRESHOLD) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                listener.loadMoreItems();
            }
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public void reset() {
        isLoading = false;
        isLastPage = false;
    }
}