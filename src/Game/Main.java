
package Game;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import Algorithms.PerformanceDashboard;
import Sounds.SoundManager;

public class Main {

    private static JFrame window;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            window = new JFrame("Mushroom Maze");

            // =====================================
            // WINDOW ICON
            // =====================================

            ImageIcon icon = new ImageIcon(
                Main.class.getResource("/Assets/Smile.png")
            );

            window.setIconImage(icon.getImage());

            CardLayout cardLayout = new CardLayout();
            JPanel screens = new JPanel(cardLayout);

            String[] selectedMode = {""};

            // =====================================
            // MAIN MENU
            // =====================================

            MainMenu menu = new MainMenu(
                // PLAY
                () -> cardLayout.show(screens, "MODE"),

                // INSTRUCTIONS
                () -> cardLayout.show(screens, "INSTRUCTIONS"),

                // PERFORMANCE
                () -> cardLayout.show(screens, "PERFORMANCE")
            );

            // =====================================
            // PERFORMANCE DASHBOARD
            // =====================================

            PerformanceDashboard performance =
                new PerformanceDashboard(
                    () -> cardLayout.show(screens, "MENU")
                );

            // =====================================
            // INSTRUCTIONS SCREEN
            // =====================================

            InstructionsPanel instructions =
                new InstructionsPanel(
                    () -> cardLayout.show(screens, "MENU")
                );

            // =====================================
            // GAME SCREEN
            // =====================================

            GamePanel gamePanel = new GamePanel(
                () -> cardLayout.show(screens, "SIZE")
            );

            // =====================================
            // MAZE SIZE SELECTION
            // =====================================

            MazeSizeSelection sizeSelection =
                new MazeSizeSelection(
                    () -> cardLayout.show(screens, "MODE"),

                    size -> {
                        gamePanel.startGame(
                            selectedMode[0],
                            size
                        );

                        cardLayout.show(screens, "GAME");
                    }
                );

            // =====================================
            // GAME MODE SELECTION
            // =====================================

            ModeSelection modeSelection =
                new ModeSelection(
                    () -> cardLayout.show(screens, "MENU"),

                    mode -> {
                        selectedMode[0] = mode;
                        sizeSelection.setSelectedMode(mode);
                        cardLayout.show(screens, "SIZE");
                    }
                );

            // =====================================
            // ADD ALL SCREENS
            // =====================================

            screens.add(menu, "MENU");
            screens.add(instructions, "INSTRUCTIONS");
            screens.add(performance, "PERFORMANCE");
            screens.add(modeSelection, "MODE");
            screens.add(sizeSelection, "SIZE");
            screens.add(gamePanel, "GAME");

            // =====================================
            // INSTALL CLICK SOUNDS ON ALL SCREENS
            // =====================================

            installClickSounds(screens);

            // =====================================
            // WINDOW SETTINGS
            // =====================================

            window.add(screens);

            window.setSize(1000, 700);

            window.setMinimumSize(
                new Dimension(900, 600)
            );

            window.setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
            );

            window.setLocationRelativeTo(null);
            window.setResizable(true);

            // =====================================
            // WINDOW EXIT CONFIRMATION
            // =====================================

            window.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    showExitConfirmation();
                }
            });

            // =====================================
            // START BACKGROUND MUSIC
            // =====================================

            SoundManager.playBackgroundMusic();

            window.setVisible(true);
        });
    }

    // ==========================================
    // INSTALL CLICK SOUNDS ON ALL BUTTONS
    // ==========================================

    private static void installClickSounds(Container container) {

        // Install the container listener only once.

        if (container instanceof JComponent) {

            JComponent panel = (JComponent) container;

            if (!Boolean.TRUE.equals(
                    panel.getClientProperty("clickSoundContainerInstalled"))) {

                panel.addContainerListener(new ContainerAdapter() {

                    @Override
                    public void componentAdded(ContainerEvent e) {
                        installClickSoundOnComponent(e.getChild());
                    }
                });

                panel.putClientProperty(
                    "clickSoundContainerInstalled",
                    true
                );
            }
        }

        // Check existing components.

        for (Component component : container.getComponents()) {
            installClickSoundOnComponent(component);
        }
    }

    private static void installClickSoundOnComponent(Component component) {

        // Install click sound on buttons only once.

        if (component instanceof AbstractButton) {

            AbstractButton button = (AbstractButton) component;

            if (!Boolean.TRUE.equals(
                    button.getClientProperty("clickSoundInstalled"))) {

                button.addActionListener(e -> SoundManager.playClick());

                button.putClientProperty(
                    "clickSoundInstalled",
                    true
                );
            }
        }

        // Check buttons inside nested panels.

        if (component instanceof Container) {
            installClickSounds((Container) component);
        }
    }

    // ==========================================
    // EXIT CONFIRMATION POPUP
    // ==========================================

    private static void showExitConfirmation() {

        JDialog dialog = new JDialog(
            window,
            "Exit Mushroom Maze",
            true
        );

        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
            new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );

        mainPanel.setBackground(
            new Color(19, 35, 27)
        );

        mainPanel.setBorder(
            new CompoundBorder(
                new LineBorder(
                    new Color(212, 175, 55),
                    2
                ),
                new EmptyBorder(30, 35, 30, 35)
            )
        );

        // =====================================
        // TITLE
        // =====================================

        JLabel title = new JLabel("MUSHROOM MAZE");

        title.setFont(
            new Font("Serif", Font.BOLD, 25)
        );

        title.setForeground(
            new Color(230, 190, 90)
        );

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // =====================================
        // MESSAGE
        // =====================================

        JLabel message = new JLabel(
            "Are you sure you want to exit?"
        );

        message.setFont(
            new Font("SansSerif", Font.BOLD, 16)
        );

        message.setForeground(Color.WHITE);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subMessage = new JLabel(
            "Your current game progress will be lost."
        );

        subMessage.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        subMessage.setForeground(
            new Color(180, 190, 180)
        );

        subMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // =====================================
        // BUTTONS
        // =====================================

        JButton cancelButton = new JButton("CANCEL");
        JButton exitButton = new JButton("EXIT");

        styleExitButton(
            cancelButton,
            new Color(49, 91, 64),
            Color.WHITE
        );

        styleExitButton(
            exitButton,
            new Color(145, 55, 45),
            Color.WHITE
        );

        cancelButton.addActionListener(e -> {

            SoundManager.playClick();
            dialog.dispose();
        });

        exitButton.addActionListener(e -> {

            SoundManager.playClick();
            dialog.dispose();

            // Allow the click sound to start before stopping audio.

            Timer exitTimer = new Timer(150, event -> {

                SoundManager.stopAll();
                window.dispose();
                System.exit(0);
            });

            exitTimer.setRepeats(false);
            exitTimer.start();
        });

        // =====================================
        // BUTTON PANEL
        // =====================================

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 0)
        );

        buttonPanel.setOpaque(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(exitButton);

        // =====================================
        // ADD COMPONENTS
        // =====================================

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(message);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(subMessage);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);

        dialog.pack();
        dialog.setSize(450, 260);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    // ==========================================
    // EXIT BUTTON STYLE
    // ==========================================

    private static void styleExitButton(
            JButton button,
            Color background,
            Color foreground) {

        button.setPreferredSize(
            new Dimension(125, 42)
        );

        button.setBackground(background);
        button.setForeground(foreground);

        button.setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
    }
}
