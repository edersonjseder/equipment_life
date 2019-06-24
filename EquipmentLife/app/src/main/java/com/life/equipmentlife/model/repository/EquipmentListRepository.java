package com.life.equipmentlife.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.FireBaseQueryLiveData;

import java.util.ArrayList;
import java.util.List;

import static com.life.equipmentlife.common.constants.Constants.EQUIPMENTS;

public class EquipmentListRepository implements Runnable {
    private static final String TAG = EquipmentListRepository.class.getSimpleName();

    private DatabaseReference databaseReference;
    private MutableLiveData<List<Equipment>> data;
    private FireBaseQueryLiveData mLiveData;
    private ControllerCompositionRoot mCompositionRoot;
    private Handler mHandler;

    public EquipmentListRepository(DatabaseReference reference, ControllerCompositionRoot compositionRoot) {

       databaseReference = reference.child(EQUIPMENTS);
       mCompositionRoot = compositionRoot;
       mHandler = new Handler();

    }

    /**
     * Method fetches a list of equipments stored in Firebase database
     *
     * @return the live data containing the equipment list
     */
    public LiveData<List<Equipment>> fetchEquipmentsList() {
        Log.i(TAG, "fetchEquipmentsList() inside method");

        data = new MutableLiveData<>();

        mLiveData = new FireBaseQueryLiveData(databaseReference);

        mHandler.postDelayed(this, 3000);

        return data;

    }

    /**
     * Method that executes the observable call
     */
    @Override
    public void run() {

        mLiveData.observe(mCompositionRoot.getActivity(), dataSnapshot -> {

            if (dataSnapshot.exists()) {

                if (dataSnapshot != null && dataSnapshot.hasChildren()) {

                    handleReturn(dataSnapshot);

                }

            } else {

                data.setValue(getEquipmentEmptyList());

            }

        });

    }

    /**
     * Handles the datasnapshot task containing the equipments list from Firebase
     *
     * @param dataSnapshot
     */
    private void handleReturn(DataSnapshot dataSnapshot) {
        Log.i(TAG, "handleReturn() inside method");

        List<Equipment> equipments = new ArrayList<>();

        for(DataSnapshot item : dataSnapshot.getChildren()) {

            String key = item.getKey();
            Equipment equipment = item.getValue(Equipment.class);
            equipment.setId(key);

            Log.i(TAG, "handleReturn() inside method - inside if loop - equipment: " + equipment);

            equipments.add(equipment);

            Log.i(TAG, "handleReturn() inside method - after equipments.add(equipment): " + equipments);

        }

        data.setValue(equipments);

    }

    private List<Equipment> getEquipmentEmptyList() {
        List<Equipment> list = new ArrayList<>();
        return list;
    }

}
