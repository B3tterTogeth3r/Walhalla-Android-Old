package de.walhalla.app.threads.runnable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import de.walhalla.app.App;

public class CheckInternetRunnable implements Runnable {

    @Override
    public void run() {
        String TAG = "CheckInternet";
        Thread.currentThread().setName(TAG);
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                assert cm != null;
                NetworkInfo info = cm.getActiveNetworkInfo();
                Network nw = cm.getActiveNetwork();
                if (nw == null) {
                    // not connected
                    App.isInternet = false;
                    App.internetKind = "-";
                }
                NetworkCapabilities actNw = cm.getNetworkCapabilities(nw);
                if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    App.isInternet = true;
                    App.internetKind = "WIFI";
                } else if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                    App.isInternet = true;
                    App.internetKind = "?";
                    //int networkType = actNw.describeContents();
                    try {
                        int networkType = info.getSubtype();
                        switch (networkType) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:     // api< 8: replace by 11
                            case TelephonyManager.NETWORK_TYPE_GSM:      // api<25: replace by 16
                                App.isInternet = true;
                                App.internetKind = "2G";
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:   // api< 9: replace by 12
                            case TelephonyManager.NETWORK_TYPE_EHRPD:    // api<11: replace by 14
                            case TelephonyManager.NETWORK_TYPE_HSPAP:    // api<13: replace by 15
                            case TelephonyManager.NETWORK_TYPE_TD_SCDMA: // api<25: replace by 17
                                App.isInternet = true;
                                App.internetKind = "3G";
                            case TelephonyManager.NETWORK_TYPE_LTE:      // api<11: replace by 13
                            case TelephonyManager.NETWORK_TYPE_IWLAN:    // api<25: replace by 18
                            case 19: // LTE_CA
                                App.isInternet = true;
                                App.internetKind = "4G";
                            case TelephonyManager.NETWORK_TYPE_NR:       // api<29: replace by 20
                                App.isInternet = true;
                                App.internetKind = "5G";
                            default:
                                App.isInternet = true;
                                App.internetKind = "?";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    App.isInternet = false;
                    App.internetKind = "?";
                }
                try {
                    long ftr = System.currentTimeMillis() + 180000;
                    wait(ftr - System.currentTimeMillis());
                } catch (Exception ignored) {
                }
            } catch (Exception e) {
                App.isInternet = false;
                App.internetKind = "?";
                //Log.e(TAG, "" + e.getMessage());
            }

        }
    }
}
