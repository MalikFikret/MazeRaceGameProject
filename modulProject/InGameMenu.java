package modulProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * In-Game Menu class - Pause menu dialog shown during gameplay
 * Provides options to resume, restart, or return to main menu
 * Responsive design that adapts to different screen sizes
 */
public class InGameMenu extends JDialog {
    
    // ==================== MENU ACTION ENUM ====================
    /**
     * Enum representing possible menu actions
     */
    public enum MenuAction {
        RESUME,      // Continue playing
        RESTART,     // Restart with same map
        MAIN_MENU    // Return to main menu
    }
    
    // ==================== CALLBACK INTERFACE ====================
    /**
     * Callback interface for handling menu selections
     */
    public interface MenuCallback {
        void onMenuAction(MenuAction action);
    }
    
    private MenuCallback callback;
    private MenuAction selectedAction = MenuAction.RESUME;
    
    // ==================== COLOR PALETTE ====================
    private static final Color BG_OVERLAY = new Color(250, 247, 255, 235);  // Semi-transparent overlay
    private static final Color ACCENT_GREEN = new Color(134, 239, 172);     // Resume button color
    private static final Color ACCENT_BLUE = new Color(147, 197, 253);      // Restart button color
    private static final Color ACCENT_RED = new Color(252, 165, 165);       // Main menu button color
    private static final Color TEXT_DARK = new Color(51, 65, 85);           // Dark text
    private static final Color TEXT_GRAY = new Color(100, 116, 139);        // Gray text
    
    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor - Create in-game pause menu
     * @param parent Parent frame
     * @param callback Callback for menu actions
     */
    public InGameMenu(JFrame parent, MenuCallback callback) {
        super(parent, "Game Paused", true);
        this.callback = callback;
        
        // Remove window decorations for custom look
        setUndecorated(true);
        setBackground(BG_OVERLAY);
        
        // Responsive sizing based on screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) Math.min(500, screenSize.width * 0.6);
        int height = (int) Math.min(500, screenSize.height * 0.7);
        setSize(width, height);
        
        createUI(width, height);
        setLocationRelativeTo(parent);
        
        // ESC key to resume
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    selectedAction = MenuAction.RESUME;
                    dispose();
                    if (callback != null) {
                        callback.onMenuAction(MenuAction.RESUME);
                    }
                }
            }
        });
        
        setFocusable(true);
    }
    
    // ==================== UI CREATION ====================
    /**
     * Create the menu UI with responsive layout
     * @param width Dialog width
     * @param height Dialog height
     */
    private void createUI(int width, int height) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        
        // Responsive padding
        int padding = Math.max(40, width / 10);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        // Calculate responsive sizes
        int iconSize = Math.max(50, Math.min(70, width / 8));
        int titleSize = Math.max(32, Math.min(44, width / 12));
        int subtitleSize = Math.max(13, Math.min(16, width / 30));
        
        // Pause icon
        JLabel pauseIcon = IconHelper.getIconLabel(IconHelper.PAUSE, iconSize, iconSize);
        pauseIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("GAME PAUSED");
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleSize));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Take a break, champion!");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, subtitleSize));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons with responsive height
        int btnHeight = Math.max(50, Math.min(65, height / 8));
        
        JButton resumeButton = createGradientMenuButton(IconHelper.PLAY, "RESUME", 
                                                       ACCENT_GREEN, new Color(74, 222, 128), btnHeight);
        JButton restartButton = createGradientMenuButton(IconHelper.RESTART, "RESTART", 
                                                        ACCENT_BLUE, new Color(96, 165, 250), btnHeight);
        JButton mainMenuButton = createGradientMenuButton(IconHelper.HOME, "MAIN MENU", 
                                                         ACCENT_RED, new Color(248, 113, 113), btnHeight);
        
        // Button actions
        resumeButton.addActionListener(e -> handleAction(MenuAction.RESUME));
        restartButton.addActionListener(e -> handleAction(MenuAction.RESTART));
        mainMenuButton.addActionListener(e -> handleAction(MenuAction.MAIN_MENU));
        
        // ESC key hint
        JLabel escLabel = new JLabel("Press ESC to resume");
        escLabel.setFont(new Font("Arial", Font.ITALIC, subtitleSize - 1));
        escLabel.setForeground(TEXT_GRAY);
        escLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Layout assembly
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(pauseIcon);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(resumeButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(restartButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(mainMenuButton);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(escLabel);
        mainPanel.add(Box.createVerticalGlue());
        
        setContentPane(mainPanel);
    }
    
    // ==================== BUTTON CREATION ====================
    /**
     * Create a gradient menu button with icon
     * @param iconName Icon filename
     * @param text Button text
     * @param color1 Start gradient color
     * @param color2 End gradient color
     * @param height Button height
     * @return Configured button with gradient background
     */
    private JButton createGradientMenuButton(String iconName, String text, 
                                            Color color1, Color color2, int height) {
        ImageIcon icon = IconHelper.getIcon(iconName, 24, 24);
        
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
        
        // Add icon if available
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
        }
        
        // Responsive font size
        int fontSize = Math.max(16, Math.min(22, height / 3));
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(300, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect - slight font size increase
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, fontSize + 1));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, fontSize));
            }
        });
        
        return button;
    }
    
    // ==================== ACTION HANDLING ====================
    /**
     * Handle menu action selection
     * @param action Selected action
     */
    private void handleAction(MenuAction action) {
        selectedAction = action;
        dispose();
        if (callback != null) {
            callback.onMenuAction(action);
        }
    }
    
    /**
     * Get the selected menu action
     * @return Selected action
     */
    public MenuAction getSelectedAction() {
        return selectedAction;
    }
    
    // ==================== STATIC SHOW METHOD ====================
    /**
     * Static method to show menu and get result
     * @param parent Parent frame
     * @param callback Callback for menu actions
     * @return Selected menu action
     */
    public static MenuAction show(JFrame parent, MenuCallback callback) {
        InGameMenu menu = new InGameMenu(parent, callback);
        menu.setVisible(true);
        return menu.getSelectedAction();
    }
}