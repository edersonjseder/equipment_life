package com.life.equipmentlife.paid.view.viewfactory;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.common.listener.OnActivityForResultImgPickerListener;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.common.listener.OnAddButtonClickListener;
import com.life.equipmentlife.common.listener.OnCreateUserListener;
import com.life.equipmentlife.common.listener.OnEnterEquipmentEditListener;
import com.life.equipmentlife.common.listener.OnEnterProfileEditListener;
import com.life.equipmentlife.common.listener.OnEquipmentItemSelectedListener;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnSetupSharedElementTransitionListener;
import com.life.equipmentlife.common.listener.OnSignInGoogle;
import com.life.equipmentlife.common.listener.RefreshScreenListener;
import com.life.equipmentlife.common.listener.SearchEquipmentBySerialListener;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.equipment.details.detailsview.EquipmentDetailsView;
import com.life.equipmentlife.paid.view.activities.equipment.details.detailsview.EquipmentDetailsViewImpl;
import com.life.equipmentlife.paid.view.activities.equipment.edit.editview.EquipmentDetailsEditView;
import com.life.equipmentlife.paid.view.activities.equipment.edit.editview.EquipmentDetailsEditViewImpl;
import com.life.equipmentlife.paid.view.activities.equipment.list.listitemview.EquipmentListItemView;
import com.life.equipmentlife.paid.view.activities.equipment.list.listitemview.EquipmentListItemViewImpl;
import com.life.equipmentlife.paid.view.activities.equipment.list.listview.EquipmentListView;
import com.life.equipmentlife.paid.view.activities.equipment.list.listview.EquipmentListViewImpl;
import com.life.equipmentlife.paid.view.activities.equipment.registration.registrationview.EquipmentRegistrationView;
import com.life.equipmentlife.paid.view.activities.equipment.registration.registrationview.EquipmentRegistrationViewImpl;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerView;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl;
import com.life.equipmentlife.paid.view.activities.login.loginview.LoginView;
import com.life.equipmentlife.paid.view.activities.login.loginview.LoginViewImpl;
import com.life.equipmentlife.paid.view.activities.profile.details.detailsview.ProfileDetailsView;
import com.life.equipmentlife.paid.view.activities.profile.details.detailsview.ProfileDetailsViewImpl;
import com.life.equipmentlife.paid.view.activities.profile.edit.editview.ProfileDetailsEditView;
import com.life.equipmentlife.paid.view.activities.profile.edit.editview.ProfileDetailsEditViewImpl;
import com.life.equipmentlife.paid.view.activities.profile.registration.registrationview.ProfileRegistrationView;
import com.life.equipmentlife.paid.view.activities.profile.registration.registrationview.ProfileRegistrationViewImpl;
import com.life.equipmentlife.paid.view.activities.signup.signupfacebook.signupfacebookview.SignUpFacebookView;
import com.life.equipmentlife.paid.view.activities.signup.signupfacebook.signupfacebookview.SignUpFacebookViewImpl;
import com.life.equipmentlife.paid.view.activities.signup.signupgoogle.signupgoogleview.SignUpGoogleView;
import com.life.equipmentlife.paid.view.activities.signup.signupgoogle.signupgoogleview.SignUpGoogleViewImpl;
import com.life.equipmentlife.paid.view.activities.slider.slidescreenview.SliderScreenView;
import com.life.equipmentlife.paid.view.activities.start.startview.StartScreenView;
import com.life.equipmentlife.paid.view.activities.start.startview.StartScreenViewImpl;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerHelper;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerView;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerViewImpl;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;
import com.life.equipmentlife.paid.view.toolbar.ToolbarView;

public class ViewMvcFactory {

