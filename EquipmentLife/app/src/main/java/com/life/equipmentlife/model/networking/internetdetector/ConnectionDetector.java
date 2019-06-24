package com.life.equipmentlife.model.networking.internetdetector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class is to be used in the Broadcast receiver to check internet connection
 *
 * Created by ederson.js on 14/12/2016.
 */

public class ConnectionDetector {

    /**
     * Checking for all possible internet providers
     *
     * @return boolean value
     */
    public static boolean isConnectingToInternet(Context context){

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null){

            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();

            if(activeNetwork != null){

                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){

                    return true;

                } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){

                    return true;

                }

            } else {

                return false;

            }

        } else {

            return false;

        }

        return false;
    }
}
