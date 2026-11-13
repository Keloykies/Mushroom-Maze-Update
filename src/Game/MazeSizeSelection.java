
package Game;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public class MazeSizeSelection extends JPanel {

    private final Runnable onBack;
    private final IntConsumer onSizeSelected;

    private String selectedMode = "";

    private final Color darkGreen = new Color(35, 90, 45);
    private final Color gold = new Color(255, 210, 90);

    private Image background;

    private JPanel contentPanel;

    public MazeSizeSelection(Runnable onBack, IntConsumer onSizeSelected) {

        this.onBack = onBack;
        this.onSizeSelected = onSizeSelected;

        setLayout(null);

        try {
            background = new ImageIcon(
                    getClass().getResource(
                            "/Assets/Background_Wallpaper.png"
                    )
            ).getImage();
        } catch (Exception e) {
            background = null;
        }

        contentPanel = new JPanel();

        contentPanel.setOpaque(false);

        contentPanel.setLayout(
                new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        );

        // Selected mode
        JLabel modeLabel = new JLabel("Choose your challenge");

        modeLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        modeLabel.setForeground(gold);

        modeLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // Maze size buttons
        JPanel buttonPanel = new JPanel(
                new GridLayout(5, 1, 0, 15)
        );

        buttonPanel.setOpaque(false);

        buttonPanel.setMaximumSize(
                new Dimension(300, 350)
        );

        int[] sizes = {5, 10, 20, 30, 50};

        for (int size : sizes) {

            JButton button = createSizeButton(size);

            button.addActionListener(e ->
                    onSizeSelected.accept(size)
            );

            buttonPanel.add(button);
        }

        // Back button
        JButton backButton = new JButton("BACK");

        styleButton(
                backButton,
                new Color(120, 65, 40)
        );

        backButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        backButton.addActionListener(e -> onBack.run());

        // Content arrangement
        contentPanel.add(modeLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(backButton);

        add(contentPanel);
    }

    private JButton createSizeButton(int size) {

        JButton button = new JButton(size + " x " + size);

        styleButton(button, darkGreen);

        button.setFont(
                new Font("Arial", Font.BOLD, 21)
        );

        return button;
    }

    private void styleButton(JButton button, Color background) {

        button.setPreferredSize(
                new Dimension(250, 55)
        );

        button.setMinimumSize(
                new Dimension(250, 55)
        );

        button.setMaximumSize(
                new Dimension(250, 55)
        );

        button.setBackground(background);

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createLineBorder(gold, 2)
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
    }

    public void setSelectedMode(String mode) {

        selectedMode = mode;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        if (background != null) {

            g2.drawImage(
                    background,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );
        }

        // Title
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        38
                )
        );

        String title = "SELECT MAZE SIZE";

        FontMetrics fm = g2.getFontMetrics();

        int titleX =
                (getWidth() - fm.stringWidth(title)) / 2;

        // Title position
        int titleY = Math.max(55, getHeight() / 5);

        // Green shadow
        g2.setColor(
                new Color(49, 91, 54)
        );

        g2.drawString(
                title,
                titleX + 2,
                titleY + 2
        );

        // Cream text
        g2.setColor(
                new Color(255, 244, 210)
        );

        g2.drawString(
                title,
                titleX,
                titleY
        );

        g2.dispose();
    }

    @Override
    public void doLayout() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int contentWidth = 300;
        int contentHeight = contentPanel.getPreferredSize().height;

        int titleY = Math.max(55, panelHeight / 5);

        int contentY = titleY + 45;

        contentPanel.setBounds(
                (panelWidth - contentWidth) / 2,
                contentY,
                contentWidth,
                contentHeight
        );
    }
}
