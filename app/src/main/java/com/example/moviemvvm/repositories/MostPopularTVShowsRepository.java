package com.example.moviemvvm.repositories;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviemvvm.models.TVShow;
import com.example.moviemvvm.network.ApiClient;
import com.example.moviemvvm.network.ApiService;
import com.example.moviemvvm.responses.TVShowsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MostPopularTVShowsRepository {
        private ApiService apiService;

        public MostPopularTVShowsRepository(){
            apiService = ApiClient.getRetrofit().create(ApiService.class);
        }

        public LiveData<TVShowsResponse>  getMostPopularTVShows(int page) {
            MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
            apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowsResponse>() {
                @Override
                public void onResponse(Call<TVShowsResponse> call, Response<TVShowsResponse> response) {
                    data.setValue(response.body());
                }

                @Override
                public void onFailure(Call<TVShowsResponse> call, Throwable t) {
                    data.setValue(null);
                }
            });
            return data;
        }
}
