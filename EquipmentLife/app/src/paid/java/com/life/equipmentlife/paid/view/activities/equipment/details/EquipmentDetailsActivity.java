package com.life.equipmentlife.paid.view.activities.equipment.details;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.life.equipmentlife.R;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.common.listener.EquipmentDataChangeSerialListener;
import com.life.equipmentlife.common.listener.OnEnterEquipmentEditListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.equipment.details.detailsview.EquipmentDetailsView;
import com.life.equipmentlife.paid.view.activities.equipment.edit.EquipmentDetailsEditActivity;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;

import static com.life.equipmentlife.paid.view.activities.equipment.details.detailsview.EquipmentDetailsViewImpl.HAS_EDITED_EQUIPMENT;

public class EquipmentDetailsActivity extends BaseActivity implements EquipmentDataChangeSerialListener,
        OnEnterEquipmentEditListener {
    private static final String TAG = EquipmentDetailsActivity.class.getSimpleName();

    private int RC_DETAIL = 100;

    private SessionManager session;

    private boolean hasEditedInfo;

    private Equipment mEquipment;

    private EquipmentDao equipmentDao;

    private ProfileDao profileDao;

    private EquipmentDetailsView mDetailsViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = getCompositionRoot().getSessionManager();

        equipmentDao = getCompositionRoot().getEquipmentDao();

        profileDao = getCompositionRoot().getProfileDao();

        Bundle bundle = getIntent().getExtras();

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                Log.i(TAG,"onCreate() inside method - inside second if");

                mEquipment = (Equipment) bundle.getSerializable(Intent.EXTRA_TEXT);

            }

        }

        mDetailsViewMvc =
                getCompositionRoot().getViewFactory()
                                    .getEquipmentDetailsView(null, profileDao,
                                            this, session);

        setContentView(mDetailsViewMvc.getRootView());

    }

    /**
     * Binds the equipment object to the screen class
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onResume() inside method - mEquipment: " + mEquipment);
        hasEditedInfo = session.getEditedEquipmentInfo();

        if (hasEditedInfo) {

            mDetailsViewMvc.showProgressIndication();

            equipmentDao.fetchEquipmentBySerial(mEquipment.getSerialNumber(), mEquipment.getId(), this);

            session.setEditedEquipmentInfo(false);

        }

    }

    /**
     * Method that receives the equipment from Firebase to be shown back in the
     * details screen when the user updates it
     *
     * @param equipment
     */
    @Override
    public void onDataSerialLoaded(Equipment equipment) {
        Log.i(TAG,"onDataSerialLoaded() inside method - equipment: " + equipment);
        mDetailsViewMvc.bindEquipment(equipment);
        mDetailsViewMvc.hideProgressIndication();
    }

    /**
     * Verifies if the user updated the info so that can be fetched again to show the info
     * updated on the details screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        // if the equipment info has not been edited then enter IF
        if (!hasEditedInfo) {
            mDetailsViewMvc.bindEquipment(mEquipment);
        }

    }

    /**
     * Method takes to the Equipment edit screen for info updating
     *
     * @param equipment
     * @param btnEquipmentDetailsEdit
     */
    @Override
    public void goToEquipmentEdit(Equipment equipment, ImageButton btnEquipmentDetailsEdit) {

        Class destinationClass = EquipmentDetailsEditActivity.class;

        Intent intentToStartDetailsEditActivity = new Intent(this, destinationClass);
        intentToStartDetailsEditActivity.putExtra(Intent.EXTRA_TEXT, equipment);

        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this, btnEquipmentDetailsEdit,
                                                  getString(R.string.transition_enter_registration));

        startActivityForResult(intentToStartDetailsEditActivity, RC_DETAIL, options.toBundle());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
