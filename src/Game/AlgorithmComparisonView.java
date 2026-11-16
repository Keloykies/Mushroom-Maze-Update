
package Game;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import Maze.Maze;

public class AlgorithmComparisonView extends JPanel {

    private static final Color DARK_GREEN = new Color(19, 35, 27);
    private static final Color PANEL_GREEN = new Color(29, 49, 37);
    private static final Color GOLD = new Color(255, 210, 80);
    private static final Color TEXT_COLOR = new Color(239, 245, 232);
    private static final Color COMPLETED_GREEN = new Color(55, 170, 85);

    private final MazeMiniPanel bfsPanel;
    private final MazeMiniPanel dfsPanel;
    private final MazeMiniPanel aStarPanel;

    private final DefaultTableModel tableModel;
    private final JLabel summaryLabel;

    private int completedAlgorithms = 0;

    public static class ComparisonData {

        public final String algorithm;
        public final double executionTime;
        public final ArrayList<Point> visited;
        public final ArrayList<Point> path;

        public ComparisonData(
            String algorithm,
            double executionTime,
            List<Point> visited,
            List<Point> path
        ) {
            this.algorithm = algorithm;
            this.executionTime = executionTime;
            this.visited = new ArrayList<>(visited);
            this.path = new ArrayList<>(path);
        }
    }

