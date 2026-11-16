
package Game;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AlgorithmSelection extends JPanel {

    private Image background;

    private JButton bfsButton;
    private JButton dfsButton;
    private JButton aStarButton;
    private JButton comparisonButton;
    private JButton backButton;

    public AlgorithmSelection(
            Runnable onBack,
            Consumer<String> onAlgorithmSelected) {

        setLayout(null);

        background = new ImageIcon(
                getClass().getResource(
                        "/Assets/Background_Wallpaper.png"
                )
        ).getImage();

        bfsButton = createButton("BFS SOLVER");
        dfsButton = createButton("DFS SOLVER");
        aStarButton = createButton("A* SOLVER");
        comparisonButton = createButton("ALGORITHM COMPARISON");
        backButton = createButton("BACK");

        add(bfsButton);
        add(dfsButton);
        add(aStarButton);
        add(comparisonButton);
        add(backButton);

        bfsButton.addActionListener(e ->
                onAlgorithmSelected.accept("BFS Solver"));

        dfsButton.addActionListener(e ->
                onAlgorithmSelected.accept("DFS Solver"));

        aStarButton.addActionListener(e ->
                onAlgorithmSelected.accept("A* Solver"));

        comparisonButton.addActionListener(e ->
                onAlgorithmSelected.accept("Algorithm Comparison"));

        backButton.addActionListener(e -> onBack.run());
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        18
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
                            java.awt.event.MouseEvent e) {

                        button.setForeground(
                                new Color(255, 220, 120)
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

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

        g2.setColor(new Color(25, 45, 27, 150));
        g2.fillRoundRect(x + 4, y + 5, w, h, 25, 25);

        g2.setColor(new Color(49, 91, 54));
        g2.fillRoundRect(x, y, w, h, 25, 25);

        g2.setColor(new Color(222, 180, 92));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, w, h, 25, 25);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        g.drawImage(
                background,
                0,
                0,
                panelWidth,
                panelHeight,
                this
        );

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        String title = "SELECT ALGORITHM";

        g2.setFont(
                new Font(
                        "Arial Rounded MT Bold",
                        Font.BOLD,
                        36
                )
        );

        FontMetrics fm = g2.getFontMetrics();

        int titleX =
                (panelWidth - fm.stringWidth(title)) / 2;

        int titleY = panelHeight / 4;

        g2.setColor(new Color(49, 91, 54));
        g2.drawString(title, titleX + 2, titleY + 2);

        g2.setColor(new Color(255, 244, 210));
        g2.drawString(title, titleX, titleY);

        g2.dispose();

        int buttonWidth = 340;
        int buttonHeight = 52;

        int buttonX =
                (panelWidth - buttonWidth) / 2;

        int startY = panelHeight / 2 - 65;
        int spacing = 68;

        bfsButton.setBounds(
                buttonX, startY,
                buttonWidth, buttonHeight
        );

        dfsButton.setBounds(
                buttonX, startY + spacing,
                buttonWidth, buttonHeight
        );

        aStarButton.setBounds(
                buttonX, startY + spacing * 2,
                buttonWidth, buttonHeight
        );

        comparisonButton.setBounds(
                buttonX, startY + spacing * 3,
                buttonWidth, buttonHeight
        );

        backButton.setBounds(
                30, panelHeight - 90,
                150, 50
        );

        Graphics2D buttonsGraphics =
                (Graphics2D) g.create();

        drawButton(buttonsGraphics, bfsButton);
        drawButton(buttonsGraphics, dfsButton);
        drawButton(buttonsGraphics, aStarButton);
        drawButton(buttonsGraphics, comparisonButton);
        drawButton(buttonsGraphics, backButton);

        buttonsGraphics.dispose();
    }
}
