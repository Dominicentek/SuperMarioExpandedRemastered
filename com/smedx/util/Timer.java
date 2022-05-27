package com.smedx.util;

import com.smedx.Main;

public class Timer {
    public Runnable onFinish = () -> {};
    private double time;
    private double initTime = 0;
    private double limit;
    private boolean countingUp;
    private boolean running = false;
    public static long nanoTime = 0;
    public static double deltaTime = 0;
    public Timer(double seconds, boolean countingUp) {
        initTime = countingUp ? 0 : seconds;
        limit = seconds;
        this.countingUp = countingUp;
    }
    public void start() {
        if (running) return;
        running = true;
        time = initTime;
        Main.timers.add(this);
    }
    public void stop() {
        if (!running) return;
        running = false;
        Main.timers.remove(this);
    }
    public void resume() {
        if (running) return;
        running = true;
        Main.timers.add(this);
    }
    public void advance() {
        if (finished()) return;
        long nanoTime = System.nanoTime();
        long diff = nanoTime - Timer.nanoTime;
        if (countingUp) time += diff / 1000000000.0;
        else time -= diff / 1000000000.0;
        if (time < 0 && !countingUp) {
            time = 0;
            stop();
            onFinish.run();
        }
        if (time >= limit && countingUp) {
            time = limit;
            stop();
            onFinish.run();
        }
    }
    public boolean finished() {
        return (!countingUp && time == 0) || (countingUp && time == limit);
    }
    public boolean running() {
        return running;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }
}
