package com.smedx.assets;

import com.smedx.assets.gamedata.Sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Sounds {
    private static HashMap<String, Sound> sounds = new HashMap<>();
    private static Sound createSound(String assetName) {
        InputStream stream = Sounds.class.getResourceAsStream(assetName);
        if (stream == null) {
            System.out.println("File \"" + assetName + "\" is missing");
            return null;
        }
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = stream.read(buffer)) != -1) {
                data.write(buffer, 0, readLength);
            }
            byte[] fileData = data.toByteArray();
            return new Sound(fileData);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Sound getSound(String sound) {
        return sounds.get(sound);
    }
    static {
        sounds.put("collectkey", createSound("/assets/sounds/collectkey.wav"));
        sounds.put("death", createSound("/assets/sounds/death.wav"));
        sounds.put("finish", createSound("/assets/sounds/finish.wav"));
        sounds.put("powerup", createSound("/assets/sounds/powerup.wav"));
        sounds.put("switchlever", createSound("/assets/sounds/switchlever.wav"));
        sounds.put("level", createSound("/assets/sounds/music/level.wav"));
        sounds.put("menu", createSound("/assets/sounds/music/menu.wav"));
        sounds.put("spotlight", createSound("/assets/sounds/music/spotlight.wav"));
        sounds.put("title", createSound("/assets/sounds/music/title.wav"));
    }
}
