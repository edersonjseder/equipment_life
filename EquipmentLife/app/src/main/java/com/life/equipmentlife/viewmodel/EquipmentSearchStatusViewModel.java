package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.repository.EquipmentSearchStatusRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

import java.util.List;

public class EquipmentSearchStatusViewModel extends AndroidViewModel {
    private static final String TAG = EquipmentSearchStatusViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<List<Equipment>> equipmentMutableLiveData;

    private EquipmentSearchStatusRepository mEquipmentSearchStatusRepository;

    public EquipmentSearchStatusViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);

        mEquipmentSearchStatusRepository = compositionRoot.getEquipmentSearchStatusRepository();

    }

    @NonNull
    public LiveData<List<Equipment>> searchEquipmentByStatusLiveData(String status, String sessionUidKey) {
        Log.i(TAG, "searchEquipmentByStatusLiveData() inside method - status: " + status + " sessionUidKey: " + sessionUidKey);

        equipmentMutableLiveData = mEquipmentSearchStatusRepository.fetchEquipmentsByStatus(status, sessionUidKey);

        return equipmentMutableLiveData;

    }

}
