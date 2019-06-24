package com.life.equipmentlife.paid.view.activities.equipment.list.listitemview;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnEquipmentItemSelectedListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.model.session.SessionManager;

public class EquipmentListItemViewImpl extends BaseObservableViewMvc
        implements EquipmentListItemView, View.OnClickListener {

    private ImageView circleViewEquipmentPicture;

    private ImageView imageViewEquipmentListStatus;

    private TextView textViewEquipmentListStatus;

    private TextView textViewEquipmentBrand;

    private TextView textViewEquipmentOwner;

    private Equipment mEquipment;

    private String sessionUidKey;

    private OnEquipmentItemSelectedListener mOnEquipmentItemSelectedListener;

    public EquipmentListItemViewImpl(LayoutInflater inflater, @Nullable ViewGroup parent,
                                     OnEquipmentItemSelectedListener onEquipmentItemSelectedListener,
                                     SessionManager mSession) {

        setRootView(inflater.inflate(R.layout.equipment_list_item, parent, false));

        mOnEquipmentItemSelectedListener = onEquipmentItemSelectedListener;

        sessionUidKey = mSession.getProfileOnPrefs().getId();

        initViews();

        getRootView().setOnClickListener(this);

    }

    @Override
    public void bindEquipment(Equipment equipment) {

        mEquipment = equipment;

        textViewEquipmentBrand.setText(equipment.getBrand());
        textViewEquipmentOwner.setText(equipment.getCurrentOwner());

        if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_owned))) {
            imageViewEquipmentListStatus.setImageDrawable(getContext().getDrawable(R.drawable.ic_owned));
            textViewEquipmentListStatus.setText(getContext().getResources().getString(R.string.equipment_owned));

        } else if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_sold))) {

            if ((equipment.getProfileCurrentOwnerId().equals(sessionUidKey))) {

                imageViewEquipmentListStatus.setImageDrawable(getContext().getDrawable(R.drawable.ic_owned));
                textViewEquipmentListStatus.setText(getContext().getResources().getString(R.string.equipment_owned));

            } else {

                imageViewEquipmentListStatus.setImageDrawable(getContext().getDrawable(R.drawable.ic_sold));
                textViewEquipmentListStatus.setText(getContext().getResources().getString(R.string.equipment_sold));

            }

        } else if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_stolen))) {
            imageViewEquipmentListStatus.setImageDrawable(getContext().getDrawable(R.drawable.ic_stolen));
            textViewEquipmentListStatus.setText(getContext().getResources().getString(R.string.equipment_stolen));
        }

        Uri imageUrl = null;

        if ((equipment.getPicture() != null)) {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")) {

                imageUrl = Utils.buildImageUrl(equipment.getPicture());
            }

        }

        if ((imageUrl != null)) {

            Glide.with(getContext())
                    .load(imageUrl)
                    .into(circleViewEquipmentPicture);


        } else {

            circleViewEquipmentPicture.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.album_icon));

        }

    }

    private void initViews() {

        circleViewEquipmentPicture = findViewById(R.id.circleView_equipment_picture);

        imageViewEquipmentListStatus = findViewById(R.id.image_view_equipment_list_status);

        textViewEquipmentListStatus = findViewById(R.id.text_view_equipment_list_status);

        textViewEquipmentBrand = findViewById(R.id.tv_equipment_brand_list_info);

        textViewEquipmentOwner = findViewById(R.id.et_equipment_owner_list_item);

    }

    @Override
    public void onClick(View v) {
        mOnEquipmentItemSelectedListener.onEquipmentItemSelected(mEquipment);
    }

}
