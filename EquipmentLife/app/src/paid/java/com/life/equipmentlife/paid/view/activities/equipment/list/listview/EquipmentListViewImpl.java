package com.life.equipmentlife.paid.view.activities.equipment.list.listview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.EquipmentsDataChangeListener;
import com.life.equipmentlife.common.listener.OnAddButtonClickListener;
import com.life.equipmentlife.common.listener.OnDialogSerialSearch;
import com.life.equipmentlife.common.listener.OnEquipmentItemSelectedListener;
import com.life.equipmentlife.common.listener.RefreshScreenListener;
import com.life.equipmentlife.common.listener.SearchEquipmentBySerialListener;
import com.life.equipmentlife.common.utils.ListHolder;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.executors.SwipeRefreshHandler;
import com.life.equipmentlife.model.executors.ThreadExecutorDeleteEquipment;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.adapter.EquipmentAdapter;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerHelper;
import com.life.equipmentlife.paid.view.toolbar.ToolbarView;
import com.life.equipmentlife.paid.view.viewfactory.ViewMvcFactory;

import java.util.ArrayList;
import java.util.List;

import static com.life.equipmentlife.common.constants.Constants.EQUIPMENTS;

public class EquipmentListViewImpl extends BaseObservableViewMvc implements EquipmentListView,
        SearchView.OnQueryTextListener, Toolbar.OnMenuItemClickListener, EquipmentsDataChangeListener {

    // Constant for logging
    private static final String TAG = EquipmentListViewImpl.class.getSimpleName();

    public static final String FLAG_CHANGED = "configChangeFlag";
    public static final String SERIAL_NUMBER_SAVED = "serialNumberSaved";

    // Equipment Adapter to show the list items
    private EquipmentAdapter mEquipmentAdapter;

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerViewEquipmentList;

    private LinearLayoutManager linearLayoutManager;

    private DefaultItemAnimator mDefaultItemAnimator;

    private Handler mHandler;

    private SearchView searchView;

    private ItemTouchHelper mItemTouchHelper;

    private ItemTouchHelper.SimpleCallback mSimpleCallback;

    private EquipmentDaoImpl implDelete;

    private ThreadExecutorDeleteEquipment delete;

    private OnAddButtonClickListener mOnAddButtonClickListener;

    private SearchEquipmentBySerialListener serialListener;

    private OnEquipmentItemSelectedListener mOnEquipmentItemSelectedListener;

    private ToolbarView mToolbarView;

    private EquipmentDao mEquipmentDao;

    private SessionManager session;

    private String sessionUidKey;

    private boolean isFromActionSearch;

    /** View Components **/
    private LinearLayout llOverboxEquipmentListLoading;
    private ProgressBar pbEquipmentListLoading;
    private FloatingActionButton fabButtonAddNewEquipment;
    private SwipeRefreshLayout swipeRefreshLayoutMain;
    private SwipeRefreshHandler mSwipeRefreshHandler;
    private LinearLayout layoutEquipmentEmptyListText;
    private TextView tvEquipmentEmptyListText;
    private Toolbar mToolbar;
    private NavDrawerHelper mNavDrawerHelper;

    public EquipmentListViewImpl(LayoutInflater inflater, ViewGroup parent,
                                 ViewMvcFactory viewFactory,
                                 NavDrawerHelper navDrawerHelper, EquipmentDao equipmentDao,
                                 RefreshScreenListener refreshScreenListener,
                                 OnAddButtonClickListener onAddButtonClickListener,
                                 SearchEquipmentBySerialListener searchEquipmentBySerialListener,
                                 OnEquipmentItemSelectedListener onEquipmentItemSelectedListener,
                                 SessionManager mSession) {

        setRootView(inflater.inflate(R.layout.activity_equipment_list_layout, parent, false));

        initComponents();

        mEquipmentDao = equipmentDao;

        session = mSession;

        sessionUidKey = mSession.getProfileOnPrefs().getId();

        mOnEquipmentItemSelectedListener = onEquipmentItemSelectedListener;

        initRecyclerViewEquipments(viewFactory);

        mNavDrawerHelper = navDrawerHelper;

        mOnAddButtonClickListener = onAddButtonClickListener;

        serialListener = searchEquipmentBySerialListener;

        mHandler = new Handler();

        initSwipeRefresh(mHandler, refreshScreenListener);

        implDelete = new EquipmentDaoImpl();

        mToolbarView = viewFactory.getToolbarView(mToolbar);

        initToolbar();

         /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        fabButtonAddNewEquipment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mOnAddButtonClickListener.onAddFloatButtonRegistration(fabButtonAddNewEquipment);

            }

        });

    }

    /**
     * Init screen components
     */
    private void initComponents() {

        mRecyclerViewEquipmentList = findViewById(R.id.rv_equipment_list);

        swipeRefreshLayoutMain = findViewById(R.id.swipe_refresh_layout_main);

        fabButtonAddNewEquipment = findViewById(R.id.fab_add_new_equipment);

        layoutEquipmentEmptyListText = findViewById(R.id.layout_equipment_empty_list_text);

        tvEquipmentEmptyListText = findViewById(R.id.tv_equipment_empty_list_text);

        llOverboxEquipmentListLoading = findViewById(R.id.ll_overbox_equipment_list_loading);

        pbEquipmentListLoading = findViewById(R.id.pb_equipment_list_loading);

        mToolbar = findViewById(R.id.equipment_toolbar);

    }

    /**
     * Method initiates the list components for the screen
     *
     * @param viewFactory
     */
    private void initRecyclerViewEquipments(ViewMvcFactory viewFactory) {

        mEquipmentAdapter = new EquipmentAdapter(viewFactory, mOnEquipmentItemSelectedListener, session);

        linearLayoutManager = new LinearLayoutManager(getContext());

        mDefaultItemAnimator = new DefaultItemAnimator();

        mRecyclerViewEquipmentList.setLayoutManager(linearLayoutManager);
        mRecyclerViewEquipmentList.setHasFixedSize(true);
        mRecyclerViewEquipmentList.setItemAnimator(mDefaultItemAnimator);

        mSimpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swypeDir) {

                int position = viewHolder.getAdapterPosition();

                List<Equipment> equipments = mEquipmentAdapter.getEquipmentList();

                delete = new ThreadExecutorDeleteEquipment(implDelete, getContext(), equipments, position);

                delete.removeItemDataThread();

            }
        };

        mItemTouchHelper = new ItemTouchHelper(mSimpleCallback);

        mItemTouchHelper.attachToRecyclerView(mRecyclerViewEquipmentList);

    }

    /**
     * Init swipe refresh component for the user to pull and refresh the list screen
     *
     * @param handler
     * @param listener
     */
    private void initSwipeRefresh(Handler handler, RefreshScreenListener listener) {

        mSwipeRefreshHandler = new SwipeRefreshHandler(handler, swipeRefreshLayoutMain, listener);

        swipeRefreshLayoutMain.setColorSchemeResources(R.color.theme_accent, R.color.light_blue, R.color.blue);

        swipeRefreshLayoutMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //handling swipe refresh
                mSwipeRefreshHandler.executeHandler();

            }
        });

    }

    /**
     * Method inits the toolbar component
     */
    @SuppressLint("RestrictedApi")
    private void initToolbar() {

        mToolbarView.setImageTitle();

        mToolbar.addView(mToolbarView.getRootView());

        mToolbar.inflateMenu(R.menu.equipment_option_status_menu);

        Menu menu = mToolbar.getMenu();

        if (menu instanceof MenuBuilder) {

            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);

        }

        setToolbarListener(mToolbar);

        mToolbarView.enableHamburgerButtonAndListen(new ToolbarView.HamburgerClickListener() {
            @Override
            public void onHamburgerClicked() {
                mNavDrawerHelper.openDrawer();
            }
        });

    }

    /**
     * Method populates the equipment list object from activity after searching on Firebase
     *
     * @param equipments the list to be shown on screen
     */
    @Override
    public void bindEquipments(List<Equipment> equipments) {
        Log.i(TAG, "bindEquipments() inside method - equipments: " + equipments);

        showProgressIndication();

        if (!equipments.isEmpty()) {
            Log.i(TAG, "bindEquipments() inside IF - equipments: " + equipments);

            hideProgressIndication();

            layoutEquipmentEmptyListText.setVisibility(View.GONE);

            mEquipmentAdapter.bindEquipments(equipments);

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

        } else {
            Log.i(TAG, "bindEquipments() inside ELSE - equipments: " + equipments);
            hideProgressIndication();

            mEquipmentAdapter.bindEquipments(equipments);

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

            layoutEquipmentEmptyListText.setVisibility(View.VISIBLE);

            tvEquipmentEmptyListText.setText(getContext().getResources().getString(R.string.equipment_list_empty));

        }

    }

    /**
     * Method populates the equipment list object from activity after refreshing the screen
     *
     * @param equipments the equipments list fetched
     * @param mSwipeRefreshLayoutMain the progress component to be out when data is fetched
     */
    @Override
    public void bindEquipmentsRefresh(List<Equipment> equipments, SwipeRefreshLayout mSwipeRefreshLayoutMain) {
        Log.i(TAG, "bindEquipmentsRefresh() inside method - equipments: " + equipments);

        mSwipeRefreshLayoutMain.setRefreshing(false);

        if (!equipments.isEmpty()) {

            hideProgressIndication();

            layoutEquipmentEmptyListText.setVisibility(View.GONE);

            mEquipmentAdapter.bindEquipments(equipments);

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

        } else {

            mEquipmentAdapter.bindEquipments(getEquipmentEmptyList());

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

            layoutEquipmentEmptyListText.setVisibility(View.VISIBLE);

            tvEquipmentEmptyListText.setText(getContext().getResources().getString(R.string.equipment_list_empty));

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState() inside method + outState: " + outState);

        ListHolder holderList = new ListHolder();

        holderList.setEquipmentList(mEquipmentAdapter.getEquipmentList());

        Log.i(TAG, "onSaveInstanceState() inside method + holderList: " + holderList.getEquipmentList());

        outState.putSerializable(EQUIPMENTS, holderList);

    }

    @Override
    public void onViewStateRestored(Bundle outState) {
        Log.i(TAG, "onViewStateRestored() inside method - outState: " + outState);

        if (outState != null) {
            Log.i(TAG, "onViewStateRestored() inside method - IF");

            ListHolder holder = (ListHolder) outState.getSerializable(EQUIPMENTS);

            List<Equipment> listMovies = holder.getEquipmentList();

            Log.i(TAG, "onViewStateRestored() inside method - IF - listMovies: " + listMovies);

            mEquipmentAdapter.bindEquipments(listMovies);

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

            hideProgressIndication();

        }

    }

    /**
     * Sets the toolbar menu on the right top of the screen
     *
     * @param toolbar
     */
    public void setToolbarListener(Toolbar toolbar) {

        toolbar.setOnMenuItemClickListener(this);

    }

    /**
     * Searchs the equipment by serial and show it on the screen
     *
     * @param serial
     */
    public void setupEquipmentBySerialNumberViewModel(String serial) {
        Log.i(TAG, "setupEquipmentBySerialNumberViewModel() inside method - isFromActionSearch: " + isFromActionSearch);

        showProgressIndication();

        serialListener.searchEquipmentBySerial(serial);

    }

    /**
     * Shows the progressbar on screen
     */
    public void showProgressIndication() {
        llOverboxEquipmentListLoading.setVisibility(View.VISIBLE);
        pbEquipmentListLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the progressbar from screen
     */
    public void hideProgressIndication() {
        llOverboxEquipmentListLoading.setVisibility(View.GONE);
        pbEquipmentListLoading.setVisibility(View.GONE);
    }

    /**
     * Query text box listener when user adds information in the search box on toolbar
     *
     * @param serial
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String serial) {
        Log.i(TAG, "onQueryTextSubmit() inside method");

        isFromActionSearch = true;

        setupEquipmentBySerialNumberViewModel(serial);

        return true;

    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    /**
     * Menu click listener activated when user clicks to show the items from it
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if (item.getItemId() == R.id.action_equipment_search) {

            searchView = (SearchView) item.getActionView();

            searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

            EditText txtSearch =
                    searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            txtSearch.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));
            txtSearch.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            txtSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.max_length_search_view))});

            searchView.setSubmitButtonEnabled(true);

            searchView.setOnQueryTextListener(this);

        }

        if (item.getItemId() == R.id.item_equipment_owned) {
            Log.i(TAG, "onMenuItemClick() inside method - item_equipment_owned");

            showProgressIndication();

            mEquipmentDao.fetchEquipmentByStatus(getContext().getResources().getString(R.string.equipment_owned), sessionUidKey,this);

            return true;

        }

        if (item.getItemId() == R.id.item_equipment_sold) {
            Log.i(TAG, "onMenuItemClick() inside method - item_equipment_sold");

            showProgressIndication();

            mEquipmentDao.fetchEquipmentByStatus(getContext().getResources().getString(R.string.equipment_sold), sessionUidKey,this);

            return true;

        }


        if (item.getItemId() == R.id.item_equipment_stolen) {
            Log.i(TAG, "onMenuItemClick() inside method - item_equipment_stolen");

            showProgressIndication();

            mEquipmentDao.fetchEquipmentByStatus(getContext().getResources().getString(R.string.equipment_stolen), sessionUidKey, this);

            return true;

        }

        return false;
    }

    /**
     * Method that gets the equipments list of item menu clicked by user when he
     * chooses one of the status listed on menu
     *
     * @param equipments the equipments list fetched
     */
    @Override
    public void onDataLoaded(List<Equipment> equipments) {
        Log.i(TAG, "onDataLoaded() inside method - equipment: " + equipments);

        if (equipments != null) {

            if (!equipments.isEmpty()) {

                hideProgressIndication();

                layoutEquipmentEmptyListText.setVisibility(View.GONE);

                mEquipmentAdapter.bindEquipments(equipments);

                mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

            } else {

                hideProgressIndication();

                mEquipmentAdapter.bindEquipments(getEquipmentEmptyList());

                mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

                layoutEquipmentEmptyListText.setVisibility(View.VISIBLE);

                tvEquipmentEmptyListText.setText(getContext().getResources().getString(R.string.equipment_list_empty));

                showEmptyList();

            }

        } else {

            hideProgressIndication();

            mEquipmentAdapter.bindEquipments(getEquipmentEmptyList());

            mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

            layoutEquipmentEmptyListText.setVisibility(View.VISIBLE);

            tvEquipmentEmptyListText.setText(getContext().getResources().getString(R.string.equipment_list_empty));

            showEmptyList();

        }

    }

    private List<Equipment> getEquipmentEmptyList() {
        List<Equipment> list = new ArrayList<>();
        return list;
    }

    private void showEmptyList() {

        Toast.makeText(getContext(), getContext().getResources().getString(R.string.equipment_message_not_found), Toast.LENGTH_LONG).show();

    }

}
