package com.aviumauctores.pioneers.sounds;

import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class GameSounds {

    MediaPlayer mediaPlayer;
    URL filePath;

    public GameSounds(URL url) {
        this.filePath = url;
        Media media = new Media(filePath.toString());
        this.mediaPlayer = new MediaPlayer(media);
    }

    public void play(){
        this.mediaPlayer.play();
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

    public boolean isRunning(){
        if(mediaPlayer==null){
            return false;
        }
        return this.mediaPlayer.getOnPlaying() != null;
    }


}
