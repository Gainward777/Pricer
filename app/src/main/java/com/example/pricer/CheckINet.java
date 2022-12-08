package com.example.pricer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// just check internet connection
public class CheckINet {

    public static boolean hasConnection(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (internetInfo != null && internetInfo.isConnected())
        {
            return true;
        }
        internetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (internetInfo != null && internetInfo.isConnected())
        {
            return true;
        }
        internetInfo = cm.getActiveNetworkInfo();
        if (internetInfo != null && internetInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
