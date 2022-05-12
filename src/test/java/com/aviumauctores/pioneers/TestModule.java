package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.service.PreferenceService;
import dagger.Module;
import dagger.Provides;

import java.util.Locale;
import java.util.prefs.Preferences;

@Module
public class TestModule {
    @Provides
    PreferenceService preferences() {
        return new PreferenceService(null){
            @Override
            public Locale getLocale() {
                return Locale.ROOT;
            }
            @Override
            public void setLocale(Locale locale) {
            }

            @Override
            public String getRefreshToken() {
                return "";
            }

            @Override
            public void setRefreshToken(String token) {
            }

            @Override
            public Boolean getRememberMe() {
                return false;
            }

            @Override
            public void setRememberMe(Boolean rememberMe) {
            }
        };
    }
}
