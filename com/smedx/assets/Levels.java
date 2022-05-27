package com.smedx.assets;

import com.smedx.assets.gamedata.Level;
import com.smedx.util.StreamReader;

import java.util.ArrayList;

public class Levels {
    private static ArrayList<Level> levels = new ArrayList<>();
    public static Level getLevel(int index) {
        return levels.get(index);
    }
    static {
        for (int i = 1; i <= 25; i++) {
            levels.add(Level.parse(StreamReader.read(Levels.class.getResourceAsStream("/assets/levels/" + i + ".lvl"))));
        }
    }
}