    private final LayoutInflater mLayoutInflater;
    private NavDrawerHelper mNavDrawerHelper;
    private EquipmentListItemViewImpl mEquipmentListItemViewImpl;
    private EquipmentListViewImpl mEquipmentListViewImpl;
    private ProfileRegistrationViewImpl mProfileRegistrationViewImpl;
    private ProfileDetailsViewImpl mProfileDetailsViewImpl;
    private ProfileDetailsEditViewImpl mProfileDetailsEditViewImpl;
    private LoginViewImpl mLoginViewMvcImpl;
    private SignUpFacebookViewImpl mSignUpFacebookViewImpl;
    private StartScreenViewImpl mStartScreenViewImpl;
    private EquipmentRegistrationViewImpl mEquipmentRegistrationViewImpl;
    private EquipmentDetailsEditViewImpl mEquipmentDetailsEditViewImpl;
    private EquipmentDetailsViewImpl mEquipmentDetailsViewImpl;
    private NavDrawerViewImpl mNavDrawerViewImpl;
    private SliderScreenView mSliderScreenView;
    private ImagePickerViewImpl mImagePickerViewImpl;
    private SignUpGoogleViewImpl mSignUpGoogleViewImpl;

    public ViewMvcFactory(LayoutInflater layoutInflater, NavDrawerHelper navDrawerHelper) {
        mLayoutInflater = layoutInflater;
        mNavDrawerHelper = navDrawerHelper;
    }

    /**
     * Method gets the slider screen view for SliderScreen activity
     *
     * @param parent
     * @param session
     * @param screensNavigator
     * @param listener
     * @param activity
     * @return
     */
    public SliderScreenView getSliderScreenView(@Nullable ViewGroup parent,
                                                SessionManager session,
                                                ScreensNavigator screensNavigator,
                                                OnFinishActivityListener listener,
                                                FragmentActivity activity) {

        mSliderScreenView =
                new SliderScreenView(mLayoutInflater, parent, session, screensNavigator, listener, activity);

        return mSliderScreenView;

    }

    /**
     * Method gets the image picker screen view for ImagePicker activity
     *
     * @param parent
     * @param mIntent
     * @param imagePicker
     * @param activity
     * @return
     */
    public ImagePickerView getImagePickerView(@Nullable ViewGroup parent, Intent mIntent,
                                              OnActivityForResultImgPickerListener imagePicker,
                                              FragmentActivity activity) {

        mImagePickerViewImpl = new ImagePickerViewImpl(mLayoutInflater, parent, mIntent, imagePicker, activity);

        return mImagePickerViewImpl;

    }

    /**
     * Method gets the login screen view for Login activity
     *
     * @param parent
     * @param auth
     * @param session
     * @param profileDao
     * @param onFinishActivityListener
     * @return
     */
    public LoginView getLoginViewMvc(@Nullable ViewGroup parent,
                                     FirebaseAuth auth,
                                     SessionManager session,
                                     ProfileDao profileDao,
                                     OnFinishActivityListener onFinishActivityListener) {

        mLoginViewMvcImpl
                = new LoginViewImpl(mLayoutInflater, parent, auth, session, profileDao, onFinishActivityListener);

        return mLoginViewMvcImpl;

    }

    /**
     * Method gets the Sign Up Facebook screen view for SignUpFacebook activity
     *
     * @param parent
     * @param session
     * @param screensNavigator
     * @param activity
     * @param implProfileDao
     * @return
     */
    public SignUpFacebookView getSignUpFacebookView(@Nullable ViewGroup parent,
                                                    SessionManager session,
                                                    ScreensNavigator screensNavigator,
                                                    FragmentActivity activity,
                                                    ProfileDao implProfileDao) {

        mSignUpFacebookViewImpl =
                new SignUpFacebookViewImpl(mLayoutInflater, parent, session,
                        screensNavigator, activity, implProfileDao);

        return mSignUpFacebookViewImpl;

    }

    /**
     * Method gets the Sign Up Google screen view for SignUpGoogle activity
     *
     * @param parent
     * @param session
     * @param screensNavigator
     * @param activity
     * @param onSignInGoogle
     * @param implProfileDao
     * @return
     */
    public SignUpGoogleView getSignUpGoogleView(@Nullable ViewGroup parent,
                                                SessionManager session,
                                                ScreensNavigator screensNavigator,
                                                FragmentActivity activity,
                                                OnSignInGoogle onSignInGoogle,
                                                ProfileDao implProfileDao) {

        mSignUpGoogleViewImpl =
                new SignUpGoogleViewImpl(mLayoutInflater, parent, session, screensNavigator,
                        activity, onSignInGoogle, implProfileDao);

        return mSignUpGoogleViewImpl;

    }

