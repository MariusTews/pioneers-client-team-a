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

    public String getUsername(){
        return preferences.get("username", "");
    }

    public void setUsername(String username){
        preferences.put("username", username);
    }

    public String getPassword(){
        return preferences.get("password", "");
    }

    public void setPassword(String password){
        preferences.put("password", password);
    }

}
