package com.life.equipmentlife.paid.view.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.EquipmentsRefreshDataChangeListener;
import com.life.equipmentlife.common.listener.OnDialogSerialSearch;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.common.listener.EquipmentDataChangeSerialListener;
import com.life.equipmentlife.common.listener.EquipmentsDataChangeListener;
import com.life.equipmentlife.common.listener.OnAddButtonClickListener;
import com.life.equipmentlife.common.listener.OnEquipmentItemSelectedListener;
import com.life.equipmentlife.common.listener.OnGoingToEquipmentRegistrationListener;
import com.life.equipmentlife.common.listener.RefreshScreenListener;
import com.life.equipmentlife.common.listener.SearchEquipmentBySerialListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseFragment;
import com.life.equipmentlife.paid.view.activities.equipment.list.listview.EquipmentListView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import java.util.ArrayList;
import java.util.List;

import static com.life.equipmentlife.paid.view.activities.equipment.list.listview.EquipmentListViewImpl.FLAG_CHANGED;
import static com.life.equipmentlife.paid.view.activities.equipment.list.listview.EquipmentListViewImpl.SERIAL_NUMBER_SAVED;

public class EquipmentListFragment extends BaseFragment implements EquipmentsDataChangeListener,
        RefreshScreenListener, OnAddButtonClickListener, EquipmentDataChangeSerialListener,
        SearchEquipmentBySerialListener, OnEquipmentItemSelectedListener, EquipmentsRefreshDataChangeListener {

    // Constant for logging
    private static final String TAG = EquipmentListFragment.class.getSimpleName();

    public static EquipmentListFragment getInstance() {

        Log.i("EquipmentListFragment", "getInstance() inside method");

        EquipmentListFragment fragment = new EquipmentListFragment();

        return fragment;

    }

    private EquipmentListView mListViewMvc;

    private ScreensNavigator mScreensNavigator;

    private EquipmentDao equipmentDao;

    private SessionManager mSession;

    private boolean savedInstance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate() inside method - savedInstanceState: " + savedInstanceState);

        if (savedInstanceState != null) {
            Log.i(TAG, "onCreate() inside method - IF - savedInstance: " + savedInstance);
            savedInstance = true;
            Log.i(TAG, "onCreate() inside method - IF - after - savedInstance: " + savedInstance);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() inside method - savedInstanceState: " + savedInstanceState);

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        equipmentDao = getCompositionRoot().getEquipmentDao();

        mSession = getCompositionRoot().getSessionManager();

        mListViewMvc =
                getCompositionRoot()
                        .getViewFactory()
                        .getEquipmentListView(container, equipmentDao,
                                this, this,
                                this, this,
                                mSession);

        return mListViewMvc.getRootView();

    }

    /**
     * Method takes to the Equipment details screen when user selects it from screen list
     *
     * @param equipment the item selected from list
     */
    @Override
    public void onEquipmentItemSelected(Equipment equipment) {

        View view = getActivity().findViewById(R.id.circleView_equipment_picture);

        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.transition_image));

        mScreensNavigator.goToEquipmentDetails(equipment, optionsCompat);

    }

    /**
     * Gets the equipment list to be shown on screen
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() inside method - savedInstance: " + savedInstance);

        if(!savedInstance) {
            Log.i(TAG, "onStart() inside method - IF - savedInstance: " + savedInstance);
            equipmentDao.fetchAllEquipments(this);
        }
    }

    /**
     * Receives the equipment list from Firebase and binds it with the screen class
     *
     * @param equipments the list got from Firebase
     */
    @Override
    public void onDataLoaded(List<Equipment> equipments) {
        Log.i(TAG, "onDataLoaded() inside method - equipment: " + equipments);
        mListViewMvc.bindEquipments(equipments);
    }

    /**
     * Refreshes the list screen when the user pulls it down
     */
    @Override
    public void onRefreshScreen(SwipeRefreshLayout mSwipeRefreshLayoutMain) {

        equipmentDao.fetchAllRefreshedEquipments(this, mSwipeRefreshLayoutMain);

    }

    /**
     * Receives the equipment list from Firebase and binds it with the screen class when refreshed
     *
     * @param equipments the list refreshed and got from Firebase
     * @param mSwipeRefreshLayoutMain
     */
    @Override
    public void onDataRefreshLoaded(List<Equipment> equipments, SwipeRefreshLayout mSwipeRefreshLayoutMain) {
        mListViewMvc.bindEquipmentsRefresh(equipments, mSwipeRefreshLayoutMain);
    }

    /**
     * Saves the state of the list when rotate the screen or leave the activity on background
     *
     * @param outState The bundle which will be saved the state of the list
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "onSaveInstanceState() inside method - outState: " + outState);

        mListViewMvc.onSaveInstanceState(outState);

        Log.i(TAG, "onSaveInstanceState() inside method - mListViewMvc.onSaveInstanceState(outState): " + outState);

        super.onSaveInstanceState(outState);

    }

    /**
     * Gets back the state of the list when screen was rotated or activity was left on background
     *
     * @param savedInstanceState The bundle which will be recovered for the list
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewStateRestored() inside method - savedInstanceState: " + savedInstanceState);

        super.onViewStateRestored(savedInstanceState);

        mListViewMvc.onViewStateRestored(savedInstanceState);

    }

    /**
     * Method searches the equipment by serial written in menu search on toolbar
     *
     * @param serial
     */
    @Override
    public void searchEquipmentBySerial(String serial) {
        equipmentDao.fetchEquipmentBySerial(serial, "", this);
    }

    /**
     * The equipment object got from the search on menu bar
     *
     * @param equipment
     */
    @Override
    public void onDataSerialLoaded(Equipment equipment) {
        Log.i(TAG, "onDataSerialLoaded() inside method - equipment: " + equipment);

        List<Equipment> equipments;

        if (equipment.getSerialNumber() != null) {

            equipments = new ArrayList<>();

            equipments.add(equipment);

        } else {

            equipments = getEquipmentEmptyList();

        }

        mListViewMvc.bindEquipments(equipments);

    }

    private List<Equipment> getEquipmentEmptyList() {
        List<Equipment> list = new ArrayList<>();
        return list;
    }

    /**
     * Method takes to the Equipment registration activity from this fragment
     *
     * @param fabButtonAddNewEquipment
     */
    @Override
    public void onAddFloatButtonRegistration(FloatingActionButton fabButtonAddNewEquipment) {
        ((OnGoingToEquipmentRegistrationListener)getContext()).goToEquipmentRegistration(fabButtonAddNewEquipment);
    }

}
