package com.example.moviemvvm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.moviemvvm.R;
import com.example.moviemvvm.adapters.TVShowAdapter;
import com.example.moviemvvm.databinding.ActivitySearchBinding;
import com.example.moviemvvm.listeners.TVShowsListener;
import com.example.moviemvvm.models.TVShow;
import com.example.moviemvvm.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowsListener {
    private ActivitySearchBinding activitySearchBinding;
    private SearchViewModel searchViewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowAdapter tvShowAdapter;
    private int currentPage = 1;
    private int totalAvailablePage = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        doInitialization();
    }

    private void doInitialization() {
        activitySearchBinding.imageViewBack.setOnClickListener(v -> onBackPressed());
        activitySearchBinding.tvShowsRecyclerView.setHasFixedSize(true);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        tvShowAdapter = new TVShowAdapter(tvShows, this);
        activitySearchBinding.tvShowsRecyclerView.setAdapter(tvShowAdapter);
        activitySearchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePage = 1;
                                searchTVShow(s.toString());
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    tvShowAdapter.notifyDataSetChanged();
                }
            }
        });
        activitySearchBinding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activitySearchBinding.tvShowsRecyclerView.canScrollVertically(1)) {
                    if (!activitySearchBinding.inputSearch.getText().toString().isEmpty()) {
                        if (currentPage < totalAvailablePage) {
                            currentPage += 1;
                            searchTVShow(activitySearchBinding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });
        activitySearchBinding.inputSearch.requestFocus();
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    private void searchTVShow(String query) {
        toggleLoading();
        searchViewModel.searchTVShow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePage = tvShowsResponse.getTotalPages();
                if (tvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(tvShowsResponse.getTvShows());
                    tvShowAdapter.notifyItemRangeChanged(oldCount, tvShows.size());
                }

            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (activitySearchBinding.getIsLoading() != null && activitySearchBinding.getIsLoading()) {
                activitySearchBinding.setIsLoading(false);
            } else {
                activitySearchBinding.setIsLoading(true);
            }
        } else {
            if (activitySearchBinding.getIsLoadingMore() != null && activitySearchBinding.getIsLoadingMore()) {
                activitySearchBinding.setIsLoadingMore(false);
            } else {
                activitySearchBinding.setIsLoadingMore(true);
            }
        }
    }
}