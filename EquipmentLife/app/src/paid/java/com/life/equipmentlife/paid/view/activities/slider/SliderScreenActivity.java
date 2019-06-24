package com.life.equipmentlife.paid.view.activities.slider;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.slider.slidescreenview.SliderScreenView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class SliderScreenActivity extends BaseActivity implements OnFinishActivityListener {

    private SessionManager manager;

    private SliderScreenView screenView;

    private ScreensNavigator mScreensNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        manager = getCompositionRoot().getSessionManager();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        screenView = getCompositionRoot()
                .getViewFactory()
                .getSliderScreenView(null, manager, mScreensNavigator, this, this);

        setContentView(screenView.getRootView());

    }

    @Override
    public void onFinishActivity() {
        finish();
    }

}
