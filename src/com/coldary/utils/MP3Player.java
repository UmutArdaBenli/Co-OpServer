package com.coldary.utils;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;

public class MP3Player {

    private Clip clip;

    public MP3Player(String path) {
        play(path);
    }

    public void play(String resourcePath) {
        try (InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
