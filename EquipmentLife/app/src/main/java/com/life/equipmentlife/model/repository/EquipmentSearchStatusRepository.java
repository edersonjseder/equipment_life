package com.life.equipmentlife.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.FireBaseQueryLiveData;

import java.util.ArrayList;
import java.util.List;

import static com.life.equipmentlife.common.constants.Constants.EQUIPMENTS;
import static com.life.equipmentlife.common.constants.Constants.PROFILE_CURRENT_OWNER_ID;
import static com.life.equipmentlife.common.constants.Constants.STATUS_CURRENT_OWNER;

public class EquipmentSearchStatusRepository implements Runnable {
    private static final String TAG = EquipmentSearchStatusRepository.class.getSimpleName();

    private DatabaseReference databaseReference;
    private MutableLiveData<List<Equipment>> data;
    private FireBaseQueryLiveData mLiveData;
    private ControllerCompositionRoot mCompositionRoot;
    private Handler mHandler;
    private String mSessionUidKey;

    public EquipmentSearchStatusRepository(DatabaseReference reference, ControllerCompositionRoot compositionRoot) {

       databaseReference = reference.child(EQUIPMENTS);
       mCompositionRoot = compositionRoot;
       mHandler = new Handler();

    }

    /**
     * Method fetches the equipment by status stored in Firebase database
     *
     * @return the live data containing the equipment list
     */
    public LiveData<List<Equipment>> fetchEquipmentsByStatus(String status, String sessionUidKey) {
        Log.i(TAG, "fetchEquipmentsList() inside method - sessionUidKey: " + sessionUidKey);

        mSessionUidKey = sessionUidKey;

        data = new MutableLiveData<>();

        Query query = databaseReference.orderByChild(STATUS_CURRENT_OWNER).equalTo(status);

        mLiveData = new FireBaseQueryLiveData(query);

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
                Log.i(TAG, "run() inside method - ELSE - dataSnapshot: " + dataSnapshot);
                data.setValue(getEquipmentEmptyList());
            }

        });

    }

    /**
     * Handles the datasnapshot task containing the equipment object from Firebase
     *
     * @param dataSnapshot
     */
    private void handleReturn(DataSnapshot dataSnapshot) {
        Log.i(TAG, "handleReturn() inside method - mSessionUidKey: " + mSessionUidKey);

        List<Equipment> equipments = new ArrayList<>();

        for(DataSnapshot item : dataSnapshot.getChildren()) {

            if (item.child(PROFILE_CURRENT_OWNER_ID).getValue().equals(mSessionUidKey)) {

                Equipment equipment = item.getValue(Equipment.class);

                Log.i(TAG, "handleReturn() inside method - inside if loop - equipment: " + equipment);

                equipments.add(equipment);

                Log.i(TAG, "handleReturn() inside method - after equipments.add(equipment): " + equipments);

            }

        }

        data.setValue(equipments);

    }

    private List<Equipment> getEquipmentEmptyList() {
        List<Equipment> list = new ArrayList<>();
        return list;
    }

}
