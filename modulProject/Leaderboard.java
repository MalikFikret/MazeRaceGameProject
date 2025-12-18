package modulProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Leaderboard class - Manages high scores and persistent storage
 * Stores top 10 scores with player names, times, opponents, and map snapshots
 * Automatically saves and loads from disk
 */
public class Leaderboard {
    private List<ScoreEntry> entries;              // List of score entries
    private static final String SAVE_FILE = "leaderboard.dat";  // Save file name
    private static final int MAX_ENTRIES = 10;     // Maximum number of entries to keep
    
    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor - Initialize leaderboard and load from file
     */
    public Leaderboard() {
        this.entries = new ArrayList<>();
        loadFromFile();
    }
    
    // ==================== ADD SCORE ====================
    /**
     * Add a new score to the leaderboard
     * Automatically sorts by time and keeps only top 10
     * 
     * @param playerName Name of the player
     * @param time Completion time in seconds
     * @param opponent Name of opponent (AI or other player)
     * @param mapSnapshot 2D array snapshot of the maze
     */
    public void addScore(String playerName, double time, String opponent, int[][] mapSnapshot) {
        ScoreEntry newEntry = new ScoreEntry(playerName, time, opponent, mapSnapshot);
        
        // Add to list
        entries.add(newEntry);
        
        // Sort by time (fastest first)
        entries.sort((a, b) -> Double.compare(a.getTime(), b.getTime()));
        
        // Keep only top 10
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
        
        // Save to disk
        saveToFile();
        
        System.out.println("✅ Score added: " + playerName + " - " + time + "s");
        System.out.println("📊 Total scores: " + entries.size());
    }
    
    // ==================== GET ENTRIES ====================
    /**
     * Get all leaderboard entries
     * @return Defensive copy of entries list
     */
    public List<ScoreEntry> getEntries() {
        return new ArrayList<>(entries);
    }
    
    // ==================== CLEAR ====================
    /**
     * Clear all entries from leaderboard
     */
    public void clear() {
        entries.clear();
        saveToFile();
    }
    
    // ==================== SAVE TO FILE ====================
    /**
     * Save leaderboard to disk
     * Serializes all entries including map snapshots
     */
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // Write number of entries
            oos.writeInt(entries.size());
            
            // Write each entry
            for (ScoreEntry entry : entries) {
                oos.writeObject(entry.getPlayerName());
                oos.writeDouble(entry.getTime());
                oos.writeObject(entry.getOpponent());
                
                // Save map snapshot
                int[][] map = entry.getMapSnapshot();
                if (map != null) {
                    oos.writeBoolean(true);  // Map exists
                    oos.writeInt(map.length);     // Rows
                    oos.writeInt(map[0].length);  // Columns
                    // Write all cells
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            oos.writeInt(map[i][j]);
                        }
                    }
                } else {
                    oos.writeBoolean(false);  // No map
                }
            }
            System.out.println("💾 Scores saved to file: " + entries.size() + " records");
        } catch (IOException e) {
            System.err.println("✗ Failed to save scores: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== LOAD FROM FILE ====================
    /**
     * Load leaderboard from disk
     * Deserializes all entries including map snapshots
     */
    private void loadFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("ℹ️ Save file not found, creating new leaderboard.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            // Read number of entries
            int count = ois.readInt();
            entries.clear();
            
            // Read each entry
            for (int i = 0; i < count; i++) {
                String playerName = (String) ois.readObject();
                double time = ois.readDouble();
                String opponent = (String) ois.readObject();
                
                // Load map snapshot
                int[][] map = null;
                boolean hasMap = ois.readBoolean();
                if (hasMap) {
                    int rows = ois.readInt();
                    int cols = ois.readInt();
                    map = new int[rows][cols];
                    // Read all cells
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            map[r][c] = ois.readInt();
                        }
                    }
                }
                
                entries.add(new ScoreEntry(playerName, time, opponent, map));
            }
            
            System.out.println("📂 Scores loaded: " + entries.size() + " records");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("✗ Failed to load scores: " + e.getMessage());
            entries.clear();
        }
    }
}