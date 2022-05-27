package com.smedx.scene;

import com.smedx.Main;
import com.smedx.assets.Images;
import com.smedx.assets.Sounds;
import com.smedx.assets.gamedata.Level;
import com.smedx.assets.gamedata.Tile;
import com.smedx.util.Timer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class Editor extends Scene {
    public int width = 10;
    public int height = 10;
    public Level level = Level.createNewLevel(width, height);
    public int selectedTile = 1;
    public Timer blinkTimer = new Timer(0.5, false);
    public boolean blinkShown = true;
    public int pointerX = 0;
    public int pointerY = 0;
    public boolean eraseMode = false;
    public boolean modeAssigned = false;
    public Editor() {
        blinkTimer.start();
        blinkTimer.onFinish = () -> {
            blinkTimer.start();
            blinkShown = !blinkShown;
        };
    }
    public void drawAndUpdate(Graphics g) {
        int spotlightWidth = g.getFontMetrics().stringWidth("Spotlight");
        g.drawString("Width", 5 + spotlightWidth - g.getFontMetrics().stringWidth("Width"), 8 + g.getFontMetrics().getHeight());
        g.drawString("Height", 5 + spotlightWidth - g.getFontMetrics().stringWidth("Height"), 8 + g.getFontMetrics().getHeight() + 37);
        g.drawString("Spotlight", 5, 8 + g.getFontMetrics().getHeight() + 37 * 2);
        button(g, 10 + spotlightWidth, 5, 64, 32, "" + width, (right) -> {
            if (right) width--;
            else width++;
            if (width == 16) width = 15;
            if (width == 4) width = 5;
            level = level.resize(width, height);
        });
        button(g, 10 + spotlightWidth, 5 + 37, 64, 32, "" + height, (right) -> {
            if (right) height--;
            else height++;
            if (height == 16) height = 15;
            if (height == 4) height = 5;
            level = level.resize(width, height);
        });
        button(g, 10 + spotlightWidth, 5 + 37 * 2, 64, 32, (("" + level.isSpotlight).charAt(0) + "").toUpperCase() + ("" + level.isSpotlight).substring(1), (right) -> {
            level.isSpotlight = !level.isSpotlight;
        });
        button(g, 5, 360 - 37, spotlightWidth + 69, 32, "Exit Editor", (right) -> {
            Main.currentScene = new MainMenu(false);
        });
        button(g, 5, 360 - 37 * 2, spotlightWidth + 69, 32, "Play Level", (right) -> {
            boolean containsFlag = false;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (level.tiles[x][y] == 5) containsFlag = true;
                }
            }
            if (!containsFlag) Main.currentScene = new Dialog("This level is missing the keyhole.", this);
            else Main.currentScene = new LevelScene(level, this);
        });
        button(g, 5, 360 - 37 * 3, spotlightWidth + 69, 32, "Load Level", (right) -> {
            new Thread(() -> {
                FileDialog dialog = new FileDialog((Frame)null);
                dialog.setMode(FileDialog.LOAD);
                dialog.setMultipleMode(false);
                dialog.setVisible(true);
                if (dialog.getFile() != null) {
                    try {
                        File file = dialog.getFiles()[0];
                        InputStream stream = new FileInputStream(file);
                        byte[] data = new byte[stream.available()];
                        stream.read(data);
                        stream.close();
                        Level level = Level.parse(data);
                        this.level = level;
                        width = level.tiles.length;
                        height = level.tiles[0].length;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
        button(g, 5, 360 - 37 * 4, spotlightWidth + 69, 32, "Save Level", (right) -> {
            new Thread(() -> {
                FileDialog dialog = new FileDialog((Frame)null);
                dialog.setMode(FileDialog.SAVE);
                dialog.setMultipleMode(false);
                dialog.setVisible(true);
                if (dialog.getFile() != null) {
                    try {
                        File file = dialog.getFiles()[0];
                        OutputStream stream = new FileOutputStream(file);
                        stream.write(level.write());
                        stream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
        int tilesInRow = (int)((spotlightWidth + 79) / 20.0);
        int offset = (spotlightWidth + 79) / 2 - (tilesInRow * 20 - 4) / 2;
        int index = 0;
        for (int i = 0; i < Tile.getTileCount(); i++) {
            int x = index % tilesInRow;
            int y = index / tilesInRow;
            Tile tile = Tile.getTile(i);
            if (tile.isHidden()) continue;
            index++;
            Point location = new Point(offset + x * 20, 4 + y * 20 + 5 + 37 * 3);
            boolean hover = Main.cursorX >= location.x && Main.cursorY >= location.y && Main.cursorX < location.x + 16 && Main.cursorY < location.y + 16;
            if (hover && Main.leftMousePressed) {
                selectedTile = i;
            }
            if (!tile.isBlinking() || (tile.isBlinking() && blinkShown)) g.drawImage((hover || selectedTile == i) ? tile.getTexture() : transparent(tile.getTexture()), location.x, location.y, null);
        }
        int offsetX = 80 + spotlightWidth + ((480 - (80 + spotlightWidth)) / 2 - width * 8);
        int offsetY = 180 - height * 8;
        g.setColor(new Color(255, 255, 255));
        g.fillRect(offsetX, offsetY, level.tiles.length * 16, level.tiles[0].length * 16);
        pointerX = Main.cursorX - offsetX > 0 ? (Main.cursorX - offsetX) / 16 : -1;
        pointerY = Main.cursorY - offsetY > 0 ? (Main.cursorY - offsetY) / 16 : -1;
        if (Main.leftMouseHeld) {
            if (pointerX >= 0 && pointerY >= 0 && pointerX < width && pointerY < height) {
                if (!modeAssigned) {
                    modeAssigned = true;
                    eraseMode = level.tiles[pointerX][pointerY] != 0;
                }
                level.tiles[pointerX][pointerY] = eraseMode ? 0 : selectedTile;
            }
        }
        else modeAssigned = false;
        if (Main.rightMousePressed && pointerX >= 0 && pointerY >= 0 && pointerX < width && pointerY < height) {
            level.spawnX = pointerX;
            level.spawnY = pointerY;
        }
        for (int i = -1; i <= level.tiles.length; i++) {
            g.drawImage(Images.getImage("tile_wall"), i * 16 + offsetX, -16 + offsetY, null);
            g.drawImage(Images.getImage("tile_wall"), i * 16 + offsetX, (level.tiles[0].length * 16) + offsetY, null);
        }
        for (int i = 0; i < level.tiles[0].length; i++) {
            g.drawImage(Images.getImage("tile_wall"), -16 + offsetX, i * 16 + offsetY, null);
            g.drawImage(Images.getImage("tile_wall"), level.tiles.length * 16 + offsetX, i * 16 + offsetY, null);
        }
        for (int x = 0; x < level.tiles.length; x++) {
            for (int y = 0; y < level.tiles[0].length; y++) {
                Tile tile = Tile.getTile(level.tiles[x][y]);
                if (tile.isBlinking() && !blinkShown) continue;
                g.drawImage(tile.getTexture(), x * 16 + offsetX, y * 16 + offsetY, null);
            }
        }
        g.drawImage(Images.getImage("player"), level.spawnX * 16 + offsetX, level.spawnY * 16 + offsetY, null);
        if (pointerX >= 0 && pointerY >= 0 && pointerX < width && pointerY < height) g.drawImage(Images.getImage("cell"), pointerX * 16 + offsetX, pointerY * 16 + offsetY, null);
        g.drawLine(79 + spotlightWidth, 0, 79 + spotlightWidth, 360);
        g.drawLine(0, 5 + 37 * 3, 79 + spotlightWidth, 5 + 37 * 3);
    }
    public void keyPressed(KeyEvent event) {}
    private BufferedImage transparent(BufferedImage image) {
        BufferedImage transparent = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                int a = (argb & 0xFF000000) >>> 24;
                a /= 4;
                transparent.setRGB(x, y, a * 16777216 + (argb & 0xFFFFFF));
            }
        }
        return transparent;
    }
}
