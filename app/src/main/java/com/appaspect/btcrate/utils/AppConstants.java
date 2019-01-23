package com.appaspect.btcrate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.appaspect.btcrate.data.prefs.SharedPreferenceUtils;

public class AppConstants {

    public static SharedPreferenceUtils sharedPreferenceUtils;
    public static final String[] currency_list={
            "USD",
            "EUR",
            "GBP",
            "AUD",
            "BRL",
            "CAD",

            "CZK",
            "IDR",
            "ILS",
            "JPY",
            "MXN",
            "MYR",

            "NZD",
            "PLN",
            "RUB",
            "SEK",
            "SGD",
            "TRY"
    };

    public static final String[] currency_symbol_list={
            "$",
            "€",
            "£",
            "$",
            "R$",
            "$",

            "Kč",
            "Rp",
            "₪",
            "¥",
            "$",
            "RM",

            "$",
            "zł",
            "py6",
            "kr",
            "$",
            "₺"
    };

    public static boolean isConnectionAvailable(Context context) {

        NetworkInfo networkInfo=null;
        try
        {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = cm.getActiveNetworkInfo();

        }
        catch (SecurityException e) {

        }catch (Exception e) {

        }

        return networkInfo != null && networkInfo.isConnected();
//        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
