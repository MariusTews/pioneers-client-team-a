package com.aviumauctores.pioneers;

import dagger.Module;
import dagger.Provides;

import java.util.prefs.Preferences;

@Module
public class PreferenceModule {
    @Provides
    Preferences preferences() {
        return Preferences.userNodeForPackage(Main.class);
    }
}
