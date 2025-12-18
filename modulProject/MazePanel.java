package modulProject;

import javax.swing.*;
import java.awt.*;

/**
 * Maze Panel class - Custom panel for rendering the maze and player
 * Features modern pastel color scheme with 3D effects
 * Responsive sizing and smooth graphics rendering
 */

public class MazePanel extends JPanel {
    private int[][] maze;              // Maze structure (0=path, 1=wall)
    private Point playerPosition;      // Current player position
    private Point exitPosition;        // Exit/goal position
    private Color playerColor;         // Player's color
    private int mazeWidth;             // Maze width in cells
    private int mazeHeight;            // Maze height in cells
    
    // ==================== PASTEL COLOR PALETTE ====================
    // Modern, soft colors for a pleasant visual experience
    private static final Color BG_COLOR = new Color(250, 247, 255);        // Very light lavender
    private static final Color WALL_COLOR = new Color(70, 65, 60);         // Warm dark gray
    private static final Color WALL_SHADOW = new Color(226, 232, 240);     // Very light shadow
    private static final Color EXIT_COLOR = new Color(167, 243, 208);      // Mint green
    private static final Color EXIT_GLOW = new Color(80, 190, 150, 120);   // Green glow
    
    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor - Initialize maze panel with dimensions
     * @param mazeWidth Width of maze in cells
     * @param mazeHeight Height of maze in cells
     */
    public MazePanel(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        setBackground(BG_COLOR);
        setMinimumSize(new Dimension(300, 300));
    }
    
    // ==================== SETTERS ====================
    /**
     * Set the maze structure
     * @param maze 2D array (0=path, 1=wall)
     */
    public void setMaze(int[][] maze) {
        this.maze = maze;
    }
    
    /**
     * Set player position
     * @param position Player's current position
     */
    public void setPlayerPosition(Point position) {
        this.playerPosition = position;
    }
    
    /**
     * Set exit position
     * @param position Exit/goal position
     */
    public void setExitPosition(Point position) {
        this.exitPosition = position;
    }
    
    /**
     * Set player color
     * @param color Color for player rendering
     */
    public void setPlayerColor(Color color) {
        this.playerColor = color;
    }
    
