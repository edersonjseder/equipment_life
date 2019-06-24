package com.life.equipmentlife.paid.controller;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import com.google.firebase.database.DatabaseReference;
import com.life.equipmentlife.common.fragmentframehelper.FragmentFrameHelper;
import com.life.equipmentlife.common.fragmentframehelper.FragmentFrameWrapper;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.daoimpl.AddressDataDaoImpl;
import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.daoimpl.EstadoDaoImpl;
import com.life.equipmentlife.model.daoimpl.ProfileDaoImpl;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.model.networking.api.CepApi;
import com.life.equipmentlife.model.networking.api.EstadoApi;
import com.life.equipmentlife.model.networking.retrofit.RetrofitCompositionRoot;
import com.life.equipmentlife.model.repository.CepSearchRepository;
import com.life.equipmentlife.model.repository.EquipmentListRepository;
import com.life.equipmentlife.model.repository.EquipmentSearchSerialRepository;
import com.life.equipmentlife.model.repository.EquipmentSearchStatusRepository;
import com.life.equipmentlife.model.repository.EstadoListRepository;
import com.life.equipmentlife.model.repository.ProfileSearchEmailRepository;
import com.life.equipmentlife.model.repository.ProfileSearchUidKeyRepository;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.logout.LogoutApplication;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerHelper;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;
import com.life.equipmentlife.paid.view.viewfactory.ViewMvcFactory;

public class ControllerCompositionRoot {

    private RetrofitCompositionRoot mCompositionRoot;

    private DatabaseRef databaseReference;
    private FragmentActivity mBaseActivity;
    private ViewMvcFactory mViewFactory;
    private SessionManager mSession;
    private ScreensNavigator mScreenNavigator;

    private EquipmentListRepository mEquipmentListRepository;
    private ProfileSearchUidKeyRepository mProfileSearchUidKeyRepository;
    private ProfileSearchEmailRepository mProfileSearchEmailRepository;
    private EquipmentSearchSerialRepository mEquipmentSearchSerialRepository;
    private EquipmentSearchStatusRepository mEquipmentSearchStatusRepository;

    private CepSearchRepository mCepSearchRepository;

    private EstadoListRepository mEstadoListRepository;

    private LogoutApplication mLogoutApplication;

    private EquipmentDaoImpl mEquipmentDaoImpl;

    private ProfileDaoImpl mProfileDaoImpl;

    private AddressDataDaoImpl mAddressDataDaoImpl;

    private EstadoDaoImpl mEstadoDaoImpl;

    private FragmentFrameHelper mFragmentFrameHelper;

    public ControllerCompositionRoot(RetrofitCompositionRoot compositionRoot, DatabaseRef databaseRef, SessionManager session, FragmentActivity baseActivity) {

        mCompositionRoot = compositionRoot;
        mBaseActivity = baseActivity;
        databaseReference = databaseRef;
        mSession = session;

    }

    /**
     * Method that gets the EquipmentList database Repository
     *
     * @return
     */
    public EquipmentListRepository getEquipmentListRepository() {

        mEquipmentListRepository = new EquipmentListRepository(getDatabaseReference(), this);

        return mEquipmentListRepository;

    }

    public EstadoListRepository getEstadoListRepository() {

        mEstadoListRepository = new EstadoListRepository(getEstadoApi());

        return mEstadoListRepository;

    }

    /**
     * Method that gets the Equipment search by serial database Repository
     *
     * @return
     */
    public EquipmentSearchSerialRepository getEquipmentSearchSerialRepository() {

        mEquipmentSearchSerialRepository = new EquipmentSearchSerialRepository(getDatabaseReference(), this);

        return mEquipmentSearchSerialRepository;

    }

    /**
     * Method that gets the Equipment search by status database Repository
     *
     * @return
     */
    public EquipmentSearchStatusRepository getEquipmentSearchStatusRepository() {

        mEquipmentSearchStatusRepository = new EquipmentSearchStatusRepository(getDatabaseReference(), this);

        return mEquipmentSearchStatusRepository;

    }

