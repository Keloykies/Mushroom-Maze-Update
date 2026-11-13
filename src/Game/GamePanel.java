
package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

import Algorithms.BFS;
import Algorithms.DFS;
import Entities.Player;
import Maze.Maze;
import Maze.MazeGenerator;
import Sounds.SoundManager;

public class GamePanel extends JPanel {

    // ==========================================
    // COLORS
    // ==========================================

    private static final Color DARK_GREEN = new Color(19, 35, 27);
    private static final Color PANEL_GREEN = new Color(29, 49, 37);
    private static final Color BUTTON_GREEN = new Color(55, 125, 65);
    private static final Color GOLD = new Color(255, 210, 80);
    private static final Color TEXT_COLOR = new Color(239, 245, 232);
    private static final Color CREAM = new Color(255, 244, 210);
    private static final Color VISITED_BLUE = new Color(40, 120, 255);
    private static final Color PATH_YELLOW = new Color(255, 210, 40);
    private static final Color COMPLETED_GREEN = new Color(55, 170, 85);

    // ==========================================
    // IMAGES
    // ==========================================

    private Image backgroundGrass;
    private Image wallSquare;
    private Image wallRectangle;
    private Image grass;
    private Image door;

    private Image player;
    private Image idleImage;
    private Image walkingLeft;
    private Image walkingRight;

    private Image completeLogo;

    private boolean facingLeft = false;

    // ==========================================
    // VICTORY POPUP
    // ==========================================

    private JWindow victoryWindow;
    private Timer victoryTimer;

    private long victoryStartTime;

    private static final int VICTORY_DURATION = 3000;
    private static final int VICTORY_ANIMATION_DURATION = 350;

    // ==========================================
    // MAZE DATA
    // ==========================================

    private Maze maze;
    private Player playerEntity;

    private String selectedMode;
    private int mazeSize;

    private boolean won = false;

    // ==========================================
    // MANUAL PLAY STATISTICS
    // ==========================================

    private ArrayList<Point> manualVisitedCells = new ArrayList<>();

    private int manualPathLength = 0;

    private long manualStartTime = 0;
    private long manualElapsedNanos = 0;

    private boolean manualPaused = false;
    private boolean manualStarted = false;

    private Timer manualStatsTimer;

    // ==========================================
    // MOVEMENT
    // ==========================================

    private Timer movementTimer;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    // ==========================================
    // ALGORITHM ANIMATION
    // ==========================================

    private Timer algorithmTimer;

    private ArrayList<Point> visitedCells = new ArrayList<>();
    private ArrayList<Point> solutionPath = new ArrayList<>();

    private int visitedIndex = 0;
    private int pathIndex = 0;

    private boolean solverFinished = false;
    private boolean solverPaused = false;

    // ==========================================
    // LIVE QUEUE / STACK DATA
    // ==========================================

    private ArrayList<ArrayList<Point>> frontierSnapshots = new ArrayList<>();

    private JLabel frontierTitleLabel;
    private JLabel frontierSizeLabel;
    private JTextArea frontierTextArea;

    // ==========================================
    // STEP-BY-STEP EXPLANATION
    // ==========================================

    private JTextArea explanationTextArea;

    // ==========================================
    // UI COMPONENTS
    // ==========================================

    private JPanel navigationBar;
    private JPanel statisticsPanel;
    private JPanel legendPanel;

    private MazeCanvas mazeCanvas;

    private JButton backButton;
    private JButton autoStartButton;
    private JButton changeMazeButton;

    private JComboBox<String> speedSelector;

    private JLabel modeLabel;
    private JLabel statusLabel;

    private JLabel visitedValue;
    private JLabel pathValue;
    private JLabel timeValue;

    private JLabel speedLabel;

    private JProgressBar solverProgressBar;

    private Runnable onBack;

    private double executionTime = 0;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public GamePanel(Runnable onBack) {

        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(DARK_GREEN);
        setFocusable(true);

        backgroundGrass = loadImage("/Assets/Background_Grass.png");
        wallSquare = loadImage("/Assets/Wall_Square.png");
        wallRectangle = loadImage("/Assets/Wall_Rectangle.png");
        grass = loadImage("/Assets/Grass.png");
        door = loadImage("/Assets/Door.png");

        idleImage = loadImage("/Assets/Idle.png");
        walkingLeft = loadImage("/Assets/Walking_Left.png");
        walkingRight = loadImage("/Assets/Walking_Right.png");

        // LOAD VICTORY LOGO
        completeLogo = loadImage("/Assets/COMPLETE.png");

        player = idleImage;

        createNavigationBar();
        createMazeArea();
        createStatisticsPanel();

        algorithmTimer = new Timer(35, e -> animateSolver());

        manualStatsTimer = new Timer(1000, e -> {

            if ("Manual Play".equals(selectedMode)
                    && manualStarted
                    && !manualPaused
                    && !won) {

                updateManualStopwatch();
            }
        });

        setupControls();
        setupKeyboardListener();
    }

    // ==========================================
    // IMAGE LOADER
    // ==========================================

    private Image loadImage(String path) {

        java.net.URL resource = getClass().getResource(path);

        if (resource == null) {
            System.out.println("Missing image: " + path);
            return null;
        }

        return new ImageIcon(resource).getImage();
    }

    // ==========================================
    // NAVIGATION BAR
    // ==========================================

    private void createNavigationBar() {

        navigationBar = new JPanel(new BorderLayout());
        navigationBar.setBackground(DARK_GREEN);

        navigationBar.setBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        );

        navigationBar.setPreferredSize(new Dimension(1000, 75));

