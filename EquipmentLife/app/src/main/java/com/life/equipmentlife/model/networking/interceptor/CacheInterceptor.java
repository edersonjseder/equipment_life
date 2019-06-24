package com.life.equipmentlife.model.networking.interceptor;

import android.content.Context;

import com.life.equipmentlife.model.networking.internetdetector.ConnectionDetector;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to control cache with retrofit when the app is offline
 */
public class CacheInterceptor {

    private Interceptor mInterceptor;
    private Context mContext;

    public CacheInterceptor(Context context) {
        mContext = context;
    }

    // Interceptor for online mode
    public Interceptor provideCacheInterceptor() {

        mInterceptor = new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                String cacheControl = originalResponse.header("Cache-Control");

                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {

                    CacheControl cc = new CacheControl.Builder()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cc)
                            .build();

                    return chain.proceed(request);

                } else {

                    return originalResponse;

                }
            }
        };

        return mInterceptor;

    }

    // Interceptor for offline mode
    public Interceptor provideOfflineCacheInterceptor() {

        mInterceptor = new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                if (!ConnectionDetector.isConnectingToInternet(mContext)) {

                    request = request.newBuilder().removeHeader("Pragma")
                            .header("Cache-Control", String.format("max-age=%d", 60))
                            .build();

                }

                return chain.proceed(request);

            }
        };

        return mInterceptor;
    }
}
