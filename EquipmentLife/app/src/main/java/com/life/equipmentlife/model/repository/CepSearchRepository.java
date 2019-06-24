package com.life.equipmentlife.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.life.equipmentlife.model.networking.api.CepApi;
import com.life.equipmentlife.model.pojo.AddressData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CepSearchRepository {
    private static final String TAG = CepSearchRepository.class.getSimpleName();

    private CepApi mCepApi;
    private Call<AddressData> call;

    public CepSearchRepository(CepApi cepApi) {

        mCepApi = cepApi;

    }

    /**
     * Method gets the address data from API by cep
     *
     * @param cep the parameter used to get the address data
     * @return
     */
    public LiveData<AddressData> getAddressDataByCep(String cep) {

        MutableLiveData<AddressData> data = new MutableLiveData<>();

        call = mCepApi.loadAddressData(cep);

        call.enqueue(new Callback<AddressData>() {

            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Log.e(TAG, "onFailure() inside method - e: " + t.getMessage());
            }

        });

        return data;

    }

}
