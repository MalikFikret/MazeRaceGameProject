package modulProject;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Icon Helper class - Utility for loading and managing game icons
 * Provides centralized icon loading with fallback support
 * Handles scaling and error cases gracefully
 */
public class IconHelper {
    // ==================== ICON TYPE CONSTANTS ====================
    public static final String SETTINGS = "setting.png";         // Settings gear icon
    public static final String TIMER = "stopwatch.png";          // Timer/stopwatch icon
    public static final String PAUSE = "pause.png";              // Pause button icon
    public static final String PLAY = "play-button.png";         // Play button icon
    public static final String HOME = "home.png";                // Home/menu icon
    public static final String RESTART = "restart.png";          // Restart/refresh icon
    public static final String CLOSE = "close.png";              // Close/exit icon
    public static final String TROPHY = "trophy.png";            // Trophy/winner icon
    public static final String MEDAL_1 = "medal(1).png";         // Gold medal (1st place)
    public static final String MEDAL_2 = "medal(2).png";         // Silver medal (2nd place)
    public static final String MEDAL_3 = "medal(3).png";         // Bronze medal (3rd place)
    public static final String VS = "vs.png";                    // Versus icon
    public static final String GAME = "game.png";                // Game controller icon
    public static final String LABYRINTH = "labyrinth.png";      // Maze/labyrinth icon
    public static final String CHECK = "check.png";              // Checkmark icon
    public static final String ROBOT = "robot.png";              // Robot/AI icon
    
    /**
     * Load an icon from classpath with specified dimensions
     * Automatically scales the icon and handles loading errors
     * 
     * @param iconName Icon filename (use constants above)
     * @param width Desired width in pixels
     * @param height Desired height in pixels
     * @return Scaled ImageIcon, or null if loading fails
     */
    public static ImageIcon getIcon(String iconName, int width, int height) {
        try {
            // Construct resource path (adjust path to match your project structure)
            String resourcePath = "/modulProject/icons/" + iconName;
            URL iconURL = IconHelper.class.getResource(resourcePath);
            
            if (iconURL != null) {
                // Icon found - load and scale it
                ImageIcon icon = new ImageIcon(iconURL);
                Image scaledImage = icon.getImage().getScaledInstance(
                    width, height, Image.SCALE_SMOOTH
                );
                return new ImageIcon(scaledImage);
            } else {
                // Icon not found at expected path
                return null;
            }
            
        } catch (Exception e) {
            // Error during loading - print for debugging (only for critical errors)
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a JLabel with icon and centered alignment
     * Used in InGameMenu and other UI components
     * 
     * @param iconName Icon filename
     * @param iconSize Icon size (used for both width and height)
     * @param iconWidth Icon width (if different from height)
     * @return Configured JLabel with icon or fallback text
     */
    public static JLabel getIconLabel(String iconName, int iconSize, int iconWidth) {
        JLabel label = new JLabel();
        ImageIcon icon = getIcon(iconName, iconWidth, iconSize);
        
        if (icon != null) {
            label.setIcon(icon);
        } else {
            // Icon couldn't be loaded - show fallback text
            label.setText("▸");
            label.setFont(new Font("Arial", Font.PLAIN, iconSize));
        }
        
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    /**
     * Create a styled button with text and icon
     * 
     * @param text Button text
     * @param iconName Icon filename
     * @param bgColor Background color
     * @param textColor Text color
     * @param iconSize Icon size in pixels
     * @return Configured JButton with hover effects
     */
    public static JButton createStyledButton(String text, String iconName, 
                                             Color bgColor, Color textColor, int iconSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add icon if provided and available
        if (iconName != null && !iconName.isEmpty()) {
            ImageIcon icon = getIcon(iconName, iconSize, iconSize);
            if (icon != null) {
                button.setIcon(icon);
                button.setHorizontalTextPosition(SwingConstants.RIGHT);
                button.setIconTextGap(10);
            }
        }
        
        // Hover effect - brighten background on mouse enter
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create an icon-only button (no text)
     * 
     * @param iconName Icon filename
     * @param iconSize Icon size in pixels
     * @return Configured JButton with icon or fallback text
     */
    public static JButton createIconOnlyButton(String iconName, int iconSize) {
        JButton button = new JButton();
        ImageIcon icon = getIcon(iconName, iconSize, iconSize);
        
        if (icon != null) {
            button.setIcon(icon);
        } else {
            // Icon couldn't be loaded - show fallback emoji/text
            String fallbackText = getFallbackText(iconName);
            button.setText(fallbackText);
            button.setFont(new Font("Arial", Font.PLAIN, iconSize));
        }
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Get fallback emoji/text when icon cannot be loaded
     * Provides visual feedback even when icon files are missing
     * 
     * @param iconName Icon filename
     * @return Emoji or symbol representing the icon
     */
    private static String getFallbackText(String iconName) {
        if (iconName == null) return "?";
        
        // Map icon names to emoji fallbacks
        switch (iconName) {
            case SETTINGS: return "⚙";
            case TIMER: return "⏱";
            case PAUSE: return "⏸";
            case PLAY: return "▶";
            case HOME: return "🏠";
            case RESTART: return "🔄";
            case CLOSE: return "✕";
            case TROPHY: return "🏆";
            case VS: return "⚔";
            case GAME: return "🎮";
            case LABYRINTH: return "🌀";
            case CHECK: return "✓";
            default: return "◉";
        }
    }
}