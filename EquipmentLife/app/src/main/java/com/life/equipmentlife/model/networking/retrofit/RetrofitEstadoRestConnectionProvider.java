package com.life.equipmentlife.model.networking.retrofit;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.life.equipmentlife.model.networking.api.CepApi;
import com.life.equipmentlife.model.networking.api.EstadoApi;
import com.life.equipmentlife.model.networking.interceptor.CacheInterceptor;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.life.equipmentlife.common.constants.Constants.URL_CEP;
import static com.life.equipmentlife.common.constants.Constants.URL_ESTADO;

public class RetrofitEstadoRestConnectionProvider {

    private EstadoApi mEstadoApi;
    private Context mContext;
    private Retrofit mRetrofit;
    private JodaModule mJodaModule;
    private CacheInterceptor mCacheInterceptor;

    public RetrofitEstadoRestConnectionProvider(Context context) {
        mContext = context;
        mJodaModule = new JodaModule();
    }

    public EstadoApi getEstadoApi() {

        if (mEstadoApi == null) {

            mEstadoApi = getRetrofit().create(EstadoApi.class);

        }

        return mEstadoApi;

    }

    private Retrofit getRetrofit() {

        if (mRetrofit == null) {

            mRetrofit = new Retrofit.Builder().baseUrl(URL_ESTADO)
                    .addConverterFactory(JacksonConverterFactory.create(getObjectMapper()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(getOkHttpClient()).build();

        }

        return mRetrofit;

    }

    private ObjectMapper getObjectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        return mapper.registerModule(mJodaModule);

    }

    private OkHttpClient getOkHttpClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder().cache(getCache())
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(getCacheInterceptor().provideCacheInterceptor())
                .addInterceptor(getCacheInterceptor().provideOfflineCacheInterceptor()).build();

        return httpClient;

    }

    private HttpLoggingInterceptor getLoggingInterceptor() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return logging;

    }

    private Cache getCache() {

        // Place for offline cache
        File httpCacheDirectory = new File(mContext.getCacheDir(), "responses");

        int cacheSize = 10 * 1024 * 1024; // 10 MiB

        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        return cache;

    }

    private CacheInterceptor getCacheInterceptor() {

        mCacheInterceptor = new CacheInterceptor(mContext);

        return mCacheInterceptor;

    }

}
