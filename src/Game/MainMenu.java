
package Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Sounds.SoundManager;

public class MainMenu extends JPanel {

    private Image background;
    private Image logo;

    private JButton playButton;
    private JButton instructionsButton;
    private JButton performanceButton;
    private JButton exitButton;
    private JButton musicButton;

    // Colors
    private static final Color BUTTON_COLOR =
            new Color(49, 91, 54);

    private static final Color HOVER_COLOR =
            new Color(65, 112, 70);

    private static final Color SHADOW_COLOR =
            new Color(25, 45, 27, 150);

    private static final Color BUTTON_TEXT =
            new Color(255, 244, 210);

    private static final Color HOVER_TEXT =
            new Color(255, 220, 120);

    private static final Color GOLD =
            new Color(222, 180, 92);

    private static final Color DARK_GREEN =
            new Color(19, 35, 27);

    private static final Color PANEL_GREEN =
            new Color(35, 65, 43);

    // Button settings
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_HEIGHT = 48;
    private static final int BUTTON_GAP = 13;

    // Original layout values
    private static final int ORIGINAL_LOGO_WIDTH = 380;
    private static final int ORIGINAL_LOGO_HEIGHT = 190;
    private static final int ORIGINAL_LOGO_GAP = 25;

    // Updated logo settings
    private static final int MAX_LOGO_WIDTH = 430;
    private static final int LOGO_UP_OFFSET = 15;

    // Music button settings
    private static final int MUSIC_BUTTON_WIDTH = 125;
    private static final int MUSIC_BUTTON_HEIGHT = 38;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public MainMenu(
            Runnable onPlay,
            Runnable onInstructions,
            Runnable onPerformance
    ) {

        setLayout(null);

        background = new ImageIcon(
                getClass().getResource(
                        "/Assets/Background_Wallpaper.png"
                )
        ).getImage();

        logo = new ImageIcon(
                getClass().getResource(
                        "/Assets/Game_Logo.png"
                )
        ).getImage();

        playButton = createButton("PLAY");
        instructionsButton = createButton("INSTRUCTIONS");
        performanceButton = createButton("PERFORMANCE");
        exitButton = createButton("EXIT");

        // MUSIC BUTTON
        musicButton = createButton("");
        updateMusicButton();

        musicButton.addActionListener(e -> {
            SoundManager.toggleMusic();
            updateMusicButton();
        });

        // PLAY
        playButton.addActionListener(
                e -> onPlay.run()
        );

        // INSTRUCTIONS
        instructionsButton.addActionListener(
                e -> onInstructions.run()
        );

        // PERFORMANCE
        performanceButton.addActionListener(
                e -> onPerformance.run()
        );

        // EXIT
        exitButton.addActionListener(e -> showExitConfirmation());

        add(playButton);
        add(instructionsButton);
        add(performanceButton);
        add(exitButton);
        add(musicButton);
    }

    // ==========================================
    // EXIT CONFIRMATION DIALOG
    // ==========================================

