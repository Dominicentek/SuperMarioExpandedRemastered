package com.smedx.scene;

import com.smedx.Main;
import com.smedx.util.ButtonAction;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class Scene {
    public abstract void drawAndUpdate(Graphics g);
    public abstract void keyPressed(KeyEvent event);
    public final void button(Graphics g, int x, int y, int w, int h, String text, ButtonAction action) {
        boolean touching = Main.cursorX >= x && Main.cursorY >= y && Main.cursorX < x + w && Main.cursorY < y + h && (!(this instanceof MainMenu) || ((MainMenu)this).timer.finished() || !((MainMenu)this).intro);
        if (touching) {
            g.setColor(new Color(255, 255, 255, 63));
            g.fillRect(x, y, w, h);
        }
        g.setColor(new Color(255, 255, 255, 255));
        g.drawRect(x, y, w, h);
        g.drawString(text, w / 2 - g.getFontMetrics().stringWidth(text) / 2 + x, h / 2 + 8 + y - 3);
        if ((Main.leftMousePressed || Main.rightMousePressed) && touching) action.run(Main.rightMousePressed);
    }
    public final void textOutline(Graphics g, String string, int x, int y, Color outlineColor, int outlineThickness) {
        if (outlineColor.getRed() == g.getColor().getRed() && outlineColor.getGreen() == g.getColor().getGreen() && outlineColor.getBlue() == g.getColor().getBlue()) return;
        Graphics2D gfx = (Graphics2D)g;
        gfx.setFont(g.getFont());
        gfx.setColor(g.getColor());
        gfx.translate(x, y);
        Shape text = gfx.getFont().createGlyphVector(g.getFontMetrics().getFontRenderContext(), string).getOutline();
        gfx.fill(text);
        gfx.setColor(outlineColor);
        gfx.fill(new BasicStroke(outlineThickness).createStrokedShape(text));
        gfx.translate(-x, -y);
    }
}
