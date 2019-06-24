package com.life.equipmentlife.paid.view.activities.profile.details;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnEnterProfileEditListener;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.profile.details.detailsview.ProfileDetailsView;
import com.life.equipmentlife.paid.view.activities.profile.edit.ProfileDetailsEditActivity;

import static com.life.equipmentlife.common.constants.Constants.PROFILE;

public class ProfileDetailsActivity extends BaseActivity implements OnEnterProfileEditListener {

    private static final String TAG = ProfileDetailsActivity.class.getSimpleName();

    private int RC_DETAIL = 100;

    private boolean hasEditedInfo;

    private SessionManager session;

    private Profile profileInfo;

    private ProfileDetailsView mDetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = getCompositionRoot().getSessionManager();

        mDetailsView = getCompositionRoot().getViewFactory()
                                   .getProfileDetailsView(null, session, this);

        setContentView(mDetailsView.getRootView());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart() inside method - profileInfo: " + profileInfo);
        hasEditedInfo = session.getEditedProfileInfo();
        Log.i(TAG,"onStart() inside method - hasEditedInfo: " + hasEditedInfo);

        /**
         * Checks if the profile object was edited - if it was then enter the first condition and
         * gets the profile objects from session
         * if it doesn't get it from the bundle
         */
        if (hasEditedInfo) {
            Log.i(TAG,"onStart() inside IF");

            setProfileInfo(session.getProfileOnPrefs());

        } else {
            Log.i(TAG,"onStart() inside ELSE");

            setProfileInfo(getProfileFromBundle());

        }

    }

    /**
     * method called to bind the profile object from this class to the
     * ProfileDetailsViewImpl class to be used there by screen components
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume() inside method");
        mDetailsView.bindProfile(profileInfo);

    }

    /**
     * Gets the profile object received from bundle when this activity is called from
     * equipment list activity
     *
     * @return the profile onject got from Bundle object
     */
    private Profile getProfileFromBundle() {
        Log.i(TAG,"getProfileFromBundle() inside method");

        Bundle bundle = getIntent().getExtras();
        Profile profile = null;

        if (bundle != null) {
            Log.i(TAG,"getProfileFromBundle() inside IF");

            profile = (Profile) bundle.getSerializable(PROFILE);

        }

        return profile;

    }

    /**
     * Method that takes to the Profile edit screen
     *
     * @param profile the profile object as parameter to be edited
     * @param btnProfileDetailsEdit button to be used for animation effects
     */
    @Override
    public void goToProfileEdit(Profile profile, ImageButton btnProfileDetailsEdit) {

        Context context = this;
        Class destinationClass = ProfileDetailsEditActivity.class;

        Intent intentToStartDetailsEditActivity = new Intent(context, destinationClass);

        Bundle bundle = new Bundle();

        bundle.putSerializable(Intent.EXTRA_TEXT, profileInfo);

        intentToStartDetailsEditActivity.putExtras(bundle);

        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this, btnProfileDetailsEdit, getString(R.string.transition_enter_registration));

        startActivityForResult(intentToStartDetailsEditActivity, RC_DETAIL, options.toBundle());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setProfileInfo(Profile profileInfo) {
        this.profileInfo = profileInfo;
    }

}
