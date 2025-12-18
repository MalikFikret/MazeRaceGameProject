package modulProject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Game UI Manager - PART 1
 * Manages the main menu, leaderboard, and setup dialogs
 * Handles all user interface creation and updates for the game
 */
public class GameUIManager {
    private JFrame parentFrame;                           // Parent window reference
    private Leaderboard leaderboard;                      // Leaderboard data manager
    private DefaultListModel<ScoreEntry> leaderboardModel; // List model for leaderboard display
    private JList<ScoreEntry> leaderboardList;            // Visual leaderboard list
    
    // ==================== COLOR PALETTE ====================
    private static final Color BG_LIGHT = new Color(250, 247, 255);      // Light background
    private static final Color BG_CARD = new Color(255, 255, 255);       // Card background
    private static final Color ACCENT_BLUE = new Color(147, 197, 253);   // Blue accent
    private static final Color ACCENT_PURPLE = new Color(196, 181, 253); // Purple accent
    private static final Color ACCENT_PINK = new Color(251, 207, 232);   // Pink accent
    private static final Color ACCENT_GREEN = new Color(134, 239, 172);  // Green accent
    private static final Color ACCENT_ORANGE = new Color(253, 186, 116); // Orange accent
    private static final Color ACCENT_RED = new Color(252, 165, 165);    // Red accent
    private static final Color TEXT_DARK = new Color(51, 65, 85);        // Dark text
    private static final Color TEXT_GRAY = new Color(100, 116, 139);     // Gray text
    
    // ==================== PLAYER COLOR OPTIONS ====================
    public static final Color[] PLAYER_COLORS = {
        new Color(96, 165, 250),   // Blue
        new Color(74, 222, 128),   // Green
        new Color(251, 146, 60),   // Orange
        new Color(167, 139, 250),  // Purple
        new Color(248, 113, 113),  // Red
        new Color(244, 114, 182),  // Pink
        new Color(250, 204, 21),   // Yellow
        new Color(34, 211, 238)    // Cyan
    };
    
    // ==================== CALLBACK INTERFACE ====================
    /**
     * Callback interface for handling UI events
     * Allows communication between UI and game logic
     */
    public interface UICallback {
        /**
         * Called when AI mode is selected
         */
        void onAIModeSelected(String playerName, Color playerColor, Color aiColor);
        
        /**
         * Called when 2-player human mode is selected
         */
        void onHumanModeSelected(String player1Name, Color player1Color, 
                                 String player2Name, Color player2Color);
        
        /**
         * Called when user wants to play a stored map from leaderboard
         */
        void onPlayStoredMap(int[][] map, boolean isAIMode, 
                            String player1Name, Color player1Color, 
                            String player2Name, Color player2Color);
    }
    
    private UICallback callback;
    
    // ==================== PLAYER INFO STORAGE ====================
    private String lastPlayer1Name = "Player 1";           // Last used Player 1 name
    private String lastPlayer2Name = "Player 2";           // Last used Player 2 name
    private Color lastPlayer1Color = PLAYER_COLORS[0];     // Last used Player 1 color
    private Color lastPlayer2Color = PLAYER_COLORS[4];     // Last used Player 2 color
    private boolean lastIsAIMode = true;                   // Last game mode (AI or 2-player)
    
    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor - Initialize UI manager with parent frame and leaderboard
     * @param parentFrame Main game window
     * @param leaderboard Leaderboard data manager
     */
    public GameUIManager(JFrame parentFrame, Leaderboard leaderboard) {
        this.parentFrame = parentFrame;
        this.leaderboard = leaderboard;
        this.leaderboardModel = new DefaultListModel<>();
    }
    
    /**
     * Set the callback for UI events
     * @param callback Callback implementation
     */
    public void setCallback(UICallback callback) {
        this.callback = callback;
    }
    
