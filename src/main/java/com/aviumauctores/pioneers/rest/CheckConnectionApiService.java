package com.aviumauctores.pioneers.rest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

import static com.aviumauctores.pioneers.Constants.CHECK_URL;

public interface CheckConnectionApiService {
    @GET(CHECK_URL)
    Observable<Object> checkConnection();
}
