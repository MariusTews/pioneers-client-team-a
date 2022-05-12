package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.controller.LoginController;
import dagger.BindsInstance;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = { MainModule.class, PreferenceModule.class, HttpModule.class })
@Singleton
public interface MainComponent {
    LoginController loginController();
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder mainApp(App app);

        MainComponent build();
    }
}
