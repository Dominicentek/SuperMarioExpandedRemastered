package com.smedx.util;

import com.smedx.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CRTFilter {
    private static final double flickeriness = 0.1;
    private static final int curvature = 20;
    private static final int scanlines = 360;
    private static final int scanlineAlpha = 127;
    public static BufferedImage filter(BufferedImage in) {
        double flicker = flickeriness == 0 ? 1 : new Random().nextDouble() % flickeriness + (1 - flickeriness);
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = out.getGraphics();
        g.drawImage(in, 0, 0, null);
        g.setColor(new Color(0, 0, 0, scanlineAlpha));
        if (scanlines > 0) {
            for (int y = 0; y < out.getHeight(); y++) {
                if (y % (out.getHeight() / scanlines) < (out.getHeight() / scanlines / 2)) continue;
                g.fillRect(0, y, out.getWidth(), 1);
            }
        }
        g.setColor(new Color(0, 0, 0, 255 - (int)(flicker * 255)));
        g.fillRect(0, 0, in.getWidth(), in.getHeight());
        BufferedImage curved = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
        g = curved.getGraphics();
        for (int x = 0; x < in.getWidth(); x++) {
            double height = Math.sin(Math.toRadians(Main.map(x, 0, in.getWidth(), 90 - curvature, 90 + curvature))) * in.getHeight();
            double offset = in.getHeight() / 2.0 - height / 2;
            g.drawImage(out.getSubimage(x, 0, 1, in.getHeight()), x, (int)offset, 1, (int)height, null);
        }
        out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
        g = out.getGraphics();
        for (int y = 0; y < in.getHeight(); y++) {
            double width = Math.sin(Math.toRadians(Main.map(y, 0, in.getHeight(), 90 - curvature, 90 + curvature))) * in.getWidth();
            double offset = in.getWidth() / 2.0 - width / 2;
            g.drawImage(curved.getSubimage(0, y, in.getWidth(), 1), (int)offset, y, (int)width, 1, null);
        }
        return out;
    }
}
