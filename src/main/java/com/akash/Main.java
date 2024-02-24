package com.akash;

import com.akash.objects.Grid;
import com.raylib.Jaylib;
import com.raylib.Jaylib.Color;

import java.io.*;

import static com.raylib.Jaylib.*;

public class Main {

    // File to store highest score
    private static final String SCORE_FILE = "highest_score.txt";

    public static void main(String[] args) {
        InitWindow(500, 560, "2048");
        SetTargetFPS(60);

        var grid = new Grid();
        Font font = LoadFont("font.ttf");
        // Load the highest score or create the file if it doesn't exist
        int highestScore = loadHighestScore();

        while (!WindowShouldClose()) {
            BeginDrawing();

            ClearBackground(new Color(204, 204, 204, 255)); // Clear with a solid color

            var result = "Score: " + grid.score();
            var highestScoreText = "Highest Score: " + highestScore;

            grid.draw();
            grid.input();


            DrawText(result, 10, 10, 30, DARKGRAY);
            DrawText(highestScoreText, 10, 45, 19, DARKGRAY);

            if (grid.isOver()) {
                var r = "GAME OVER";
                var m = "PRESS SPACE TO CONTINUE";
                var rw = MeasureText(r, 55);
                var mw = MeasureText(m, 25);

                // Update the highest score if necessary
                if (grid.score() > highestScore) {
                    highestScore = grid.score();
                    saveHighestScore(highestScore);
                }

                // Draw the semi-transparent white rectangle multiple times with offsets to create a blur-like effect
                for (int i = 0; i < 50; i++) {
                    DrawRectangle(-i, -i, 500 + 2 * i, 560 + 2 * i, new Color(255, 255, 255, 5));
                }

                DrawText(r, (500 / 2) - (rw / 2), (500 / 2), 55, DARKGRAY);
                DrawText(m, (500 / 2) - (mw / 2), (500 / 2) + 75, 25, DARKGRAY);
            }

            EndDrawing();
        }

        CloseWindow();
        System.exit(0);
    }

    private static int loadHighestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions (file not found, format error, etc.)
            // If file does not exist, return 0 and create it
            saveHighestScore(0);
        }
        // Return 0 if unable to load the highest score
        return 0;
    }

    private static void saveHighestScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(Integer.toString(score));
        } catch (IOException e) {
            // Handle IO exception
            e.printStackTrace();
        }
    }
}
