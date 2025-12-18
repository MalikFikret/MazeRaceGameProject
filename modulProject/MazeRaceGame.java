package modulProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.HashSet;

/**
 * Maze Race Game - Main game window class
 * Manages the entire game flow including:
 * - Main menu and game panel switching
 * - Player and AI movement
 * - Game state management
 * - Timer updates
 * - Win condition checking
 * - Leaderboard integration
 */
public class MazeRaceGame extends JFrame {
    // Maze dimensions constants
    private static final int MAZE_WIDTH = 15;
    private static final int MAZE_HEIGHT = 15;
    
    // Timer display labels for both players
    private JLabel playerTimeLabel = new JLabel("Player: 0.00s");
    private JLabel player2TimeLabel = new JLabel("Player 2: 0.00s");
    
    // Game core components
    private int[][] maze;                    // Generated maze structure
    private int[][] currentMaze;             // Current maze being played (for replay)
    private GameState gameState;             // Tracks game progress and player states
    private GameUIManager uiManager;         // Manages UI creation and updates
    
    // Game timers
    private javax.swing.Timer gameTimer;     // Updates player time displays
    private javax.swing.Timer aiTimer;       // Controls AI movement speed
    private AIPlayer aiPlayer;               // AI opponent instance
    
    // UI panel components
    private MazePanel leftPanel;             // Left maze panel (Player 1)
    private MazePanel rightPanel;            // Right maze panel (Player 2/AI)
    private JPanel mainMenuPanel;            // Main menu panel
    private JPanel gamePanel;                // Game play panel
    private JLayeredPane gameLayeredPane;    // Layered pane for overlay effects
    private JLabel startGameLabel;           // "Start Game" overlay label
    
    // Game settings
    private boolean isAIMode = true;         // True if playing against AI, false for 2-player
    private Color player1Color = Color.BLUE; // Player 1's color
    private Color player2Color = Color.RED;  // Player 2/AI's color
    
    // Input handling
    private Set<Integer> pressedKeys = new HashSet<>();  // Currently pressed keys
    
    // Manager instances
    private Leaderboard leaderboard;         // High score management
    private MazeGenerator mazeGenerator;     // Maze generation utility
    
    /**
     * Constructor - Initializes the game window and all components
     */
    public MazeRaceGame() {
        setTitle("Maze Race");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Inherited method
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new CardLayout());
        
