
package Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InstructionsPanel extends JPanel {

    private static final Color DARK_GREEN =
            new Color(19, 35, 27);

    private static final Color PANEL_GREEN =
            new Color(29, 49, 36);

    private static final Color BUTTON_GREEN =
            new Color(49, 91, 54);

    private static final Color GOLD =
            new Color(222, 180, 92);

    private static final Color CREAM =
            new Color(255, 244, 210);

    public InstructionsPanel(Runnable onBack) {

        setLayout(new BorderLayout(20, 20));
        setBackground(DARK_GREEN);
        setBorder(new EmptyBorder(30, 45, 30, 45));

        // =====================================
        // HEADER
        // =====================================

        JLabel title = new JLabel(
                "HOW TO PLAY",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial Rounded MT Bold", Font.BOLD, 34)
        );

        title.setForeground(GOLD);

        JLabel subtitle = new JLabel(
                "Everything you need to know about Mushroom Maze",
                SwingConstants.CENTER
        );

        subtitle.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        subtitle.setForeground(CREAM);

        JPanel header = new JPanel(new GridLayout(2, 1, 5, 5));
        header.setOpaque(false);

        header.add(title);
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // =====================================
        // INSTRUCTIONS CONTENT
        // =====================================

        JPanel content = new JPanel(new GridLayout(2, 3, 18, 18));
        content.setOpaque(false);

        // 01 OBJECTIVE
        content.add(createCard(
                "01  OBJECTIVE",
                "Navigate through the maze and find the exit.\n\n" +
                "Avoid walls and discover the correct path from " +
                "the starting point to the destination."
        ));

        // 02 MOVEMENT CONTROLS
        content.add(createCard(
                "02  MOVEMENT CONTROLS",
                "W or UP ARROW       - Move Up\n\n" +
                "S or DOWN ARROW   - Move Down\n\n" +
                "A or LEFT ARROW    - Move Left\n\n" +
                "D or RIGHT ARROW - Move Right"
        ));

        // 03 BFS SOLVER
        content.add(createCard(
                "03  BFS SOLVER",
                "Breadth-First Search explores the maze level " +
                "by level.\n\n" +
                "It uses a QUEUE and finds the shortest path " +
                "in an unweighted maze."
        ));

        // 04 DFS SOLVER
        content.add(createCard(
                "04  DFS SOLVER",
                "Depth-First Search explores one direction " +
                "as far as possible before backtracking.\n\n" +
                "It uses a STACK to keep track of its exploration."
        ));

        // 05 A* SOLVER
        content.add(createCard(
                "05  A* SOLVER",
                "A* Search finds a path by considering the " +
                "distance already traveled and the estimated " +
                "distance to the goal.\n\n" +
                "It uses a PRIORITY QUEUE to explore the most " +
                "promising path first."
        ));
        
     // 06 ENJOY THE GAME
        content.add(createCard(
                "06  ENJOY THE GAME!",
                "Put your skills to the test, explore every maze, " +
                "and discover the power of algorithms.\n\n" +
                "Have fun, think smart, and happy solving!"
        ));

        add(content, BorderLayout.CENTER);

        // =====================================
        // BACK BUTTON
        // =====================================

        JButton backButton = new JButton("BACK TO MAIN MENU");

        backButton.setFont(
                new Font("Arial Rounded MT Bold", Font.BOLD, 15)
        );

        backButton.setForeground(CREAM);
        backButton.setBackground(BUTTON_GREEN);
        backButton.setFocusPainted(false);

        backButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD, 2),
                        new EmptyBorder(12, 30, 12, 30)
                )
        );

        backButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        backButton.addActionListener(e -> onBack.run());

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.add(backButton);

        add(footer, BorderLayout.SOUTH);
    }

    // =====================================
    // INSTRUCTION CARD
    // =====================================

    private JPanel createCard(String heading, String description) {

        JPanel card = new JPanel(new BorderLayout(10, 15));

        card.setBackground(PANEL_GREEN);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD, 2),
                        new EmptyBorder(20, 22, 20, 22)
                )
        );

        JLabel title = new JLabel(heading);

        title.setFont(
                new Font("Arial Rounded MT Bold", Font.BOLD, 19)
        );

        title.setForeground(GOLD);

        JTextArea text = new JTextArea(description);

        text.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        text.setForeground(CREAM);
        text.setBackground(PANEL_GREEN);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(true);

        card.add(title, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);

        return card;
    }
}
