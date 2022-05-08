package com.aviumauctores.pioneers;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = { MainModule.class })
@Singleton
public interface TestComponent extends MainComponent{
    @Component.Builder
    interface Builder extends MainComponent.Builder{}
}
