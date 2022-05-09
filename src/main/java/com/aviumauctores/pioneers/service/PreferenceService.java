package com.aviumauctores.pioneers.service;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class PreferenceService {
    private final Preferences preferences;

    @Inject
    public PreferenceService(Preferences preferences){

        this.preferences = preferences;
    }

    public Boolean getRememberMe(){
        return preferences.getBoolean("rememberMe", false);
    }

    public void setRememberMe(Boolean rememberMe){
        preferences.putBoolean("rememberMe", rememberMe);
    }

    public String getRefreshToken(){
        return preferences.get("token", "");
    }

    public void setRefreshToken(String token){
        preferences.put("token", token);
    }

}
