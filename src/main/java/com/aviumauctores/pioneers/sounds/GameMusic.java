package com.aviumauctores.pioneers.sounds;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class GameMusic {

    MediaPlayer mediaPlayer;
    URL filePath;

    public GameMusic(URL url) {
        this.filePath = url;
        Media media = new Media(filePath.toString());
        this.mediaPlayer = new MediaPlayer(media);

    }

    public void play(){
        this.mediaPlayer.play();
    }

    public void pause() {
        this.mediaPlayer.pause();
    }

    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer = null;
        }
    }

    public boolean isRunning(){
        if(mediaPlayer==null){
            return false;
        }

        return mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);


    }

}