    /**
     * Method gets the Nav Drawer screen view for EquipmentList activity
     *
     * @param parent
     * @param profileOwner
     * @return
     */
    public NavDrawerView getNavDrawerView(@Nullable ViewGroup parent, Profile profileOwner) {

        mNavDrawerViewImpl =
                    new NavDrawerViewImpl(mLayoutInflater, parent, profileOwner);

        return mNavDrawerViewImpl;

    }

    /**
     * Method gets the Profile registration screen view for ProfileRegistration activity
     *
     * @param parent
     * @param screensNavigator
     * @param sessionManager
     * @param activity
     * @param listener
     * @param settingsListener
     * @param contentResolver
     * @param estadoDao
     * @return
     */
    public ProfileRegistrationView getProfileRegistrationView(@Nullable ViewGroup parent,
                                                              ScreensNavigator screensNavigator,
                                                              SessionManager sessionManager,
                                                              FragmentActivity activity, PickerOptionListener listener,
                                                              OnOpenSettingsListener settingsListener,
                                                              ContentResolver contentResolver,
                                                              AddressDataDao addressDao, EstadoDao estadoDao) {

        mProfileRegistrationViewImpl =
                new ProfileRegistrationViewImpl(mLayoutInflater, parent,
                        screensNavigator, sessionManager, activity, listener,
                        settingsListener, contentResolver, addressDao, estadoDao);

        return mProfileRegistrationViewImpl;

    }

    /**
     * Method gets the Profile details screen view for ProfileDetails activity
     *
     * @param parent
     * @param mSession
     * @param onEnterProfileEditListener
     * @return
     */
    public ProfileDetailsView getProfileDetailsView(@Nullable ViewGroup parent,
                                                    SessionManager mSession,
                                                    OnEnterProfileEditListener onEnterProfileEditListener) {

        mProfileDetailsViewImpl =
                new ProfileDetailsViewImpl(mLayoutInflater, parent, mSession, onEnterProfileEditListener);

        return mProfileDetailsViewImpl;

    }

    /**
     * Method gets the Profile details edit screen view for ProfileDetailsEdit activity
     *
     * @param parent
     * @param profileDao
     * @param session
     * @param finishListener
     * @param activity
     * @param optionListener
     * @param settingsListener
     * @param contentResolver
     * @return
     */
    public ProfileDetailsEditView getProfileDetailsEditView(@Nullable ViewGroup parent, ProfileDao profileDao,
                                                            SessionManager session, OnFinishActivityListener finishListener,
                                                            FragmentActivity activity,
                                                            PickerOptionListener optionListener,
                                                            OnOpenSettingsListener settingsListener,
                                                            ContentResolver contentResolver,
                                                            AddressDataDao addressDao, EstadoDao estadoDao) {

        mProfileDetailsEditViewImpl =
                new ProfileDetailsEditViewImpl(mLayoutInflater, parent, session,
                        profileDao, finishListener, activity, optionListener, settingsListener,
                        contentResolver, addressDao, estadoDao);

        return mProfileDetailsEditViewImpl;

    }

    /**
     * Method gets the start screen view for StartScreen activity
     *
     * @param parent
     * @param mScreensNavigator
     * @param activity
     * @return
     */
    public StartScreenView getStartScreenView(@Nullable ViewGroup parent,
                                              ScreensNavigator mScreensNavigator,
                                              FragmentActivity activity) {

        mStartScreenViewImpl =
                new StartScreenViewImpl(mLayoutInflater, parent,
                        mScreensNavigator, activity);

        return mStartScreenViewImpl;

    }

    /**
     * Method gets the Equipment list screen view for EquipmentList activity
     *
     * @param parent
     * @param mEquipmentDao
     * @param refreshScreenListener
     * @param onAddButtonClickListener
     * @param searchEquipmentBySerialListener
     * @param onEquipmentItemSelectedListener
     * @param mSession
     * @return
     */
    public EquipmentListView getEquipmentListView(@Nullable ViewGroup parent,
                                                  EquipmentDao mEquipmentDao,
                                                  RefreshScreenListener refreshScreenListener,
                                                  OnAddButtonClickListener onAddButtonClickListener,
                                                  SearchEquipmentBySerialListener searchEquipmentBySerialListener,
                                                  OnEquipmentItemSelectedListener onEquipmentItemSelectedListener,
                                                  SessionManager mSession) {

        mEquipmentListViewImpl =
                new EquipmentListViewImpl(mLayoutInflater, parent,
                        this, mNavDrawerHelper, mEquipmentDao,
                        refreshScreenListener, onAddButtonClickListener,
                        searchEquipmentBySerialListener,
                        onEquipmentItemSelectedListener,
                        mSession);

        return mEquipmentListViewImpl;

    }

