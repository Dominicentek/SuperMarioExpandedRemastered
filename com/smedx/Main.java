package com.smedx;

import com.smedx.assets.Images;
import com.smedx.scene.Editor;
import com.smedx.scene.MainMenu;
import com.smedx.util.CRTFilter;
import com.smedx.scene.Scene;
import com.smedx.util.Timer;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {
    public static ArrayList<Timer> timers = new ArrayList<>();
    public static Scene currentScene = new MainMenu(true);
    public static Timer bgTimer = new Timer(1, false);
    public static boolean crtFilter = true;
    public static double logoPos = 0;
    public static double logoRotation = 0;
    public static BufferedImage background;
    public static int cursorX = 0;
    public static int cursorY = 0;
    public static boolean leftMousePressed = false;
    public static boolean leftMouseHeld = false;
    public static boolean rightMousePressed = false;
    public static Editor editor = new Editor();
    public static void main(String[] args) throws Exception {
        updateTitleScreenBG();
        bgTimer.start();
        JFrame frame = new JFrame("Super Mario Expanded Remastered");
        frame.setLocation(100, 100);
        frame.getContentPane().setPreferredSize(new Dimension(960, 720));
        frame.pack();
        frame.setDefaultCloseOperation(3);
        frame.setResizable(false);
        frame.setIconImage(Images.getImage("icon"));
        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank"));
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1) crtFilter = !crtFilter;
                Main.currentScene.keyPressed(e);
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftMousePressed = true;
                    leftMouseHeld = true;
                }
                if (e.getButton() == MouseEvent.BUTTON3) rightMousePressed = true;
            }
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) leftMouseHeld = false;
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
            public void mouseMoved(MouseEvent e) {
                Point cursor = e.getLocationOnScreen();
                Point container = frame.getContentPane().getLocationOnScreen();
                cursorX = (cursor.x - container.x) / 2;
                cursorY = (cursor.y - container.y) / 2;
            }
        });
        frame.add(new JPanel() {
            public void paint(Graphics graphics) {
                try {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/assets/font.ttf")).deriveFont(16f);
                    BufferedImage image = new BufferedImage(480, 360, BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.getGraphics();
                    g.setColor(new Color(16, 16, 16));
                    g.fillRect(0, 0, 480, 360);
                    if (bgTimer.finished()) {
                        bgTimer.start();
                        updateTitleScreenBG();
                    }
                    g.drawImage(background, 0, 0, null);
                    g.setColor(new Color(255, 255, 255));
                    g.setFont(font);
                    Main.currentScene.drawAndUpdate(g);
                    if (!(Main.currentScene instanceof MainMenu)) g.drawImage(Images.getImage("cursor"), cursorX, cursorY, null);
                    BufferedImage upscaled = new BufferedImage(960, 720, BufferedImage.TYPE_INT_ARGB);
                    upscaled.getGraphics().drawImage(image, 0, 0, 960, 720, null);
                    graphics.drawImage(crtFilter ? CRTFilter.filter(upscaled) : upscaled, 0, 0, null);
                    leftMousePressed = false;
                    rightMousePressed = false;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        frame.setVisible(true);
        while (true) {
            long time = System.currentTimeMillis();
            ArrayList<Timer> timers = new ArrayList<>(Main.timers);
            for (Timer timer : timers) {
                timer.advance();
            }
            logoPos = Math.sin(System.currentTimeMillis() / 3000.0 * 2 * Math.PI) * 12.5;
            logoRotation = Math.toRadians(Math.cos(System.currentTimeMillis() / 5000.0 * 2 * Math.PI) * 10);
            frame.repaint();
            Timer.nanoTime = System.nanoTime();
            Thread.sleep(Math.max(0, 16 - System.currentTimeMillis() + time));
            Timer.deltaTime = System.currentTimeMillis() - time;
        }
    }
    public static void updateTitleScreenBG() {
        background = new BufferedImage(480, 360, BufferedImage.TYPE_INT_ARGB);
        Graphics g = background.getGraphics();
        Random random = new Random();
        HashMap<Rectangle, Color> pixels = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            int size = random.nextInt(26) + 25;
            int x = random.nextInt(480 + size * 2 - 2) - size + 1;
            int y = random.nextInt(360 + size * 2 - 2) - size + 1;
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(16) + 16);
            pixels.put(new Rectangle(x, y, size, size), color);
        }
        for (HashMap.Entry<Rectangle, Color> pixel : pixels.entrySet()) {
            g.setColor(pixel.getValue());
            g.fillRect(pixel.getKey().x, pixel.getKey().y, pixel.getKey().width, pixel.getKey().height);
        }
    }
    public static double map(double x, double min1, double max1, double min2, double max2) {
        return (x - min1) / (max1 - min1) * (max2 - min2) + min2;
    }
}
