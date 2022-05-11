package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.rest.*;
import com.aviumauctores.pioneers.service.PreferenceService;
import com.aviumauctores.pioneers.service.TokenStorage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import javax.inject.Singleton;
import java.util.ResourceBundle;

@Module
public class MainModule {
    @Provides
    ResourceBundle bundle(PreferenceService preferenceService){
        return ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", preferenceService.getLocale());
    }
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
    OkHttpClient client(TokenStorage tokenStorage) {
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

    @Provides
    @Singleton
    Retrofit retrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    AuthenticationApiService authenticationApiService(Retrofit retrofit) {
        return retrofit.create(AuthenticationApiService.class);
    }

    @Provides
    @Singleton
    MessagesApiService messagesApiService(Retrofit retrofit) {
        return retrofit.create(MessagesApiService.class);
    }

    @Provides
    @Singleton
    UsersApiService usersApiService(Retrofit retrofit) {
        return retrofit.create(UsersApiService.class);
    }

    @Provides
    @Singleton
    GroupsApiService groupsApiService(Retrofit retrofit) {
        return retrofit.create(GroupsApiService.class);
    }

    @Provides
    @Singleton
    GameMembersApiService gameMembersApiService(Retrofit retrofit) {
        return retrofit.create(GameMembersApiService.class);
    }

    @Provides
    @Singleton
    GamesApiService gamesApiService(Retrofit retrofit) {
        return retrofit.create(GamesApiService.class);
    }
}
