package com.virtualhuman.voicedictionary.Helper;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Internet connection handling
 * Created by Min Thiri on 6/16/2018.
 */

public class InternetConnectionHandling {

    /**
     * To check there is connection or not
     * @return boolean
     */
    public static boolean haveNetworkConnection(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
