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
import static com.life.equipmentlife.common.constants.Constants.SERIAL;

public class EquipmentSearchSerialRepository implements Runnable {
    private static final String TAG = EquipmentSearchSerialRepository.class.getSimpleName();

    private DatabaseReference databaseReference;
    private MutableLiveData<Equipment> data;
    private FireBaseQueryLiveData liveData;
    private ControllerCompositionRoot mCompositionRoot;
    private Equipment mEquipment;
    private Handler mHandler;
    private String idKey;

    public EquipmentSearchSerialRepository(DatabaseReference reference, ControllerCompositionRoot compositionRoot) {

       databaseReference = reference.child(EQUIPMENTS);
       mCompositionRoot = compositionRoot;
       mHandler = new Handler();

    }

    /**
     * Method fetches the equipment by serial number stored in Firebase database
     *
     * @return the live data containing the equipment object
     */
    public LiveData<Equipment> fetchEquipmentBySerial(String serial, String uidKey) {
        Log.i(TAG, "fetchEquipmentBySerial() inside method - serial: " + serial);

        data = new MutableLiveData<>();

        idKey = uidKey;

        Query query = databaseReference.orderByChild(SERIAL).equalTo(serial);

        liveData = new FireBaseQueryLiveData(query);

        mHandler.postDelayed(this, 3000);

        return data;

    }

    /**
     * Method that executes the observable call
     */
    @Override
    public void run() {

        liveData.observe(mCompositionRoot.getActivity(), dataSnapshot -> {
            Log.i(TAG, "run() inside method - dataSnapshot: " + dataSnapshot);

            if (dataSnapshot.exists()) {
                Log.i(TAG, "run() inside method - IF - dataSnapshot: " + dataSnapshot);

                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Log.i(TAG, "run() inside method - another IF - dataSnapshot: " + dataSnapshot);

                    handleReturn(dataSnapshot);

                }

            } else {
                Log.i(TAG, "run() inside method - ELSE - dataSnapshot: " + dataSnapshot);
                data.setValue(getEquipmentEmpty());
            }

        });

    }

    /**
     * Handles the datasnapshot task containing the equipment object from Firebase
     *
     * @param dataSnapshot
     */
    private void handleReturn(DataSnapshot dataSnapshot) {
        Log.i(TAG, "handleReturn() inside method - serial: " + dataSnapshot);

        for(DataSnapshot item : dataSnapshot.getChildren()) {

            // Verifies if the search comes from onResume method on Equipment details
            // or from the icon search on menu bar of the Equipment list screen
            // If the idKey has a value, then comes from details, else it comes from list screen
            if (!idKey.equals("")) {

                if (item.getKey().equals(idKey)) {

                    mEquipment = item.getValue(Equipment.class);

                    data.setValue(mEquipment);

                    break;

                }

            } else {

                mEquipment = item.getValue(Equipment.class);

                data.setValue(mEquipment);

                break;

            }

        }

    }

    private Equipment getEquipmentEmpty() {
        Equipment equipment = new Equipment();
        return equipment;
    }

}
