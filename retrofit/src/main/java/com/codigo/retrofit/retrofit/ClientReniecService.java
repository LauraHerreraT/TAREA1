package com.codigo.retrofit.retrofit;

import com.codigo.retrofit.aggregates.response.ReniecResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ClientReniecService {

    @GET("v2/reniec/dni")
    Call<ReniecResponse> findReniec(@Header("Authorization") String token,
                                    @Query("numero") String dni);
}
