package com.smedx.assets.gamedata;

import javax.sound.sampled.*;
import java.io.*;

public class Sound implements Serializable {
    private static Clip clip;
    private byte[] data;
    public Sound(byte[] data) {
        this.data = data;
    }
    public void playAsSound() {
        try {
            Clip clip = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(data)));
            clip.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playAsMusic() {
        stopMusic();
        try {
            clip = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
            clip.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(data)));
            clip.loop(-1);
            clip.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}
