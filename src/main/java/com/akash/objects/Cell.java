package com.akash.objects;

import com.raylib.Jaylib;

import static com.raylib.Raylib.DrawRectangleRounded;

public class Cell {
    private final int x;
    private final int y;
    private Tile tile;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        DrawRectangleRounded(
                new Jaylib.Rectangle(
                        (y * 100) + (20 * (y + 1)),
                        (20 * (x + 1)) + (x * 100) + 60,
                        100,
                        100
                ),
                0.2F,
                10,
                new Jaylib.Color(170, 170, 170, 255)
        );
    }

    public Tile setTile() {
        tile = new Tile(2, x, y);
        return tile;
    }

    public void setTile(Tile tile) {
        if (tile != null) {
            this.tile = tile;
            tile.set(this.x, this.y);
        } else {
            this.tile = null;
        }
    }

    public boolean canAccept(Tile tile) {
        if (this.tile != null) {
            return this.tile.getValue() == tile.getValue();
        }
        return true;
    }

    public boolean isEmpty() {
        return tile == null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Tile getTile() {
        return this.tile;
    }
}