    /**
     * Method that gets the Profile search by uidKey database Repository
     *
     * @return
     */
    public ProfileSearchUidKeyRepository getProfileSearchUidKeyRepository() {

        mProfileSearchUidKeyRepository = new ProfileSearchUidKeyRepository(getDatabaseReference(), this);

        return mProfileSearchUidKeyRepository;

    }

    /**
     * Method that gets the Profile search by email database Repository
     *
     * @return
     */
    public ProfileSearchEmailRepository getProfileSearchEmailRepository() {

        mProfileSearchEmailRepository = new ProfileSearchEmailRepository(getDatabaseReference(), this);

        return mProfileSearchEmailRepository;

    }

    /**
     * Method that gets the ViewMvcFactory class that provides activities
     *
     * @return
     */
    public ViewMvcFactory getViewFactory() {

        mViewFactory = new ViewMvcFactory(getLayoutInflater(), getNavDrawerHelper());

        return mViewFactory;

    }

    /**
     * Method that gets the Equipment Data Access Object
     *
     * @return
     */
    public EquipmentDao getEquipmentDao() {

        mEquipmentDaoImpl = new EquipmentDaoImpl(getActivity().getApplication(), this);

        return mEquipmentDaoImpl;

    }

    /**
     * Method that gets the Profile data Access Object
     *
     * @return
     */
    public ProfileDao getProfileDao() {

        mProfileDaoImpl = new ProfileDaoImpl(getActivity().getApplication(), this);

        return mProfileDaoImpl;

    }

    /**
     * Method that gets the Address data Access Object
     *
     * @return
     */
    public AddressDataDao getAddressDataDao() {

        mAddressDataDaoImpl = new AddressDataDaoImpl(getActivity().getApplication(), this);

        return mAddressDataDaoImpl;

    }

    /**
     * Method that gets the estado data Access Object
     *
     * @return
     */
    public EstadoDao getEstadoDao() {

        mEstadoDaoImpl = new EstadoDaoImpl(getActivity().getApplication(), this);

        return mEstadoDaoImpl;

    }

    /**
     * Method that gets the Screens navigator class that provides access to the
     * app flowing activities
     *
     * @return
     */
    public ScreensNavigator getScreensNavigator() {

        mScreenNavigator = new ScreensNavigator(getContext(), getFragmentFrameHelper());

        return mScreenNavigator;

    }

    /**
     * Method that gets the FragmentHelper class
     *
     * @return
     */
    private FragmentFrameHelper getFragmentFrameHelper() {

        mFragmentFrameHelper = new FragmentFrameHelper(getFragmentFrameWrapper(), getFragmentManager());

        return mFragmentFrameHelper;

    }

    /**
     * Method that returns the session manager class
     *
     * @return
     */
    public SessionManager getSessionManager() {
        return mSession;
    }

    /**
     * Method that gets the base activity
     *
     * @return
     */
    public FragmentActivity getActivity() {
        return mBaseActivity;
    }

    /**
     * Method that gets the Logout application class
     *
     * @return
     */
    public LogoutApplication getLogoutApplication() {

        mLogoutApplication =
                new LogoutApplication(getScreensNavigator(), getSessionManager(), getContext());

        return mLogoutApplication;

    }

    /**
     * Method that provides the Retrofit repository
     *
     * @return
     */
    public CepSearchRepository getCepSearchRepository() {

        mCepSearchRepository = new CepSearchRepository(getCepApi());

        return mCepSearchRepository;

    }

    private CepApi getCepApi() {
        return mCompositionRoot.getRetrofitCepRestConnectionProvider().getCepApi();
    }

    private EstadoApi getEstadoApi() {
        return mCompositionRoot.getRetrofitEstadoRestConnectionProvider().getEstadoApi();
    }

    private NavDrawerHelper getNavDrawerHelper() {
        return (NavDrawerHelper) getActivity();
    }

    private FragmentManager getFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    private FragmentFrameWrapper getFragmentFrameWrapper() {
        return (FragmentFrameWrapper) getActivity();
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    private DatabaseReference getDatabaseReference() {
        return databaseReference.getReference();
    }

    private Context getContext() {
        return mBaseActivity;
    }

}
