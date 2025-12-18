package modulProject;

/**
 * Score Entry class - Represents a single high score record
 * Stores player information, completion time, opponent, and map snapshot
 */
public class ScoreEntry {
    private String playerName;     // Name of the player who achieved this score
    private double time;           // Time taken to complete the maze (in seconds)
    private String opponent;       // Name of the opponent (AI or other player)
    private int[][] mapSnapshot;   // Snapshot of the maze configuration for this game
    
    /**
     * Constructor - Creates a new score entry
     * @param playerName Name of the player
     * @param time Completion time in seconds
     * @param opponent Name of the opponent
     * @param mapSnapshot 2D array snapshot of the maze
     */
    public ScoreEntry(String playerName, double time, String opponent, int[][] mapSnapshot) {
        this.playerName = playerName;
        this.time = time;
        this.opponent = opponent;
        this.mapSnapshot = mapSnapshot;
    }
    
    /**
     * Get the player's name
     * @return Player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Get the completion time
     * @return Time in seconds
     */
    public double getTime() {
        return time;
    }
    
    /**
     * Get the opponent's name
     * @return Opponent name
     */
    public String getOpponent() {
        return opponent;
    }
    
    /**
     * Get the maze snapshot for this score
     * @return 2D array representing the maze
     */
    public int[][] getMapSnapshot() {
        return mapSnapshot;
    }
    
    /**
     * Format score entry as a string for display
     * @return Formatted string with player name, time, and opponent
     */
    @Override
    public String toString() {
        return String.format("%-15s | %6.2fs | vs %-15s", 
            truncate(playerName, 15), 
            time, 
            truncate(opponent, 15));
    }
    
    /**
     * Truncate string to maximum length with ellipsis if needed
     * @param str String to truncate
     * @param maxLen Maximum length
     * @return Truncated string
     */
    private String truncate(String str, int maxLen) {
        if (str.length() > maxLen) {
            return str.substring(0, maxLen - 2) + "..";
        }
        return str;
    }
}