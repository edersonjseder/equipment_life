package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.repository.EquipmentListRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

import java.util.List;

public class EquipmentListViewModel extends AndroidViewModel {
    private static final String TAG = EquipmentListViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<List<Equipment>> equipmentsListObservable;

    private EquipmentListRepository mEquipmentListRepository;

    public EquipmentListViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);
        Log.i(TAG, "EquipmentListViewModel() inside constructor");

        mEquipmentListRepository = compositionRoot.getEquipmentListRepository();

    }

    @NonNull
    public LiveData<List<Equipment>> getEquipmentListLiveData() {
        Log.i(TAG, "getEquipmentListLiveData() inside method");

        equipmentsListObservable = mEquipmentListRepository.fetchEquipmentsList();

        return equipmentsListObservable;

    }

}
