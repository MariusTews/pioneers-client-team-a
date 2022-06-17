package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;

import javax.inject.Inject;
import java.net.URL;

public class SoundService {
    @Inject
    public SoundService() {
    }

    public GameMusic createGameMusic(URL filePath) {
        return new GameMusic(filePath);
    }

    public GameSounds createGameSounds(URL filePath) {
        return new GameSounds(filePath);
    }
}
