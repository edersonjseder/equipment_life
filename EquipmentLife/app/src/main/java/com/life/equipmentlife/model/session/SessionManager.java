package com.life.equipmentlife.model.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Profile;

import java.util.HashMap;

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    public SharedPreferences pref;

    // Editor for Shared preferences
    public SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    private static final String HAS_EDITED = "hasEditedInfo";

    private static final String HAS_EDITED_EQUIPMENT = "hasEditedEquipmentInfo";

    // Sharedpref file name
    private static final String PREF_NAME = "EquipmentLife";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "is_logged_in";

    public static final String HAS_LOGGED_IN_FIRST_TIME_ALREADY = "has_logged_in_first_time_already";

    // Owner name
    public static final String KEY_ID = "profile_id";

    // Owner name
    public static final String KEY_NAME = "profile_name";

    // Owner family name
    public static final String AMOUNT_INSERTED_EQUIPMENT = "equipment_amount";

    // Email
    public static final String KEY_EMAIL = "profile_email";

    // User password
    public static final String KEY_PASSWORD = "profile_password";

    public static final String UID_KEY = "uidKey";

    public static final String FROM_GOOGLE_SIGNUP = "signUpFromGoogle";

    public static final String FROM_FACEBOOK_SIGNUP = "signUpFromFacebook";

    public static final String _ID = "id";

    public static final String _NAME = "name";

    public static final String _CPF = "cpf";

    public static final String _TELEPHONE = "telephone";

    public static final String _EMAIL = "email";

    public static final String _CEP = "cep";

    public static final String _ADDRESS = "address";

    public static final String _ADDRESS_NUMBER = "addressNumber";

    public static final String _CITY = "city";

    public static final String _STATE = "state";

    public static final String _UF = "uf";

    public static final String _PASSWORD = "password";

    public static final String _PICTURE_URL = "picture";

    private static final String IS_FIRST_TIME = "IsFirstTime";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void countEquipmentAmount(int amount) {

        // Storing email in pref
        editor.putInt(AMOUNT_INSERTED_EQUIPMENT, amount);

        // commit changes
        editor.commit();

    }

    public String getUidKey() {

        return pref.getString(UID_KEY, "");

    }

    public void setProfileOnPrefs(Profile profile){
        Log.i(TAG, "setProfileOnPrefs() inside method - profile.getPicture(): " + profile.getPicture());

        // Storing profile id
        editor.putString(_ID, profile.getId());

        // commit changes
        editor.commit();

        // Storing profile name
        editor.putString(_NAME, profile.getName());

        // Storing profile cpf
        editor.putString(_CPF, profile.getCpf());

        // Storing profile telephone
        editor.putString(_TELEPHONE, profile.getTelephone());

        // commit changes
        editor.commit();

        // Storing profile email
        editor.putString(_EMAIL, profile.getEmail());

        // Storing profile CEP
        editor.putString(_CEP, profile.getCep());

        // Storing profile address
        editor.putString(_ADDRESS, profile.getAddress());

        // Storing profile address number
        editor.putString(_ADDRESS_NUMBER, profile.getAddressNumber());

        // Storing profile city
        editor.putString(_CITY, profile.getCity());

        // commit changes
        editor.commit();

        // Storing profile state
        editor.putString(_STATE, profile.getState());

        // Storing profile UF
        editor.putString(_UF, profile.getUf());

        // Storing profile password
        editor.putString(_PASSWORD, profile.getPassword());

        // Storing profile picture
        editor.putString(_PICTURE_URL, profile.getPicture());

        // commit changes
        editor.commit();

    }

    public void setEditedProfileInfo(boolean isEdited) {

        // Storing edited info boolean
        editor.putBoolean(HAS_EDITED, isEdited);

        // commit changes
        editor.commit();

    }

    public boolean getEditedProfileInfo() {

        return pref.getBoolean(HAS_EDITED, false);

    }

    public void setEditedEquipmentInfo(boolean isEdited) {

        // Storing edited info boolean
        editor.putBoolean(HAS_EDITED_EQUIPMENT, isEdited);

        // commit changes
        editor.commit();

    }

    public boolean getEditedEquipmentInfo() {

        return pref.getBoolean(HAS_EDITED_EQUIPMENT, false);

    }

    public Profile getProfileOnPrefs() {
        Log.i(TAG, "getProfileOnPrefs() inside method");

        Profile profile = new Profile();

        String _id = pref.getString(_ID, "");
        String _name = pref.getString(_NAME, "");
        String _cpf = pref.getString(_CPF, "");
        String _telephone = pref.getString(_TELEPHONE, "");
        String _email = pref.getString(_EMAIL, "");
        String _cep = pref.getString(_CEP, "");
        String _address = pref.getString(_ADDRESS, "");
        String _addressNumber = pref.getString(_ADDRESS_NUMBER, "");
        String _city = pref.getString(_CITY, "");
        String _state = pref.getString(_STATE, "");
        String _uf = pref.getString(_UF, "");
        String _password = pref.getString(_PASSWORD, "");
        String _picture = pref.getString(_PICTURE_URL, "");

        profile.setId(_id);
        profile.setName(_name);
        profile.setCpf(_cpf);
        profile.setTelephone(_telephone);
        profile.setEmail(_email);
        profile.setCep(_cep);
        profile.setAddress(_address);
        profile.setAddressNumber(_addressNumber);
        profile.setCity(_city);
        profile.setState(_state);
        profile.setUf(_uf);
        profile.setPassword(_password);
        profile.setPicture(_picture);

        Log.i(TAG, "getProfileOnPrefs() inside method - profile: " + profile);
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getId(): " + profile.getId());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getName(): " + profile.getName());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getCpf(): " + profile.getCpf());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getTelephone(): " + profile.getTelephone());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getEmail(): " + profile.getEmail());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getAddress(): " + profile.getAddress());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getCity(): " + profile.getCity());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getState(): " + profile.getState());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getPassword(): " + profile.getPassword());
        Log.i(TAG, "getProfileOnPrefs() inside method - profile.getPicture(): " + profile.getPicture());

        return profile;

    }

    public boolean loginFromGoogle() {

        return pref.getBoolean(FROM_GOOGLE_SIGNUP, false);

    }

    public boolean loginFromFacebook() {

        return pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

    }

    public boolean verifyIfUserHasLoggedInAlreadyFirstTime() {

        //if SharedPreferences contains flag to check if user has logged in already
        if(pref.contains(HAS_LOGGED_IN_FIRST_TIME_ALREADY)){
            return true;
        }

        return false;
    }

    /**
     * Clear session details
     * */
    public void logoutUserGoogle(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_GOOGLE_SIGNUP, true);
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.putBoolean(IS_LOGIN, true);

        // commit changes
        editor.commit();

    }

    /**
     * Clear session details
     * */
    public void logoutUserFacebook(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.putBoolean(IS_LOGIN, true);

        // commit changes
        editor.commit();

    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.putBoolean(IS_LOGIN, true);

        // commit changes
        editor.commit();

    }

    public void setFirstTimeGoogleLogin() {

        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_GOOGLE_SIGNUP, true);
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.commit();

    }

    public void setFirstTimeFacebookLogin() {

        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
        editor.putBoolean(IS_FIRST_TIME, true);
        editor.commit();

    }

    /**
     * Method that sets first time launched flag so that the SliderScreen
     * are not shown to the user again every time he opens the app
     *
     * @param isFirstTime
     */
    public void setFirstTimeLaunch(boolean isFirstTime) {

        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();

    }

    /**
     * Method that verify if the app is first launched on the phone
     *
     * @return
     */
    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(IS_FIRST_TIME, true);

    }

    /**
     * Quick check for login
     *
     **/
    public boolean isLoggedOut(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLoggedOut(boolean offline) {

        editor.putBoolean(IS_LOGIN, offline);
        editor.commit();

    }
}
