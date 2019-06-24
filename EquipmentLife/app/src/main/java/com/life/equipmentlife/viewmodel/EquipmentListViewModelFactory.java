package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class EquipmentListViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private ControllerCompositionRoot mControllerCompositionRoot;


    public EquipmentListViewModelFactory(Application application, ControllerCompositionRoot controllerCompositionRoot) {

        mApplication = application;

        mControllerCompositionRoot = controllerCompositionRoot;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EquipmentListViewModel(mApplication, mControllerCompositionRoot);
    }
}
