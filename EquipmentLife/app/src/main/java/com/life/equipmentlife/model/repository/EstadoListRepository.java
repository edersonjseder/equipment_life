package com.life.equipmentlife.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.life.equipmentlife.model.networking.api.EstadoApi;
import com.life.equipmentlife.model.pojo.Estado;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadoListRepository {
    private static final String TAG = EstadoListRepository.class.getSimpleName();

    private EstadoApi mEstadoApi;
    private Call<List<Estado>> call;

    public EstadoListRepository(EstadoApi estadoApi) {

       mEstadoApi = estadoApi;

    }

    /**
     * Method gets the estado list data from API
     *
     * @return
     */
    public LiveData<List<Estado>> getEstadosList() {

        MutableLiveData<List<Estado>> data = new MutableLiveData<>();

        call = mEstadoApi.loadEstadosList();

        call.enqueue(new Callback<List<Estado>>() {

            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<Estado>> call, Throwable t) {
                Log.e(TAG, "onFailure() inside method - e: " + t.getMessage());
            }

        });

        return data;

    }

}
