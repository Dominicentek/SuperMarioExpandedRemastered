package com.smedx.scene;

import com.smedx.Main;
import com.smedx.assets.Sounds;
import com.smedx.assets.gamedata.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class RandomLevelConfig extends Scene {
    public int width = 10;
    public int height = 10;
    public int greenSpawnRate = 1;
    public int redSpawnRate = 1;
    public int iceSpawnRate = 1;
    public int keyAmount = 3;
    public boolean spotlight = false;
    public long seed = new Random().nextLong();
    public RandomLevelConfig() {
        Sounds.getSound("menu").playAsMusic();
    }
    public void drawAndUpdate(Graphics g) {
        int x = 23;
        int y = 2;
        button(g, x + 219, y, 215, 32, "" + width, (right) -> {
            if (right) width--;
            else width++;
            if (width == 16) width = 15;
            if (width == 4) width = 5;
        });
        button(g, x + 219, y + 36, 215, 32, "" + height, (right) -> {
            if (right) height--;
            else height++;
            if (height == 16) height = 15;
            if (height == 4) height = 5;
        });
        button(g, x + 219, y + 36 * 2, 215, 32, "" + greenSpawnRate, (right) -> {
            if (right) greenSpawnRate--;
            else greenSpawnRate++;
            if (greenSpawnRate == 4) greenSpawnRate = 3;
            if (greenSpawnRate == -1) greenSpawnRate = 0;
        });
        button(g, x + 219, y + 36 * 3, 215, 32, "" + redSpawnRate, (right) -> {
            if (right) redSpawnRate--;
            else redSpawnRate++;
            if (redSpawnRate == 4) redSpawnRate = 3;
            if (redSpawnRate == -1) redSpawnRate = 0;
        });
        button(g, x + 219, y + 36 * 4, 215, 32, "" + iceSpawnRate, (right) -> {
            if (right) iceSpawnRate--;
            else iceSpawnRate++;
            if (iceSpawnRate == 4) iceSpawnRate = 3;
            if (iceSpawnRate == -1) iceSpawnRate = 0;
        });
        button(g, x + 219, y + 36 * 5, 215, 32, "" + keyAmount, (right) -> {
            if (right) keyAmount--;
            else keyAmount++;
            if (keyAmount == 6) keyAmount = 5;
            if (keyAmount == 0) keyAmount = 1;
        });
        button(g, x + 219, y + 36 * 6, 215, 32, (("" + spotlight).charAt(0) + "").toUpperCase() + ("" + spotlight).substring(1), (right) -> {
            spotlight = !spotlight;
        });
        button(g, x + 219, y + 36 * 7, 215, 32, "" + seed, (right) -> {
            new Thread(() -> {
                String seed = JOptionPane.showInputDialog("Input New Seed");
                if (seed.isEmpty()) this.seed = new Random().nextLong();
                else {
                    try {
                        this.seed = Long.parseLong(seed);
                    } catch (Exception e) {
                        this.seed = seed.hashCode();
                    }
                }
            }).start();
        });
        g.drawString("Width", 238 - g.getFontMetrics().stringWidth("Width"), y + 20);
        g.drawString("Height", 238 - g.getFontMetrics().stringWidth("Height"), y + 20 + 36);
        g.drawString("Green Spawn Rate", 238 - g.getFontMetrics().stringWidth("Green Spawn Rate"), y + 20 + 36 * 2);
        g.drawString("Red Spawn Rate", 238 - g.getFontMetrics().stringWidth("Red Spawn Rate"), y + 20 + 36 * 3);
        g.drawString("Ice Spawn Rate", 238 - g.getFontMetrics().stringWidth("Ice Spawn Rate"), y + 20 + 36 * 4);
        g.drawString("Key Amount", 238 - g.getFontMetrics().stringWidth("Key Amount"), y + 20 + 36 * 5);
        g.drawString("Spotlight", 238 - g.getFontMetrics().stringWidth("Spotlight"), y + 20 + 36 * 6);
        g.drawString("Random Seed", 238 - g.getFontMetrics().stringWidth("Random Seed"), y + 20 + 36 * 7);
        button(g, x, y + 36 * 8, 434, 32, "Generate", (right) -> {
            Level randomLevel = Level.createRandomizedLevel(width, height, spotlight, greenSpawnRate, redSpawnRate, iceSpawnRate, keyAmount, seed);
            if (randomLevel == null) {
                new Thread(() -> {
                    Main.currentScene = new Dialog("Cannot generate level. Try changing some settings.", this);
                }).start();
            }
            else Main.currentScene = new LevelScene(randomLevel, this);
        });
        button(g, x, y + 36 * 9, 434, 32, "Back to Main Menu", (right) -> {
            Main.currentScene = new MainMenu(false);
        });
    }
    public void keyPressed(KeyEvent event) {}
}
