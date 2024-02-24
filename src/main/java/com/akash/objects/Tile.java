package com.akash.objects;

import com.raylib.Jaylib;

import static com.raylib.Raylib.*;

public class Tile {
    private int value;
    private Jaylib.Rectangle rectangle;
    private int x;
    private int y;

    // fade in animation
    private float opacity = 0.0f;
    private float increment = 0.028f;

    // sliding animation
    private boolean sliding = false;
    private float slideProgress = 0.0f;
    private static final float SLIDE_DURATION = 30f;
    private int targetX;
    private int targetY;

    public Tile(int value, int x, int y) {
        this.value = value;
        this.y = (y * 100) + (20 * (y + 1)) + 60;
        this.x = (20 * (x + 1)) + (x * 100);
        rectangle = new Jaylib.Rectangle(this.x, this.y, 100, 100);
    }

    public void draw() {
        if (sliding) {
            if (this.targetX != this.x) {
                this.x = (int) ((1.0f - slideProgress) * this.x + slideProgress * targetX);
                rectangle = new Jaylib.Rectangle(this.x, this.y, 100, 100);
            }
            if (this.targetY != this.y) {
                this.y = (int) ((1.0f - slideProgress) * this.y + slideProgress * targetY);
                rectangle = new Jaylib.Rectangle(this.x, this.y, 100, 100);
            }
        }

        float textWidth = MeasureText(value+"", 50);

        DrawRectangleRounded(
                rectangle,
                0.2F,
                10,
//                new Jaylib.Color(0, 170, 170, (int) (opacity * 255))
                HSLtoRGB(200, 50, (float) logBase2(value) * 5)
        );

        DrawText(
                ""+value,
                ((x + 100 + x) / 2) - (int)(textWidth / 2),
                ((y + 100 + y) / 2) - 25,
                50,
                new Jaylib.Color(255, 255, 255, (int) (opacity * 255))
        );


        opacity += increment;
        if (opacity > 1.0f) {
            opacity = 1.0f;
            increment = 0.0f;
        }

        if (slideProgress >= 1.0) {
            slideProgress = 0.0f;
            sliding = false;
        }

        updateSlide();
    }

    private double logBase2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public int getValue() {
        return value;
    }

    public void set(int x, int y) {
        this.targetX = (20 * (x + 1)) + (x * 100);
        this.targetY = (y * 100) + (20 * (y + 1)) + 60;
        this.sliding = true;
        this.slideProgress = 0.0f;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private void updateSlide() {
        if (sliding) {
            slideProgress += 1.0f / SLIDE_DURATION;
            slideProgress = Math.min(slideProgress, 1.0f); // Ensure progress doesn't exceed 1.0
        }
    }

    public static Color HSLtoRGB(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60.0f) % 2 - 1));
        float m = l - c / 2;

        float r, g, b;
        if (h >= 0 && h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (h >= 60 && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (h >= 120 && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (h >= 180 && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (h >= 240 && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        return new Jaylib.Color((int)((r + m) * 255), (int)((g + m) * 255), (int)((b + m) * 255), 255);
    }
}
