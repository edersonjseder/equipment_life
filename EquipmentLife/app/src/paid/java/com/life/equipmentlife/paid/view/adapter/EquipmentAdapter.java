package com.life.equipmentlife.paid.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.life.equipmentlife.common.listener.OnEquipmentItemSelectedListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.equipment.list.listitemview.EquipmentListItemView;
import com.life.equipmentlife.paid.view.viewfactory.ViewMvcFactory;

import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private static final String TAG = EquipmentAdapter.class.getSimpleName();

    private List<Equipment> equipmentList;
    private ViewMvcFactory mViewMvcFactory;
    private SessionManager mSession;
    private OnEquipmentItemSelectedListener mOnEquipmentItemSelectedListener;

    public EquipmentAdapter(ViewMvcFactory viewMvcFactory,
                            OnEquipmentItemSelectedListener onEquipmentItemSelectedListener,
                            SessionManager session) {

        mViewMvcFactory = viewMvcFactory;
        mSession = session;
        mOnEquipmentItemSelectedListener = onEquipmentItemSelectedListener;

    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        EquipmentListItemView listItemView =
                mViewMvcFactory.getEquipmentListItemView(viewGroup,
                        mOnEquipmentItemSelectedListener, mSession);

        return new EquipmentViewHolder(listItemView);

    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder equipmentViewHolder, int position) {

        final Equipment equipment = equipmentList.get(position);

        equipmentViewHolder.mItemView.bindEquipment(equipment);

    }

    /**
     * When data changes, this method updates the list of equipments
     * and notifies the adapter to use the new values on it
     */
    public void bindEquipments(List<Equipment> equipments) {
        equipmentList = equipments;
        notifyDataSetChanged();
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    @Override
    public int getItemCount() {
        return (equipmentList != null) ? equipmentList.size() : 0;
    }

    /** Adapter ViewHolder Class **/
    public class EquipmentViewHolder extends RecyclerView.ViewHolder {

        private EquipmentListItemView mItemView;

        public EquipmentViewHolder(EquipmentListItemView itemView) {
            super(itemView.getRootView());

            mItemView = itemView;

        }

    }

}
