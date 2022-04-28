package com.aviumauctores.pioneers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;

import static org.junit.jupiter.api.Assertions.*;

class MainModuleTest {
    @Test
    void testRetrofitCreation() {
        ObjectMapper mapper = MainModule.mapper();
        Retrofit retrofit = MainModule.retrofit(mapper);
        assertEquals(retrofit.baseUrl().toString(), Constants.BASE_URL);
    }
}