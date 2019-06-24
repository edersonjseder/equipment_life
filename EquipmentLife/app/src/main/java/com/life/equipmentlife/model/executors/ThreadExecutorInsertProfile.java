package com.life.equipmentlife.model.executors;

import com.life.equipmentlife.model.daoimpl.ProfileDaoImpl;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class ThreadExecutorInsertProfile implements Runnable {

    private ProfileDaoImpl mProfileDaoImpl;

    private Profile mProfile;

    private ScreensNavigator mScreensNavigator;

    private SessionManager mSession;

    public ThreadExecutorInsertProfile(ProfileDaoImpl implInsert, ScreensNavigator screensNavigator, Profile profile, SessionManager session) {
        mProfileDaoImpl = implInsert;
        mProfile = profile;
        mSession = session;
        mScreensNavigator = screensNavigator;
    }

    public void insertDataThread() {

        AppExecutors.getsInstance().getDiskIO().execute(this);

    }

    @Override
    public void run() {

        mSession.setProfileOnPrefs(mProfile);

        mProfileDaoImpl.insertProfile(mProfile, mProfile.getId());

        mScreensNavigator.goToEquipmentListActivity(mProfile);

    }

}
