package com.smedx.scene;

import com.smedx.Main;
import com.smedx.assets.Images;
import com.smedx.assets.Sounds;
import com.smedx.util.Timer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MainMenu extends Scene {
    public Timer timer;
    public boolean startedTimer = false;
    public boolean intro;
    public MainMenu(boolean intro) {
        timer = new Timer(2.5, false);
        Runnable playSong = () -> {
            Sounds.getSound("title").playAsMusic();
        };
        if (!intro) playSong.run();
        timer.onFinish = playSong;
        this.intro = intro;
    }
    public void drawAndUpdate(Graphics g) {
        if (!startedTimer && intro) timer.start();
        startedTimer = true;
        ((Graphics2D)g).rotate(Main.logoRotation, 240, (int)(50 + Main.logoPos + Images.getImage("logo").getHeight()));
        g.drawImage(Images.getImage("logo"), 120, (int)(50 + Main.logoPos), null);
        ((Graphics2D)g).rotate(-Main.logoRotation, 240, (int)(50 + Main.logoPos + Images.getImage("logo").getHeight()));
        button(g, 176, 175, 128, 32, "Level Select", (right) -> {
            Main.currentScene = new LevelSelect();
        });
        button(g, 176, 212, 128, 32, "Random Level", (right) -> {
            Main.currentScene = new RandomLevelConfig();
        });
        button(g, 176, 249, 128, 32, "Level Editor", (right) -> {
            Main.currentScene = Main.editor;
            Sounds.getSound("menu").playAsMusic();
        });
        g.drawImage(Images.getImage("cursor"), Main.cursorX, Main.cursorY, null);
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(new Color(0, 0, 0, intro ? Math.min(255, (int)(timer.getTime() * 255)) : 0));
        g.fillRect(0, 0, 480, 360);
        g.setColor(new Color(0, 255, 0));
        if (!timer.finished() && intro) g.drawString("AV1", 20, 13 + g.getFontMetrics().getHeight());
    }
    public void keyPressed(KeyEvent ev) {}
}
