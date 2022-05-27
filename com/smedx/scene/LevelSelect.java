package com.smedx.scene;

import com.smedx.Main;
import com.smedx.assets.Levels;
import com.smedx.assets.Sounds;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LevelSelect extends Scene {
    public LevelSelect() {
        Sounds.getSound("menu").playAsMusic();
    }
    public void drawAndUpdate(Graphics g) {
        String levelSelect = "Level Select";
        g.setFont(g.getFont().deriveFont(32f));
        int x = 240 - g.getFontMetrics().stringWidth(levelSelect) / 2;
        for (int i = 0; i < levelSelect.length(); i++) {
            g.drawString(levelSelect.charAt(i) + "", x, (int)(50 + Math.sin(Math.toRadians((System.currentTimeMillis() / 4) - i * 45)) * 10));
            x += g.getFontMetrics().stringWidth(levelSelect.charAt(i) + "");
        }
        g.setFont(g.getFont().deriveFont(16f));
        for (int i = 0; i < 25; i++) {
            final int level = i;
                x = 240 + 36 * (i % 5) - 98 + 2;
            int y = 180 + 36 * (i / 5) - 106 + 2;
            button(g, x, y, 32, 32, "" + (i + 1), (right) -> {
                Main.currentScene = new LevelScene(Levels.getLevel(level), this);
            });
        }
        button(g, 144, 256, 176, 32, "Back to Main Menu", (right) -> {
            Main.currentScene = new MainMenu(false);
        });
    }
    public void keyPressed(KeyEvent ev) {}

}
