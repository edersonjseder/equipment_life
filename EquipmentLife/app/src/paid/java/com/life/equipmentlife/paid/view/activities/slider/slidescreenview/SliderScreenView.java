package com.life.equipmentlife.paid.view.activities.slider.slidescreenview;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.slider.pageadapter.EquipmentViewPagerAdapter;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class SliderScreenView extends BaseObservableViewMvc implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip;
    private Button btnNext;

    private TextView[] dots;
    private int[] layouts;

    private EquipmentViewPagerAdapter mViewPagerAdapter;

    private SessionManager mSession;

    private ScreensNavigator mScreensNavigator;

    private OnFinishActivityListener mListener;

    private FragmentActivity mBaseActivity;

    public SliderScreenView(LayoutInflater inflater,
                            ViewGroup parent,
                            SessionManager session,
                            ScreensNavigator screensNavigator,
                            OnFinishActivityListener listener,
                            FragmentActivity activity) {

        setRootView(inflater.inflate(R.layout.activity_slider_screen_main, parent, false));

        mBaseActivity = activity;

        mListener = listener;

        mScreensNavigator = screensNavigator;

        mSession = session;

        initComponents();

        if (!mSession.isFirstTimeLaunch()) {
            launchHomeScreen();
            mListener.onFinishActivity();
        }

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.equipment_slider_screen_first,
                R.layout.equipment_slider_screen_second,
                R.layout.equipment_slider_screen_third};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        mViewPagerAdapter = new EquipmentViewPagerAdapter(layouts, getContext());
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

    }

    private void initComponents() {

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = mBaseActivity.getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);

        }

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {

        addBottomDots(position);

        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == layouts.length - 1) {

            // last page. make button text to GOT IT
            btnNext.setText(getString(R.string.start));

            btnSkip.setVisibility(View.GONE);

        } else {

            // still pages are left
            btnNext.setText(getString(R.string.next));

            btnSkip.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * Adds dots to the botton of the screen
     *
     * @param currentPage
     */
    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];

        int[] colorsActive = getContext().getResources().getIntArray(R.array.array_pager_active);

        int[] colorsInactive = getContext().getResources().getIntArray(R.array.array_pager_inactive);

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(getContext());

            dots[i].setText(Html.fromHtml("•"));

            dots[i].setTextSize(35);

            dots[i].setTextColor(colorsInactive[currentPage]);

            dotsLayout.addView(dots[i]);

        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);

    }

    /**
     * Gets the pages on screen until the last one
     *
     * @param i
     * @return
     */
    private int getItem(int i) {

        return viewPager.getCurrentItem() + i;

    }

    /**
     * Method that takes to the StartScreen
     */
    private void launchHomeScreen() {

        mSession.setFirstTimeLaunch(false);

        mScreensNavigator.goToStartScreen();

        mListener.onFinishActivity();

    }

    /**
     * Method that takes to the next screen on pageview
     */
    private void onNext() {

        // checking for last page
        // if last page home screen will be launched
        int current = getItem(+1);

        if (current < layouts.length) {

            // move to next screen
            viewPager.setCurrentItem(current);

        } else {

            launchHomeScreen();

        }

    }

    /**
     * Method that responds to the user clicks on screen
     *
     * @param view the buttons skip and next available
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_skip:
                launchHomeScreen();
                break;

            case R.id.btn_next:
                onNext();
                break;

        }

    }

}