        JPanel leftPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 0)
        );

        leftPanel.setOpaque(false);

        backButton = createButton("BACK", 85, 38);

        backButton.addActionListener(e -> {
            stopTimers();
            onBack.run();
        });

        JLabel gameTitle = new JLabel("MUSHROOM MAZE");

        gameTitle.setFont(
            new Font("Arial Rounded MT Bold", Font.BOLD, 17)
        );

        gameTitle.setForeground(GOLD);

        leftPanel.add(backButton);
        leftPanel.add(gameTitle);

        JPanel rightPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        rightPanel.setOpaque(false);

        modeLabel = new JLabel("GAME MODE");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        modeLabel.setForeground(TEXT_COLOR);

        speedLabel = new JLabel("SPEED:");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 11));
        speedLabel.setForeground(GOLD);

        speedSelector = new JComboBox<>(
            new String[]{"SLOW", "NORMAL", "FAST"}
        );

        speedSelector.setSelectedItem("NORMAL");
        speedSelector.setFont(new Font("Arial", Font.BOLD, 11));

        speedSelector.setUI(
            new javax.swing.plaf.basic.BasicComboBoxUI() {

                @Override
                protected JButton createArrowButton() {

                    JButton arrowButton = new JButton("▼");

                    arrowButton.setFont(
                        new Font("Arial", Font.BOLD, 10)
                    );

                    arrowButton.setForeground(CREAM);
                    arrowButton.setBackground(PANEL_GREEN);

                    arrowButton.setBorderPainted(false);
                    arrowButton.setFocusPainted(false);
                    arrowButton.setContentAreaFilled(false);
                    arrowButton.setOpaque(false);

                    return arrowButton;
                }
            }
        );

        speedSelector.setBackground(PANEL_GREEN);
        speedSelector.setForeground(TEXT_COLOR);
        speedSelector.setFocusable(false);

        speedSelector.setPreferredSize(new Dimension(90, 35));

        speedSelector.addActionListener(e -> {
            SoundManager.playClick();
            updateAnimationSpeed();
        });

        autoStartButton = createButton("AUTO START", 105, 38);

        autoStartButton.addActionListener(e -> {

            if ("Manual Play".equals(selectedMode)) {
                handleManualButton();
            } else {
                handleSolverButton();
            }
        });

        changeMazeButton = createButton("CHANGE MAZE", 115, 38);

        changeMazeButton.addActionListener(e -> changeMaze());

        rightPanel.add(modeLabel);
        rightPanel.add(speedLabel);
        rightPanel.add(speedSelector);
        rightPanel.add(autoStartButton);
        rightPanel.add(changeMazeButton);

        navigationBar.add(leftPanel, BorderLayout.WEST);
        navigationBar.add(rightPanel, BorderLayout.EAST);

        add(navigationBar, BorderLayout.NORTH);
    }

    // ==========================================
    // BUTTON DESIGN
    // ==========================================

    private JButton createButton(String text, int width, int height) {

        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(width, height));

        button.setFont(
            new Font("Arial Rounded MT Bold", Font.BOLD, 11)
        );

        button.setBackground(BUTTON_GREEN);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);

        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
            )
        );

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusable(false);

        button.addMouseListener(
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(77, 151, 86));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(BUTTON_GREEN);
                }
            }
        );

        return button;
    }

    // ==========================================
    // UPDATE ANIMATION SPEED
    // ==========================================

    private void updateAnimationSpeed() {

        if (algorithmTimer == null || speedSelector == null) {
            return;
        }

        String selectedSpeed =
            (String) speedSelector.getSelectedItem();

        int delay;

        if ("SLOW".equals(selectedSpeed)) {
            delay = 150;
        } else if ("FAST".equals(selectedSpeed)) {
            delay = 5;
        } else {
            delay = 35;
        }

        algorithmTimer.setDelay(delay);
        algorithmTimer.setInitialDelay(delay);
    }

    // ==========================================
    // MAZE AREA
    // ==========================================

    private void createMazeArea() {

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.setBackground(DARK_GREEN);

        centerPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 5, 20)
        );

        mazeCanvas = new MazeCanvas();

        centerPanel.add(mazeCanvas, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // STATISTICS PANEL
    // ==========================================

    private void createStatisticsPanel() {

        statisticsPanel = new JPanel(new BorderLayout());

        statisticsPanel.setBackground(DARK_GREEN);

        statisticsPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 20, 12, 20)
        );

        statusLabel = new JLabel("Ready to explore the maze!");

        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(GOLD);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout(0, 8));

        statusPanel.setOpaque(false);

        statusPanel.setBorder(
            BorderFactory.createEmptyBorder(3, 0, 8, 0)
        );

        statusPanel.add(statusLabel, BorderLayout.NORTH);

        // LIVE SOLVER PROGRESS BAR

        solverProgressBar = new JProgressBar(0, 100);

        solverProgressBar.setValue(0);
        solverProgressBar.setStringPainted(true);
        solverProgressBar.setString("0%");

        solverProgressBar.setFont(new Font("Arial", Font.BOLD, 10));

        solverProgressBar.setForeground(VISITED_BLUE);
        solverProgressBar.setBackground(PANEL_GREEN);

        solverProgressBar.setBorderPainted(false);

        solverProgressBar.setPreferredSize(new Dimension(100, 16));
        solverProgressBar.setVisible(false);

        statusPanel.add(solverProgressBar, BorderLayout.SOUTH);

        // STATISTICS CARDS

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 12, 0));

        cardsPanel.setOpaque(false);

        visitedValue = createStatValue("0");
        pathValue = createStatValue("0");
        timeValue = createStatValue("00:00:00");

        cardsPanel.add(createStatCard("VISITED CELLS", visitedValue));
        cardsPanel.add(createStatCard("PATH LENGTH", pathValue));
        cardsPanel.add(createStatCard("EXECUTION TIME", timeValue));

        // QUEUE / STACK PANEL

        JPanel frontierPanel = new JPanel(new BorderLayout(5, 5));

        frontierPanel.setBackground(PANEL_GREEN);

        frontierPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(77, 111, 78), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
            )
        );

        frontierPanel.setPreferredSize(new Dimension(300, 190));

        frontierTitleLabel = new JLabel("ALGORITHM DATA STRUCTURE");

        frontierTitleLabel.setFont(new Font("Arial", Font.BOLD, 10));
        frontierTitleLabel.setForeground(GOLD);

        frontierSizeLabel = new JLabel("Size: 0");

        frontierSizeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        frontierSizeLabel.setForeground(TEXT_COLOR);

        JPanel frontierHeader = new JPanel(new BorderLayout());

        frontierHeader.setOpaque(false);

        frontierHeader.add(frontierTitleLabel, BorderLayout.WEST);
        frontierHeader.add(frontierSizeLabel, BorderLayout.EAST);

        frontierTextArea = new JTextArea();

        frontierTextArea.setEditable(false);
        frontierTextArea.setFocusable(false);
        frontierTextArea.setLineWrap(true);
        frontierTextArea.setWrapStyleWord(true);

        frontierTextArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        frontierTextArea.setForeground(TEXT_COLOR);
        frontierTextArea.setBackground(PANEL_GREEN);

        frontierTextArea.setText("Waiting for algorithm...");

        JScrollPane frontierScroll = new JScrollPane(frontierTextArea);
        frontierScroll.setBorder(null);

        JLabel explanationTitle =
            new JLabel("STEP-BY-STEP EXPLANATION");

        explanationTitle.setFont(new Font("Arial", Font.BOLD, 10));
        explanationTitle.setForeground(GOLD);

        explanationTextArea = new JTextArea();

        explanationTextArea.setEditable(false);
        explanationTextArea.setFocusable(false);
        explanationTextArea.setLineWrap(true);
        explanationTextArea.setWrapStyleWord(true);

        explanationTextArea.setFont(new Font("Arial", Font.PLAIN, 10));
        explanationTextArea.setForeground(TEXT_COLOR);
        explanationTextArea.setBackground(PANEL_GREEN);

        explanationTextArea.setText(
            "Start the algorithm to see its explanation."
        );

        JScrollPane explanationScroll =
            new JScrollPane(explanationTextArea);

        explanationScroll.setBorder(null);

        JPanel frontierContent = new JPanel(new GridLayout(2, 1, 0, 8));
        frontierContent.setOpaque(false);

        JPanel queueSection = new JPanel(new BorderLayout(0, 5));
        queueSection.setOpaque(false);

        queueSection.add(frontierHeader, BorderLayout.NORTH);
        queueSection.add(frontierScroll, BorderLayout.CENTER);

        JPanel explanationSection = new JPanel(new BorderLayout(0, 5));
        explanationSection.setOpaque(false);

        explanationSection.add(explanationTitle, BorderLayout.NORTH);
        explanationSection.add(explanationScroll, BorderLayout.CENTER);

        frontierContent.add(queueSection);
        frontierContent.add(explanationSection);

        frontierPanel.add(frontierContent, BorderLayout.CENTER);

        // COMBINE STATISTICS AND DATA STRUCTURE

        JPanel statisticsContent =
            new JPanel(new BorderLayout(12, 0));

        statisticsContent.setOpaque(false);

        statisticsContent.add(cardsPanel, BorderLayout.CENTER);
        statisticsContent.add(frontierPanel, BorderLayout.EAST);

        // LEGEND

        legendPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 18, 8)
        );

        legendPanel.setOpaque(false);

        addLegend(legendPanel, VISITED_BLUE, "Visited");
        addLegend(legendPanel, PATH_YELLOW, "Solution Path");
        addLegend(legendPanel, new Color(110, 150, 85), "Open Path");

        statisticsPanel.add(statusPanel, BorderLayout.NORTH);
        statisticsPanel.add(statisticsContent, BorderLayout.CENTER);
        statisticsPanel.add(legendPanel, BorderLayout.SOUTH);

        statisticsPanel.setPreferredSize(new Dimension(1000, 245));

        add(statisticsPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // CREATE STATISTICS VALUE
    // ==========================================

    private JLabel createStatValue(String value) {

        JLabel label = new JLabel(value);

        label.setFont(
            new Font("Arial Rounded MT Bold", Font.BOLD, 17)
        );

        label.setForeground(GOLD);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }

    // ==========================================
    // CREATE STATISTICS CARD
    // ==========================================

    private JPanel createStatCard(String title, JLabel value) {

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(PANEL_GREEN);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(77, 111, 78), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 10));
        titleLabel.setForeground(new Color(190, 211, 190));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // ADD LEGEND
    // ==========================================

    private void addLegend(JPanel panel, Color color, String text) {

        JPanel item = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 5, 0)
        );

        item.setOpaque(false);

        JPanel colorBox = new JPanel();

        colorBox.setPreferredSize(new Dimension(12, 12));
        colorBox.setBackground(color);

        colorBox.setBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1)
        );

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setForeground(TEXT_COLOR);

        item.add(colorBox);
        item.add(label);

        panel.add(item);
    }

    // ==========================================
    // UPDATE QUEUE / STACK DISPLAY
    // ==========================================

    private void updateFrontierDisplay(int index) {

        if (frontierTextArea == null) {
            return;
        }

        if (index < 0 || index >= frontierSnapshots.size()) {
            return;
        }

        ArrayList<Point> snapshot = frontierSnapshots.get(index);

        StringBuilder display = new StringBuilder();

        if ("BFS Solver".equals(selectedMode)) {
            display.append("FRONT -> ");
        } else {
            display.append("TOP -> ");
        }

        if (snapshot.isEmpty()) {

            display.append("EMPTY");

        } else {

            for (int i = 0; i < snapshot.size(); i++) {

                Point point = snapshot.get(i);

                display.append("(")
                    .append(point.x + 1)
                    .append(", ")
                    .append(point.y + 1)
                    .append(")");

                if (i < snapshot.size() - 1) {
                    display.append("  |  ");
                }
            }
        }

        frontierTextArea.setText(display.toString());

        frontierSizeLabel.setText("Size: " + snapshot.size());

        frontierTextArea.setCaretPosition(0);
    }

    // ==========================================
    // UPDATE ALGORITHM EXPLANATION
    // ==========================================

    private void updateAlgorithmExplanation() {

        if (explanationTextArea == null) {
            return;
        }

        StringBuilder explanation = new StringBuilder();

        if (solverFinished && solutionPath.isEmpty()) {

            explanation.append(
                "The algorithm has finished searching.\n\n"
            );

            explanation.append(
                "No solution path was found for this maze."
            );

        } else if ("BFS Solver".equals(selectedMode)) {

            if (visitedIndex > 0 && visitedIndex <= visitedCells.size()) {

                Point current = visitedCells.get(visitedIndex - 1);

                explanation.append("BFS is exploring cell (")
                    .append(current.x + 1)
                    .append(", ")
                    .append(current.y + 1)
                    .append(").\n\n");

                explanation.append(
                    "The current cell was removed from the FRONT of the Queue.\n\n"
                );

                explanation.append(
                    "BFS checks neighboring cells and adds valid, unvisited cells to the BACK of the Queue.\n\n"
                );

                explanation.append("FIFO: First In, First Out.");
            }

        } else if ("DFS Solver".equals(selectedMode)) {

            if (visitedIndex > 0 && visitedIndex <= visitedCells.size()) {

                Point current = visitedCells.get(visitedIndex - 1);

                explanation.append("DFS is exploring cell (")
                    .append(current.x + 1)
                    .append(", ")
                    .append(current.y + 1)
                    .append(").\n\n");

                explanation.append(
                    "The current cell was removed from the TOP of the Stack.\n\n"
                );

                explanation.append(
                    "DFS explores an available neighboring cell and continues deeper before backtracking when necessary.\n\n"
                );

                explanation.append("LIFO: Last In, First Out.");
            }
        }

        if (!solverFinished
                && visitedIndex >= visitedCells.size()
                && pathIndex < solutionPath.size()) {

            Point current = solutionPath.get(pathIndex);

            explanation.setLength(0);

            explanation.append(
                "Exploration is complete.\n\n"
            );

            explanation.append(
                "The algorithm is now following the solution path.\n\n"
            );

            explanation.append("Current path cell: (")
                .append(current.x + 1)
                .append(", ")
                .append(current.y + 1)
                .append(").\n\n");

            explanation.append("Path progress: ")
                .append(pathIndex + 1)
                .append("/")
                .append(solutionPath.size());
        }

        if (solverFinished && !solutionPath.isEmpty()) {

            explanation.setLength(0);

            explanation.append(selectedMode)
                .append(" has completed the maze search.\n\n");

            explanation.append("Visited cells: ")
                .append(visitedCells.size())
                .append("\n\n");

            explanation.append("Solution path length: ")
                .append(solutionPath.size())
                .append("\n\n");

            explanation.append("Execution time: ")
                .append(String.format("%.4f ms", executionTime))
                .append("\n\n");

            explanation.append(
                "The algorithm has completed its search."
            );
        }

        if (explanation.length() == 0) {
            explanation.append(
                "Start the algorithm to see its explanation."
            );
        }

        explanationTextArea.setText(explanation.toString());
        explanationTextArea.setCaretPosition(0);
    }

    // ==========================================
    // SOLVER BUTTON
    // ==========================================

    private void handleSolverButton() {

        if (maze == null || won) {
            return;
        }

        if (algorithmTimer.isRunning()) {

            algorithmTimer.stop();
            solverPaused = true;

            autoStartButton.setText("RESUME");

            statusLabel.setText(
                "Solver paused. Press RESUME to continue."
            );

        } else if (solverPaused) {

            solverPaused = false;

            algorithmTimer.start();

            autoStartButton.setText("PAUSE");

            statusLabel.setText(
                "Solver resumed. Continuing exploration..."
            );

        } else {

            if ("BFS Solver".equals(selectedMode)) {
                runBFS();
            } else if ("DFS Solver".equals(selectedMode)) {
                runDFS();
            }
        }
    }

    // ==========================================
    // MANUAL PLAY BUTTON
    // ==========================================

    private void handleManualButton() {

        if (maze == null || won) {
            return;
        }

        if (manualPaused) {
            resumeManualPlay();
        } else {
            pauseManualPlay();
        }
    }

    // ==========================================
    // PAUSE MANUAL PLAY
    // ==========================================

    private void pauseManualPlay() {

        if (manualPaused || won) {
            return;
        }

        movementTimer.stop();
        manualStatsTimer.stop();

        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        if (manualStarted && manualStartTime > 0) {

            manualElapsedNanos +=
                System.nanoTime() - manualStartTime;

            manualStartTime = 0;

            updateManualStopwatch();
        }

        manualPaused = true;

        player = idleImage;

        autoStartButton.setText("RESUME");

        statusLabel.setText(
            "Game paused. Press RESUME to continue."
        );

        mazeCanvas.repaint();
    }

    // ==========================================
    // RESUME MANUAL PLAY
    // ==========================================

    private void resumeManualPlay() {

        if (!manualPaused || won) {
            return;
        }

        manualPaused = false;

        if (manualStarted) {
            manualStartTime = System.nanoTime();
            manualStatsTimer.start();
        }

        autoStartButton.setText("PAUSE");

        statusLabel.setText(
            "Manual Play resumed. Find the exit!"
        );

        requestFocusInWindow();
    }

    // ==========================================
    // MANUAL STOPWATCH
    // ==========================================

    private void updateManualStopwatch() {

        long elapsed = manualElapsedNanos;

        if (manualStartTime > 0) {
            elapsed += System.nanoTime() - manualStartTime;
        }

        long elapsedSeconds = elapsed / 1_000_000_000L;

        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;

        timeValue.setText(
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        );
    }

    // ==========================================
    // GAME INITIALIZATION
    // ==========================================

    public void startGame(String mode, int size) {

        stopTimers();

        selectedMode = mode;
        mazeSize = size;

        changeMaze();

        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    // ==========================================
    // CHANGE MAZE
    // ==========================================

    private void changeMaze() {

        stopTimers();

        solverPaused = false;
        solverFinished = false;

        statusLabel.setText("Ready to explore the maze!");

        autoStartButton.setText("AUTO START");

        maze = MazeGenerator.generateMaze(mazeSize, mazeSize);

        // INITIALIZE PLAYER OBJECT
        playerEntity = new Player(maze);

        facingLeft = false;
        player = idleImage;

        won = false;

        visitedCells.clear();
        solutionPath.clear();

        visitedIndex = 0;
        pathIndex = 0;

        // RESET QUEUE / STACK

        frontierSnapshots.clear();

        if (frontierTextArea != null) {
            frontierTextArea.setText("Waiting for algorithm...");
        }

        if (frontierSizeLabel != null) {
            frontierSizeLabel.setText("Size: 0");
        }

        if (frontierTitleLabel != null) {
            frontierTitleLabel.setText("ALGORITHM DATA STRUCTURE");
        }

        // RESET EXPLANATION

        if (explanationTextArea != null) {
            explanationTextArea.setText(
                "Start the algorithm to see its explanation."
            );
        }

        executionTime = 0;

        // RESET MANUAL STATISTICS

        manualVisitedCells.clear();

        manualPathLength = 0;

        manualStartTime = 0;
        manualElapsedNanos = 0;

        manualPaused = false;
        manualStarted = false;

        visitedValue.setText("0");
        pathValue.setText("0");

        timeValue.setText("00:00:00");

        // RESET SOLVER PROGRESS

        solverProgressBar.setValue(0);
        solverProgressBar.setString("0%");
        solverProgressBar.setForeground(VISITED_BLUE);

        // RESET SPEED TO NORMAL

        speedSelector.setSelectedItem("NORMAL");

        updateAnimationSpeed();

        boolean isSolver =
            "BFS Solver".equals(selectedMode) ||
            "DFS Solver".equals(selectedMode);

        boolean isManual = "Manual Play".equals(selectedMode);

        autoStartButton.setVisible(isSolver || isManual);

        if (isManual) {
            autoStartButton.setText("PAUSE");
        } else {
            autoStartButton.setText("AUTO START");
        }

        speedSelector.setVisible(isSolver);
        speedLabel.setVisible(isSolver);

        solverProgressBar.setVisible(isSolver);

        modeLabel.setText(
            selectedMode + "  |  " + mazeSize + " x " + mazeSize
        );

        mazeCanvas.repaint();
        revalidate();

        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    // ==========================================
    // STOP TIMERS
    // ==========================================

    private void stopTimers() {

        if (movementTimer != null) {
            movementTimer.stop();
        }

        if (algorithmTimer != null) {
            algorithmTimer.stop();
        }

        if (manualStatsTimer != null) {
            manualStatsTimer.stop();
        }

        // CLOSE VICTORY POPUP IF IT IS OPEN

        if (victoryTimer != null) {
            victoryTimer.stop();
            victoryTimer = null;
        }

        if (victoryWindow != null) {
            victoryWindow.dispose();
            victoryWindow = null;
        }

        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        player = idleImage;
    }

    // ==========================================
    // MOVEMENT TIMER
    // ==========================================

    private void setupControls() {

        movementTimer = new Timer(130, e -> {

            if (maze == null || playerEntity == null
                    || won || manualPaused) {
                return;
            }

            int rowChange = 0;
            int colChange = 0;

            if (upPressed) {
                rowChange = -1;
            } else if (downPressed) {
                rowChange = 1;
            } else if (leftPressed) {
                colChange = -1;
            } else if (rightPressed) {
                colChange = 1;
            }

            if (rowChange != 0 || colChange != 0) {
                movePlayer(rowChange, colChange);
            }
        });
    }

    // ==========================================
    // KEYBOARD CONTROLS
    // ==========================================

    private void setupKeyboardListener() {

        bindMovementKey(
            KeyEvent.VK_UP, "MOVE_UP",
            () -> upPressed = true,
            () -> upPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_DOWN, "MOVE_DOWN",
            () -> downPressed = true,
            () -> downPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_LEFT, "MOVE_LEFT",
            () -> leftPressed = true,
            () -> leftPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_RIGHT, "MOVE_RIGHT",
            () -> rightPressed = true,
            () -> rightPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_W, "MOVE_W",
            () -> upPressed = true,
            () -> upPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_S, "MOVE_S",
            () -> downPressed = true,
            () -> downPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_A, "MOVE_A",
            () -> leftPressed = true,
            () -> leftPressed = false
        );

        bindMovementKey(
            KeyEvent.VK_D, "MOVE_D",
            () -> rightPressed = true,
            () -> rightPressed = false
        );
    }

    private void bindMovementKey(
        int keyCode,
        String name,
        Runnable onPress,
        Runnable onRelease
    ) {

        InputMap inputMap = getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actionMap = getActionMap();

        inputMap.put(
            KeyStroke.getKeyStroke(keyCode, 0, false),
            name + "_PRESSED"
        );

        inputMap.put(
            KeyStroke.getKeyStroke(keyCode, 0, true),
            name + "_RELEASED"
        );

        actionMap.put(
            name + "_PRESSED",
            new AbstractAction() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (maze == null || playerEntity == null
                            || won || manualPaused) {
                        return;
                    }

                    if (algorithmTimer.isRunning()) {

                        algorithmTimer.stop();

                        solverPaused = false;
                        solverFinished = false;

                        autoStartButton.setText("AUTO START");

                        statusLabel.setText(
                            "Manual control activated."
                        );
                    }

                    onPress.run();

                    if (!movementTimer.isRunning()) {

                        int rowChange = 0;
                        int colChange = 0;

                        if (upPressed) {
                            rowChange = -1;
                        } else if (downPressed) {
                            rowChange = 1;
                        } else if (leftPressed) {
                            colChange = -1;
                        } else if (rightPressed) {
                            colChange = 1;
                        }

                        if (rowChange != 0 || colChange != 0) {
                            movePlayer(rowChange, colChange);
                        }

                        if (!won && !manualPaused) {
                            movementTimer.start();
                        }
                    }
                }
            }
        );

        actionMap.put(
            name + "_RELEASED",
            new AbstractAction() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    onRelease.run();

                    if (!upPressed && !downPressed
                            && !leftPressed && !rightPressed) {

                        movementTimer.stop();

                        player = idleImage;

                        mazeCanvas.repaint();
                    }
                }
            }
        );
    }

    // ==========================================
    // PLAYER MOVEMENT
    // ==========================================

    private void movePlayer(int rowChange, int colChange) {

        if (maze == null || playerEntity == null
                || won || manualPaused) {
            return;
        }

        // USE PLAYER CLASS FOR MOVEMENT
        boolean moved = playerEntity.move(
            maze,
            rowChange,
            colChange
        );

        if (moved) {

            // PLAYER MOVEMENT SOUND
            SoundManager.playMove();

            if ("Manual Play".equals(selectedMode)
                    && !manualStarted
                    && !manualPaused) {

                manualStarted = true;
                manualStartTime = System.nanoTime();

                manualStatsTimer.start();
            }

            if ("Manual Play".equals(selectedMode)) {

                Point currentCell = new Point(
                    playerEntity.getRow(),
                    playerEntity.getCol()
                );

                manualPathLength++;

                if (!manualVisitedCells.contains(currentCell)) {
                    manualVisitedCells.add(currentCell);
                }

                visitedValue.setText(
                    String.valueOf(manualVisitedCells.size())
                );

                pathValue.setText(
                    String.valueOf(manualPathLength)
                );
            }

            if (colChange < 0) {

                facingLeft = true;
                player = walkingLeft;

            } else if (colChange > 0) {

                facingLeft = false;
                player = walkingRight;

            } else {

                player = idleImage;
            }

            mazeCanvas.repaint();

            // CHECK IF PLAYER REACHED THE EXIT
            if (playerEntity.hasReachedExit(maze)) {

                stopTimers();
                winGame();
            }
        }
    }

    // ==========================================
    // RUN BFS
    // ==========================================

    private void runBFS() {

        if (!"BFS Solver".equals(selectedMode) || maze == null
                || playerEntity == null) {
            return;
        }

        stopTimers();

        solverPaused = false;

        autoStartButton.setText("PAUSE");

        statusLabel.setText("BFS is exploring the maze...");

        long startTime = System.nanoTime();

        BFS.SearchResult result = BFS.findPath(maze);

        long endTime = System.nanoTime();

        executionTime = (endTime - startTime) / 1_000_000.0;

        visitedCells = result.getVisitedOrder();
        solutionPath = result.getPath();

        // LOAD BFS QUEUE SNAPSHOTS

        frontierSnapshots = result.getQueueSnapshots();

        frontierTitleLabel.setText("BFS QUEUE (FIFO)");
        frontierTextArea.setText("Queue is ready...");
        frontierSizeLabel.setText("Size: 0");

        // RESET PLAYER USING PLAYER CLASS
        playerEntity.reset(maze);

        player = idleImage;
        facingLeft = false;

        visitedIndex = 0;
        pathIndex = 0;

        updateStatistics();

        startSolverAnimation();
    }

    // ==========================================
    // RUN DFS
    // ==========================================

    private void runDFS() {

        if (!"DFS Solver".equals(selectedMode) || maze == null
                || playerEntity == null) {
            return;
        }

        stopTimers();

        solverPaused = false;

        autoStartButton.setText("PAUSE");

        statusLabel.setText("DFS is exploring the maze...");

        long startTime = System.nanoTime();

        DFS.SearchResult result = DFS.findPath(maze);

        long endTime = System.nanoTime();

        executionTime = (endTime - startTime) / 1_000_000.0;

        visitedCells = result.getVisitedOrder();
        solutionPath = result.getPath();

        // LOAD DFS STACK SNAPSHOTS

        frontierSnapshots = result.getStackSnapshots();

        frontierTitleLabel.setText("DFS STACK (LIFO)");
        frontierTextArea.setText("Stack is ready...");
        frontierSizeLabel.setText("Size: 0");

        // RESET PLAYER USING PLAYER CLASS
        playerEntity.reset(maze);

        player = idleImage;
        facingLeft = false;

        visitedIndex = 0;
        pathIndex = 0;

        updateStatistics();

        startSolverAnimation();
    }

    // ==========================================
    // UPDATE ALGORITHM STATISTICS
    // ==========================================

    private void updateStatistics() {

        visitedValue.setText("0");
        pathValue.setText("0");

        timeValue.setText(
            String.format("%.4f ms", executionTime)
        );
    }

    // ==========================================
    // START SOLVER ANIMATION
    // ==========================================

    private void startSolverAnimation() {

        visitedIndex = 0;
        pathIndex = 0;

        solverFinished = false;
        solverPaused = false;

        autoStartButton.setText("PAUSE");

        solverProgressBar.setForeground(VISITED_BLUE);
        solverProgressBar.setValue(0);
        solverProgressBar.setString("0%");

        if (visitedCells.isEmpty() && solutionPath.isEmpty()) {

            solverFinished = true;

            autoStartButton.setText("AUTO START");

            statusLabel.setText("No solution was found.");

            updateAlgorithmExplanation();

            mazeCanvas.repaint();

            return;
        }

        statusLabel.setText(
            selectedMode + " is exploring the maze..."
        );

        updateAlgorithmExplanation();

        algorithmTimer.start();
    }

    // ==========================================
    // ANIMATE BFS / DFS
    // ==========================================

    private void animateSolver() {

        if (playerEntity == null || maze == null) {
            algorithmTimer.stop();
            return;
        }

        // PHASE 1: VISITED CELLS - BLUE

        if (visitedIndex < visitedCells.size()) {

            solverProgressBar.setForeground(VISITED_BLUE);

            visitedIndex++;

            visitedValue.setText(String.valueOf(visitedIndex));

            updateFrontierDisplay(visitedIndex - 1);
            updateAlgorithmExplanation();

            statusLabel.setText(
                selectedMode + " is exploring... Visited: " + visitedIndex
            );
        }

        // PHASE 2: SOLUTION PATH - YELLOW

        else if (pathIndex < solutionPath.size()) {

            solverProgressBar.setForeground(PATH_YELLOW);

            Point current = solutionPath.get(pathIndex);

            int previousCol = playerEntity.getCol();

            // MOVE PLAYER USING PLAYER CLASS
            boolean moved = playerEntity.moveTo(
                maze,
                current.x,
                current.y
            );

            if (moved) {

                // PLAYER MOVEMENT SOUND
                SoundManager.playMove();

                int currentCol = playerEntity.getCol();

                if (currentCol < previousCol) {

                    facingLeft = true;
                    player = walkingLeft;

                } else if (currentCol > previousCol) {

                    facingLeft = false;
                    player = walkingRight;

                } else {

                    player = idleImage;
                }
            }

            pathIndex++;

            pathValue.setText(String.valueOf(pathIndex));

            updateAlgorithmExplanation();

            statusLabel.setText(
                selectedMode + " is following the solution... "
                + pathIndex + "/" + solutionPath.size()
            );
        }

        // UPDATE LIVE PROGRESS

        int totalSteps = visitedCells.size() + solutionPath.size();

        int completedSteps = visitedIndex + pathIndex;

        int progress = totalSteps == 0
            ? 0
            : (int) (completedSteps * 100.0 / totalSteps);

        solverProgressBar.setValue(progress);
        solverProgressBar.setString(progress + "%");

        // PHASE 3: COMPLETION - GREEN

        if (visitedIndex >= visitedCells.size()
                && pathIndex >= solutionPath.size()) {

            algorithmTimer.stop();

            solverFinished = true;
            solverPaused = false;

            player = idleImage;

            solverProgressBar.setForeground(COMPLETED_GREEN);
            solverProgressBar.setValue(100);
            solverProgressBar.setString("100%");

            autoStartButton.setText("AUTO START");

            statusLabel.setText(
                selectedMode + " completed successfully!"
            );

            updateAlgorithmExplanation();

            mazeCanvas.repaint();

            // SHOW VICTORY POPUP FOR BFS / DFS
            // ONLY IF A VALID SOLUTION WAS FOUND
            // AND THE PLAYER REACHED THE EXIT

            if (!solutionPath.isEmpty()
                    && playerEntity.hasReachedExit(maze)) {

                winGame();
            }
        }

        mazeCanvas.repaint();
    }

    // ==========================================
    // BLUR BACKGROUND
    // ==========================================

    private BufferedImage blurImage(BufferedImage original) {

        float[] matrix = {
            1, 4, 6, 4, 1,
            4, 16, 24, 16, 4,
            6, 24, 36, 24, 6,
            4, 16, 24, 16, 4,
            1, 4, 6, 4, 1
        };

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] /= 256f;
        }

        Kernel kernel = new Kernel(5, 5, matrix);

        ConvolveOp blur = new ConvolveOp(
            kernel,
            ConvolveOp.EDGE_NO_OP,
            null
        );

        BufferedImage blurred = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );

        blur.filter(original, blurred);

        return blurred;
    }

    // ==========================================
    // SHOW VICTORY POPUP
    // ==========================================

    private void showVictoryPopup() {

        if (completeLogo == null) {
            System.out.println("Missing image: /Assets/COMPLETE.png");
            return;
        }

        if (!isShowing() || getWidth() <= 0 || getHeight() <= 0) {
            System.out.println("Game panel is not visible.");
            return;
        }

        try {

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // CAPTURE CURRENT GAME PANEL

            BufferedImage screenshot = new BufferedImage(
                panelWidth,
                panelHeight,
                BufferedImage.TYPE_INT_RGB
            );

            Graphics2D screenshotGraphics = screenshot.createGraphics();

            printAll(screenshotGraphics);

            screenshotGraphics.dispose();

            // BLUR THE CAPTURED BACKGROUND

            BufferedImage blurredBackground = blurImage(screenshot);

            Window owner = SwingUtilities.getWindowAncestor(this);

            victoryWindow = new JWindow(owner);

            victoryWindow.setSize(panelWidth, panelHeight);
            victoryWindow.setLocation(getLocationOnScreen());

            JComponent overlay = new JComponent() {

                @Override
                protected void paintComponent(Graphics g) {

                    super.paintComponent(g);

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR
                    );

                    g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    // BLURRED BACKGROUND

                    g2.drawImage(
                        blurredBackground,
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        this
                    );

                    // DARKEN BACKGROUND

                    g2.setColor(new Color(0, 0, 0, 115));

                    g2.fillRect(
                        0,
                        0,
                        getWidth(),
                        getHeight()
                    );

                    // ANIMATION PROGRESS

                    long elapsed = System.currentTimeMillis()
                        - victoryStartTime;

                    float progress = Math.min(
                        1f,
                        elapsed / (float) VICTORY_ANIMATION_DURATION
                    );

                    // SMOOTH POP-UP EFFECT

                    float eased = 1f - (float) Math.pow(1f - progress, 3);

                    float scale = 0.75f + (0.30f * eased);

                    int originalWidth = completeLogo.getWidth(this);
                    int originalHeight = completeLogo.getHeight(this);

                    if (originalWidth > 0 && originalHeight > 0) {

                        // KEEP LOGO PROPORTIONS

                        int maxWidth = (int) (getWidth() * 0.65);
                        int maxHeight = (int) (getHeight() * 0.70);

                        double fitScale = Math.min(
                            maxWidth / (double) originalWidth,
                            maxHeight / (double) originalHeight
                        );

                        int logoWidth = (int) (
                            originalWidth * fitScale * scale
                        );

                        int logoHeight = (int) (
                            originalHeight * fitScale * scale
                        );

                        int logoX = (getWidth() - logoWidth) / 2;
                        int logoY = (getHeight() - logoHeight) / 2;

                        // DRAW CENTERED LOGO

                        g2.drawImage(
                            completeLogo,
                            logoX,
                            logoY,
                            logoWidth,
                            logoHeight,
                            this
                        );
                    }

                    g2.dispose();
                }
            };

            overlay.setOpaque(false);

            victoryWindow.setContentPane(overlay);

            // SET TIME BEFORE FIRST PAINT

            victoryStartTime = System.currentTimeMillis();

            victoryWindow.setVisible(true);

            // UPDATE ANIMATION AND AUTO CLOSE

            victoryTimer = new Timer(16, e -> {

                long elapsed = System.currentTimeMillis()
                    - victoryStartTime;

                if (elapsed >= VICTORY_DURATION) {

                    victoryTimer.stop();

                    victoryWindow.dispose();

                    victoryTimer = null;
                    victoryWindow = null;

                    requestFocusInWindow();

                } else {

                    overlay.repaint();
                }
            });

            victoryTimer.start();

        } catch (Exception ex) {

            System.out.println(
                "Unable to display victory popup: " + ex.getMessage()
            );
        }
    }

    // ==========================================
    // VICTORY
    // ==========================================

    private void winGame() {

        // PREVENT MULTIPLE VICTORY TRIGGERS

        if (won) {
            return;
        }

        won = true;

        // PLAY VICTORY SOUND
        SoundManager.playVictory();

        movementTimer.stop();
        algorithmTimer.stop();
        manualStatsTimer.stop();

        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        if ("Manual Play".equals(selectedMode)
                && manualStarted
                && manualStartTime > 0) {

            manualElapsedNanos +=
                System.nanoTime() - manualStartTime;

            manualStartTime = 0;

            updateManualStopwatch();
        }

        manualPaused = false;

        player = idleImage;

        statusLabel.setText(
            "Congratulations! Maze completed!"
        );

        mazeCanvas.repaint();

        // SHOW CUSTOM VICTORY POPUP INSTEAD OF DIALOG

        showVictoryPopup();
    }

    // ==========================================
    // MAZE CANVAS
    // ==========================================

    private class MazeCanvas extends JPanel {

        public MazeCanvas() {

            setBackground(new Color(35, 57, 39));

            setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        new Color(116, 151, 95), 2
                    ),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
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

            if (backgroundGrass != null) {

                g2.drawImage(
                    backgroundGrass,
                    0, 0,
                    getWidth(),
                    getHeight(),
                    this
                );
            }

            if (maze == null) {
                g2.dispose();
                return;
            }

            drawMaze(g2);

            g2.dispose();
        }

        // ======================================
        // DRAW MAZE
        // ======================================

        private void drawMaze(Graphics2D g2) {

            int margin = 20;

            int availableWidth = getWidth() - margin * 2;
            int availableHeight = getHeight() - margin * 2;

            int tileSize = Math.min(
                availableWidth / maze.getCols(),
                availableHeight / maze.getRows()
            );

            tileSize = Math.min(tileSize, 48);
            tileSize = Math.max(tileSize, 1);

            int mazeWidth = tileSize * maze.getCols();
            int mazeHeight = tileSize * maze.getRows();

            int startX = (getWidth() - mazeWidth) / 2;
            int startY = (getHeight() - mazeHeight) / 2;

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

            // VISITED CELLS

            for (int i = 0; i < visitedIndex; i++) {

                Point point = visitedCells.get(i);

                int x = startX + point.y * tileSize;
                int y = startY + point.x * tileSize;

                g2.setColor(new Color(40, 120, 255, 150));

                g2.fillRect(
                    x, y,
                    tileSize, tileSize
                );
            }

            // SOLUTION PATH

            for (int i = 0; i < pathIndex; i++) {

                Point point = solutionPath.get(i);

                int x = startX + point.y * tileSize;
                int y = startY + point.x * tileSize;

                g2.setColor(new Color(255, 210, 40, 190));

                g2.fillRect(
                    x, y,
                    tileSize, tileSize
                );
            }

            // EXIT DOOR

            int exitX = startX + maze.getExitCol() * tileSize;
            int exitY = startY + maze.getExitRow() * tileSize;

            if (door != null) {

                g2.drawImage(
                    door,
                    exitX, exitY,
                    tileSize, tileSize,
                    this
                );
            }

            // PLAYER - POSITION FROM PLAYER CLASS

            if (playerEntity != null) {

                int playerX =
                    startX + playerEntity.getCol() * tileSize;

                int playerY =
                    startY + playerEntity.getRow() * tileSize;

                if (player != null) {

                    g2.drawImage(
                        player,
                        playerX, playerY,
                        tileSize, tileSize,
                        this
                    );
                }
            }
        }
    }
}
