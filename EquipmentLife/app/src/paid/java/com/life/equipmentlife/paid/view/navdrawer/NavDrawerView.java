package com.life.equipmentlife.paid.view.navdrawer;

import android.widget.FrameLayout;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Profile;

public interface NavDrawerView extends ObservableViewMvc<NavDrawerView.OnSelectDrawerMenuItem> {

    interface OnSelectDrawerMenuItem {

        void goToProfileDetails(Profile profile);

        void logoutProfile();

    }

    FrameLayout getFragmentFrame();
    boolean isDrawerOpen();
    void openDrawer();
    void closeDrawer();

}