    // ==================== RENDERING ====================
    /**
     * Custom paint method - Renders maze, exit portal, and player
     * Features 3D wall effects, glowing portal, and smooth player rendering
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (maze == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        // Enable antialiasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // ==================== RESPONSIVE CELL SIZE ====================
        // Calculate cell size based on panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int cellWidth = panelWidth / mazeWidth;
        int cellHeight = panelHeight / mazeHeight;
        int cellSize = Math.min(cellWidth, cellHeight);
        
        // Center the maze in the panel
        int offsetX = (panelWidth - (cellSize * mazeWidth)) / 2;
        int offsetY = (panelHeight - (cellSize * mazeHeight)) / 2;
        
        // ==================== RENDER WALLS WITH 3D EFFECT ====================
        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                if (maze[i][j] == 1) {
                    int x = offsetX + j * cellSize;
                    int y = offsetY + i * cellSize;
                    
                    // Shadow (bottom-right offset)
                    g2d.setColor(WALL_SHADOW);
                    g2d.fillRoundRect(x + 2, y + 2, cellSize - 1, cellSize - 1, 8, 8);
                    
                    // Main wall body
                    g2d.setColor(WALL_COLOR);
                    g2d.fillRoundRect(x, y, cellSize - 2, cellSize - 2, 8, 8);
                    
                    // Top highlight (light reflection)
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.fillRoundRect(x + 2, y + 2, cellSize - 6, cellSize / 3, 6, 6);
                }
            }
        }
        
        // ==================== RENDER EXIT PORTAL ====================
        // Purple portal with concentric rings fading inward
        if (exitPosition != null) {
            int exitX = offsetX + exitPosition.x * cellSize;
            int exitY = offsetY + exitPosition.y * cellSize;
            int exitSize = Math.max(cellSize - 8, 16);
            int exitPadding = (cellSize - exitSize) / 2;
            int centerX = exitX + cellSize / 2;
            int centerY = exitY + cellSize / 2;
            
            // Outer purple glow (soft)
            for (int i = 3; i > 0; i--) {
                g2d.setColor(new Color(167, 139, 250, 20 * i));
                g2d.fillOval(centerX - (exitSize/2 + i*3), centerY - (exitSize/2 + i*3), 
                           exitSize + i*6, exitSize + i*6);
            }
            
            // Portal rings (shrinking inward)
            int numRings = 5;
            for (int i = numRings; i > 0; i--) {
                float ratio = (float)i / numRings;
                int ringSize = (int)(exitSize * ratio);
                int alpha = (int)(100 + 100 * (1 - ratio)); // Inner rings darker
                
                // Ring color (purple fading inward)
                g2d.setColor(new Color(196, 181, 253, alpha));
                g2d.fillOval(centerX - ringSize/2, centerY - ringSize/2, ringSize, ringSize);
                
                // Ring border
                g2d.setColor(new Color(167, 139, 250, alpha + 50));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(centerX - ringSize/2, centerY - ringSize/2, ringSize, ringSize);
            }
            
            // Center black hole
            int coreSize = exitSize / 6;
            g2d.setColor(new Color(88, 28, 135)); // Very dark purple
            g2d.fillOval(centerX - coreSize/2, centerY - coreSize/2, coreSize, coreSize);
            
            // Center sparkle (subtle)
            g2d.setColor(new Color(255, 255, 255, 150));
            int sparkleSize = coreSize / 2;
            g2d.fillOval(centerX - sparkleSize/2, centerY - sparkleSize/2, sparkleSize, sparkleSize);
        }
        
        // ==================== RENDER PLAYER ====================
        // Smooth sphere with glow, shadow, and highlight
        if (playerPosition != null && playerColor != null) {
            int playerX = offsetX + playerPosition.x * cellSize;
            int playerY = offsetY + playerPosition.y * cellSize;
            int playerSize = Math.max(cellSize - 10, 14);
            int playerPadding = (cellSize - playerSize) / 2;
            
            // Outer glow (subtle)
            g2d.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), 
                                   playerColor.getBlue(), 20));
            g2d.fillOval(playerX + playerPadding - 4, playerY + playerPadding - 4, 
                        playerSize + 8, playerSize + 8);
            
            // Shadow (soft)
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(playerX + playerPadding + 1, playerY + playerPadding + playerSize/3, 
                        playerSize - 2, playerSize/2);
            
            // Base tone (3D depth)
            g2d.setColor(new Color(Math.max(playerColor.getRed() - 30, 0), 
                                   Math.max(playerColor.getGreen() - 30, 0), 
                                   Math.max(playerColor.getBlue() - 30, 0), 100));
            g2d.fillOval(playerX + playerPadding + 2, playerY + playerPadding + 3, 
                        playerSize - 4, playerSize - 4);
            
            // Main player body
            g2d.setColor(playerColor);
            g2d.fillOval(playerX + playerPadding, playerY + playerPadding, 
                        playerSize, playerSize);
            
            // Top highlight (light reflection)
            g2d.setColor(new Color(255, 255, 255, 140));
            int highlightSize = Math.max(playerSize / 4, 4);
            g2d.fillOval(playerX + playerPadding + playerSize / 4, 
                        playerY + playerPadding + playerSize / 5,
                        highlightSize, highlightSize);
            
            // Thin outer edge (subtle)
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(playerX + playerPadding + 1, playerY + playerPadding + 1, 
                        playerSize - 2, playerSize - 2);
        }
    }
    
    // ==================== PREFERRED SIZE ====================
    /**
     * Calculate preferred size based on screen dimensions
     * @return Preferred dimension for the panel
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = Math.min(screenSize.width / 2 - 50, 600);
        int maxHeight = Math.min(screenSize.height - 200, 600);
        
        return new Dimension(maxWidth, maxHeight);
    }
}