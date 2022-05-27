package com.smedx.scene;

import com.smedx.Main;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Dialog extends Scene {
    public Scene parent;
    public String message;
    public Dialog(String message, Scene parent) {
        this.parent = parent;
        this.message = message;
    }
    public void drawAndUpdate(Graphics g) {
        g.drawString(message, 240 - g.getFontMetrics().stringWidth(message) / 2, 178);
        button(g, 176, 182, 128, 32, "OK", (right) -> {
            Main.currentScene = parent;
        });
    }
    public void keyPressed(KeyEvent event) {}
}
