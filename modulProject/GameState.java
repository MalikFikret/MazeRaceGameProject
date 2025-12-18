package modulProject;

import java.awt.Point;

/**
 * Game State class - Manages the current state of the game
 * Tracks player positions, times, statistics, and game progress
 * Thread-safe implementation for concurrent player movements
 */
public class GameState {
    // ==================== POSITIONS ====================
    private Point playerPos;      // Player 1 position
    private Point player2Pos;     // Player 2/AI position
    private Point exit;           // Exit position
    
    // ==================== THREAD SAFETY LOCKS ====================
    private final Object playerLock = new Object();    // Lock for Player 1 operations
    private final Object player2Lock = new Object();   // Lock for Player 2 operations
    
    // ==================== TIMES ====================
    private long playerStartTime;         // Player 1 start time
    private long player2StartTime;        // Player 2/AI start time
    private long playerFinishTime = -1;   // Player 1 finish time (-1 = not finished)
    private long player2FinishTime = -1;  // Player 2/AI finish time (-1 = not finished)
    
    // ==================== STATE FLAGS ====================
    private boolean playerFinished = false;   // Has Player 1 finished?
    private boolean player2Finished = false;  // Has Player 2/AI finished?
    private boolean gameStarted = false;      // Has the game started?
    
    // ==================== DEBUG STATISTICS ====================
    private int playerMoveAttempts = 0;    // Total move attempts by Player 1
    private int playerFailedMoves = 0;     // Failed moves (hit wall) by Player 1
    private int player2MoveAttempts = 0;   // Total move attempts by Player 2/AI
    private int player2FailedMoves = 0;    // Failed moves (hit wall) by Player 2/AI
    
    // ==================== MOVEMENT COOLDOWN ====================
    private long lastPlayerMoveTime = 0;   // Last move time for Player 1
    private long lastPlayer2MoveTime = 0;  // Last move time for Player 2
    private long lastAIMoveTime = 0;       // Last move time for AI
    private static final long MOVE_COOLDOWN = 80; // Cooldown in milliseconds
    
    // ==================== PLAYER INFO ====================
    private String player1Name = "Player 1";  // Player 1 name
    private String player2Name = "Player 2";  // Player 2/AI name
    
    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor - Initialize game state with starting positions
     * @param startPos Starting position for both players
     * @param exitPos Exit position
     */
    public GameState(Point startPos, Point exitPos) {
        this.playerPos = new Point(startPos);
        this.player2Pos = new Point(startPos);
        this.exit = exitPos;
    }
    
    // ==================== GAME START ====================
    /**
     * Start the game - Initialize all timers and timestamps
     */
    public void startGame() {
        gameStarted = true;
        playerStartTime = System.currentTimeMillis();
        player2StartTime = System.currentTimeMillis();
        lastPlayerMoveTime = System.currentTimeMillis();
        lastPlayer2MoveTime = System.currentTimeMillis();
        lastAIMoveTime = System.currentTimeMillis();
        
        System.out.println("🎮 Game started!");
    }
    
    // ==================== GAME RESET ====================
    /**
     * Reset the game state to initial values
     * @param startPos New starting position
     * @param exitPos New exit position
     */
    public void reset(Point startPos, Point exitPos) {
        // Reset positions
        this.playerPos = new Point(startPos);
        this.player2Pos = new Point(startPos);
        this.exit = exitPos;
        
        // Reset state flags
        playerFinished = false;
        player2Finished = false;
        gameStarted = false;
        
        // Reset times
        playerFinishTime = -1;
        player2FinishTime = -1;
        
        // Reset statistics
        playerMoveAttempts = 0;
        playerFailedMoves = 0;
        player2MoveAttempts = 0;
        player2FailedMoves = 0;
        
        // Reset cooldowns
        lastPlayerMoveTime = 0;
        lastPlayer2MoveTime = 0;
        lastAIMoveTime = 0;
        
        System.out.println("🔄 Game reset!");
    }
    
    // ==================== TIME CALCULATION ====================
    /**
     * Get Player 1's current time in seconds
     * @return Time in seconds (0.0 if not started, finish time if completed, current time if ongoing)
     */
    public double getPlayerTime() {
        if (playerFinishTime > 0) {
            // Player finished - return completion time
            return (playerFinishTime - playerStartTime) / 1000.0;
        }
        if (!gameStarted) {
            // Game not started - return 0
            return 0.0;
        }
        // Game ongoing - calculate current elapsed time
        return (System.currentTimeMillis() - playerStartTime) / 1000.0;
    }
    
    /**
     * Get Player 2/AI's current time in seconds
     * @return Time in seconds (0.0 if not started, finish time if completed, current time if ongoing)
     */
    public double getPlayer2Time() {
        if (player2FinishTime > 0) {
            return (player2FinishTime - player2StartTime) / 1000.0;
        }
        if (!gameStarted) {
            return 0.0;
        }
        return (System.currentTimeMillis() - player2StartTime) / 1000.0;
    }
    
    // ==================== PLAYER FINISH ====================
    /**
     * Mark Player 1 as finished
     * Records finish time and prints statistics
     */
    public void finishPlayer() {
        if (playerFinished) return; // Already finished, don't do it again
        
        playerFinished = true;
        playerFinishTime = System.currentTimeMillis();
        
        System.out.println("🏁 " + player1Name + " finished!");
        printPlayerStats(player1Name, playerMoveAttempts, playerFailedMoves);
    }
    
    /**
     * Mark Player 2/AI as finished
     * Records finish time and prints statistics
     */
    public void finishPlayer2() {
        if (player2Finished) return; // Already finished, don't do it again
        
        player2Finished = true;
        player2FinishTime = System.currentTimeMillis();
        
        System.out.println("🏁 " + player2Name + " finished!");
        printPlayerStats(player2Name, player2MoveAttempts, player2FailedMoves);
    }
    
