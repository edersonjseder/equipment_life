package com.life.equipmentlife.model.daoimpl;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.util.Log;

import com.life.equipmentlife.common.listener.AddressDataChangeCepListener;
import com.life.equipmentlife.common.listener.EstadoChangeListener;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.pojo.Estado;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.CepSearchViewModel;
import com.life.equipmentlife.viewmodel.CepSearchViewModelFactory;
import com.life.equipmentlife.viewmodel.EstadosListViewModel;
import com.life.equipmentlife.viewmodel.EstadosListViewModelFactory;

import java.util.List;

public class EstadoDaoImpl implements EstadoDao {

    private static final String TAG = EstadoDaoImpl.class.getSimpleName();

    private List<Estado> mEstadoList;

    private EstadoChangeListener mEstadoListener;

    private ControllerCompositionRoot mCompositionRoot;

    private EstadosListViewModel mEstadosViewModel;
    private EstadosListViewModelFactory viewModelEstadosFactory;

    /**
     * Constructor with the viewmodel
     *
     * @param application
     * @param compositionRoot
     */
    public EstadoDaoImpl(Application application,
                         ControllerCompositionRoot compositionRoot) {

        mCompositionRoot = compositionRoot;

        /** Gets Estados list **/
        viewModelEstadosFactory =
                new EstadosListViewModelFactory(application, compositionRoot);

        mEstadosViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelEstadosFactory)
                        .get(EstadosListViewModel.class);

    }

    /**
     * Get estados list from API
     *
     * @param estadoListener
     * @return
     */
    @Override
    public List<Estado> getEstadoList(EstadoChangeListener estadoListener) {

        mEstadoListener = estadoListener;

        mEstadosViewModel
                .getEstadosListLiveData().observe(mCompositionRoot.getActivity(), estadosList -> {

            Log.i(TAG, "observe() inside method - estadosList: " + estadosList);

            mEstadoList = estadosList;
            mEstadoListener.onEstadoLoaded(estadosList);

        });

        return mEstadoList;

    }

}