        // Set the application icon (Window corner & Taskbar)
        ImageIcon appIcon = IconHelper.getIcon(IconHelper.LABYRINTH, 128, 128);
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }
        
        // Initialize managers
        leaderboard = new Leaderboard();
        mazeGenerator = new MazeGenerator(MAZE_WIDTH, MAZE_HEIGHT);
        uiManager = new GameUIManager(this, leaderboard);
        
        // Setup UI callbacks for menu interactions
        setupUICallbacks();
        
        // Create main panels
        createMainMenu();
        createGamePanel();
        
        // Add panels to card layout
        add(mainMenuPanel, "menu");
        add(gameLayeredPane, "game");
        showMainMenu();
        
        setLocationRelativeTo(null);
    }
    
    /**
     * Setup UI callback handlers for menu actions
     * Handles transitions between menu selections and game modes
     */
    private void setupUICallbacks() {
        uiManager.setCallback(new GameUIManager.UICallback() {
            /**
             * Called when user selects AI mode from menu
             */
            @Override
            public void onAIModeSelected(String playerName, Color playerColor, Color aiColor) {
                isAIMode = true;
                player1Color = playerColor;
                player2Color = aiColor;
                if (gameState != null) {
                    gameState.setPlayer1Name(playerName);
                    gameState.setPlayer2Name("AI");
                }
                uiManager.savePlayerInfo(playerName, playerColor, "AI", aiColor, true);
                showGamePanel(playerName, "AI");
            }
            
            /**
             * Called when user selects 2-player human mode from menu
             */
            @Override
            public void onHumanModeSelected(String player1Name, Color player1Color, 
                                           String player2Name, Color player2Color) {
                isAIMode = false;
                MazeRaceGame.this.player1Color = player1Color;
                MazeRaceGame.this.player2Color = player2Color;
                if (gameState != null) {
                    gameState.setPlayer1Name(player1Name);
                    gameState.setPlayer2Name(player2Name);
                }
                uiManager.savePlayerInfo(player1Name, player1Color, player2Name, player2Color, false);
                showGamePanel(player1Name, player2Name);
            }
            
            /**
             * Called when user wants to replay a previously saved map
             */
            @Override
            public void onPlayStoredMap(int[][] map, boolean isAIMode, 
                                       String player1Name, Color player1Color, 
                                       String player2Name, Color player2Color) {
                currentMaze = deepCopyMaze(map);
                MazeRaceGame.this.isAIMode = isAIMode;
                MazeRaceGame.this.player1Color = player1Color;
                MazeRaceGame.this.player2Color = player2Color;
                if (gameState != null) {
                    gameState.setPlayer1Name(player1Name);
                    gameState.setPlayer2Name(player2Name);
                }
                uiManager.savePlayerInfo(player1Name, player1Color, player2Name, player2Color, isAIMode);
                showGamePanelWithLoadedMap(player1Name, player2Name);
            }
        });
    }
    
    /**
     * Create the main menu panel using UI manager
     */
    private void createMainMenu() {
        mainMenuPanel = uiManager.createMainMenuPanel();
    }
    
    /**
     * Show the main menu screen
     */
    private void showMainMenu() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "menu");
    }
    
    /**
     * Show game panel with a newly generated maze
     * @param player1Name Name of first player
     * @param player2Name Name of second player/AI
     */
    private void showGamePanel(String player1Name, String player2Name) {
        generateMaze();
        
        gameState.setPlayer1Name(player1Name);
        gameState.setPlayer2Name(player2Name);
        
        // Update timer labels with player names and colors
        playerTimeLabel.setText(player1Name + ": 0.00s");
        player2TimeLabel.setText(player2Name + ": 0.00s");
        playerTimeLabel.setForeground(player1Color);
        player2TimeLabel.setForeground(player2Color);
                
        // Show "Start Game" overlay label
        showStartGameLabel();
        
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "game");
        requestFocusInWindow();
    }
    
    /**
     * Show game panel with a previously loaded maze
     * @param player1Name Name of first player
     * @param player2Name Name of second player/AI
     */
    private void showGamePanelWithLoadedMap(String player1Name, String player2Name) {
        loadCurrentMaze();
        
        gameState.setPlayer1Name(player1Name);
        gameState.setPlayer2Name(player2Name);
        
        // Update timer labels with player names and colors
        playerTimeLabel.setText(player1Name + ": 0.00s");
        player2TimeLabel.setText(player2Name + ": 0.00s");
        playerTimeLabel.setForeground(player1Color);
        player2TimeLabel.setForeground(player2Color);
        
        // Show "Start Game" overlay label
        showStartGameLabel();
        
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "game");
        requestFocusInWindow();
    }
    
    /**
     * Display the "Start Game" overlay label
     */
    private void showStartGameLabel() {
        if (startGameLabel != null) {
            startGameLabel.setVisible(true);
        }
    }
    
    /**
     * Hide the "Start Game" overlay label
     */
    private void hideStartGameLabel() {
        if (startGameLabel != null) {
            startGameLabel.setVisible(false);
        }
    }
    
    /**
     * Create the game panel with maze displays and controls
     * Sets up all UI components, event listeners, and game timers
     */
    private void createGamePanel() {
        // Use layered pane for overlay support
        gameLayeredPane = new JLayeredPane();
        gameLayeredPane.setLayout(new OverlayLayout(gameLayeredPane));
        
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setOpaque(true);
        
        // Top panel containing timers and settings button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(250, 247, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Timer display panel for both players
        JPanel timerPanel = new JPanel(new GridLayout(1, 2));
        timerPanel.setOpaque(false);

        // Player 1 timer with icon
        JPanel timer1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        timer1Panel.setOpaque(false);
        JLabel timer1Icon = new JLabel();
        timer1Icon.setIcon(IconHelper.getIcon(IconHelper.TIMER, 20, 20));
        playerTimeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerTimeLabel.setForeground(Color.WHITE);
        timer1Panel.add(timer1Icon);
        timer1Panel.add(playerTimeLabel);

        // Player 2/AI timer with icon
        JPanel timer2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        timer2Panel.setOpaque(false);
        JLabel timer2Icon = new JLabel();
        timer2Icon.setIcon(IconHelper.getIcon(IconHelper.TIMER, 20, 20));
        player2TimeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player2TimeLabel.setForeground(Color.WHITE);
        timer2Panel.add(timer2Icon);
        timer2Panel.add(player2TimeLabel);

        timerPanel.add(timer1Panel);
        timerPanel.add(timer2Panel);
        
        // Settings/Pause button
        JButton settingsButton = new JButton();
        ImageIcon settingsIcon = IconHelper.getIcon(IconHelper.SETTINGS, 32, 32);
        if (settingsIcon != null) {
            settingsButton.setIcon(settingsIcon);
        } else {
            // Fallback to text if icon not available
            settingsButton.setText("☰");
            settingsButton.setFont(new Font("Arial", Font.BOLD, 28));
        }
        settingsButton.setFocusPainted(false);
        settingsButton.setBorderPainted(true);
        settingsButton.setContentAreaFilled(true);
        settingsButton.setBackground(new Color(59, 130, 246));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 235), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setToolTipText("Pause Menu (ESC)");
        settingsButton.addActionListener(e -> showInGameMenu());
        
        // Hover effect for settings button
        settingsButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                settingsButton.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(MouseEvent evt) {
                settingsButton.setBackground(new Color(59, 130, 246));
            }
        });
        
        topPanel.add(timerPanel, BorderLayout.CENTER);
        topPanel.add(settingsButton, BorderLayout.EAST);
        gamePanel.add(topPanel, BorderLayout.NORTH);
     // Maze container with two side-by-side panels
        JPanel mazeContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        leftPanel = new MazePanel(MAZE_WIDTH, MAZE_HEIGHT);
        rightPanel = new MazePanel(MAZE_WIDTH, MAZE_HEIGHT);
        mazeContainer.add(leftPanel);
        mazeContainer.add(rightPanel);
        gamePanel.add(mazeContainer, BorderLayout.CENTER);
        
        // Set alignment for proper overlay positioning
        gamePanel.setAlignmentX(0.5f);
        gamePanel.setAlignmentY(0.5f);
        
        // Create "Start Game" overlay label with wrapper for centering
        JPanel startLabelWrapper = new JPanel(new GridBagLayout());
        startLabelWrapper.setOpaque(false);
        startLabelWrapper.setAlignmentX(0.5f);
        startLabelWrapper.setAlignmentY(0.5f);
        
        startGameLabel = new JLabel("<html><div style='text-align: center;'>Press any key or click<br>to start the game</div></html>");
        startGameLabel.setFont(new Font("Arial", Font.BOLD, 32));
        startGameLabel.setForeground(new Color(71, 85, 105));
        startGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startGameLabel.setOpaque(true);
        startGameLabel.setBackground(new Color(255, 255, 255, 230));
        startGameLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225, 150), 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        startGameLabel.setVisible(false);
        
        // Center the start label using GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        startLabelWrapper.add(startGameLabel, gbc);
        
        // Mouse click listener on maze container to start game
        mazeContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState != null && !gameState.isGameStarted()) {
                    startGame();
                }
            }
        });
        
        // Mouse click listener on left panel
        leftPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState != null && !gameState.isGameStarted()) {
                    startGame();
                }
            }
        });
        
        // Mouse click listener on right panel
        rightPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState != null && !gameState.isGameStarted()) {
                    startGame();
                }
            }
        });
        
        // Keyboard input handler
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ESC key opens pause menu
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (gameState != null && gameState.isGameStarted()) {
                        showInGameMenu();
                    }
                    return;
                }
                
                // Any key starts the game if not started
                if (gameState == null || !gameState.isGameStarted()) {
                    startGame();
                    return;
                }
                
                // Track pressed keys for continuous movement
                pressedKeys.add(e.getKeyCode());
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (gameState == null || !gameState.isGameStarted()) {
                    return;
                }
                // Remove released key from tracking
                pressedKeys.remove(e.getKeyCode());
            }
        });
        
        // Movement timer for smooth continuous movement
        javax.swing.Timer movementTimer = new javax.swing.Timer(25, e -> {
            if (gameState != null && gameState.isGameStarted()) {
                processMovement();
            }
        });
        movementTimer.start();
        
        setFocusable(true);
    }
    
    /**
     * Display in-game pause menu
     * Stops all timers while menu is shown
     */
    private void showInGameMenu() {
        // Pause game timers
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (aiTimer != null) {
            aiTimer.stop();
        }
        
        // Show menu and handle user choice
        InGameMenu.show(this, action -> {
            switch (action) {
                case RESUME:
                    resumeGame();
                    break;
                case RESTART:
                    restartGameWithSameMap();
                    break;
                case MAIN_MENU:
                    returnToMainMenu();
                    break;
            }
        });
    }

    /**
     * Resume game after pause
     * Restarts all necessary timers
     */
    private void resumeGame() {
        if (gameTimer != null && gameState.isGameStarted()) {
            gameTimer.start();
        }
        if (aiTimer != null && isAIMode && gameState != null && !gameState.isPlayer2Finished()) {
            aiTimer.start();
        }
        requestFocusInWindow();
    }
    
    /**
     * Generate a new random maze
     * Initializes game state and updates display panels
     */
    private void generateMaze() {
        maze = mazeGenerator.generate();
        Point startPos = mazeGenerator.getStartPosition();
        Point exitPos = mazeGenerator.getExitPosition();
        
        // Preserve player names if game state exists
        String oldP1Name = (gameState != null) ? gameState.getPlayer1Name() : "Player 1";
        String oldP2Name = (gameState != null) ? gameState.getPlayer2Name() : "Player 2";
        
        gameState = new GameState(startPos, exitPos);
        
        gameState.setPlayer1Name(oldP1Name);
        gameState.setPlayer2Name(oldP2Name);
        
        // Create deep copy for replay functionality
        currentMaze = new int[MAZE_HEIGHT][MAZE_WIDTH];
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                currentMaze[i][j] = maze[i][j];
            }
        }
        
        updateMazePanels();
    }
    
    /**
     * Load a previously saved maze from currentMaze array
     */
    private void loadCurrentMaze() {
        maze = new int[MAZE_HEIGHT][MAZE_WIDTH];
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                maze[i][j] = currentMaze[i][j];
            }
        }
        
        Point startPos = mazeGenerator.getStartPosition();
        Point exitPos = mazeGenerator.getExitPosition();
        gameState = new GameState(startPos, exitPos);
        
        updateMazePanels();
    }
    
    /**
     * Update both maze panels with current game state
     * Configures colors, positions, and layer ordering
     */
    private void updateMazePanels() {
        // Update left panel (Player 1)
        leftPanel.setMaze(maze);
        leftPanel.setExitPosition(gameState.getExit());
        leftPanel.setPlayerPosition(gameState.getPlayerPos());
        leftPanel.setPlayerColor(player1Color);
        
        // Update right panel (Player 2/AI)
        rightPanel.setMaze(maze);
        rightPanel.setExitPosition(gameState.getExit());
        rightPanel.setPlayerPosition(gameState.getPlayer2Pos());
        rightPanel.setPlayerColor(player2Color);
        
        // Setup layered pane on first call
        if (gameLayeredPane.getComponentCount() == 0) {
            gamePanel.setAlignmentX(0.5f);
            gamePanel.setAlignmentY(0.5f);
            
            // Create wrapper for start label
            JPanel startLabelWrapper = new JPanel(new GridBagLayout());
            startLabelWrapper.setOpaque(false);
            startLabelWrapper.setAlignmentX(0.5f);
            startLabelWrapper.setAlignmentY(0.5f);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            startLabelWrapper.add(startGameLabel, gbc);
            
            // Add components to layered pane (overlay system)
            gameLayeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);        // Layer 0
            gameLayeredPane.add(startLabelWrapper, JLayeredPane.PALETTE_LAYER); // Layer 100
            
        } else {
            // On subsequent updates: ensure start label stays on top
            Component[] components = gameLayeredPane.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    // Find startLabelWrapper (non-opaque GridBagLayout panel)
                    if (!panel.isOpaque() && panel.getLayout() instanceof GridBagLayout) {
                        gameLayeredPane.setLayer(panel, JLayeredPane.PALETTE_LAYER);
                    }
                }
            }
        }
        
        gameLayeredPane.revalidate();
        gameLayeredPane.repaint();
    }
    
    /**
     * Start the game
     * Initializes AI, starts timers, and hides start label
     */
    private void startGame() {
        pressedKeys.clear();
        gameState.startGame();
        
        // Hide "Start Game" overlay
        hideStartGameLabel();
        
        updateMazePanels();
        leftPanel.repaint();
        rightPanel.repaint();
        
        // Initialize AI if in AI mode
        if (isAIMode) {
            aiPlayer = new AIPlayer(gameState.getPlayer2Pos(), MAZE_WIDTH, MAZE_HEIGHT);
            aiTimer = new javax.swing.Timer(90, e -> moveAI());
            aiTimer.start();
        }
        
        // Start game timer for display updates
        gameTimer = new javax.swing.Timer(10, e -> updateTimers());
        gameTimer.start();
        
        requestFocusInWindow();
    }
    
    /**
     * Process player movement based on currently pressed keys
     * Handles both players in 2-player mode, or player + AI in AI mode
     */
    private void processMovement() {
        if (!gameState.isGameStarted()) {
            return;
        }
        
        // Player 1 controls (WASD keys)
        if (!gameState.isPlayerFinished()) {
            if (pressedKeys.contains(KeyEvent.VK_W)) movePlayer(0, -1);
            if (pressedKeys.contains(KeyEvent.VK_S)) movePlayer(0, 1);
            if (pressedKeys.contains(KeyEvent.VK_A)) movePlayer(-1, 0);
            if (pressedKeys.contains(KeyEvent.VK_D)) movePlayer(1, 0);
            
            // In AI mode, also allow arrow keys for Player 1
            if (isAIMode) {
                if (pressedKeys.contains(KeyEvent.VK_UP)) movePlayer(0, -1);
                if (pressedKeys.contains(KeyEvent.VK_DOWN)) movePlayer(0, 1);
                if (pressedKeys.contains(KeyEvent.VK_LEFT)) movePlayer(-1, 0);
                if (pressedKeys.contains(KeyEvent.VK_RIGHT)) movePlayer(1, 0);
            }
        }
        
        // Player 2 controls (Arrow keys) - only in 2-player mode
        if (!isAIMode && !gameState.isPlayer2Finished()) {
            if (pressedKeys.contains(KeyEvent.VK_UP)) movePlayer2(0, -1);
            if (pressedKeys.contains(KeyEvent.VK_DOWN)) movePlayer2(0, 1);
            if (pressedKeys.contains(KeyEvent.VK_LEFT)) movePlayer2(-1, 0);
            if (pressedKeys.contains(KeyEvent.VK_RIGHT)) movePlayer2(1, 0);
        }
    }
    
    /**
     * Move Player 1 in specified direction
     * @param dx Horizontal movement (-1 left, 1 right, 0 none)
     * @param dy Vertical movement (-1 up, 1 down, 0 none)
     */
    private void movePlayer(int dx, int dy) {
        // Check if player can move (finished or cooldown)
        if (gameState.isPlayerFinished() || !gameState.canPlayerMove()) {
            return;
        }
        
        gameState.incrementPlayerMoveAttempts();
        
        Point playerPos = gameState.getPlayerPos();
        int newX = playerPos.x + dx;
        int newY = playerPos.y + dy;
        
        // Check if new position is valid (in bounds and not a wall)
        if (newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT 
            && maze[newY][newX] == 0) {
            
            // Valid move - update position
            gameState.setPlayerPos(newX, newY);
            gameState.updatePlayerMoveTime();
            
            leftPanel.setPlayerPosition(new Point(gameState.getPlayerPos()));
            
            // Check if player reached the exit
            if (gameState.getPlayerPos().equals(gameState.getExit()) && !gameState.isPlayerFinished()) {
                gameState.finishPlayer();
                checkGameEnd();
            }
            
            SwingUtilities.invokeLater(() -> leftPanel.repaint());
        } else {
            // Invalid move (hit wall) - update time but don't move
            gameState.updatePlayerMoveTime();
            gameState.incrementPlayerFailedMoves();
        }
    }
    
    /**
     * Move Player 2 in specified direction (2-player mode only)
     * @param dx Horizontal movement (-1 left, 1 right, 0 none)
     * @param dy Vertical movement (-1 up, 1 down, 0 none)
     */
    private void movePlayer2(int dx, int dy) {
        // Check if player 2 can move (finished or cooldown)
        if (gameState.isPlayer2Finished() || !gameState.canPlayer2Move()) {
            return;
        }
        
        gameState.incrementPlayer2MoveAttempts();
        
        Point player2Pos = gameState.getPlayer2Pos();
        int newX = player2Pos.x + dx;
        int newY = player2Pos.y + dy;
        
        // Check if new position is valid (in bounds and not a wall)
        if (newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT 
            && maze[newY][newX] == 0) {
            
            // Valid move - update position
            gameState.setPlayer2Pos(newX, newY);
            gameState.updatePlayer2MoveTime();
            
            rightPanel.setPlayerPosition(new Point(gameState.getPlayer2Pos()));
            
            // Check if player 2 reached the exit
            if (gameState.getPlayer2Pos().equals(gameState.getExit()) && !gameState.isPlayer2Finished()) {
                gameState.finishPlayer2();
                checkGameEnd();
            }
            
            SwingUtilities.invokeLater(() -> rightPanel.repaint());
        } else {
            // Invalid move (hit wall) - update time but don't move
            gameState.updatePlayer2MoveTime();
            gameState.incrementPlayer2FailedMoves();
        }
    }
    
    /**
     * Move AI player one step towards the exit
     * Uses AI algorithm from AIPlayer class
     */
    private void moveAI() {
        if (gameState.isPlayer2Finished() || !gameState.isGameStarted()) {
            return;
        }
        
        // Check AI movement cooldown
        if (!gameState.canAIMove()) {
            return;
        }
        
        Point oldPos = new Point(gameState.getPlayer2Pos());
        aiPlayer.move(maze, gameState.getExit());
        Point newPos = aiPlayer.getPosition();
        
        // Update position if AI moved
        if (!oldPos.equals(newPos)) {
            gameState.setPlayer2Pos(newPos.x, newPos.y);
            gameState.updateAIMoveTime();  // Update cooldown
            rightPanel.setPlayerPosition(new Point(gameState.getPlayer2Pos()));
            
            // Check if AI reached the exit
            if (gameState.getPlayer2Pos().equals(gameState.getExit()) && !gameState.isPlayer2Finished()) {
                gameState.finishPlayer2();
                aiTimer.stop();
                checkGameEnd();
            }
            
            SwingUtilities.invokeLater(() -> rightPanel.repaint());
        }
    }
    
    /**
     * Update timer displays for both players
     */
    private void updateTimers() {
        if (!gameState.isPlayerFinished()) {
            playerTimeLabel.setText(String.format("%s: %.2fs", 
                gameState.getPlayer1Name(), gameState.getPlayerTime()));
        }
        
        if (!gameState.isPlayer2Finished()) {
            player2TimeLabel.setText(String.format("%s: %.2fs", 
                gameState.getPlayer2Name(), gameState.getPlayer2Time()));
        }
    }
    
    /**
     * Check if game has ended (both players finished)
     * Saves scores and shows end game dialog
     */
    private void checkGameEnd() {
        if (gameState.isBothFinished()) {
            // Stop all timers
            if (gameTimer != null) {
                gameTimer.stop();
            }
            if (aiTimer != null) {
                aiTimer.stop();
            }
            
            final double playerTime = gameState.getPlayerTime();
            final double player2Time = gameState.getPlayer2Time();
            
            // Update final time displays
            playerTimeLabel.setText(String.format("%s: %.2fs", gameState.getPlayer1Name(), playerTime));
            player2TimeLabel.setText(String.format("%s: %.2fs", gameState.getPlayer2Name(), player2Time));
            
            leftPanel.repaint();
            rightPanel.repaint();
            
            // Save scores to leaderboard
            try {
                if (!isAIMode) {
                    // 2-player mode: save both scores
                    leaderboard.addScore(gameState.getPlayer1Name(), playerTime, 
                                       gameState.getPlayer2Name(), deepCopyMaze(currentMaze));
                    leaderboard.addScore(gameState.getPlayer2Name(), player2Time, 
                                       gameState.getPlayer1Name(), deepCopyMaze(currentMaze));
                } else {
                    // AI mode: save only player score
                    leaderboard.addScore(gameState.getPlayer1Name(), playerTime, 
                                       "AI", deepCopyMaze(currentMaze));
                }
                
                SwingUtilities.invokeLater(() -> {
                    uiManager.updateLeaderboardDisplay();
                });
                
            } catch (Exception e) {
                System.err.println("✗ Failed to save score: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Show end game dialog after brief delay
            javax.swing.Timer delayTimer = new javax.swing.Timer(300, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int choice = uiManager.showGameEndDialog(
                        gameState.getPlayer1Name(), playerTime,
                        gameState.getPlayer2Name(), player2Time
                    );
                    
                    // Handle user choice
                    if (choice == 0) {
                        restartGameWithSameMap();
                    } else if (choice == 1) {
                        restartGameWithNewMap();
                    } else {
                        returnToMainMenu();
                    }
                    
                    ((javax.swing.Timer)e.getSource()).stop();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    
    /**
     * Restart game with the same maze
     */
    private void restartGameWithSameMap() {
        loadCurrentMaze();
        
        // Restore last player info
        String p1Name = uiManager.getLastPlayer1Name();
        String p2Name = uiManager.getLastPlayer2Name();
        
        player1Color = uiManager.getLastPlayer1Color();
        player2Color = uiManager.getLastPlayer2Color();
        isAIMode = uiManager.getLastIsAIMode();
        
        resetGameState();
        
        gameState.setPlayer1Name(p1Name);
        gameState.setPlayer2Name(p2Name);
        
        playerTimeLabel.setText(p1Name + ": 0.00s");
        player2TimeLabel.setText(p2Name + ": 0.00s");
        playerTimeLabel.setForeground(player1Color);
        player2TimeLabel.setForeground(player2Color);
        
        leftPanel.setPlayerColor(player1Color);
        rightPanel.setPlayerColor(player2Color);
        
        // Show "Start Game" overlay
        showStartGameLabel();
        
        leftPanel.repaint();
        rightPanel.repaint();
        requestFocusInWindow();
    }
    
    /**
     * Restart game with a new randomly generated maze
     */
    private void restartGameWithNewMap() {
        generateMaze();
        
        // Restore last player info
        String p1Name = uiManager.getLastPlayer1Name();
        String p2Name = uiManager.getLastPlayer2Name();
        
        player1Color = uiManager.getLastPlayer1Color();
        player2Color = uiManager.getLastPlayer2Color();
        isAIMode = uiManager.getLastIsAIMode();
        
        resetGameState();
        
        gameState.setPlayer1Name(p1Name);
        gameState.setPlayer2Name(p2Name);
        
        playerTimeLabel.setText(p1Name + ": 0.00s");
        player2TimeLabel.setText(p2Name + ": 0.00s");
        playerTimeLabel.setForeground(player1Color);
        player2TimeLabel.setForeground(player2Color);
        
        leftPanel.setPlayerColor(player1Color);
        rightPanel.setPlayerColor(player2Color);
        
        // Show "Start Game" overlay
        showStartGameLabel();
        
        leftPanel.repaint();
        rightPanel.repaint();
        requestFocusInWindow();
    }
    
    /**
     * Reset game state to initial values
     */
    private void resetGameState() {
        Point startPos = mazeGenerator.getStartPosition();
        Point exitPos = mazeGenerator.getExitPosition();
        
        String p1Name = uiManager.getLastPlayer1Name();
        String p2Name = uiManager.getLastPlayer2Name();
        
        gameState.reset(startPos, exitPos);
        gameState.setPlayer1Name(p1Name);
        gameState.setPlayer2Name(p2Name);
        
        // Stop all timers
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (aiTimer != null) {
            aiTimer.stop();
        }
        
        playerTimeLabel.setText(p1Name + ": 0.00s");
        player2TimeLabel.setText(p2Name + ": 0.00s");
    }
    
    /**
     * Return to main menu
     * Stops all timers and resets game state
     */
    private void returnToMainMenu() {
        // Stop all timers
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (aiTimer != null) {
            aiTimer.stop();
        }
        
        // Reset game state
        Point startPos = mazeGenerator.getStartPosition();
        Point exitPos = mazeGenerator.getExitPosition();
        gameState.reset(startPos, exitPos);
        
        // Update leaderboard display
        uiManager.updateLeaderboardDisplay();
        
        // Show main menu
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "menu");
    }
    
    /**
     * Create a deep copy of a maze array
     * @param src Source maze array
     * @return Deep copy of the maze
     */
    private int[][] deepCopyMaze(int[][] src) {
        if (src == null) return null;
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }
}