    // ==================== PLAYER INFO MANAGEMENT ====================
    /**
     * Save player information for later use
     * @param p1Name Player 1 name
     * @param p1Color Player 1 color
     * @param p2Name Player 2/AI name
     * @param p2Color Player 2/AI color
     * @param isAIMode Whether AI mode is active
     */
    public void savePlayerInfo(String p1Name, Color p1Color, String p2Name, Color p2Color, boolean isAIMode) {
        this.lastPlayer1Name = p1Name;
        this.lastPlayer1Color = p1Color;
        this.lastPlayer2Name = p2Name;
        this.lastPlayer2Color = p2Color;
        this.lastIsAIMode = isAIMode;
    }
    
    // Getters for stored player information
    public String getLastPlayer1Name() { return lastPlayer1Name; }
    public String getLastPlayer2Name() { return lastPlayer2Name; }
    public Color getLastPlayer1Color() { return lastPlayer1Color; }
    public Color getLastPlayer2Color() { return lastPlayer2Color; }
    public boolean getLastIsAIMode() { return lastIsAIMode; }
    
    // ==================== MAIN MENU PANEL ====================
    /**
     * Create the main menu panel with game mode selection and leaderboard
     * @return Configured main menu panel
     */
    public JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout());
        mainMenuPanel.setBackground(BG_LIGHT);
        
        // Left side: Game mode selection
        JPanel leftPanel = createLeftMenuPanel();
        // Right side: Leaderboard display
        JPanel rightPanel = createLeaderboardPanel();
        
        mainMenuPanel.add(leftPanel, BorderLayout.CENTER);
        mainMenuPanel.add(rightPanel, BorderLayout.EAST);
        
        return mainMenuPanel;
    }
    
    // ==================== LEFT MENU PANEL ====================
    /**
     * Create the left menu panel with logo and game mode buttons
     * @return Configured left menu panel
     */
    private JPanel createLeftMenuPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BG_LIGHT);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 30));
        
        // ==================== LOGO AREA ====================
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        
        // Labyrinth icon
        JLabel iconLabel = new JLabel();
        ImageIcon labyrinthIcon = IconHelper.getIcon(IconHelper.LABYRINTH, 150, 150);
        if (labyrinthIcon != null) {
            iconLabel.setIcon(labyrinthIcon);
        } else {
            // Fallback emoji if icon not available
            iconLabel.setText("🌀");
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 64));
        }
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Main title
        JLabel titleLabel = new JLabel("MAZE RACE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Gradient underline for title
        JPanel titleUnderline = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_BLUE, getWidth(), 0, ACCENT_PURPLE);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        titleUnderline.setPreferredSize(new Dimension(250, 4));
        titleUnderline.setMaximumSize(new Dimension(250, 4));
        titleUnderline.setOpaque(false);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Choose Your Battle");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add all logo components
        logoPanel.add(iconLabel);
        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(12));
        logoPanel.add(titleUnderline);
        logoPanel.add(Box.createVerticalStrut(12));
        logoPanel.add(subtitleLabel);
        
        // ==================== GAME MODE BUTTONS ====================
        // AI mode button with robot icon
        JButton aiModeButton = createGradientButtonWithIcon(
            "HUMAN VS AI", 
            IconHelper.ROBOT, 
            ACCENT_BLUE, 
            ACCENT_PURPLE
        );
        aiModeButton.addActionListener(e -> showAIModeSetup());
        
        // Human vs Human button with VS icon
        JButton humanModeButton = createGradientButtonWithIcon(
            "HUMAN VS HUMAN", 
            IconHelper.VS, 
            ACCENT_PINK, 
            ACCENT_ORANGE
        );
        humanModeButton.addActionListener(e -> showHumanModeSetup());
        
        // Layout assembly
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoPanel);
        leftPanel.add(Box.createVerticalStrut(50));
        leftPanel.add(aiModeButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(humanModeButton);
        leftPanel.add(Box.createVerticalGlue());
        
        return leftPanel;
    }
    
    // ==================== LEADERBOARD PANEL ====================
    /**
     * Create the leaderboard panel showing top scores
     * @return Configured leaderboard panel
     */
    private JPanel createLeaderboardPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(15, 23, 42));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(100, 116, 139)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        rightPanel.setPreferredSize(new Dimension(420, 0));
        
        // ==================== HEADER PANEL ====================
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        // Trophy icon
        JLabel trophyIconLabel = new JLabel();
        ImageIcon trophyIcon = IconHelper.getIcon(IconHelper.TROPHY, 48, 48);
        if (trophyIcon != null) {
            trophyIconLabel.setIcon(trophyIcon);
        } else {
            trophyIconLabel.setText("🏆");
            trophyIconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        }
        trophyIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel leaderboardTitle = new JLabel("LEADERBOARD");
        leaderboardTitle.setFont(new Font("Arial", Font.BOLD, 26));
        leaderboardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardTitle.setForeground(new Color(248, 250, 252));
        leaderboardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitle = new JLabel("Top 10 Champions");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitle.setForeground(new Color(148, 163, 184));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(trophyIconLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(leaderboardTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitle);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // ==================== LEADERBOARD LIST ====================
        leaderboardList = new JList<>(leaderboardModel);
        leaderboardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaderboardList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        leaderboardList.setFixedCellHeight(70);
        leaderboardList.setVisibleRowCount(10);
        leaderboardList.setBackground(new Color(30, 41, 59));
        leaderboardList.setForeground(new Color(248, 250, 252));
        leaderboardList.setSelectionBackground(new Color(59, 130, 246));
        leaderboardList.setSelectionForeground(Color.WHITE);
        leaderboardList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Set custom cell renderer for leaderboard entries
        leaderboardList.setCellRenderer(new LeaderboardCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(leaderboardList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 2));
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        
        // ==================== PLAY MAP BUTTON ====================
        // Button to play selected map with game icon
        JButton playMapButton = createGradientButtonWithIcon(
            "PLAY SELECTED MAP",
            IconHelper.GAME,
            ACCENT_GREEN, 
            new Color(74, 222, 128)
        );
        playMapButton.addActionListener(e -> handlePlayStoredMap());
        playMapButton.setMaximumSize(null);
        playMapButton.setPreferredSize(new Dimension(350, 55));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        bottomPanel.add(playMapButton, BorderLayout.CENTER);
        
        rightPanel.add(headerPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Load initial leaderboard data
        updateLeaderboardDisplay();
        
        return rightPanel;
    }
    
    // ==================== LEADERBOARD CELL RENDERER ====================
    /**
     * Custom cell renderer for leaderboard entries
     * Displays rank, medals, player names, and times with styling
     */
    private class LeaderboardCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            
            if (value instanceof ScoreEntry) {
                ScoreEntry entry = (ScoreEntry) value;
                
                // ==================== RANK PANEL WITH MEDAL ICONS ====================
                JPanel rankPanel = new JPanel();
                rankPanel.setOpaque(false);
                rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS));
                
                // Medal icons for top 3 positions
                if (index < 3) {
                    JLabel medalLabel = new JLabel();
                    ImageIcon medalIcon = null;
                    
                    switch (index) {
                        case 0: // Gold
                            medalIcon = IconHelper.getIcon(IconHelper.MEDAL_1, 24, 24);
                            break;
                        case 1: // Silver
                            medalIcon = IconHelper.getIcon(IconHelper.MEDAL_2, 24, 24);
                            break;
                        case 2: // Bronze
                            medalIcon = IconHelper.getIcon(IconHelper.MEDAL_3, 24, 24);
                            break;
                    }
                    
                    if (medalIcon != null) {
                        medalLabel.setIcon(medalIcon);
                    } else {
                        // Fallback to emoji if icon not available
                        String medalEmoji = index == 0 ? "🥇" : index == 1 ? "🥈" : "🥉";
                        medalLabel.setText(medalEmoji);
                        medalLabel.setFont(new Font("Arial", Font.PLAIN, 24));
                    }
                    medalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    rankPanel.add(medalLabel);
                }
                
                // Rank number label
                JLabel rankLabel = new JLabel("#" + (index + 1));
                rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
                rankLabel.setForeground(isSelected ? Color.WHITE : new Color(148, 163, 184));
                rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                rankPanel.add(rankLabel);
                rankPanel.setPreferredSize(new Dimension(50, 50));
                
                // ==================== INFO PANEL ====================
                JPanel infoPanel = new JPanel();
                infoPanel.setOpaque(false);
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                
                // Player name
                JLabel nameLabel = new JLabel(truncate(entry.getPlayerName(), 15));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
                nameLabel.setForeground(isSelected ? Color.WHITE : new Color(248, 250, 252));
                
                // Opponent name
                JLabel vsLabel = new JLabel("vs " + truncate(entry.getOpponent(), 15));
                vsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                vsLabel.setForeground(isSelected ? new Color(226, 232, 240) : new Color(148, 163, 184));
                
                infoPanel.add(nameLabel);
                infoPanel.add(Box.createVerticalStrut(3));
                infoPanel.add(vsLabel);
                
                // ==================== TIME PANEL WITH TIMER ICON ====================
                JPanel timePanel = new JPanel();
                timePanel.setOpaque(false);
                timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
                
                // Time display
                JLabel timeLabel = new JLabel(String.format("%.2fs", entry.getTime()));
                timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
                timeLabel.setForeground(isSelected ? new Color(134, 239, 172) : ACCENT_GREEN);
                timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // Timer icon
                JLabel timeIcon = new JLabel();
                ImageIcon timerIcon = IconHelper.getIcon(IconHelper.TIMER, 16, 16);
                if (timerIcon != null) {
                    timeIcon.setIcon(timerIcon);
                } else {
                    timeIcon.setText("⏱️");
                    timeIcon.setFont(new Font("Arial", Font.PLAIN, 16));
                }
                timeIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                timePanel.add(timeIcon);
                timePanel.add(timeLabel);
                timePanel.setPreferredSize(new Dimension(80, 50));
                
                panel.add(rankPanel, BorderLayout.WEST);
                panel.add(infoPanel, BorderLayout.CENTER);
                panel.add(timePanel, BorderLayout.EAST);
            }
            
            // Alternating row colors
            Color bgColor = isSelected ? new Color(59, 130, 246) : 
                           (index % 2 == 0 ? new Color(30, 41, 59) : new Color(37, 50, 71));
            panel.setBackground(bgColor);
            
            return panel;
        }
    }
    
    // ==================== LEADERBOARD UPDATE ====================
    /**
     * Update the leaderboard display with current scores
     */
    public void updateLeaderboardDisplay() {
        leaderboardModel.clear();
        List<ScoreEntry> entries = leaderboard.getEntries();
        
        for (ScoreEntry entry : entries) {
            leaderboardModel.addElement(entry);
        }
        
        if (leaderboardList != null) {
            leaderboardList.revalidate();
            leaderboardList.repaint();
        }
    }
    
    // ==================== PLAY STORED MAP HANDLER ====================
    /**
     * Handle user request to play a selected map from leaderboard
     */
    private void handlePlayStoredMap() {
        ScoreEntry selectedEntry = leaderboardList.getSelectedValue();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(parentFrame,
                "Please select a map from the leaderboard first!",
                "No Map Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int[][] storedMap = selectedEntry.getMapSnapshot();
        if (storedMap == null) {
            JOptionPane.showMessageDialog(parentFrame,
                "This entry doesn't have a saved map.",
                "Map Not Available",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
     // Call new game mode selection dialog
        showGameModeSelectionForMap(storedMap);
    }
    
    // ==================== GRADIENT BUTTON WITH ICON ====================
    /**
     * Create a gradient button with optional icon
     * @param text Button text
     * @param iconName Icon identifier (can be null)
     * @param color1 Start color for gradient
     * @param color2 End color for gradient
     * @return Configured button
     */
    private JButton createGradientButtonWithIcon(String text, String iconName, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        // Add icon if provided
        if (iconName != null) {
            ImageIcon icon = IconHelper.getIcon(iconName, 28, 28);
            if (icon != null) {
                button.setIcon(icon);
                button.setHorizontalTextPosition(SwingConstants.RIGHT);
                button.setIconTextGap(12);
            }
        }
        
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(450, 70));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 24));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 22));
            }
        });
        
        return button;
    }
    
    /**
     * Create gradient button without icon
     * @param text Button text
     * @param color1 Start color
     * @param color2 End color
     * @return Configured button
     */
    private JButton createGradientButton(String text, Color color1, Color color2) {
        return createGradientButtonWithIcon(text, null, color1, color2);
    }
