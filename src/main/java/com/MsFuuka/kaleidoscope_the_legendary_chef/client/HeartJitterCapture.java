package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

public final class HeartJitterCapture {
    private static final int[] JITTER = new int[64];
    private static int total = 0;
    private static int cursor = 0;

    private HeartJitterCapture() {
    }

    public static void begin(int heartTotal) {
        total = heartTotal;
        cursor = 0;
    }

    public static void push(int value) {
        if (cursor < JITTER.length) {
            JITTER[cursor++] = value;
        }
    }

    public static int jitterFor(int heartIndex) {
        int index = total - 1 - heartIndex;
        return index >= 0 && index < cursor ? JITTER[index] : 0;
    }
}