    private void showExitConfirmation() {

        Window owner = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
                owner,
                "MUSHROOM MAZE",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setUndecorated(true);
        dialog.setSize(430, 220);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(DARK_GREEN);

        mainPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD, 3),
                        new EmptyBorder(20, 25, 20, 25)
                )
        );

        // TITLE
        JLabel title = new JLabel(
                "MUSHROOM MAZE",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial Rounded MT Bold", Font.BOLD, 21)
        );

        title.setForeground(GOLD);

        // MESSAGE
        JLabel message = new JLabel(
                "Are you sure you want to exit the game?",
                SwingConstants.CENTER
        );

        message.setFont(
                new Font("Arial Rounded MT Bold", Font.PLAIN, 14)
        );

        message.setForeground(BUTTON_TEXT);

        // CENTER CONTENT
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        centerPanel.setOpaque(false);

        centerPanel.add(title);
        centerPanel.add(message);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 18, 0)
        );

        buttonPanel.setOpaque(false);

        RoundedButton cancelButton = new RoundedButton("CANCEL");
        RoundedButton confirmButton = new RoundedButton("EXIT");

        cancelButton.setPreferredSize(new Dimension(135, 42));
        confirmButton.setPreferredSize(new Dimension(135, 42));

        cancelButton.addActionListener(e -> {
            SoundManager.playClick();
            dialog.dispose();
        });

        confirmButton.addActionListener(e -> {
            SoundManager.playClick();
            dialog.dispose();

            // Allow the click sound to start before stopping audio.
            Timer exitTimer = new Timer(150, event -> {
                SoundManager.stopAll();
                System.exit(0);
            });

            exitTimer.setRepeats(false);
            exitTimer.start();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.getRootPane().setDefaultButton(cancelButton);

        dialog.pack();
        dialog.setSize(430, 220);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    // ==========================================
    // UPDATE MUSIC BUTTON TEXT
    // ==========================================

    private void updateMusicButton() {

        if (SoundManager.isMusicEnabled()) {
            musicButton.setText("MUSIC ON");
        } else {
            musicButton.setText("MUSIC OFF");
        }

        musicButton.setIcon(null);

        musicButton.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        musicButton.setVerticalAlignment(
                SwingConstants.CENTER
        );

        musicButton.setHorizontalTextPosition(
                SwingConstants.CENTER
        );

        musicButton.setVerticalTextPosition(
                SwingConstants.CENTER
        );

        musicButton.setMargin(new Insets(0, 0, 0, 0));
        musicButton.repaint();
    }

    // ==========================================
    // CREATE BUTTON
    // ==========================================

    private JButton createButton(String text) {

        RoundedButton button = new RoundedButton(text);

        button.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        15
                )
        );

        button.setForeground(BUTTON_TEXT);

        button.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        button.setVerticalAlignment(
                SwingConstants.CENTER
        );

        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // ==========================================
    // ORIGINAL BUTTON POSITION
    // ==========================================

    private int getButtonStartY(int panelHeight) {

        int buttonsHeight =
                (BUTTON_HEIGHT * 4) +
                (BUTTON_GAP * 3);

        int originalGroupHeight =
                ORIGINAL_LOGO_HEIGHT +
                ORIGINAL_LOGO_GAP +
                buttonsHeight;

        int groupStartY = Math.max(
                15,
                (panelHeight - originalGroupHeight) / 2
        );

        return groupStartY +
                ORIGINAL_LOGO_HEIGHT +
                ORIGINAL_LOGO_GAP;
    }

    // ==========================================
    // LOGO WIDTH
    // ==========================================

    private int getLogoWidth(int panelWidth) {

        int logoWidth = Math.min(
                MAX_LOGO_WIDTH,
                panelWidth - 80
        );

        return Math.max(100, logoWidth);
    }

    // ==========================================
    // BUTTON LAYOUT
    // ==========================================

    @Override
    public void doLayout() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Keep original button positions
        int buttonStartY = getButtonStartY(panelHeight);

        int buttonX =
                (panelWidth - BUTTON_WIDTH) / 2;

        playButton.setBounds(
                buttonX,
                buttonStartY,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );

        instructionsButton.setBounds(
                buttonX,
                buttonStartY + BUTTON_HEIGHT + BUTTON_GAP,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );

        performanceButton.setBounds(
                buttonX,
                buttonStartY + (BUTTON_HEIGHT + BUTTON_GAP) * 2,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );

        exitButton.setBounds(
                buttonX,
                buttonStartY + (BUTTON_HEIGHT + BUTTON_GAP) * 3,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );

        // MUSIC BUTTON - TOP RIGHT
        musicButton.setBounds(
                panelWidth - MUSIC_BUTTON_WIDTH - 25,
                25,
                MUSIC_BUTTON_WIDTH,
                MUSIC_BUTTON_HEIGHT
        );
    }

    // ==========================================
    // BACKGROUND AND LOGO
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Background
        g2.drawImage(
                background,
                0,
                0,
                getWidth(),
                getHeight(),
                this
        );

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Larger logo
        int logoWidth = getLogoWidth(panelWidth);
        int logoHeight = logoWidth / 2;

        // Original button position
        int buttonStartY = getButtonStartY(panelHeight);

        // Center logo horizontally
        int logoX = (panelWidth - logoWidth) / 2;

        // Move logo higher without moving buttons
        int logoY =
                buttonStartY -
                ORIGINAL_LOGO_GAP -
                logoHeight -
                LOGO_UP_OFFSET;

        g2.drawImage(
                logo,
                logoX,
                logoY,
                logoWidth,
                logoHeight,
                this
        );

        g2.dispose();
    }

    // ==========================================
    // ROUNDED BUTTON
    // ==========================================

    private class RoundedButton extends JButton {

        public RoundedButton(String text) {
            super(text);

            setFont(
                    new Font(
                            "Arial Rounded MT Bold",
                            Font.BOLD,
                            15
                    )
            );

            setForeground(BUTTON_TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Shadow
            g2.setColor(SHADOW_COLOR);

            g2.fillRoundRect(
                    4,
                    5,
                    width - 4,
                    height - 3,
                    25,
                    25
            );

            // Green button
            if (getModel().isRollover()) {
                g2.setColor(HOVER_COLOR);
            } else {
                g2.setColor(BUTTON_COLOR);
            }

            g2.fillRoundRect(
                    0,
                    0,
                    width - 5,
                    height - 5,
                    25,
                    25
            );

            // Golden border
            g2.setColor(GOLD);
            g2.setStroke(new BasicStroke(3));

            g2.drawRoundRect(
                    0,
                    0,
                    width - 5,
                    height - 5,
                    25,
                    25
            );

            // Button text
            g2.setFont(getFont());

            if (getModel().isRollover()) {
                g2.setColor(HOVER_TEXT);
            } else {
                g2.setColor(BUTTON_TEXT);
            }

            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(getText());

            // Center text horizontally
            int textX = (width - 5 - textWidth) / 2;

            // Center text vertically inside the button face
            int buttonFaceHeight = height - 5;

            int textY =
                    (buttonFaceHeight - fm.getHeight()) / 2
                    + fm.getAscent();

            g2.drawString(
                    getText(),
                    textX,
                    textY
            );

            g2.dispose();
        }
    }
}
