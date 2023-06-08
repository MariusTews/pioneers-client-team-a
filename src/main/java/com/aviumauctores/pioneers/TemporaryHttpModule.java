package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import com.aviumauctores.pioneers.rest.CheckConnectionApiService;
import com.aviumauctores.pioneers.service.TokenStorage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TemporaryHttpModule {
    private static ObjectMapper mapper() {
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    private static OkHttpClient client() {
        TokenStorage tokenStorage = new TokenStorage();
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            final String token = tokenStorage.getToken();
            if (token == null) {
                return chain.proceed(chain.request());
            }
            final Request request = chain
                    .request()
                    .newBuilder()
                    .addHeader(Constants.HEADER_AUTH, Constants.HEADER_AUTH_BEARER + token)
                    .build();
            return chain.proceed(request);
        }).build();
    }

    private static Retrofit retrofit() {
        OkHttpClient client = client();
        ObjectMapper mapper = mapper();
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    public static CheckConnectionApiService checkConnectionApiService() {
        return retrofit().create(CheckConnectionApiService.class);
    }
}
