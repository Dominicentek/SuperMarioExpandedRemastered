package com.smedx.assets.gamedata;

import com.smedx.assets.Images;
import com.smedx.assets.Sounds;
import com.smedx.scene.LevelScene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Tile {
    public static final int DIR_UP = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    public static final int DIR_DOWN = 3;
    private static ArrayList<Tile> tiles = new ArrayList<>();
    private BufferedImage texture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    private boolean blinking = false;
    private boolean hidden = false;
    public abstract boolean step(int x, int y, int direction, Level level, LevelScene scene);
    public Tile assignTexture(BufferedImage texture) {
        this.texture = texture;
        return this;
    }
    public Tile blink() {
        blinking = true;
        return this;
    }
    public Tile hideFromEditor() {
        hidden = true;
        return this;
    }
    public BufferedImage getTexture() {
        return texture;
    }
    public boolean isBlinking() {
        return blinking;
    }
    public boolean isHidden() {
        return hidden;
    }
    public static Tile getTile(int index) {
        return tiles.get(index);
    }
    public static int getTileCount() {
        return tiles.size();
    }
    static {
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return true;
            }
        }.hideFromEditor());
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return false;
            }
        }.assignTexture(Images.getImage("tile_green")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.reset();
                Sounds.getSound("death").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_red")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                if (direction == DIR_UP) {
                    scene.playerY--;
                    scene.executeMovement(x, y, direction);
                }
                if (direction == DIR_LEFT) {
                    scene.playerX--;
                    scene.executeMovement(x, y, direction);
                }
                if (direction == DIR_RIGHT) {
                    scene.playerX++;
                    scene.executeMovement(x, y, direction);
                }
                if (direction == DIR_DOWN) {
                    scene.playerY++;
                    scene.executeMovement(x, y, direction);
                }
                level.tiles[x][y] = 0;
                return true;
            }
        }.assignTexture(Images.getImage("tile_ice")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.keys++;
                level.tiles[x][y] = 0;
                Sounds.getSound("collectkey").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_key")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                if (scene.totalKeys == scene.keys) {
                    scene.finish();
                    Sounds.getSound("finish").playAsSound();
                }
                return true;
            }
        }.assignTexture(Images.getImage("tile_keydoor")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                for (int X = 0; X < level.tiles.length; X++) {
                    for (int Y = 0; Y < level.tiles[X].length; Y++) {
                        if (level.tiles[X][Y] == 8) level.tiles[X][Y] = 0;
                    }
                }
                level.tiles[x][y] = 7;
                Sounds.getSound("switchlever").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_lever_off")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return true;
            }
        }.assignTexture(Images.getImage("tile_lever_on")).hideFromEditor());
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return false;
            }
        }.assignTexture(Images.getImage("tile_door")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.hasTorch = true;
                level.tiles[x][y] = 0;
                Sounds.getSound("powerup").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_torch")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.pickaxes++;
                level.tiles[x][y] = 0;
                Sounds.getSound("powerup").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_pickaxe")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return false;
            }
        }.assignTexture(Images.getImage("tile_green")).blink());
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.reset();
                Sounds.getSound("death").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_red")).blink());
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.blinkingTime.resume();
                for (int X = 0; X < level.tiles.length; X++) {
                    for (int Y = 0; Y < level.tiles[0].length; Y++) {
                        if (level.tiles[X][Y] == 13) level.tiles[X][Y] = 14;
                    }
                }
                Sounds.getSound("switchlever").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_blinklever_on")).hideFromEditor());
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                scene.blinkingTime.stop();
                for (int X = 0; X < level.tiles.length; X++) {
                    for (int Y = 0; Y < level.tiles[0].length; Y++) {
                        if (level.tiles[X][Y] == 14) level.tiles[X][Y] = 13;
                    }
                }
                Sounds.getSound("switchlever").playAsSound();
                return true;
            }
        }.assignTexture(Images.getImage("tile_blinklever_off")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return direction != DIR_DOWN;
            }
        }.assignTexture(Images.getImage("tile_oneway_up")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return direction != DIR_RIGHT;
            }
        }.assignTexture(Images.getImage("tile_oneway_left")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return direction != DIR_LEFT;
            }
        }.assignTexture(Images.getImage("tile_oneway_right")));
        tiles.add(new Tile() {
            public boolean step(int x, int y, int direction, Level level, LevelScene scene) {
                return direction != DIR_UP;
            }
        }.assignTexture(Images.getImage("tile_oneway_down")));
    }
}
