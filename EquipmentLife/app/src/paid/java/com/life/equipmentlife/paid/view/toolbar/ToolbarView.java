package com.life.equipmentlife.paid.view.toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseViewMvc;

public class ToolbarView extends BaseViewMvc {

    public interface NavigateUpClickListener {
        void onNavigateUpClicked();
    }

    public interface HamburgerClickListener {
        void onHamburgerClicked();
    }

    private final ImageView mEquipmentTitle;
    private final ImageButton mBtnBack;
    private final ImageButton mBtnHamburger;

    private NavigateUpClickListener mNavigateUpClickListener;
    private HamburgerClickListener mHamburgerClickListener;

    public ToolbarView(LayoutInflater inflater, ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.equipment_toolbar_layout, parent, false));

        mEquipmentTitle = findViewById(R.id.img_equipment_title);

        mBtnHamburger = findViewById(R.id.btn_hamburger);

        mBtnHamburger.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mHamburgerClickListener.onHamburgerClicked();
            }

        });

        mBtnBack = findViewById(R.id.btn_back);

        mBtnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mNavigateUpClickListener.onNavigateUpClicked();
            }

        });

    }

    public void setImageTitle() {
        mEquipmentTitle.setImageDrawable(getContext().getDrawable(R.drawable.logo_equipment_cam));
    }

    public void enableHamburgerButtonAndListen(HamburgerClickListener hamburgerClickListener) {

        if (mNavigateUpClickListener != null) {
            throw new RuntimeException("hamburger and up shouldn't be shown together");
        }

        mHamburgerClickListener = hamburgerClickListener;

        mBtnHamburger.setVisibility(View.VISIBLE);

    }

    public void enableUpButtonAndListen(NavigateUpClickListener navigateUpClickListener) {

        if (mHamburgerClickListener != null) {
            throw new RuntimeException("hamburger and up shouldn't be shown together");
        }

        mNavigateUpClickListener = navigateUpClickListener;

        mBtnBack.setVisibility(View.VISIBLE);

    }

}
