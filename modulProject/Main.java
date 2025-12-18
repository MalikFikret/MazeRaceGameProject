package modulProject;

import javax.swing.*;

/**
 * Main class - Entry point of the Maze Race Game application
 * Initializes and launches the game on the Event Dispatch Thread
 */
public class Main {
    public static void main(String[] args) {
        // Launch the game on the Swing Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            MazeRaceGame game = new MazeRaceGame();
            game.setVisible(true);
        });
    }
}