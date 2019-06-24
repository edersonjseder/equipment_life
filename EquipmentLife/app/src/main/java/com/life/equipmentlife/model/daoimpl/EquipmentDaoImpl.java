package com.life.equipmentlife.model.daoimpl;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.life.equipmentlife.common.listener.EquipmentDataChangeSerialListener;
import com.life.equipmentlife.common.listener.EquipmentsDataChangeListener;
import com.life.equipmentlife.common.listener.EquipmentsRefreshDataChangeListener;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.EquipmentListViewModel;
import com.life.equipmentlife.viewmodel.EquipmentListViewModelFactory;
import com.life.equipmentlife.viewmodel.EquipmentSearchSerialViewModel;
import com.life.equipmentlife.viewmodel.EquipmentSearchSerialViewModelFactory;
import com.life.equipmentlife.viewmodel.EquipmentSearchStatusViewModel;
import com.life.equipmentlife.viewmodel.EquipmentSearchStatusViewModelFactory;

import java.util.List;

import static com.life.equipmentlife.common.constants.Constants.EQUIPMENTS;
import static com.life.equipmentlife.common.constants.Constants.SERIAL;

public class EquipmentDaoImpl implements EquipmentDao {

    private static final String TAG = EquipmentDaoImpl.class.getSimpleName();

    private DatabaseRef reference;
    private List<Equipment> mEquipments;
    private Equipment mEquipment;
    private EquipmentsDataChangeListener mListener;
    private EquipmentDataChangeSerialListener mSerialListener;
    private EquipmentsRefreshDataChangeListener mRefreshedListener;

    private EquipmentListViewModel mEquipmentMViewModel;
    private EquipmentListViewModelFactory viewModelFactory;

    private EquipmentSearchSerialViewModel mEquipmentSearchSerialViewModel;
    private EquipmentSearchSerialViewModelFactory viewModelSerialFactory;

    private EquipmentSearchStatusViewModel mEquipmentSearchStatusViewModel;
    private EquipmentSearchStatusViewModelFactory viewModelStatusFactory;

    private ControllerCompositionRoot mCompositionRoot;

    /**
     * Constructor for insert, update and delete
     */
    public EquipmentDaoImpl() {
        reference = getDatabaseRef();
    }

    /**
     * Constructor with the viewmodel for searching
     *
     * @param application
     * @param compositionRoot
     */
    public EquipmentDaoImpl(Application application,
                            ControllerCompositionRoot compositionRoot){

        reference = getDatabaseRef();

        mCompositionRoot = compositionRoot;

        /** Search list **/
        viewModelFactory =
                new EquipmentListViewModelFactory(application, compositionRoot);

        mEquipmentMViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelFactory)
                        .get(EquipmentListViewModel.class);

        /** Search by serial **/
        viewModelSerialFactory =
                new EquipmentSearchSerialViewModelFactory(application, compositionRoot);

        mEquipmentSearchSerialViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelSerialFactory)
                        .get(EquipmentSearchSerialViewModel.class);

        /** Search by status **/
        viewModelStatusFactory =
                new EquipmentSearchStatusViewModelFactory(application, compositionRoot);

        mEquipmentSearchStatusViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelStatusFactory)
                        .get(EquipmentSearchStatusViewModel.class);

    }

    /**
     * Fetch the equipments list from FireBase
     *
     * @param listener The listener which will be set the result list
     * @return The equipment list
     */
    @Override
    public List<Equipment> fetchAllEquipments(EquipmentsDataChangeListener listener) {

        mListener = listener;

        mEquipmentMViewModel
                .getEquipmentListLiveData().observe(mCompositionRoot.getActivity(), equipment -> {

            Log.i(TAG, "onChanged() inside method - equipment: " + equipment);

            mEquipments = equipment;
            mListener.onDataLoaded(equipment);

        });

        return mEquipments;

    }

    /**
     * Method fetches the equipments list by refreshing the screen
     *
     * @param listener data change listener
     * @param mSwipeRefreshLayoutMain screen component to take the progress image out when data is reached
     * @return the equipment list data fetched
     */
    @Override
    public List<Equipment> fetchAllRefreshedEquipments(EquipmentsRefreshDataChangeListener listener,
                                                       SwipeRefreshLayout mSwipeRefreshLayoutMain) {

        mRefreshedListener = listener;

        mEquipmentMViewModel
                .getEquipmentListLiveData().observe(mCompositionRoot.getActivity(), equipment -> {

            Log.i(TAG, "onChanged() inside method - equipment: " + equipment);

            mEquipments = equipment;
            mRefreshedListener.onDataRefreshLoaded(equipment, mSwipeRefreshLayoutMain);

        });

        return mEquipments;

    }

    /**
     * Fetch equipments by serial number from FireBase
     *
     * @param serialNumber the serial which will be used as parameter to the search
     * @param listener The listener which will be set the result list
     * @return the equipment data fetched
     */
    @Override
    public Equipment fetchEquipmentBySerial(String serialNumber, String uidKey, EquipmentDataChangeSerialListener listener) {

        mSerialListener = listener;

        mEquipmentSearchSerialViewModel
                .searchEquipmentBySerialLiveData(serialNumber, uidKey)
                .observe(mCompositionRoot.getActivity(), equipment -> {

            mEquipment = equipment;
            mSerialListener.onDataSerialLoaded(equipment);

        });

        return mEquipment;

    }

    /**
     * Fetch equipments by status from FireBase
     *
     * @param status the status which will be used as parameter to the search
     * @param listener The listener which will be set the result list
     * @return the equipment list data fetched
     */
    @Override
    public List<Equipment> fetchEquipmentByStatus(String status, String sessionUidKey, EquipmentsDataChangeListener listener) {

        mListener = listener;

        mEquipmentSearchStatusViewModel
                .searchEquipmentByStatusLiveData(status, sessionUidKey)
                .observe(mCompositionRoot.getActivity(), equipments -> {

                    mEquipments = equipments;
                    mListener.onDataLoaded(equipments);

                });

        return mEquipments;

    }

    /**
     * Insert new equipment data on Firebase
     *
     * @param equipment the object to be inserted
     */
    @Override
    public void insertEquipment(Equipment equipment) {

        // Save the data on firebase
        String key = reference.getReference().child(EQUIPMENTS).push().getKey();

        reference.getReference().child(EQUIPMENTS).child(key).setValue(equipment);

    }

    /**
     * Update the equipment data on Firebase
     *
     * @param equipment the object to be updated
     */
    @Override
    public void updateEquipment(Equipment equipment) {

        String id = equipment.getId();

        // I set the id to empty so it doesn't be added on firebase under the child id of equipment
        equipment.setId(null);

        reference.getReference().child(EQUIPMENTS).child(id).setValue(equipment);

        // Put back the reference to be used later
        equipment.setId(id);

    }

    /**
     * Delete the equipment data from Firebase
     *
     * @param equipment the object to be deleted
     */
    @Override
    public void deleteEquipment(final Equipment equipment) {

        final Query deleteQuery = reference.getReference().child(EQUIPMENTS).orderByChild(SERIAL).equalTo(equipment.getSerialNumber());

        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child(equipment.getId()).removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private DatabaseRef getDatabaseRef() {

        if (reference == null) {
            reference = new DatabaseRef();
        }

        return reference;

    }

}
