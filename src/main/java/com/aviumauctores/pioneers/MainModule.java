package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.service.PreferenceService;
import dagger.Module;
import dagger.Provides;

import java.util.ResourceBundle;

@Module
public class MainModule {
    @Provides
    ResourceBundle bundle(PreferenceService preferenceService){
        return ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", preferenceService.getLocale());
    }
}
