package com.life.equipmentlife.paid.view.activities.profile.details.detailsview;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnEnterProfileEditListener;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.life.equipmentlife.model.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static com.life.equipmentlife.model.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class ProfileDetailsViewImpl extends BaseObservableViewMvc implements ProfileDetailsView {

    private static final String TAG = ProfileDetailsViewImpl.class.getSimpleName();

    @BindView(R.id.tv_owner_name_text)
    protected TextView textViewProfileName;

    @BindView(R.id.et_profile_cpf)
    protected TextView textViewProfileCpf;

    @BindView(R.id.et_profile_phone)
    protected TextView textViewProfileTelephone;

    @BindView(R.id.tv_owner_email_text)
    protected TextView textViewProfileEmail;

    @BindView(R.id.tv_owner_cep_text)
    protected TextView textViewProfileCEP;

    @BindView(R.id.tv_owner_address_text)
    protected TextView textViewProfileAddress;

    @BindView(R.id.tv_owner_number_text)
    protected TextView textViewProfileAddressNumber;

    @BindView(R.id.tv_owner_city_text)
    protected TextView textViewProfileCity;

    @BindView(R.id.tv_owner_state_text)
    protected TextView textViewProfileState;

    @BindView(R.id.tv_owner_uf_text)
    protected TextView textViewProfileUF;

    @BindView(R.id.image_btn_profile_edit)
    protected ImageButton imageButtonProfileDetailsEdit;

    @BindView(R.id.img_profile_details)
    protected ImageView imgProfileDetails;

    private OnEnterProfileEditListener mOnEnterProfileEditListener;

    private Profile mProfile;

    // Flag to check if login was from Google
    private boolean isLoginFromGoogle;

    // Flag to check if login was from facebook
    private boolean isLoginFromFacebook;

    public ProfileDetailsViewImpl(LayoutInflater inflater, ViewGroup parent,
                                  SessionManager session,
                                  OnEnterProfileEditListener onEnterProfileEditListener) {

        setRootView(inflater.inflate(R.layout.activity_profile_details, parent, false));

        ButterKnife.bind(this, getRootView());

        mOnEnterProfileEditListener = onEnterProfileEditListener;

        // Check if the login was from Google
        isLoginFromGoogle = session.loginFromGoogle();

        // Check if the login was from Facebook
        isLoginFromFacebook = session.loginFromFacebook();

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonProfileDetailsEdit.setOnClickListener(view -> onEditButtonClicked());

    }

    @Override
    public void bindProfile(Profile profile) {

        mProfile = profile;

        setDetailsFields(profile);

    }

    private void setDetailsFields(Profile profile) {
        Log.i(TAG,"setDetailsFields() inside method - profile: " + profile);

        textViewProfileName.setText(profile.getName());
        textViewProfileCpf.setText(profile.getCpf());
        textViewProfileTelephone.setText(profile.getTelephone());
        textViewProfileEmail.setText(profile.getEmail());
        textViewProfileCEP.setText(profile.getCep());
        textViewProfileAddress.setText(profile.getAddress());
        textViewProfileAddressNumber.setText(profile.getAddressNumber());
        textViewProfileCity.setText(profile.getCity());
        textViewProfileState.setText(profile.getState());
        textViewProfileUF.setText(profile.getUf());

        Uri imageUrl;

        if (profile.getPicture() != null) {
            Log.i(TAG,"setDetailsFields() inside method - if (profile.getPicture() != null): " + profile.getPicture());
            Log.i(TAG,"setDetailsFields() inside method - if (isLoginFromGoogle || isLoginFromFacebook): " + isLoginFromGoogle);
            Log.i(TAG,"setDetailsFields() inside method - if (isLoginFromGoogle || isLoginFromFacebook): " + isLoginFromFacebook);

            if ((!profile.getPicture().isEmpty()) || (profile.getPicture() != "")) {

                if (isLoginFromGoogle || isLoginFromFacebook) {

                    Glide.with(getContext().getApplicationContext())
                            .load(profile.getPicture())
                            .into(imgProfileDetails);

                } else {

                    imageUrl = Utils.buildImageUrl(profile.getPicture());

                    if (imageUrl != null) {
                        Log.i(TAG,"setDetailsFields() inside method - if(imageUrl != null)");

                        Glide.with(getContext().getApplicationContext())
                                .load(imageUrl)
                                .into(imgProfileDetails);

                    } else {
                        Log.i(TAG,"setDetailsFields() inside method - else");

                        setDefaultImage();

                    }

                }

            } else {
                Log.i(TAG,"setDetailsFields() inside method - else");

                setDefaultImage();

            }


        } else {
            Log.i(TAG,"setDetailsFields() inside method - else");

            setDefaultImage();

        }

    }

    private void setDefaultImage() {
        Log.i(TAG,"setDefaultImage() inside method");

        imgProfileDetails.setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(),
                R.drawable.baseline_account_circle_black_48));

    }

    private void onEditButtonClicked() {
        mOnEnterProfileEditListener.goToProfileEdit(mProfile, imageButtonProfileDetailsEdit);
    }

}
