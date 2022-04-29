package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import javax.inject.Singleton;

@Module
public class MainModule {
    @Provides
    @Singleton
    ObjectMapper mapper() {
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    Retrofit retrofit(ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
    }

    @Provides
    @Singleton
    AuthenticationApiService authenticationApiService(Retrofit retrofit) {
        return retrofit.create(AuthenticationApiService.class);
    }
}
