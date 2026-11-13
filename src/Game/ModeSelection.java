
package Game;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ModeSelection extends JPanel {

    private Image background;

    private JButton manualButton;
    private JButton bfsButton;
    private JButton dfsButton;
    private JButton backButton;

    public ModeSelection(Runnable onBack, Consumer<String> onModeSelected) {

        setLayout(null);

        background = new ImageIcon(
                getClass().getResource(
                        "/Assets/Background_Wallpaper.png"
                )
        ).getImage();

        manualButton = createButton("MANUAL PLAY");
        bfsButton = createButton("BFS SOLVER");
        dfsButton = createButton("DFS SOLVER");
        backButton = createButton("BACK");

        add(manualButton);
        add(bfsButton);
        add(dfsButton);
        add(backButton);

        // Select Manual Play
        manualButton.addActionListener(e -> {
            onModeSelected.accept("Manual Play");
        });

        // Select BFS
        bfsButton.addActionListener(e -> {
            onModeSelected.accept("BFS Solver");
        });

        // Select DFS
        dfsButton.addActionListener(e -> {
            onModeSelected.accept("DFS Solver");
        });

        // Return to Main Menu
        backButton.addActionListener(e -> onBack.run());
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        20
                )
        );

        button.setForeground(
                new Color(255, 244, 210)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setForeground(
                                new Color(255, 220, 120)
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setForeground(
                                new Color(255, 244, 210)
                        );
                    }
                }
        );

        return button;
    }

    private void drawButton(Graphics2D g2, JButton button) {

        int x = button.getX();
        int y = button.getY();
        int w = button.getWidth();
        int h = button.getHeight();

        // Shadow
        g2.setColor(
                new Color(25, 45, 27, 150)
        );

        g2.fillRoundRect(
                x + 4,
                y + 5,
                w,
                h,
                25,
                25
        );

        // Green button
        g2.setColor(
                new Color(49, 91, 54)
        );

        g2.fillRoundRect(
                x,
                y,
                w,
                h,
                25,
                25
        );

        // Golden border
        g2.setColor(
                new Color(222, 180, 92)
        );

        g2.setStroke(
                new BasicStroke(3)
        );

        g2.drawRoundRect(
                x,
                y,
                w,
                h,
                25,
                25
        );
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Background
        g.drawImage(
                background,
                0,
                0,
                panelWidth,
                panelHeight,
                this
        );

        // =====================================
        // TITLE: SELECT GAME MODE
        // =====================================

        Graphics2D titleGraphics =
                (Graphics2D) g.create();

        titleGraphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        titleGraphics.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        38
                )
        );

        String title = "SELECT GAME MODE";

        FontMetrics fm =
                titleGraphics.getFontMetrics();

        int titleX =
                (panelWidth - fm.stringWidth(title)) / 2;

        int titleY =
                panelHeight / 4;

        // Dark green shadow behind the letters
        titleGraphics.setColor(
                new Color(49, 91, 54)
        );

        titleGraphics.drawString(
                title,
                titleX + 2,
                titleY + 2
        );

        // Cream-colored title text
        titleGraphics.setColor(
                new Color(255, 244, 210)
        );

        titleGraphics.drawString(
                title,
                titleX,
                titleY
        );

        titleGraphics.dispose();

        // =====================================
        // BUTTON POSITIONS
        // =====================================

        int buttonWidth = 280;
        int buttonHeight = 55;

        int buttonX =
                (panelWidth - buttonWidth) / 2;

        int startY =
                panelHeight / 2 - 30;

        manualButton.setBounds(
                buttonX,
                startY,
                buttonWidth,
                buttonHeight
        );

        bfsButton.setBounds(
                buttonX,
                startY + 75,
                buttonWidth,
                buttonHeight
        );

        dfsButton.setBounds(
                buttonX,
                startY + 150,
                buttonWidth,
                buttonHeight
        );

        backButton.setBounds(
                30,
                panelHeight - 90,
                150,
                50
        );

        // Draw button backgrounds
        Graphics2D g2 =
                (Graphics2D) g.create();

        drawButton(g2, manualButton);
        drawButton(g2, bfsButton);
        drawButton(g2, dfsButton);
        drawButton(g2, backButton);

        g2.dispose();
    }
}
