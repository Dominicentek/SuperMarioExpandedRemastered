package com.smedx.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Images {
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static BufferedImage readImage(String assetName) {
        InputStream stream = Images.class.getResourceAsStream(assetName);
        if (stream == null) {
            System.out.println("File \"" + assetName + "\" is missing");
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        try {
            return ImageIO.read(stream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static BufferedImage getImage(String name) {
        return images.get(name);
    }
    static {
        images.put("logo", readImage("/assets/images/logo.png"));
        images.put("cursor", readImage("/assets/images/cursor.png"));
        images.put("icon", readImage("/assets/images/icon.png"));
        images.put("cell", readImage("/assets/images/cell.png"));
        images.put("player", readImage("/assets/images/player.png"));
        images.put("tile_wall", readImage("/assets/images/tiles/wall.png"));
        images.put("tile_green", readImage("/assets/images/tiles/green.png"));
        images.put("tile_red", readImage("/assets/images/tiles/red.png"));
        images.put("tile_ice", readImage("/assets/images/tiles/ice.png"));
        images.put("tile_key", readImage("/assets/images/tiles/key.png"));
        images.put("tile_keydoor", readImage("/assets/images/tiles/keydoor.png"));
        images.put("tile_lever_off", readImage("/assets/images/tiles/lever_off.png"));
        images.put("tile_lever_on", readImage("/assets/images/tiles/lever_on.png"));
        images.put("tile_door", readImage("/assets/images/tiles/door.png"));
        images.put("tile_torch", readImage("/assets/images/tiles/torch.png"));
        images.put("tile_pickaxe", readImage("/assets/images/tiles/pickaxe.png"));
        images.put("tile_blinklever_off", readImage("/assets/images/tiles/blinklever_off.png"));
        images.put("tile_blinklever_on", readImage("/assets/images/tiles/blinklever_on.png"));
        images.put("tile_oneway_up", readImage("/assets/images/tiles/oneway_up.png"));
        images.put("tile_oneway_left", readImage("/assets/images/tiles/oneway_left.png"));
        images.put("tile_oneway_right", readImage("/assets/images/tiles/oneway_right.png"));
        images.put("tile_oneway_down", readImage("/assets/images/tiles/oneway_down.png"));
    }
}