    /**
     * Method gets the Equipment details screen view for EquipmentDetails activity
     *
     * @param parent
     * @param profileDao
     * @param onEnterEquipmentEditListener
     * @param sessionManager
     * @return
     */
    public EquipmentDetailsView getEquipmentDetailsView(@Nullable ViewGroup parent, ProfileDao profileDao,
                                                        OnEnterEquipmentEditListener onEnterEquipmentEditListener,
                                                        SessionManager sessionManager) {

        mEquipmentDetailsViewImpl =
                new EquipmentDetailsViewImpl(mLayoutInflater, parent, profileDao,
                                             onEnterEquipmentEditListener, sessionManager);

        return mEquipmentDetailsViewImpl;

    }

    /**
     * Method gets the Equipment details edit screen view for EquipmentDetailsEdit activity
     *
     * @param parent
     * @param profileDao
     * @param equipmentDao
     * @param session
     * @param contentResolver
     * @param activity
     * @param listener
     * @param settingsListener
     * @param finishListener
     * @return
     */
    public EquipmentDetailsEditView getEquipmentDetailsEditView(@Nullable ViewGroup parent, ProfileDao profileDao,
                                                                EquipmentDao equipmentDao, SessionManager session,
                                                                ContentResolver contentResolver, FragmentActivity activity,
                                                                PickerOptionListener listener, OnOpenSettingsListener settingsListener,
                                                                OnFinishActivityListener finishListener) {

        mEquipmentDetailsEditViewImpl =
                new EquipmentDetailsEditViewImpl(mLayoutInflater, parent, session, profileDao,
                                                 equipmentDao, contentResolver, activity, listener,
                                                 settingsListener, finishListener);

        return mEquipmentDetailsEditViewImpl;

    }

    /**
     * Method gets the Equipment List Item screen view for EquipmentListItem activity
     *
     * @param parent
     * @param onEquipmentItemSelectedListener
     * @param mSession
     * @return
     */
    public EquipmentListItemView getEquipmentListItemView(@Nullable ViewGroup parent,
                                                          OnEquipmentItemSelectedListener onEquipmentItemSelectedListener,
                                                          SessionManager mSession) {

        mEquipmentListItemViewImpl =
                new EquipmentListItemViewImpl(mLayoutInflater, parent,
                        onEquipmentItemSelectedListener, mSession);

        return mEquipmentListItemViewImpl;

    }

    /**
     * Method gets the Equipment registration screen view for EquipmentRegistration activity
     *
     * @param parent
     * @param session
     * @param contentResolver
     * @param onFinishActivityListener
     * @param sharedElementTransitionListener
     * @param equipmentDao
     * @param activity
     * @param listener
     * @param settingsListener
     * @return
     */
    public EquipmentRegistrationView getEquipmentRegistrationView(@Nullable ViewGroup parent,
                                                                  SessionManager session,
                                                                  ContentResolver contentResolver,
                                                                  OnFinishActivityListener onFinishActivityListener,
                                                                  OnSetupSharedElementTransitionListener sharedElementTransitionListener,
                                                                  EquipmentDao equipmentDao, FragmentActivity activity,
                                                                  PickerOptionListener listener, OnOpenSettingsListener settingsListener) {

        mEquipmentRegistrationViewImpl
                = new EquipmentRegistrationViewImpl(mLayoutInflater,
                                                    parent, session,
                                                    contentResolver,
                                                    onFinishActivityListener,
                                                    sharedElementTransitionListener,
                                                    equipmentDao, activity,
                                                    listener, settingsListener);

        return mEquipmentRegistrationViewImpl;

    }

    /**
     * Method gets the Toolbar view for the activities
     *
     * @param parent
     * @return
     */
    public ToolbarView getToolbarView(@Nullable ViewGroup parent) {
        return new ToolbarView(mLayoutInflater, parent);
    }

}
