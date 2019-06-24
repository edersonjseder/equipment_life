package com.life.equipmentlife.paid.view.navdrawer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.model.pojo.Profile;

public class NavDrawerViewImpl extends BaseObservableViewMvc<NavDrawerView.OnSelectDrawerMenuItem>
        implements NavDrawerView, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private NavigationView mNavigationView;

    private FrameLayout mFrameLayout;

    private TextView textViewEquipmentNavMenu;

    private Profile mProfile;

    public NavDrawerViewImpl(LayoutInflater inflater, @Nullable ViewGroup parent, Profile profileOwner) {

        setRootView(inflater.inflate(R.layout.activity_drawer_layout_equipment_list, parent, false));

        mProfile = profileOwner;

        textViewEquipmentNavMenu = findViewById(R.id.text_view_equipment_nav_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout_equipment_life);

        mFrameLayout = findViewById(R.id.frame_equipment_content);

        mNavigationView = findViewById(R.id.nav_view_equipment_life);

        setNameTitle();

        mNavigationView.setNavigationItemSelectedListener(this);

    }

    private void setNameTitle() {

        textViewEquipmentNavMenu.setText(mProfile.getEmail());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        mDrawerLayout.closeDrawers();

        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.action_profile) {

            for (OnSelectDrawerMenuItem drawerItem : getListeners()) {
                drawerItem.goToProfileDetails(mProfile);
            }

        } else if (id == R.id.action_profile_logout) {

            for (OnSelectDrawerMenuItem drawerItem : getListeners()) {
                drawerItem.logoutProfile();
            }

        }

        return false;

    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    @Override
    public FrameLayout getFragmentFrame() {
        return mFrameLayout;
    }

    @Override
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

}
