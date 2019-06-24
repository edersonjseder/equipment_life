package com.life.equipmentlife.paid.view.activities.equipment.details.detailsview;

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
import com.life.equipmentlife.common.listener.OnEnterEquipmentEditListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentDetailsViewImpl extends BaseObservableViewMvc implements EquipmentDetailsView,
        ProfileDataChangeUidKeyListener {

    private static final String TAG = EquipmentDetailsViewImpl.class.getSimpleName();

    public static final String HAS_EDITED_EQUIPMENT = "hasEditedInfo";

    @BindView(R.id.imageView_equipment_details_picture)
    protected ImageView imageViewEquipmentDetailsPicture;

    @BindView(R.id.tv_equipment_brand)
    protected TextView textViewEquipmentBrandDetails;

    @BindView(R.id.tv_equipment_model)
    protected TextView textViewEquipmentModelDetails;

    @BindView(R.id.tv_equipment_serial_number)
    protected TextView textViewEquipmentSerialNumberDetails;

    @BindView(R.id.tv_equipment_registration_date)
    protected TextView textViewEquipmentRegisteredDateDetails;

    @BindView(R.id.tv_equipment_owner_details)
    protected TextView textViewEquipmentOwnerDetails;

    @BindView(R.id.tv_equipment_owner_label)
    protected TextView textViewEquipmentOwnerLabel;

    @BindView(R.id.view_equipment_owner_divider)
    protected View viewEquipmentOwnerDivider;

    @BindView(R.id.tv_equipment_status)
    protected TextView textViewEquipmentStatusDetails;

    @BindView(R.id.tv_equipment_short_description)
    protected TextView textViewEquipmentShortDescriptionDetails;

    @BindView(R.id.layout_equipment_status_details)
    protected LinearLayout layoutEquipmentStatusDetails;

    @BindView(R.id.image_view_equipment_status_icon)
    protected ImageView imageViewEquipmentStatusIcon;

    @BindView(R.id.image_btn_equipment_details_edit)
    protected ImageButton imageButtonEquipmentDetailsEdit;

    @BindView(R.id.ll_profile_name_sold_to_equipment_details)
    protected LinearLayout linearLayoutProfileNameSoldToEquipmentDetails;

    @BindView(R.id.ll_overbox_equipment_details_loading)
    protected LinearLayout llOverboxEquipmentDetailsLoading;

    @BindView(R.id.pb_equipment_detail_loading)
    protected ProgressBar pbEquipmentDetailLoading;

    @BindView(R.id.tv_equipment_edit_sold_to_owner_name)
    protected TextView textViewEquipmentEditSoldToOwnerName;

    private Equipment mEquipment;

    private ProfileDao mProfileDao;

    private SessionManager mSession;

    private String sessionUidKey;

    private OnEnterEquipmentEditListener mOnEnterEquipmentEditListener;


    public EquipmentDetailsViewImpl(LayoutInflater inflater, ViewGroup parent, ProfileDao profileDao,
                                    OnEnterEquipmentEditListener onEnterEquipmentEditListener,
                                    SessionManager sessionManager) {

        setRootView(inflater.inflate(R.layout.activity_equipment_details, parent, false));

        ButterKnife.bind(this, getRootView());

        mSession = sessionManager;

        mProfileDao = profileDao;

        mOnEnterEquipmentEditListener = onEnterEquipmentEditListener;

        sessionUidKey = mSession.getProfileOnPrefs().getId();

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonEquipmentDetailsEdit.setOnClickListener(view -> onEditButtonClicked());

    }

    /**
     * Method takes to the edit screen
     */
    private void onEditButtonClicked() {
        mOnEnterEquipmentEditListener.goToEquipmentEdit(mEquipment, imageButtonEquipmentDetailsEdit);
    }

    /**
     * Equipment object got from activity to be shown on screen
     *
     * @param equipment
     */
    @Override
    public void bindEquipment(Equipment equipment) {
        Log.i(TAG,"bindEquipment() inside method");

        mEquipment = equipment;

        // Verifies if the equipment has the sold status and write the new owner on screen
        if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_sold))) {

            showProgressIndication();

            Log.i(TAG,"bindEquipment() inside method - inside if");

            getProfileEquipmentWasSoldTo(equipment.getProfileCurrentOwnerId());

        } else {

            Log.i(TAG,"bindEquipment() inside method - inside else");

            setDetailsFields(equipment);

        }

    }

    /**
     * Sets the equipment details info on screen fields
     *
     * @param equipment
     */
    private void setDetailsFields(Equipment equipment) {
        Log.i(TAG, "setDetailsFields() inside method");

        textViewEquipmentBrandDetails.setText(equipment.getBrand());
        textViewEquipmentModelDetails.setText(equipment.getModel());
        textViewEquipmentSerialNumberDetails.setText(equipment.getSerialNumber());
        textViewEquipmentRegisteredDateDetails.setText(equipment.getRegistrationDate());
        textViewEquipmentShortDescriptionDetails.setText(equipment.getShortDescription());

        verifyEquipmentStatus(equipment);

        Uri imageUrl = null;

        if ((equipment.getPicture() != null)) {
            Log.i(TAG,"setDetailsFields() inside method - if(equipment.getPicture() != null)");

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")) {
                Log.i(TAG,"setDetailsFields() inside method - if((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != \"\"))");
                imageUrl = Utils.buildImageUrl(equipment.getPicture());

            }

        }

        if ((imageUrl != null)) {
            Log.i(TAG,"setDetailsFields() inside method - if(imageUrl != null)");

            Glide.with(getContext().getApplicationContext())
                    .load(imageUrl)
                    .into(imageViewEquipmentDetailsPicture);

        } else {
            Log.i(TAG,"setDetailsFields() inside method - else");

            imageViewEquipmentDetailsPicture.setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.album_icon));

        }

    }

    private void verifyEquipmentStatus(Equipment equipment) {
        Log.i(TAG,"verifyEquipmentStatus() inside method - status: " + equipment.getStatusCurrentOwner());

        if (equipment.getProfilePreviousOwnerId().equals(sessionUidKey) && (equipment.getProfileCurrentOwnerId().equals(sessionUidKey)) &&
                equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_owned))) {
            Log.i(TAG,"verifyEquipmentStatus() inside method - IF - equipment.getProfilePreviousOwnerId().equals(sessionUidKey): " + sessionUidKey);
            Log.i(TAG,"verifyEquipmentStatus() inside method - IF - equipment.getProfileCurrentOwnerId().equals(sessionUidKey): " + equipment.getProfileCurrentOwnerId());

            textViewEquipmentStatusDetails.setText(getContext().getResources().getString(R.string.string_equipment_owned));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_equipment_status_green));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_owned));
            textViewEquipmentOwnerDetails.setText(equipment.getCurrentOwner());

        } else if ((!equipment.getProfileCurrentOwnerId().equals(sessionUidKey)) &&
                equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_owned))) {

            imageButtonEquipmentDetailsEdit.setVisibility(View.GONE);
            textViewEquipmentStatusDetails.setText(getContext().getResources().getString(R.string.string_equipment_owned));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_equipment_status_green));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_owned));
            textViewEquipmentOwnerDetails.setText(equipment.getCurrentOwner());

        }

        if (equipment.getStatusPreviousOwner().equals(getContext().getResources().getString(R.string.equipment_sold))) {

            if(equipment.getProfileCurrentOwnerId().equals(sessionUidKey)) {

                textViewEquipmentStatusDetails.setText(getContext().getResources().getString(R.string.string_equipment_owned));
                textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
                layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_equipment_status_green));
                imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_owned));
                textViewEquipmentOwnerDetails.setText(equipment.getCurrentOwner());

            } else {

                imageButtonEquipmentDetailsEdit.setVisibility(View.GONE);
                textViewEquipmentStatusDetails.setText(getContext().getResources().getString(R.string.string_equipment_sold));
                textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.theme_primary));
                layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_equipment_status_blue));
                imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_sold));
                textViewEquipmentOwnerDetails.setVisibility(View.GONE);
                textViewEquipmentOwnerLabel.setVisibility(View.GONE);
                viewEquipmentOwnerDivider.setVisibility(View.GONE);
                linearLayoutProfileNameSoldToEquipmentDetails.setVisibility(View.VISIBLE);

            }

        } else if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_stolen))) {

            if (!(equipment.getProfileCurrentOwnerId().equals(sessionUidKey))) {
                imageButtonEquipmentDetailsEdit.setVisibility(View.GONE);
            }

            textViewEquipmentStatusDetails.setText(getContext().getResources().getString(R.string.string_equipment_stolen));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_red));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_equipment_status_red));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_stolen));
            textViewEquipmentOwnerDetails.setText(equipment.getCurrentOwner());

        }

    }

    /**
     * Search the new owner on Firebase to write his name in the owner TextView
     * if the equipment has the sold status
     *
     * @param uid
     */
    private void getProfileEquipmentWasSoldTo(String uid) {
        Log.i(TAG,"getProfileEquipmentWasSoldTo() inside method - uid: " + uid);
        mProfileDao.fetchProfileById(uid, this);
    }

    /**
     * Profile object got from Firebase to be written on textview
     *
     * @param profile
     */
    @Override
    public void onDataUidKeyLoaded(Profile profile) {
        Log.i(TAG,"onDataUidKeyLoaded() inside method - profile: " + profile);

        if (profile != null) {

            textViewEquipmentEditSoldToOwnerName.setText(profile.getName());

        } else {

            textViewEquipmentEditSoldToOwnerName.setText("");

        }

        setDetailsFields(mEquipment);

        hideProgressIndication();

    }

    /**
     * Shows progressbar
     */
    @Override
    public void showProgressIndication() {
        llOverboxEquipmentDetailsLoading.setVisibility(View.VISIBLE);
        pbEquipmentDetailLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progressbar
     */
    @Override
    public void hideProgressIndication() {
        llOverboxEquipmentDetailsLoading.setVisibility(View.GONE);
        pbEquipmentDetailLoading.setVisibility(View.GONE);
    }

}
