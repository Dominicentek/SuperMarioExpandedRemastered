package com.smedx.assets.gamedata;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class Level {
    public int[][] tiles;
    public int spawnX = 0;
    public int spawnY = 0;
    public boolean isSpotlight = false;
    private Level() {}
    public static Level createNewLevel(int width, int height) {
        Level level = new Level();
        level.tiles = new int[width][height];
        return level;
    }
    public Level resize(int width, int height) {
        Level level = new Level();
        level.tiles = new int[width][height];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (x >= width || y >= height) continue;
                level.tiles[x][y] = tiles[x][y];
            }
        }
        level.isSpotlight = isSpotlight;
        level.spawnX = spawnX;
        level.spawnY = spawnY;
        return level;
    }
    public static Level parse(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Level level = new Level();
        level.spawnX = Byte.toUnsignedInt(buffer.get());
        level.spawnY = Byte.toUnsignedInt(buffer.get());
        level.isSpotlight = Byte.toUnsignedInt(buffer.get()) != 0;
        int width = Byte.toUnsignedInt(buffer.get());
        int height = Byte.toUnsignedInt(buffer.get());
        level.tiles = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                level.tiles[x][y] = Byte.toUnsignedInt(buffer.get());
            }
        }
        return level;
    }
    public byte[] write() {
        ByteBuffer buffer = ByteBuffer.allocate(5 + tiles.length * tiles[0].length);
        buffer.put((byte)spawnX);
        buffer.put((byte)spawnY);
        buffer.put(isSpotlight ? (byte)1 : 0);
        buffer.put((byte)tiles.length);
        buffer.put((byte)tiles[0].length);
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                buffer.put((byte)tiles[x][y]);
            }
        }
        return buffer.array();
    }
    public static Level createRandomizedLevel(int width, int height, boolean spotlight, int greenSpawnRate, int redSpawnRate, int iceSpawnRate, int keyAmount, long seed) {
        int attempt = 1;
        Level level;
        do {
            if (attempt == 10000) return null;
            level = new Level();
            Random random = new Random(seed);
            level.isSpotlight = spotlight;
            level.tiles = new int[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int tile = random.nextInt(10);
                    if (tile == 1 && greenSpawnRate >= 1) level.tiles[x][y] = 1;
                    else if (tile == 2 && greenSpawnRate >= 2) level.tiles[x][y] = 1;
                    else if (tile == 3 && greenSpawnRate >= 3) level.tiles[x][y] = 1;
                    else if (tile == 4 && redSpawnRate >= 1) level.tiles[x][y] = 2;
                    else if (tile == 5 && redSpawnRate >= 2) level.tiles[x][y] = 2;
                    else if (tile == 6 && redSpawnRate >= 3) level.tiles[x][y] = 2;
                    else if (tile == 7 && iceSpawnRate >= 1) level.tiles[x][y] = 3;
                    else if (tile == 8 && iceSpawnRate >= 2) level.tiles[x][y] = 3;
                    else if (tile == 9 && iceSpawnRate >= 3) level.tiles[x][y] = 3;
                }
            }
            boolean retry = false;
            int keys = 0;
            int keyAttempts = 0;
            while (keys < keyAmount) {
                keyAttempts++;
                if (keyAttempts == 5000 * keyAmount) {
                    retry = true;
                    break;
                }
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                if (level.tiles[x][y] == 0) {
                    level.tiles[x][y] = 4;
                    keys++;
                }
            }
            if (retry) {
                attempt++;
                continue;
            }
            boolean spawn = false;
            int spawnAttempts = 0;
            while (!spawn) {
                spawnAttempts++;
                if (spawnAttempts == 5000) {
                    retry = true;
                    break;
                }
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                if (level.tiles[x][y] == 0) {
                    spawn = true;
                    level.spawnX = x;
                    level.spawnY = y;
                }
            }
            if (retry) {
                attempt++;
                continue;
            }
            boolean keydoor = false;
            int keydoorAttempts = 0;
            while (!keydoor) {
                keydoorAttempts++;
                if (keydoorAttempts == 5000) {
                    retry = true;
                    break;
                }
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                if (level.tiles[x][y] == 0) {
                    keydoor = true;
                    level.tiles[x][y] = 5;
                }
            }
            if (retry) {
                attempt++;
                continue;
            }
            if (level.isSpotlight) {
                boolean torch = false;
                int torchAttempts = 0;
                while (!torch) {
                    torchAttempts++;
                    if (torchAttempts == 5000) {
                        retry = true;
                        break;
                    }
                    int x = random.nextInt(width);
                    int y = random.nextInt(height);
                    if (level.tiles[x][y] == 0) {
                        torch = true;
                        level.tiles[x][y] = 9;
                    }
                }
                if (retry) {
                    attempt++;
                    continue;
                }
            }
            seed = random.nextLong();
            attempt++;
        }
        while (!isLevelPossibleToBeat(level));
        return level;
    }
    private static boolean isLevelPossibleToBeat(Level level) {
        boolean[][] data = new boolean[level.tiles.length][level.tiles[0].length];
        ArrayList<Point> keys = new ArrayList<>();
        Point keydoor = new Point(0, 0);
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                int tile = level.tiles[x][y];
                data[x][y] = tile >= 1 && tile <= 3;
                if (tile == 4) keys.add(new Point(x, y));
                if (tile == 5) keydoor = new Point(x, y);
            }
        }
        floodFill(data, level.spawnX, level.spawnY);
        if (!data[keydoor.x][keydoor.y]) return false;
        for (Point key : keys) {
            if (!data[key.x][key.y]) return false;
        }
        return true;
    }
    private static void floodFill(boolean[][] data, int x, int y) {
        if (x < 0 || y < 0 || x >= data.length || y >= data[0].length) return;
        if (data[x][y]) return;
        data[x][y] = true;
        floodFill(data, x - 1, y);
        floodFill(data, x + 1, y);
        floodFill(data, x, y - 1);
        floodFill(data, x, y + 1);
    }
    public Level copy() {
        Level copied = new Level();
        copied.spawnX = spawnX;
        copied.spawnY = spawnY;
        copied.isSpotlight = isSpotlight;
        copied.tiles = new int[tiles.length][tiles[0].length];
        for (int x = 0; x < copied.tiles.length; x++) {
            for (int y = 0; y < copied.tiles[0].length; y++) {
                copied.tiles[x][y] = tiles[x][y];
            }
        }
        return copied;
    }
}
