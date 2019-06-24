package com.life.equipmentlife.model.networking.retrofit;

import android.content.Context;
import android.support.annotation.UiThread;

@UiThread
public class RetrofitCompositionRoot {

    private RetrofitCepRestConnectionProvider cepConnectionProvider;
    private RetrofitEstadoRestConnectionProvider estadoConnectionProvider;
    private Context mContext;

    public RetrofitCompositionRoot(Context context) {

        mContext = context;

    }

    public RetrofitCepRestConnectionProvider getRetrofitCepRestConnectionProvider() {

        cepConnectionProvider = new RetrofitCepRestConnectionProvider(getContext());

        return cepConnectionProvider;

    }

    public RetrofitEstadoRestConnectionProvider getRetrofitEstadoRestConnectionProvider() {

        estadoConnectionProvider = new RetrofitEstadoRestConnectionProvider(getContext());

        return estadoConnectionProvider;

    }

    public Context getContext() {
        return mContext;
    }

}
