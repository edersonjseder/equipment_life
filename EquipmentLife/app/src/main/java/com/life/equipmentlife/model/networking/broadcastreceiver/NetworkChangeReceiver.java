package com.life.equipmentlife.model.networking.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.life.equipmentlife.common.listener.ConnectivityReceiverListener;
import com.life.equipmentlife.model.networking.internetdetector.ConnectionDetector;

/**
 * BroadcastReceiver to verify the network connection status
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkChangeReceiver.class.getSimpleName();

    public NetworkChangeReceiver(){
        super();
    }

    /**
     * Method that verifies if the network connection is connected or not
     *
     * @param context The context that verifies if the connection is fine
     * @param intent The intent which makes the broadcast work
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive() inside method");

        boolean isConnected = ConnectionDetector.isConnectingToInternet(context);

        ((ConnectivityReceiverListener)context).onNetworkConnectionChanged(isConnected);

    }

}
