package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.repository.CepSearchRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class CepSearchViewModel extends AndroidViewModel {

    // This data will be fetched asynchronously
    private LiveData<AddressData> addressDataObservable;

    private CepSearchRepository mCepSearchRepository;

    public CepSearchViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);

        mCepSearchRepository = compositionRoot.getCepSearchRepository();

    }

    //we will call this method to get the data
    public LiveData<AddressData> getAddressData(String cep) {

        addressDataObservable = mCepSearchRepository.getAddressDataByCep(cep);

        // Return the Address data
        return addressDataObservable;

    }

}