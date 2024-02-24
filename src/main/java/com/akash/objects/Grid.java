package com.akash.objects;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.raylib.Jaylib.DARKGRAY;
import static com.raylib.Raylib.*;


public class Grid {
    CopyOnWriteArrayList<Tile> tiles;
    ArrayList<Cell> cells;
    private final Timer timer;
    private boolean animating;
    private boolean over;

    public Grid() {
        tiles = new CopyOnWriteArrayList<>();
        cells = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Cell cell = new Cell(i, j);
                cells.add(cell);
            }
        }
        timer = new Timer();
        animating = false;
        over = false;
        getRandomTile();
    }

    private ArrayList<ArrayList<Cell>> getCellsByColumn() {
        return cells.stream().reduce(new ArrayList<>(), (acc, cell) -> {
            int index = findIndex(acc, cell.getX());
            if (index == -1) {
                ArrayList<Cell> newList = new ArrayList<>();
                newList.add(cell);
                acc.add(newList);
            } else {
                acc.get(index).add(cell);
            }
            return acc;
        }, (acc1, acc2) -> {
            acc1.addAll(acc2);
            return acc1;
        });
    }

    private ArrayList<ArrayList<Cell>> getCellsByRow() {
        return cells.stream().reduce(new ArrayList<>(), (acc, cell) -> {
            int index = findIndexY(acc, cell.getY());
            if (index == -1) {
                ArrayList<Cell> newList = new ArrayList<>();
                newList.add(cell);
                acc.add(newList);
            } else {
                acc.get(index).add(cell);
            }
            return acc;
        }, (acc1, acc2) -> {
            acc1.addAll(acc2);
            return acc1;
        });
    }

    private static int findIndex(ArrayList<ArrayList<Cell>> list, int x) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isEmpty() && list.get(i).get(0).getX() == x) {
                return i;
            }
        }
        return -1;
    }

    private static int findIndexY(ArrayList<ArrayList<Cell>> list, int y) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isEmpty() && list.get(i).get(0).getY() == y) {
                return i;
            }
        }
        return -1;
    }

    public void input() {
        if (animating) return;
        if (over) {
            if (IsKeyPressed(KEY_SPACE)) {
                tiles = new CopyOnWriteArrayList<>();
                cells = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        Cell cell = new Cell(i, j);
                        cells.add(cell);
                    }
                }
                animating = false;
                over = false;
                getRandomTile();
            }
        } else {
            if (IsKeyPressed(KEY_UP)) {
                var columns = getCellsByColumn();
                var slides = slide(columns);
                if (slides)
                    getRandomTile();
            }
            if (IsKeyPressed(KEY_DOWN)) {
                var reverseColumns = getCellsByColumn();
                for (var col: reverseColumns) {
                    Collections.reverse(col);
                }
                var slides = slide(reverseColumns);
                if (slides)
                    getRandomTile();
            }
            if (IsKeyPressed(KEY_LEFT)) {
                var rows = getCellsByRow();

                var slides = slide(rows);
                if (slides)
                    getRandomTile();
            }
            if (IsKeyPressed(KEY_RIGHT)) {
                var reverseRow = getCellsByRow();
                for (var row: reverseRow) {
                    Collections.reverse(row);
                }
                var slides = slide(reverseRow);
                if (slides)
                    getRandomTile();
            }
        }
    }

    private boolean slide(ArrayList<ArrayList<Cell>> groups) {
        AtomicBoolean slides = new AtomicBoolean(false);
        groups.forEach(group -> {
            for (int i = 1; i < group.size(); i++) {
                var cell = group.get(i);
                if (cell.getTile() == null) continue;
                AtomicReference<Cell> lastValidCell = new AtomicReference<>();

                for(var c: group) {

                    if (c.getTile() == null && lastValidCell.get() == null) {
                        lastValidCell.set(c);
                    } else if (c == cell) {
                        break;
                    } else if (c.getTile() != null && c.getTile().getValue() == cell.getTile().getValue()) {
                        lastValidCell.set(c);
                    } else if (!c.canAccept(cell.getTile()) && lastValidCell.get() != null) {
                        lastValidCell.set(null);
                    }
                }

                if (lastValidCell.get() != null) {
                    if (lastValidCell.get().getTile() != null && cell.getTile().getValue() == lastValidCell.get().getTile().getValue()) {
                        cell.getTile().set(lastValidCell.get().getX(), lastValidCell.get().getY()); // Initiate animation
                        Tile tile = lastValidCell.get().getTile();
                        tile.setValue(cell.getTile().getValue() * 2);
                        lastValidCell.get().setTile(tile);
                        var deletedCell = cell.getTile();
                        animating = true;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                tiles.remove(deletedCell);
                                animating = false;
                            }
                        }, 250);


                    } else {
                        lastValidCell.get().setTile(cell.getTile());
                    }
                    cell.setTile(null);

                    slides.set(true);
                }
            }
        });
        return slides.get();
    }

    public void draw() {
        cells.forEach(Cell::draw);
        synchronized (tiles) {
            tiles.forEach(Tile::draw);
        }
        if (tiles.size() == 16) {
            over = true;
        }
    }

    public int score() {
        int max = 0;
        for (var tile: tiles) {
            if (tile.getValue() > max)
                max = tile.getValue();
        }

        return max;
    }

    public void getRandomTile() {
        var random = new Random();
        var emptyCells = getEmptyCells();
        if (!emptyCells.isEmpty()){
            var index = random.nextInt(emptyCells.size());
            Tile tile = emptyCells.get(index).setTile();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tiles.add(tile);
                }
            }, 250);
        }
    }

    private ArrayList<Cell> getEmptyCells() {
        return (ArrayList<Cell>) cells.stream()
                .filter(Cell::isEmpty)
                .collect(Collectors.toList());
    }

    public boolean isOver() {
        return over;
    }
}
