package com.life.equipmentlife.paid.view.activities.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.life.equipmentlife.application.FirebaseHandler;
import com.life.equipmentlife.common.fragmentframehelper.FragmentFrameWrapper;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerHelper;

public class BaseInitialActivity extends AppCompatActivity implements FragmentFrameWrapper, NavDrawerHelper {

    private ControllerCompositionRoot mControllerCompositionRoot;

    private SessionManager session;

    private DatabaseRef databaseRef;

    public ControllerCompositionRoot getCompositionRoot() {


        databaseRef = new DatabaseRef();
        session = new SessionManager(getApplicationContext());

        if (mControllerCompositionRoot == null) {

            mControllerCompositionRoot =
                    new ControllerCompositionRoot(
                            ((FirebaseHandler)getApplication()).getCompositionCep(),
                            databaseRef, session, this);

        }

        return mControllerCompositionRoot;

    }

    @Override
    public FrameLayout getFragmentFrame() {
        return null;
    }

    @Override
    public void openDrawer() {

    }

    @Override
    public void closeDrawer() {

    }

    @Override
    public boolean isDrawerOpen() {
        return false;
    }
}
