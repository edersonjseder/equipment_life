package com.life.equipmentlife.model.daoimpl;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.util.Log;

import com.life.equipmentlife.common.listener.AddressDataChangeCepListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeEmailListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.CepSearchViewModel;
import com.life.equipmentlife.viewmodel.CepSearchViewModelFactory;
import com.life.equipmentlife.viewmodel.ProfileSearchEmailViewModel;
import com.life.equipmentlife.viewmodel.ProfileSearchEmailViewModelFactory;
import com.life.equipmentlife.viewmodel.ProfileSearchUidKeyViewModel;
import com.life.equipmentlife.viewmodel.ProfileSearchUidKeyViewModelFactory;

import static com.life.equipmentlife.common.constants.Constants.PROFILES;

public class AddressDataDaoImpl implements AddressDataDao {

    private static final String TAG = AddressDataDaoImpl.class.getSimpleName();

    private AddressData mAddressData;

    private AddressDataChangeCepListener mCepListener;

    private ControllerCompositionRoot mCompositionRoot;

    private CepSearchViewModel mCepSearchViewModel;
    private CepSearchViewModelFactory viewModelCepFactory;

    /**
     * Constructor with the viewmodel for searching
     *
     * @param application
     * @param compositionRoot
     */
    public AddressDataDaoImpl(Application application,
                              ControllerCompositionRoot compositionRoot) {

        mCompositionRoot = compositionRoot;

        /** Search Address data by CEP **/
        viewModelCepFactory =
                new CepSearchViewModelFactory(application, compositionRoot);

        mCepSearchViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelCepFactory)
                        .get(CepSearchViewModel.class);

    }

    /**
     * Fetch Address data by cep from API
     *
     * @param cep the cep which will be used as parameter to the search
     * @param dataChangeCepListener
     * @return
     */
    @Override
    public AddressData fetchAddressDataByCep(String cep, AddressDataChangeCepListener dataChangeCepListener) {

        mCepListener = dataChangeCepListener;

        mCepSearchViewModel
                .getAddressData(cep).observe(mCompositionRoot.getActivity(), addressData -> {

            Log.i(TAG, "observe() inside method - addressData: " + addressData);

            mAddressData = addressData;
            mCepListener.onDataCepLoaded(addressData);

        });

        return mAddressData;

    }

}