    public AlgorithmComparisonView() {

        setLayout(new BorderLayout(12, 12));
        setBackground(DARK_GREEN);

        setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // ======================================
        // THREE MAZE PANELS
        // ======================================

        JPanel mazePanels = new JPanel(
            new GridLayout(1, 3, 14, 0)
        );

        mazePanels.setBackground(DARK_GREEN);

        // ADDED: Extra space below the three mazes.
        mazePanels.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 12, 0)
        );

        bfsPanel = new MazeMiniPanel("BFS");
        dfsPanel = new MazeMiniPanel("DFS");
        aStarPanel = new MazeMiniPanel("A*");

        mazePanels.add(bfsPanel);
        mazePanels.add(dfsPanel);
        mazePanels.add(aStarPanel);

        // ======================================
        // RESULTS TABLE
        // ======================================

        String[] columns = {
            "Algorithm",
            "Execution Time (ms)",
            "Visited Cells",
            "Path Moves",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addRow(new Object[]{
            "BFS", "-", 0, 0, "Waiting"
        });

        tableModel.addRow(new Object[]{
            "DFS", "-", 0, 0, "Waiting"
        });

        tableModel.addRow(new Object[]{
            "A*", "-", 0, 0, "Waiting"
        });

        JTable resultsTable = new JTable(tableModel);

        resultsTable.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        resultsTable.setRowHeight(32);

        resultsTable.setBackground(PANEL_GREEN);
        resultsTable.setForeground(TEXT_COLOR);

        resultsTable.setGridColor(
            new Color(77, 111, 78)
        );

        resultsTable.setSelectionBackground(
            new Color(55, 125, 65)
        );

        resultsTable.setSelectionForeground(Color.WHITE);
        resultsTable.setFocusable(false);

        resultsTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        resultsTable.getTableHeader().setPreferredSize(
            new Dimension(0, 36)
        );

        resultsTable.getTableHeader().setBackground(DARK_GREEN);
        resultsTable.getTableHeader().setForeground(GOLD);
        resultsTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer =
            new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setBackground(PANEL_GREEN);
        renderer.setForeground(TEXT_COLOR);

        for (int i = 0; i < resultsTable.getColumnCount(); i++) {

            resultsTable.getColumnModel()
                .getColumn(i)
                .setCellRenderer(renderer);
        }

        resultsTable.getColumnModel().getColumn(0)
            .setPreferredWidth(120);

        resultsTable.getColumnModel().getColumn(1)
            .setPreferredWidth(190);

        resultsTable.getColumnModel().getColumn(2)
            .setPreferredWidth(150);

        resultsTable.getColumnModel().getColumn(3)
            .setPreferredWidth(130);

        resultsTable.getColumnModel().getColumn(4)
            .setPreferredWidth(130);

        JScrollPane tableScroll = new JScrollPane(resultsTable);

        tableScroll.setBorder(
            BorderFactory.createLineBorder(
                new Color(77, 111, 78), 1
            )
        );

        tableScroll.getViewport().setBackground(PANEL_GREEN);

        // ======================================
        // RESULTS SECTION
        // ======================================

        JPanel resultsPanel = new JPanel(
            new BorderLayout(8, 8)
        );

        resultsPanel.setBackground(DARK_GREEN);

        resultsPanel.setBorder(
        	    BorderFactory.createEmptyBorder(0, 0, 0, 0)
        	);

        JLabel resultsTitle = new JLabel(
            "LIVE ALGORITHM COMPARISON"
        );

        resultsTitle.setFont(
            new Font("Arial Rounded MT Bold", Font.BOLD, 17)
        );

        resultsTitle.setForeground(GOLD);
        resultsTitle.setHorizontalAlignment(SwingConstants.CENTER);

        summaryLabel = new JLabel(
            "Waiting for comparison..."
        );

        summaryLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        summaryLabel.setForeground(TEXT_COLOR);
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel resultsHeader = new JPanel(
            new GridLayout(2, 1, 0, 6)
        );

        resultsHeader.setOpaque(false);

        resultsHeader.add(resultsTitle);
        resultsHeader.add(summaryLabel);

        resultsPanel.add(resultsHeader, BorderLayout.NORTH);
        resultsPanel.add(tableScroll, BorderLayout.CENTER);

        resultsPanel.setPreferredSize(
            new Dimension(500, 190)
        );

        resultsPanel.setMinimumSize(
            new Dimension(300, 150)
        );

        // ======================================
        // MAIN LAYOUT
        // ======================================

        add(mazePanels, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // SET THE SAME MAZE IN ALL PANELS
    // ==========================================

    public void setMaze(Maze maze) {

        stopAnimations();

        bfsPanel.setMaze(maze);
        dfsPanel.setMaze(maze);
        aStarPanel.setMaze(maze);

        completedAlgorithms = 0;

        resetTable();

        summaryLabel.setText(
            "Same maze | BFS vs DFS vs A*"
        );

        summaryLabel.setForeground(TEXT_COLOR);

        revalidate();
        repaint();
    }

    // ==========================================
    // SET ANIMATION SPEED
    // ==========================================

    public void setAnimationDelay(int delay) {

        bfsPanel.setAnimationDelay(delay);
        dfsPanel.setAnimationDelay(delay);
        aStarPanel.setAnimationDelay(delay);
    }

    // ==========================================
    // START COMPARISON
    // ==========================================

    public void startComparison(
        List<ComparisonData> results,
        Runnable onComplete
    ) {

        stopAnimations();

        completedAlgorithms = 0;

        bfsPanel.resetRank();
        dfsPanel.resetRank();
        aStarPanel.resetRank();

        resetTable();

        if (results == null || results.size() != 3) {

            summaryLabel.setText(
                "Unable to start comparison: missing results."
            );

            return;
        }

        for (ComparisonData result : results) {

            int row = getRow(result.algorithm);

            if (row >= 0) {

                tableModel.setValueAt(
                    String.format("%.4f", result.executionTime),
                    row,
                    1
                );

                tableModel.setValueAt(
                    "Running",
                    row,
                    4
                );
            }
        }

        summaryLabel.setText(
            "BFS, DFS, and A* are running..."
        );

        summaryLabel.setForeground(TEXT_COLOR);

        // Start all three animations together.

        bfsPanel.start(
            findResult(results, "BFS"),
            () -> algorithmFinished("BFS", results, onComplete)
        );

        dfsPanel.start(
            findResult(results, "DFS"),
            () -> algorithmFinished("DFS", results, onComplete)
        );

        aStarPanel.start(
            findResult(results, "A*"),
            () -> algorithmFinished("A*", results, onComplete)
        );
    }

    private ComparisonData findResult(
        List<ComparisonData> results,
        String algorithm
    ) {

        for (ComparisonData result : results) {

            if (algorithm.equals(result.algorithm)) {
                return result;
            }
        }

        return null;
    }

    // ==========================================
    // UPDATE LIVE TABLE
    // ==========================================

    private void updateLiveResult(
        String algorithm,
        double executionTime,
        int visited,
        int pathMoves,
        String status
    ) {

        int row = getRow(algorithm);

        if (row < 0) {
            return;
        }

        tableModel.setValueAt(
            String.format("%.4f", executionTime),
            row,
            1
        );

        tableModel.setValueAt(visited, row, 2);
        tableModel.setValueAt(pathMoves, row, 3);
        tableModel.setValueAt(status, row, 4);
    }

    private int getRow(String algorithm) {

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            if (algorithm.equals(
                    tableModel.getValueAt(i, 0))) {

                return i;
            }
        }

        return -1;
    }

    // ==========================================
    // ALGORITHM FINISHED AND RANKING
    // ==========================================

    private void algorithmFinished(
        String algorithm,
        List<ComparisonData> results,
        Runnable onComplete
    ) {

        completedAlgorithms++;

        int place = completedAlgorithms;

        switch (algorithm) {

            case "BFS":
                bfsPanel.setRank(place);
                break;

            case "DFS":
                dfsPanel.setRank(place);
                break;

            case "A*":
                aStarPanel.setRank(place);
                break;
        }

        if (completedAlgorithms >= 3) {

            double fastestTime = Double.MAX_VALUE;
            String fastestAlgorithm = "";

            for (ComparisonData result : results) {

                if (result.executionTime < fastestTime) {

                    fastestTime = result.executionTime;
                    fastestAlgorithm = result.algorithm;
                }
            }

            summaryLabel.setForeground(COMPLETED_GREEN);

            summaryLabel.setText(
                "COMPARISON COMPLETE | FASTEST: "
                + fastestAlgorithm
            );

            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    // ==========================================
    // UPDATE TABLE ROW
    // ==========================================

    private void updateRowFromPanel(
        String algorithm,
        double executionTime,
        int visited,
        int pathMoves,
        String status
    ) {

        updateLiveResult(
            algorithm,
            executionTime,
            visited,
            pathMoves,
            status
        );
    }

    // ==========================================
    // RESET TABLE
    // ==========================================

    private void resetTable() {

        for (int row = 0; row < 3; row++) {

            tableModel.setValueAt("-", row, 1);
            tableModel.setValueAt(0, row, 2);
            tableModel.setValueAt(0, row, 3);
            tableModel.setValueAt("Waiting", row, 4);
        }
    }

    // ==========================================
    // STOP ANIMATIONS
    // ==========================================

    public void stopAnimations() {

        if (bfsPanel != null) {
            bfsPanel.stop();
        }

        if (dfsPanel != null) {
            dfsPanel.stop();
        }

        if (aStarPanel != null) {
            aStarPanel.stop();
        }
    }

    public boolean isRunning() {

        return bfsPanel.isRunning()
            || dfsPanel.isRunning()
            || aStarPanel.isRunning();
    }

    // ==========================================
    // INDIVIDUAL MINI MAZE PANEL
    // ==========================================

    private class MazeMiniPanel extends JPanel {

        private final String algorithm;

        private Maze maze;

        private ArrayList<Point> visited = new ArrayList<>();
        private ArrayList<Point> path = new ArrayList<>();

        private int visitedIndex = 0;
        private int pathIndex = 0;

        private double executionTime = 0;

        private Timer animationTimer;

        private Runnable onFinished;

        private Image grass;
        private Image wallSquare;
        private Image wallRectangle;
        private Image door;
        private Image player;

        private boolean finished = false;

        private JLabel rankLabel;

        MazeMiniPanel(String algorithm) {

            this.algorithm = algorithm;

            setLayout(new BorderLayout());

            setBackground(PANEL_GREEN);

            setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD, 2),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                )
            );

            setMinimumSize(new Dimension(150, 200));

            // MAZE HEADER AND RANKING BANNER

            JPanel headerPanel = new JPanel(
                new GridLayout(2, 1, 0, 5)
            );

            headerPanel.setOpaque(false);

            JLabel title = new JLabel(
                algorithm + " SOLVER"
            );

            title.setFont(
                new Font("Arial Rounded MT Bold", Font.BOLD, 16)
            );

            title.setForeground(GOLD);
            title.setHorizontalAlignment(SwingConstants.CENTER);

            rankLabel = new JLabel("WAITING");

            rankLabel.setFont(
                new Font("Arial", Font.BOLD, 11)
            );

            rankLabel.setForeground(TEXT_COLOR);
            rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rankLabel.setOpaque(true);
            rankLabel.setBackground(new Color(45, 65, 50));

            rankLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            );

            headerPanel.add(title);
            headerPanel.add(rankLabel);

            add(headerPanel, BorderLayout.NORTH);

            // LOAD IMAGES

            grass = loadImage("/Assets/Grass.png");
            wallSquare = loadImage("/Assets/Wall_Square.png");
            wallRectangle = loadImage("/Assets/Wall_Rectangle.png");
            door = loadImage("/Assets/Door.png");
            player = loadImage("/Assets/Idle.png");

            animationTimer = new Timer(35, e -> animate());
        }

        // RESET RANKING BANNER

        void resetRank() {

            rankLabel.setText("WAITING");
            rankLabel.setForeground(TEXT_COLOR);
            rankLabel.setBackground(new Color(45, 65, 50));

            repaint();
        }

        // SET RANKING BANNER

        void setRank(int place) {

            switch (place) {

                case 1:

                    rankLabel.setText("🥇 1ST PLACE");
                    rankLabel.setForeground(Color.WHITE);
                    rankLabel.setBackground(new Color(170, 125, 30));

                    break;

                case 2:

                    rankLabel.setText("🥈 2ND PLACE");
                    rankLabel.setForeground(Color.WHITE);
                    rankLabel.setBackground(new Color(95, 105, 115));

                    break;

                case 3:

                    rankLabel.setText("🥉 3RD PLACE");
                    rankLabel.setForeground(Color.WHITE);
                    rankLabel.setBackground(new Color(135, 85, 50));

                    break;
            }

            repaint();
        }

        private Image loadImage(String path) {

            java.net.URL resource =
                getClass().getResource(path);

            if (resource == null) {

                System.out.println("Missing image: " + path);

                return null;
            }

            return new ImageIcon(resource).getImage();
        }

        void setMaze(Maze maze) {

            stop();

            this.maze = maze;

            visited.clear();
            path.clear();

            visitedIndex = 0;
            pathIndex = 0;

            finished = false;
            onFinished = null;

            resetRank();

            repaint();
        }

        void setAnimationDelay(int delay) {

            animationTimer.setDelay(delay);
            animationTimer.setInitialDelay(delay);
        }

        void start(
            ComparisonData result,
            Runnable onFinished
        ) {

            stop();

            if (result == null) {
                return;
            }

            this.visited = new ArrayList<>(result.visited);
            this.path = new ArrayList<>(result.path);

            this.executionTime = result.executionTime;

            this.onFinished = onFinished;

            visitedIndex = 0;
            pathIndex = 0;

            finished = false;

            updateLiveTable();

            animationTimer.start();
        }

        void stop() {

            if (animationTimer != null) {
                animationTimer.stop();
            }

            finished = true;
            onFinished = null;
        }

        boolean isRunning() {

            return animationTimer != null
                && animationTimer.isRunning();
        }

        private void animate() {

            if (maze == null) {
                stop();
                return;
            }

            // PHASE 1: EXPLORE CELLS

            if (visitedIndex < visited.size()) {

                visitedIndex++;
            }

            // PHASE 2: FOLLOW SOLUTION PATH

            else if (pathIndex < path.size()) {

                pathIndex++;
            }

            updateLiveTable();

            repaint();

            // PHASE 3: FINISH

            if (visitedIndex >= visited.size()
                    && pathIndex >= path.size()) {

                animationTimer.stop();

                finished = true;

                updateLiveTable();

                if (onFinished != null) {

                    Runnable callback = onFinished;

                    onFinished = null;

                    callback.run();
                }
            }
        }

        private void updateLiveTable() {

            String status = finished
                ? "Complete"
                : "Running";

            int pathMoves = Math.max(0, pathIndex - 1);

            updateRowFromPanel(
                algorithm,
                executionTime,
                visitedIndex,
                pathMoves,
                status
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (maze == null) {

                g2.dispose();

                return;
            }

            int margin = 12;

            int availableWidth = getWidth() - margin * 2;

            int availableHeight =
                getHeight() - 65 - margin * 2;

            if (availableWidth <= 0 || availableHeight <= 0) {

                g2.dispose();
                return;
            }

            int tileSize = Math.min(
                availableWidth / maze.getCols(),
                availableHeight / maze.getRows()
            );

            tileSize = Math.max(1, Math.min(tileSize, 64));

            int mazeWidth = tileSize * maze.getCols();
            int mazeHeight = tileSize * maze.getRows();

            int startX = (getWidth() - mazeWidth) / 2;

         // Move the maze slightly lower below the ranking banner.
            int startY = 70
                + (availableHeight - mazeHeight) / 2;

            // DRAW FLOOR AND WALLS

            for (int row = 0; row < maze.getRows(); row++) {

                for (int col = 0; col < maze.getCols(); col++) {

                    int x = startX + col * tileSize;
                    int y = startY + row * tileSize;

                    if (grass != null) {

                        g2.drawImage(
                            grass,
                            x, y,
                            tileSize, tileSize,
                            this
                        );

                    } else {

                        g2.setColor(new Color(110, 150, 85));

                        g2.fillRect(
                            x, y,
                            tileSize, tileSize
                        );
                    }

                    if (maze.getCell(row, col) == Maze.WALL) {

                        Image wallImage =
                            (row + col) % 2 == 0
                            ? wallSquare
                            : wallRectangle;

                        if (wallImage != null) {

                            g2.drawImage(
                                wallImage,
                                x, y,
                                tileSize, tileSize,
                                this
                            );

                        } else {

                            g2.setColor(new Color(70, 90, 65));

                            g2.fillRect(
                                x, y,
                                tileSize, tileSize
                            );
                        }
                    }

                    g2.setColor(new Color(0, 0, 0, 35));

                    g2.drawRect(
                        x, y,
                        tileSize, tileSize
                    );
                }
            }

            // DRAW VISITED CELLS

            for (int i = 0; i < visitedIndex; i++) {

                Point point = visited.get(i);

                int x = startX + point.y * tileSize;
                int y = startY + point.x * tileSize;

                g2.setColor(new Color(40, 120, 255, 165));

                g2.fillRect(
                    x, y,
                    tileSize, tileSize
                );
            }

            // DRAW SOLUTION PATH

            for (int i = 0; i < pathIndex; i++) {

                Point point = path.get(i);

                int x = startX + point.y * tileSize;
                int y = startY + point.x * tileSize;

                g2.setColor(new Color(255, 210, 40, 200));

                g2.fillRect(
                    x, y,
                    tileSize, tileSize
                );
            }

            // DRAW EXIT

            int exitX =
                startX + maze.getExitCol() * tileSize;

            int exitY =
                startY + maze.getExitRow() * tileSize;

            if (door != null) {

                g2.drawImage(
                    door,
                    exitX, exitY,
                    tileSize, tileSize,
                    this
                );
            }

            // DRAW PLAYER

            int playerRow = maze.getStartRow();
            int playerCol = maze.getStartCol();

            if (pathIndex > 0 && !path.isEmpty()) {

                Point current = path.get(
                    Math.min(pathIndex - 1, path.size() - 1)
                );

                playerRow = current.x;
                playerCol = current.y;
            }

            int playerX = startX + playerCol * tileSize;
            int playerY = startY + playerRow * tileSize;

            if (player != null) {

                g2.drawImage(
                    player,
                    playerX, playerY,
                    tileSize, tileSize,
                    this
                );
            }

            g2.dispose();
        }
    }
}
