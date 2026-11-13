
package Algorithms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ComplexityAnalysis extends JPanel {

    // COLORS
    private static final Color DARK_GREEN =
        new Color(19, 35, 27);

    private static final Color PANEL_GREEN =
        new Color(29, 49, 38);

    private static final Color LIGHT_BACKGROUND =
        new Color(241, 246, 239);

    private static final Color TEXT_DARK =
        new Color(35, 48, 39);

    private static final Color ACCENT_GREEN =
        new Color(91, 151, 103);

    public ComplexityAnalysis() {

        setLayout(new BorderLayout());
        setBackground(LIGHT_BACKGROUND);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(
            mainPanel, BoxLayout.Y_AXIS
        ));

        mainPanel.setBackground(LIGHT_BACKGROUND);
        mainPanel.setBorder(
            new EmptyBorder(25, 30, 25, 30)
        );

        // HEADER
        JLabel title = new JLabel(
            "BFS vs DFS - Complexity Analysis"
        );

        title.setFont(new Font(
            "Arial", Font.BOLD, 25
        ));

        title.setForeground(DARK_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Understanding the performance of maze-solving algorithms"
        );

        subtitle.setFont(new Font(
            "Arial", Font.PLAIN, 13
        ));

        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(25));

        // BFS
        addSection(
            mainPanel,
            "1. Breadth-First Search (BFS)",
            "BFS explores the maze level by level. It visits "
            + "the nearest neighboring cells first using a Queue.\n\n"
            + "Time Complexity: O(V + E)\n"
            + "Space Complexity: O(V)\n\n"
            + "Advantage:\n"
            + "Finds the shortest path in an unweighted maze.\n\n"
            + "Disadvantage:\n"
            + "May require more memory because it stores "
            + "discovered cells while exploring."
        );

        // DFS
        addSection(
            mainPanel,
            "2. Depth-First Search (DFS)",
            "DFS explores one direction deeply before "
            + "backtracking. It uses a Stack to keep track "
            + "of the cells that need to be explored.\n\n"
            + "Time Complexity: O(V + E)\n"
            + "Space Complexity: O(V)\n\n"
            + "Advantage:\n"
            + "Explores deep routes before trying other directions.\n\n"
            + "Disadvantage:\n"
            + "Does not guarantee the shortest path."
        );

        // INPUT SIZE
        addSection(
            mainPanel,
            "3. Effect of Input Size",
            "The input size refers to the dimensions of the maze.\n\n"
            + "For example:\n"
            + "5 x 5 Maze = 25 total cells\n"
            + "10 x 10 Maze = 100 total cells\n"
            + "20 x 20 Maze = 400 total cells\n"
            + "30 x 30 Maze = 900 total cells\n"
            + "50 x 50 Maze = 2,500 total cells\n\n"
            + "As the maze grows, the number of cells and "
            + "possible connections can increase. This may "
            + "affect execution time and memory usage.\n\n"
            + "Actual results also depend on the maze layout "
            + "and the order in which cells are explored."
        );

        // COMPARISON
        addSection(
            mainPanel,
            "4. Overall Comparison",
            "Both BFS and DFS have O(V + E) time complexity "
            + "and O(V) auxiliary space complexity under "
            + "the current visited-tracking implementation.\n\n"
            + "BFS is preferable when finding the shortest "
            + "path is important.\n\n"
            + "DFS is useful for exploring possible routes "
            + "through the maze.\n\n"
            + "Although their theoretical complexities are "
            + "the same, their actual execution times, visited "
            + "cells, and allocated memory may differ."
        );

        // DEFINITIONS
        addSection(
            mainPanel,
            "5. Complexity Terminology",
            "V = Vertices\n"
            + "The number of traversable cells in the maze.\n\n"
            + "E = Edges\n"
            + "The number of valid connections between "
            + "neighboring traversable cells.\n\n"
            + "O(V + E)\n"
            + "Represents the time needed to process vertices "
            + "and edges during traversal.\n\n"
            + "O(V)\n"
            + "Represents the additional memory needed for "
            + "visited tracking and the search structure."
        );

        JScrollPane scrollPane = new JScrollPane(mainPanel);

        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addSection(
        JPanel panel,
        String heading,
        String description
    ) {

        JPanel card = new JPanel(new BorderLayout(0, 10));

        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(220, 228, 219)
            ),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel sectionTitle = new JLabel(heading);

        sectionTitle.setFont(new Font(
            "Arial", Font.BOLD, 17
        ));

        sectionTitle.setForeground(ACCENT_GREEN);

        JTextArea content = new JTextArea(description);

        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setFont(new Font(
            "Arial", Font.PLAIN, 14
        ));

        content.setForeground(TEXT_DARK);
        content.setBackground(Color.WHITE);
        content.setBorder(null);

        card.add(sectionTitle, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(card);
        panel.add(Box.createVerticalStrut(15));
    }

    // STANDALONE TEST
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                "Mushroom Maze - Complexity Analysis"
            );

            frame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
            );

            frame.setSize(850, 650);
            frame.setLocationRelativeTo(null);

            frame.add(new ComplexityAnalysis());

            frame.setVisible(true);
        });
    }
}
