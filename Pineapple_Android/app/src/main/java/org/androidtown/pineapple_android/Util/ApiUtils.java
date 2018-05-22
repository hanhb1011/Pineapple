package org.androidtown.pineapple_android.Util;


import org.androidtown.pineapple_android.Retrofit.RetrofitClient;
import org.androidtown.pineapple_android.Retrofit.RetrofitService;

/**
 * Created by MSI on 2018-04-24.
 */

public class ApiUtils {
    public static final String BASE_URL = "https://api2.sktelecom.com/";


    public static RetrofitService getRetrofitService() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitService.class);
    }
}
