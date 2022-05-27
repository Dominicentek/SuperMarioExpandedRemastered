package com.smedx.scene;

import com.smedx.Main;
import com.smedx.assets.Images;
import com.smedx.assets.Sounds;
import com.smedx.assets.gamedata.Level;
import com.smedx.assets.gamedata.Tile;
import com.smedx.util.Timer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class LevelScene extends Scene {
    public static boolean accessedFirstTime = false;
    public boolean firstTime = false;
    public Level level;
    public Level origLevel;
    public int playerX = 0;
    public int playerY = 0;
    public int keys = 0;
    public int totalKeys = 0;
    public int pickaxes = 0;
    public boolean hasTorch = false;
    public Scene parent;
    public boolean blinkingShown = false;
    public Timer blinkingTime = new Timer(0.5, false);
    public boolean dead = false;
    public boolean won = false;
    public int deathParticleSpread = 0;
    public Timer finishTime = new Timer(2, false);
    public boolean paused = false;
    public LevelScene(Level level, Scene parent) {
        this.level = level.copy();
        this.origLevel = level;
        for (int x = 0; x < level.tiles.length; x++) {
            for (int y = 0; y < level.tiles[0].length; y++) {
                if (level.tiles[x][y] == 4) totalKeys++;
            }
        }
        this.parent = parent;
        if (!accessedFirstTime) {
            accessedFirstTime = true;
            firstTime = true;
        }
        playerX = level.spawnX;
        playerY = level.spawnY;
        blinkingTime.onFinish = () -> {
            blinkingTime.start();
            blinkingShown = !blinkingShown;
        };
        blinkingTime.start();
        Sounds.getSound(level.isSpotlight ? "spotlight" : "level").playAsMusic();
    }
    public void drawAndUpdate(Graphics g) {
        if (dead) deathParticleSpread += (int)(Timer.deltaTime * (finishTime.getTime() / 2));
        int spotlightSize = 420;
        if (level.isSpotlight) spotlightSize = hasTorch ? 80 : 48;
        g.translate(240 - (playerX * 16 + 8), 180 - (playerY * 16 + 8));
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, level.tiles.length * 16, level.tiles[0].length * 16);
        for (int i = -1; i <= level.tiles.length; i++) {
            g.drawImage(Images.getImage("tile_wall"), i * 16, -16, null);
            g.drawImage(Images.getImage("tile_wall"), i * 16, level.tiles[0].length * 16, null);
        }
        for (int i = 0; i < level.tiles[0].length; i++) {
            g.drawImage(Images.getImage("tile_wall"), -16, i * 16, null);
            g.drawImage(Images.getImage("tile_wall"), level.tiles.length * 16, i * 16, null);
        }
        for (int x = 0; x < level.tiles.length; x++) {
            for (int y = 0; y < level.tiles[0].length; y++) {
                Tile tile = Tile.getTile(level.tiles[x][y]);
                if (tile.isBlinking() && ((playerX == x && playerY == 0) || !blinkingShown)) continue;
                g.drawImage(tile.getTexture(), x * 16, y * 16, null);
            }
        }
        g.drawImage(Images.getImage("player"), playerX * 16, playerY * 16, null);
        g.translate(-(240 - (playerX * 16 + 8)), -(180 - (playerY * 16 + 8)));
        BufferedImage spotlight = new BufferedImage(480, 360, BufferedImage.TYPE_INT_ARGB);
        Graphics gfx = spotlight.getGraphics();
        gfx.setColor(new Color(0, 0, 0, 255));
        gfx.fillRect(0, 0, 480, 360);
        ((Graphics2D)gfx).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
        int width = spotlightSize;
        int height = spotlightSize;
        int x = 240 - width / 2;
        int y = 180 - height / 2;
        gfx.setColor(new Color(0, 0, 0, 191));
        gfx.fillOval(x, y, width, height); // For some reason, first method call doesn't work. That's why it's there twice.
        gfx.fillOval(x, y, width, height);
        width = (int)(spotlightSize / 8.0 * 7);
        height = (int)(spotlightSize / 8.0 * 7);
        x = 240 - width / 2;
        y = 180 - height / 2;
        gfx.setColor(new Color(0, 0, 0, 127));
        gfx.fillOval(x, y, width, height);
        gfx.fillOval(x, y, width, height);
        width = (int)(spotlightSize / 8.0 * 6);
        height = (int)(spotlightSize / 8.0 * 6);
        x = 240 - width / 2;
        y = 180 - height / 2;
        gfx.setColor(new Color(0, 0, 0, 63));
        gfx.fillOval(x, y, width, height);
        gfx.fillOval(x, y, width, height);
        width = (int)(spotlightSize / 8.0 * 5);
        height = (int)(spotlightSize / 8.0 * 5);
        x = 240 - width / 2;
        y = 180 - height / 2;
        gfx.setColor(new Color(0, 0, 0, 0));
        gfx.fillOval(x, y, width, height);
        g.drawImage(spotlight, 0, 0, null);
        int fontHeight = g.getFontMetrics().getHeight();
        g.drawImage(Images.getImage("tile_key"), 5, 5, null);
        g.drawImage(Images.getImage("tile_pickaxe"), 5, 26, null);
        g.drawString(keys + "/" + totalKeys, 26, 5 + (16 - fontHeight) / 2 + fontHeight);
        g.drawString(pickaxes + "", 26, 26 + (16 - fontHeight) / 2 + fontHeight);
        if (paused) {
            g.setColor(new Color(0, 0, 0, 191));
            g.fillRect(0, 0, 480, 360);
            g.setColor(new Color(255, 255, 255));
            g.setFont(g.getFont().deriveFont(32f));
            g.drawString("Paused", 10, 10 + g.getFontMetrics().getHeight());
            g.setFont(g.getFont().deriveFont(16f));
            g.setColor(new Color(127, 127, 127));
            String resume = "Resume";
            String exit = "Exit Level";
            String returnMainMenu = "Return to Main Menu";
            g.drawString(resume, 10, 47 + g.getFontMetrics().getHeight());
            g.drawString(exit, 10, 68 + g.getFontMetrics().getHeight());
            g.drawString(returnMainMenu, 10, 89 + g.getFontMetrics().getHeight());
            g.setColor(new Color(255, 255, 255));
            fontHeight = g.getFontMetrics().getHeight();
            Rectangle2D resumeBounds = g.getFontMetrics().getStringBounds(resume, g);
            Rectangle2D exitBounds = g.getFontMetrics().getStringBounds(exit, g);
            Rectangle2D returnMainMenuBounds = g.getFontMetrics().getStringBounds(returnMainMenu, g);
            if (resumeBounds.intersects(Main.cursorX - 10, Main.cursorY - 49 - fontHeight, 1, 1)) {
                g.drawString(resume, 10, 47 + fontHeight);
                if (Main.leftMousePressed) paused = false;
            }
            if (exitBounds.intersects(Main.cursorX - 10, Main.cursorY - 68 - fontHeight, 1, 1)) {
                g.drawString(exit, 10, 68 + fontHeight);
                if (Main.leftMousePressed) {
                    Main.currentScene = parent;
                    Sounds.getSound("menu").playAsMusic();
                }
            }
            if (returnMainMenuBounds.intersects(Main.cursorX - 10, Main.cursorY - 89 - fontHeight, 1, 1)) {
                g.drawString(returnMainMenu, 10, 89 + fontHeight);
                if (Main.leftMousePressed) Main.currentScene = new MainMenu(false);
            }
        }
        g.setFont(g.getFont().deriveFont(80f));
        String win = "Win";
        String lose = "Lose";
        if (won) {
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(win, g);
            g.setColor(new Color((int)(finishTime.getTime() * 127), 255, (int)(finishTime.getTime() * 127)));
            textOutline(g, win, (int)(240 - bounds.getWidth() / 2), (int)(180 + bounds.getHeight() / 2), new Color(0, 0, 0), 3);
        }
        if (dead) {
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(lose, g);
            g.setColor(new Color(255, (int)(finishTime.getTime() * 127), (int)(finishTime.getTime() * 127)));
            textOutline(g, lose, (int)(240 - bounds.getWidth() / 2), (int)(180 + bounds.getHeight() / 2), new Color(0, 0, 0), 3);
        }
    }
    public void keyPressed(KeyEvent ev) {
        if (dead || won) return;
        if (!paused) {
            int prevX = playerX;
            int prevY = playerY;
            int direction = Tile.DIR_UP;
            boolean moved = false;
            if (ev.getKeyCode() == KeyEvent.VK_W || ev.getKeyCode() == KeyEvent.VK_UP) {
                playerY--;
                moved = true;
            }
            if (ev.getKeyCode() == KeyEvent.VK_A || ev.getKeyCode() == KeyEvent.VK_LEFT) {
                direction = Tile.DIR_LEFT;
                playerX--;
                moved = true;
            }
            if (ev.getKeyCode() == KeyEvent.VK_D || ev.getKeyCode() == KeyEvent.VK_RIGHT) {
                direction = Tile.DIR_RIGHT;
                playerX++;
                moved = true;
            }
            if (ev.getKeyCode() == KeyEvent.VK_S || ev.getKeyCode() == KeyEvent.VK_DOWN) {
                direction = Tile.DIR_DOWN;
                playerY++;
                moved = true;
            }
            if (moved) executeMovement(prevX, prevY, direction);
        }
        if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            if (paused) blinkingTime.stop();
            else blinkingTime.resume();
        }
    }
    public void executeMovement(int previousPosX, int previousPosY, int direction) {
        boolean canMove;
        boolean oob = playerX < 0 || playerY < 0 || playerX >= level.tiles.length || playerY >= level.tiles[0].length;
        if (oob) canMove = false;
        else {
            Tile tile = Tile.getTile(level.tiles[playerX][playerY]);
            if (!blinkingShown && tile.isBlinking()) canMove = true;
            else canMove = tile.step(playerX, playerY, direction, level, this);
        }
        if (!canMove) {
            if (pickaxes > 0 && !oob) {
                pickaxes--;
                level.tiles[playerX][playerY] = 0;
                Sounds.getSound("switchlever").playAsSound();
            }
            playerX = previousPosX;
            playerY = previousPosY;
        }
    }
    public void finish() {
        won = true;
        finishTime.onFinish = () -> {
            Main.currentScene = parent;
            Sounds.getSound("menu").playAsMusic();
        };
        finishTime.start();
    }
    public void reset() {
        dead = true;
        finishTime.onFinish = () -> {
            Main.currentScene = new LevelScene(origLevel, parent);
        };
        finishTime.start();
    }
}
