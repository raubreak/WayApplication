package com.wayapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author raubreak
 *
 */
public class WayAppServiceStarter extends BroadcastReceiver {
    static final String TAG = "WayAppService";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Received intent only when the system boot is completed
        Log.d(TAG, "onReceiveIntent");
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        if (prefs.getBoolean("prefAutoStartKey", true)){
        	Log.d(TAG, "Trying to auto start.");
        	Intent sint = new Intent();
        	sint.setAction("com.wayapp.services.WayAppService");
        	context.startService(sint);

//        }
    }
    }
