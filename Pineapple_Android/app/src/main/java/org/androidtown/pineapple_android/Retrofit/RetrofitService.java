package org.androidtown.pineapple_android.Retrofit;

import org.androidtown.pineapple_android.Model.FindTheWay;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by MSI on 2018-04-24.
 */

public interface RetrofitService {
    @POST("tmap/routes/pedestrian")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
            "appKey: 345ff3b1-839d-47a2-860f-de2d9dc3acd8",
            "Host: api2.sktelecom.com"
    })
    Call<FindTheWay> getFindTheWay(@Body RequestBody body);
}
