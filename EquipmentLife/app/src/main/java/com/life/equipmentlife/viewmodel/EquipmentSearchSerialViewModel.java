package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.repository.EquipmentSearchSerialRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class EquipmentSearchSerialViewModel extends AndroidViewModel {
    private static final String TAG = EquipmentSearchSerialViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<Equipment> equipmentMutableLiveData;

    private EquipmentSearchSerialRepository mEquipmentSearchSerialRepository;

    public EquipmentSearchSerialViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);

        mEquipmentSearchSerialRepository = compositionRoot.getEquipmentSearchSerialRepository();

    }

    @NonNull
    public LiveData<Equipment> searchEquipmentBySerialLiveData(String serial, String uidKey) {
        Log.i(TAG, "searchEquipmentBySerialLiveData() inside method - serial: " + serial);

        equipmentMutableLiveData = mEquipmentSearchSerialRepository.fetchEquipmentBySerial(serial, uidKey);

        return equipmentMutableLiveData;

    }

}
