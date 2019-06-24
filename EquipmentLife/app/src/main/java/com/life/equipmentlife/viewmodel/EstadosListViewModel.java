package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Estado;
import com.life.equipmentlife.model.repository.EstadoListRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

import java.util.List;

public class EstadosListViewModel extends AndroidViewModel {
    private static final String TAG = EstadosListViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<List<Estado>> estadosListObservable;

    private EstadoListRepository mEstadoListRepository;

    public EstadosListViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);
        Log.i(TAG, "EquipmentListViewModel() inside constructor");

        mEstadoListRepository = compositionRoot.getEstadoListRepository();

    }

    @NonNull
    public LiveData<List<Estado>> getEstadosListLiveData() {
        Log.i(TAG, "getEquipmentListLiveData() inside method");

        estadosListObservable = mEstadoListRepository.getEstadosList();

        return estadosListObservable;

    }

}