// ==================== PART 2: SETUP DIALOGS AND HELPER METHODS ====================
    
    // ==================== SETUP METHOD WRAPPERS ====================
    /**
     * Show AI mode setup dialog
     */
    public void showAIModeSetup() {
        showResponsiveSetup(true, null);
    }
    
    /**
     * Show human vs human mode setup dialog
     */
    public void showHumanModeSetup() {
        showResponsiveSetup(false, null);
    }
    
    /**
     * Show AI mode setup dialog for a specific map
     * @param map The maze to load
     */
    private void showAIModeSetupForMap(int[][] map) {
        showResponsiveSetup(true, map);
    }
    
    /**
     * Show human mode setup dialog for a specific map
     * @param map The maze to load
     */
    private void showHumanModeSetupForMap(int[][] map) {
        showResponsiveSetup(false, map);
    }
    
 // ==================== GAME MODE SELECTION FOR MAP ====================
    /**
     * Show game mode selection dialog for playing a stored map
     * @param map The stored map to play
     */
    private void showGameModeSelectionForMap(int[][] map) {
        JDialog dialog = new JDialog(parentFrame, "Select Game Mode", true);
        dialog.setUndecorated(true);
        
        int dialogWidth = 500;
        int dialogHeight = 400;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Title
        JLabel titleLabel = new JLabel("Choose Game Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("How do you want to play this map?");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons
        JButton aiModeButton = createGradientButtonWithIcon(
            "HUMAN VS AI", 
            IconHelper.ROBOT, 
            ACCENT_BLUE, 
            ACCENT_PURPLE
        );
        aiModeButton.setMaximumSize(new Dimension(350, 70));
        aiModeButton.addActionListener(e -> {
            dialog.dispose();
            showAIModeSetupForMap(map);
        });
        
        JButton humanModeButton = createGradientButtonWithIcon(
            "HUMAN VS HUMAN", 
            IconHelper.VS, 
            ACCENT_PINK, 
            ACCENT_ORANGE
        );
        humanModeButton.setMaximumSize(new Dimension(350, 70));
        humanModeButton.addActionListener(e -> {
            dialog.dispose();
            showHumanModeSetupForMap(map);
        });
        
        JButton cancelButton = createDialogButtonWithIcon(
            "CANCEL", 
            IconHelper.CLOSE, 
            ACCENT_RED, 
            new Color(248, 113, 113)
        );
        cancelButton.setPreferredSize(new Dimension(150, 50));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Layout
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(aiModeButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(humanModeButton);
        contentPanel.add(Box.createVerticalStrut(30));
        
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelPanel.setOpaque(false);
        cancelPanel.add(cancelButton);
        contentPanel.add(cancelPanel);
        contentPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // ==================== MAIN SETUP DIALOG ====================
    /**
     * Create and show the game setup dialog
     * @param isAIMode True for AI mode, false for 2-player mode
     * @param map Optional pre-loaded map (null for new game)
     */
    private void showResponsiveSetup(boolean isAIMode, int[][] map) {
        JDialog dialog = new JDialog(parentFrame, isAIMode ? "Human vs AI Setup" : "Human vs Human Setup", true);
        dialog.setUndecorated(true);
        
        int dialogWidth = 700;
        int dialogHeight = 720;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);
        
        // ==================== CONTENT PANEL ====================
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // ==================== TITLE WITH ICON ====================
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        // Mode icon (robot for AI, VS for human)
        JLabel titleIcon = new JLabel();
        ImageIcon modeIcon = IconHelper.getIcon(isAIMode ? IconHelper.ROBOT : IconHelper.VS, 36, 36);
        if (modeIcon != null) {
            titleIcon.setIcon(modeIcon);
        } else {
            titleIcon.setText(isAIMode ? "🤖" : "👥");
            titleIcon.setFont(new Font("Arial", Font.PLAIN, 36));
        }
        
        JLabel titleLabel = new JLabel(isAIMode ? " HUMAN VS AI" : " HUMAN VS HUMAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(TEXT_DARK);
        
        titlePanel.add(titleIcon);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Customize Your Players");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ==================== FORM PANEL ====================
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 1 configuration
        JTextField player1NameField = createModernTextField(lastPlayer1Name);
        ColorSelectorPanel player1ColorPanel = new ColorSelectorPanel(getColorIndex(lastPlayer1Color));
        
        JPanel player1Panel = createPlayerPanel("PLAYER 1" + (isAIMode ? "" : " (WASD)"), 
                                                player1NameField, player1ColorPanel, ACCENT_BLUE);
        
        // Player 2 / AI configuration
        JTextField player2NameField = null;
        ColorSelectorPanel player2ColorPanel = new ColorSelectorPanel(getColorIndex(lastPlayer2Color));
        JPanel player2Panel;
        
        if (!isAIMode) {
            // Human player 2
            player2NameField = createModernTextField(lastPlayer2Name);
            player2Panel = createPlayerPanel("PLAYER 2 (ARROW KEYS)", 
                                            player2NameField, player2ColorPanel, ACCENT_PINK);
        } else {
            // AI opponent
            JLabel aiLabel = new JLabel("AI");
            aiLabel.setFont(new Font("Arial", Font.BOLD, 24));
            aiLabel.setForeground(TEXT_GRAY);
            player2Panel = createPlayerPanel("AI OPPONENT", 
                                            aiLabel, player2ColorPanel, ACCENT_RED);
        }
        
        formPanel.add(player1Panel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(player2Panel);
        
        // ==================== BUTTONS WITH ICONS ====================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(650, 90));
        
        // Start button with check icon
        JButton startButton = createDialogButtonWithIcon("START GAME", IconHelper.CHECK, ACCENT_GREEN, new Color(74, 222, 128));
        
        // Cancel button with close icon
        JButton cancelButton = createDialogButtonWithIcon("CANCEL", IconHelper.CLOSE, ACCENT_RED, new Color(248, 113, 113));
        
        startButton.setPreferredSize(new Dimension(220, 60));
        cancelButton.setPreferredSize(new Dimension(220, 60));
        
        final JTextField finalPlayer2NameField = player2NameField;
        
        // Start button action
        startButton.addActionListener(e -> {
            String p1Name = player1NameField.getText().trim();
            if (p1Name.isEmpty() || p1Name.equals("Player 1")) p1Name = lastPlayer1Name;
            
            Color p1Color = player1ColorPanel.getSelectedColor();
            Color p2Color = player2ColorPanel.getSelectedColor();
            
            if (isAIMode) {
                savePlayerInfo(p1Name, p1Color, "AI", p2Color, true);
                if (map == null) {
                    callback.onAIModeSelected(p1Name, p1Color, p2Color);
                } else {
                    callback.onPlayStoredMap(map, true, p1Name, p1Color, "AI", p2Color);
                }
            } else {
                String p2Name = finalPlayer2NameField.getText().trim();
                if (p2Name.isEmpty() || p2Name.equals("Player 2")) p2Name = lastPlayer2Name;
                
                savePlayerInfo(p1Name, p1Color, p2Name, p2Color, false);
                if (map == null) {
                    callback.onHumanModeSelected(p1Name, p1Color, p2Name, p2Color);
                } else {
                    callback.onPlayStoredMap(map, false, p1Name, p1Color, p2Name, p2Color);
                }
            }
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        
        // ==================== LAYOUT ASSEMBLY ====================
        JPanel titleContainer = new JPanel();
        titleContainer.setOpaque(false);
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleContainer.add(titlePanel);
        titleContainer.add(Box.createVerticalStrut(8));
        titleContainer.add(subtitleLabel);
        
        contentPanel.add(titleContainer);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(35));
        contentPanel.add(buttonPanel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // ==================== PLAYER PANEL CREATION ====================
    /**
     * Create a player configuration panel with name and color selection
     * @param title Panel title
     * @param inputComponent Name input component (TextField or Label)
     * @param colorPanel Color selector panel
     * @param accentColor Border accent color
     * @return Configured player panel
     */
    private JPanel createPlayerPanel(String title, Component inputComponent, 
                                     ColorSelectorPanel colorPanel, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 3, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        panel.setMaximumSize(new Dimension(550, 180));
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(accentColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Name label
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nameLabel.setForeground(TEXT_GRAY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Configure input component
        if (inputComponent instanceof JTextField) {
            ((JTextField) inputComponent).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        inputComponent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Color label
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        colorLabel.setForeground(TEXT_GRAY);
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Assemble panel
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(inputComponent);
        panel.add(Box.createVerticalStrut(12));
        panel.add(colorLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(colorPanel);
        
        return panel;
    }
    
    // ==================== COLOR SELECTOR PANEL ====================
    /**
     * Panel for selecting player color from available options
     */
    class ColorSelectorPanel extends JPanel {
        private int selectedIndex;              // Currently selected color index
        private JToggleButton[] colorButtons;   // Color option buttons
        
        /**
         * Constructor - Create color selector with default selection
         * @param defaultIndex Index of initially selected color
         */
        public ColorSelectorPanel(int defaultIndex) {
            this.selectedIndex = defaultIndex;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
            
            ButtonGroup group = new ButtonGroup();
            colorButtons = new JToggleButton[PLAYER_COLORS.length];
            
            // Create button for each color option
            for (int i = 0; i < PLAYER_COLORS.length; i++) {
                final int index = i;
                JToggleButton btn = new JToggleButton();
                btn.setPreferredSize(new Dimension(45, 45));
                btn.setBackground(PLAYER_COLORS[i]);
                btn.setOpaque(true);
                btn.setBorderPainted(true);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(i == defaultIndex ? TEXT_DARK : new Color(203, 213, 225), 3),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                
                // Update selection on click
                btn.addActionListener(e -> {
                    selectedIndex = index;
                    updateBorders();
                });
                
                if (i == defaultIndex) {
                    btn.setSelected(true);
                }
                
                group.add(btn);
                colorButtons[i] = btn;
                add(btn);
            }
        }
        
        /**
         * Update button borders to show current selection
         */
        private void updateBorders() {
            for (int i = 0; i < colorButtons.length; i++) {
                colorButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(i == selectedIndex ? TEXT_DARK : new Color(203, 213, 225), 3),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }
        
        /**
         * Get the currently selected color
         * @return Selected color
         */
        public Color getSelectedColor() {
            return PLAYER_COLORS[selectedIndex];
        }
    }
    
    // ==================== GAME END DIALOG ====================
    /**
     * Show game end dialog with results and options
     * @param player1Name Name of player 1
     * @param player1Time Time of player 1
     * @param player2Name Name of player 2/AI
     * @param player2Time Time of player 2/AI
     * @return User's choice (0=same map, 1=new map, 2=main menu)
     */
    public int showGameEndDialog(String player1Name, double player1Time, 
                                  String player2Name, double player2Time) {
        JDialog dialog = new JDialog(parentFrame, "Game Over", true);
        dialog.setUndecorated(true);
        
        int dialogWidth = 650;
        int dialogHeight = 600;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 247, 255));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        
        // ==================== WINNER DISPLAY WITH TROPHY ICON ====================
        String winner;
        Color winnerColor;
        if (player1Time < player2Time) {
            winner = player1Name.toUpperCase() + " WINS!";
            winnerColor = ACCENT_GREEN;
        } else if (player2Time < player1Time) {
            winner = player2Name.toUpperCase() + " WINS!";
            winnerColor = ACCENT_GREEN;
        } else {
            winner = "IT'S A TIE!";
            winnerColor = ACCENT_ORANGE;
        }
        
        // Trophy icon
        JLabel trophyLabel = new JLabel();
        ImageIcon trophyIcon = IconHelper.getIcon(IconHelper.TROPHY, 70, 70);
        if (trophyIcon != null) {
            trophyLabel.setIcon(trophyIcon);
        } else {
            trophyLabel.setText("🏆");
            trophyLabel.setFont(new Font("Arial", Font.PLAIN, 70));
        }
        trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel winnerLabel = new JLabel(winner);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 42));
        winnerLabel.setForeground(winnerColor);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ==================== SCORES DISPLAY ====================
        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setOpaque(false);
        scoresPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        JLabel score1 = createScoreLabel(player1Name, player1Time, player1Time <= player2Time, 26);
        JLabel score2 = createScoreLabel(player2Name, player2Time, player2Time <= player1Time, 26);
        
        scoresPanel.add(score1);
        scoresPanel.add(Box.createVerticalStrut(15));
        scoresPanel.add(score2);
        
        // ==================== ACTION BUTTONS WITH ICONS ====================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // First row - Same Map and New Map buttons
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(600, 70));
        
        // Same map button with restart icon
        JButton sameMapBtn = createDialogButtonWithIcon("SAME MAP", IconHelper.RESTART, ACCENT_BLUE, new Color(96, 165, 250));
        // New map button with game icon
        JButton newMapBtn = createDialogButtonWithIcon("NEW MAP", IconHelper.GAME, ACCENT_PURPLE, new Color(167, 139, 250));
        
        sameMapBtn.setPreferredSize(new Dimension(200, 60));
        newMapBtn.setPreferredSize(new Dimension(200, 60));
        
        row1.add(sameMapBtn);
        row1.add(newMapBtn);
        
        // Second row - Main Menu button
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(600, 70));
        
        // Main menu button with home icon
        JButton mainMenuBtn = createDialogButtonWithIcon("MAIN MENU", IconHelper.HOME, ACCENT_RED, new Color(248, 113, 113));
        mainMenuBtn.setPreferredSize(new Dimension(200, 60));
        
        row2.add(mainMenuBtn);
        
        final int[] choice = {2}; // Default to main menu
        
        // Button actions
        sameMapBtn.addActionListener(e -> {
            choice[0] = 0;
            dialog.dispose();
        });
        newMapBtn.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });
        mainMenuBtn.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });
        
        buttonPanel.add(row1);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(row2);
        
        // ==================== LAYOUT ASSEMBLY ====================
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(trophyLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(winnerLabel);
        contentPanel.add(scoresPanel);
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
        
        return choice[0];
    }
    
    // ==================== DIALOG BUTTON WITH ICON ====================
    /**
     * Create a dialog button with gradient background and optional icon
     * @param text Button text
     * @param iconName Icon identifier (can be null)
     * @param color1 Start color for gradient
     * @param color2 End color for gradient
     * @return Configured button
     */
    private JButton createDialogButtonWithIcon(String text, String iconName, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        
        // Add icon if provided
        if (iconName != null) {
            ImageIcon icon = IconHelper.getIcon(iconName, 24, 24);
            if (icon != null) {
                button.setIcon(icon);
                button.setHorizontalTextPosition(SwingConstants.RIGHT);
                button.setIconTextGap(8);
            }
        }
        
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(400, 65));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    // ==================== HELPER METHODS ====================
    /**
     * Create a modern styled text field
     * @param placeholder Placeholder text
     * @return Configured text field
     */
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(241, 245, 249));
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setText(placeholder);
        field.setForeground(TEXT_DARK);
        
        // Select all text on focus
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.selectAll();
            }
        });
        
        return field;
    }
    
    /**
     * Create a score display label
     * @param name Player name
     * @param time Player time
     * @param isWinner Whether this player won
     * @param fontSize Font size for the label
     * @return Configured label
     */
    private JLabel createScoreLabel(String name, double time, boolean isWinner, int fontSize) {
        String text = String.format("%s: %.2fs", name, time);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(isWinner ? ACCENT_GREEN : TEXT_GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    /**
     * Show a simple message dialog
     * @param title Dialog title
     * @param message Message text
     */
    private void showModernMessage(String title, String message) {
        JOptionPane.showMessageDialog(parentFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Truncate a string to maximum length with ellipsis
     * @param str String to truncate
     * @param maxLen Maximum length
     * @return Truncated string
     */
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() > maxLen) {
            return str.substring(0, maxLen - 2) + "..";
        }
        return str;
    }
    
    /**
     * Find the index of a color in PLAYER_COLORS array
     * @param color Color to find
     * @return Index of color, or 0 if not found
     */
    private int getColorIndex(Color color) {
        for (int i = 0; i < PLAYER_COLORS.length; i++) {
            if (PLAYER_COLORS[i].equals(color)) {
                return i;
            }
        }
        return 0;
    }
}
