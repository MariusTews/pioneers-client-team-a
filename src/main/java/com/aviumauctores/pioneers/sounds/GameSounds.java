package com.aviumauctores.pioneers.sounds;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class GameSounds {

    MediaPlayer mediaPlayer;
    URL filePath;

    public GameSounds() {
    }

    public GameSounds(URL url) {
        this.filePath = url;
        Media media = new Media(filePath.toString());
        this.mediaPlayer = new MediaPlayer(media);
    }

    public void play() {
        if (mediaPlayer != null) {
            this.mediaPlayer.play();
        }
    }

    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer = null;
        }
    }

    public void pause() {
        this.mediaPlayer.pause();
    }

    public boolean isRunning() {
        if (mediaPlayer == null) {
            return false;
        }
        return this.mediaPlayer.getOnPlaying() != null;
    }


}