    // ==================== STATISTICS PRINTING ====================
    /**
     * Print player statistics to console
     * @param name Player name
     * @param attempts Total move attempts
     * @param failed Failed move attempts
     */
    private void printPlayerStats(String name, int attempts, int failed) {
        System.out.println("📊 " + name + " STATISTICS:");
        System.out.println("   ├─ Total move attempts: " + attempts);
        System.out.println("   ├─ Failed moves: " + failed);
        if (attempts > 0) {
            double successRate = (1 - (double)failed/attempts) * 100;
            System.out.println("   └─ Success rate: " + String.format("%.1f%%", successRate));
        }
    }
    
    // ==================== COOLDOWN CHECKS ====================
    /**
     * Check if Player 1 can move (cooldown expired)
     * @return True if cooldown has passed
     */
    public boolean canPlayerMove() {
        return System.currentTimeMillis() - lastPlayerMoveTime >= MOVE_COOLDOWN;
    }
    
    /**
     * Check if Player 2 can move (cooldown expired)
     * @return True if cooldown has passed
     */
    public boolean canPlayer2Move() {
        return System.currentTimeMillis() - lastPlayer2MoveTime >= MOVE_COOLDOWN;
    }
    
    /**
     * Check if AI can move (cooldown expired)
     * @return True if cooldown has passed
     */
    public boolean canAIMove() {
        return System.currentTimeMillis() - lastAIMoveTime >= MOVE_COOLDOWN;
    }
    
    // ==================== COOLDOWN UPDATES ====================
    /**
     * Update Player 1's last move time (reset cooldown)
     */
    public void updatePlayerMoveTime() {
        lastPlayerMoveTime = System.currentTimeMillis();
    }
    
    /**
     * Update Player 2's last move time (reset cooldown)
     */
    public void updatePlayer2MoveTime() {
        lastPlayer2MoveTime = System.currentTimeMillis();
    }
    
    /**
     * Update AI's last move time (reset cooldown)
     */
    public void updateAIMoveTime() {
        lastAIMoveTime = System.currentTimeMillis();
    }
    
    // ==================== MOVEMENT STATISTICS ====================
    /**
     * Increment Player 1's move attempt counter
     */
    public void incrementPlayerMoveAttempts() { 
        playerMoveAttempts++; 
    }
    
    /**
     * Increment Player 1's failed move counter
     */
    public void incrementPlayerFailedMoves() { 
        playerFailedMoves++; 
    }
    
    /**
     * Increment Player 2's move attempt counter
     */
    public void incrementPlayer2MoveAttempts() { 
        player2MoveAttempts++; 
    }
    
    /**
     * Increment Player 2's failed move counter
     */
    public void incrementPlayer2FailedMoves() { 
        player2FailedMoves++; 
    }
    
    // ==================== POSITION GETTERS ====================
    /**
     * Get Player 1's current position (thread-safe)
     * @return Defensive copy of player position
     */
    public Point getPlayerPos() {
        synchronized(playerLock) {
            return new Point(playerPos);
        }
    }
    
    /**
     * Get Player 2/AI's current position (thread-safe)
     * @return Defensive copy of player 2 position
     */
    public Point getPlayer2Pos() {
        synchronized(player2Lock) {
            return new Point(player2Pos);
        }
    }
    
    /**
     * Get exit position
     * @return Defensive copy of exit position
     */
    public Point getExit() { 
        return new Point(exit);
    }
    
    // ==================== POSITION SETTERS ====================
    /**
     * Set Player 1's position (thread-safe)
     * @param x New x coordinate
     * @param y New y coordinate
     */
    public void setPlayerPos(int x, int y) {
        synchronized(playerLock) {
            playerPos.x = x;
            playerPos.y = y;
        }
    }
    
    /**
     * Set Player 2/AI's position (thread-safe)
     * @param x New x coordinate
     * @param y New y coordinate
     */
    public void setPlayer2Pos(int x, int y) {
        synchronized(player2Lock) {
            player2Pos.x = x;
            player2Pos.y = y;
        }
    }
    
    // ==================== STATE CHECKS ====================
    /**
     * Check if Player 1 has finished
     * @return True if finished
     */
    public boolean isPlayerFinished() { 
        return playerFinished; 
    }
    
    /**
     * Check if Player 2/AI has finished
     * @return True if finished
     */
    public boolean isPlayer2Finished() { 
        return player2Finished; 
    }
    
    /**
     * Check if game has started
     * @return True if started
     */
    public boolean isGameStarted() { 
        return gameStarted; 
    }
    
    /**
     * Check if both players have finished
     * @return True if both finished
     */
    public boolean isBothFinished() { 
        return playerFinished && player2Finished; 
    }
    
    // ==================== NAME GETTERS ====================
    /**
     * Get Player 1's name
     * @return Player 1 name
     */
    public String getPlayer1Name() { 
        return player1Name; 
    }
    
    /**
     * Get Player 2/AI's name
     * @return Player 2/AI name
     */
    public String getPlayer2Name() { 
        return player2Name; 
    }
    
    // ==================== NAME SETTERS ====================
    /**
     * Set Player 1's name
     * @param name New name (defaults to "Player 1" if null/empty)
     */
    public void setPlayer1Name(String name) { 
        this.player1Name = name != null && !name.trim().isEmpty() ? name : "Player 1";
    }
    
    /**
     * Set Player 2/AI's name
     * @param name New name (defaults to "Player 2" if null/empty)
     */
    public void setPlayer2Name(String name) { 
        this.player2Name = name != null && !name.trim().isEmpty() ? name : "Player 2";
    }
}