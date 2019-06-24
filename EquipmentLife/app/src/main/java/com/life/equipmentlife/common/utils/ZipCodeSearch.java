package com.life.equipmentlife.common.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.life.equipmentlife.common.listener.AddressDataChangeCepListener;
import com.life.equipmentlife.model.dao.AddressDataDao;

public class ZipCodeSearch implements TextWatcher {
    private static final String TAG = ZipCodeSearch.class.getSimpleName();

    private AddressDataDao mAddressDataDao;

    private AddressDataChangeCepListener changeCepListener;

    private ProgressBar mProgressBarProfileRegistrationUpdating;

    public ZipCodeSearch(AddressDataDao addressDataDao, AddressDataChangeCepListener cepListener,
                         ProgressBar progressBarProfileRegistrationUpdating) {
        mAddressDataDao = addressDataDao;
        changeCepListener = cepListener;
        mProgressBarProfileRegistrationUpdating = progressBarProfileRegistrationUpdating;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.i(TAG, "afterTextChanged() inside method - editable: " + editable.toString());

        String cep = editable.toString().replaceAll("-", "");

        Log.i(TAG, "afterTextChanged() inside method - cep: " + cep);

        if( cep.length() == 8 ){

            mAddressDataDao.fetchAddressDataByCep(cep, changeCepListener);

            mProgressBarProfileRegistrationUpdating.setVisibility(View.VISIBLE);

        }

    }

}
