package Algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class PerformanceDashboard extends JPanel {

    // ==========================================
    // THEME COLORS
    // ==========================================

    private static final Color BACKGROUND =
        new Color(19, 29, 25);

    private static final Color PANEL =
        new Color(29, 43, 36);

    private static final Color CARD =
        new Color(38, 55, 45);

    private static final Color GREEN =
        new Color(75, 145, 91);

    private static final Color LIGHT_GREEN =
        new Color(164, 211, 153);

    private static final Color TEXT =
        new Color(235, 242, 235);

    private static final Color MUTED =
        new Color(164, 181, 168);

    private static final Color BFS_COLOR =
        new Color(75, 155, 235);

    private static final Color DFS_COLOR =
        new Color(235, 153, 76);

    private static final Color ASTAR_COLOR =
        new Color(174, 120, 230);

    private static final Color[] ALGORITHM_COLORS = {
        BFS_COLOR,
        DFS_COLOR,
        ASTAR_COLOR
    };

    private static final String[] ALGORITHMS = {
        "BFS",
        "DFS",
        "A*"
    };

    private static final String CSV_FILE =
        "MushroomMaze_Benchmark.csv";

    // ==========================================
    // CSV COLUMN INDEXES
    // ==========================================

    private static final int CSV_TIME = 2;
    private static final int CSV_VISITED = 3;
    private static final int CSV_MEMORY = 4;

    // ==========================================
    // GRAPH DATA INDEXES
    // ==========================================

    // Execution time
    private static final int BFS_TIME = 0;
    private static final int DFS_TIME = 1;
    private static final int ASTAR_TIME = 2;

    // Memory allocation
    private static final int BFS_MEMORY = 3;
    private static final int DFS_MEMORY = 4;
    private static final int ASTAR_MEMORY = 5;

    // Visited cells
    private static final int BFS_VISITED = 6;
    private static final int DFS_VISITED = 7;
    private static final int ASTAR_VISITED = 8;

    // ==========================================
    // DATA AND UI COMPONENTS
    // ==========================================

    private final List<String[]> results =
        new ArrayList<>();

    private final List<JButton> navigationButtons =
        new ArrayList<>();

    private final Runnable onBack;

    private JPanel navigationPanel;
    private JPanel pageContainer;
    private JPanel summaryPanel;

    private CardLayout pageLayout;

    private JButton runButton;
    private JButton exportButton;

    private JLabel statusLabel;

    private String currentPage = "OVERVIEW";

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PerformanceDashboard(Runnable onBack) {

        this.onBack = onBack;

        setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        loadCSV();

        pageLayout = new CardLayout();

        pageContainer = new JPanel(pageLayout);
        pageContainer.setBackground(BACKGROUND);

        navigationPanel = new JPanel();

        navigationPanel.setLayout(
            new BoxLayout(
                navigationPanel,
                BoxLayout.Y_AXIS
            )
        );

        navigationPanel.setBackground(PANEL);

        add(createHeader(), BorderLayout.NORTH);

        buildPages();

        add(createDashboardBody(), BorderLayout.CENTER);
    }

    // ==========================================
    // MAIN DASHBOARD BODY
    // ==========================================

    private JPanel createDashboardBody() {

        JPanel body = new JPanel(new BorderLayout());

        body.setBackground(BACKGROUND);

        body.add(createSidebar(), BorderLayout.WEST);
        body.add(createContentArea(), BorderLayout.CENTER);

        return body;
    }

    // ==========================================
    // LEFT SIDEBAR
    // ==========================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setPreferredSize(
            new Dimension(185, 0)
        );

        sidebar.setBackground(PANEL);

        sidebar.setBorder(
            BorderFactory.createMatteBorder(
                0,
                0,
                0,
                1,
                new Color(57, 78, 63)
            )
        );

        JPanel sidebarHeader = new JPanel();

        sidebarHeader.setLayout(
            new BoxLayout(
                sidebarHeader,
                BoxLayout.Y_AXIS
            )
        );

        sidebarHeader.setBackground(PANEL);

        sidebarHeader.setBorder(
            BorderFactory.createEmptyBorder(
                18,
                15,
                15,
                10
            )
        );

        JLabel heading = new JLabel("ANALYTICS");

        heading.setForeground(LIGHT_GREEN);

        heading.setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        JLabel description =
            new JLabel("Dashboard sections");

        description.setForeground(MUTED);

        description.setFont(
            new Font("Arial", Font.PLAIN, 10)
        );

        sidebarHeader.add(heading);
        sidebarHeader.add(Box.createVerticalStrut(5));
        sidebarHeader.add(description);

        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        JScrollPane navigationScroll =
            new JScrollPane(navigationPanel);

        navigationScroll.setBorder(null);

        navigationScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        navigationScroll.getViewport().setBackground(PANEL);

        navigationScroll.getVerticalScrollBar()
            .setUnitIncrement(15);

        sidebar.add(
            navigationScroll,
            BorderLayout.CENTER
        );

        return sidebar;
    }

    // ==========================================
    // RIGHT CONTENT AREA
    // ==========================================

    private JPanel createContentArea() {

        JPanel content = new JPanel(new BorderLayout());

        content.setBackground(BACKGROUND);

        content.setBorder(
            BorderFactory.createEmptyBorder(
                12,
                15,
                12,
                15
            )
        );

        content.add(
            pageContainer,
            BorderLayout.CENTER
        );

        return content;
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    private void buildPages() {

        pageContainer.removeAll();

        navigationPanel.removeAll();

        navigationButtons.clear();

        addNavigationItem("OVERVIEW", "Overview");
        addNavigationItem("RESULTS", "Results Table");
        addNavigationItem("EXECUTION", "Execution Time");
        addNavigationItem("MEMORY", "Memory Usage");
        addNavigationItem("COMPLEXITY", "Complexity");

        addNavigationItem(
            "BFS_DFS",
            "Algorithm Summary"
        );

        addNavigationItem(
            "ALGORITHM",
            "Algorithm Comparison"
        );

        addNavigationItem(
            "MEMORY_COMPARE",
            "Memory Comparison"
        );

        addNavigationItem(
            "VISITED",
            "Visited Cells"
        );

        addNavigationItem(
            "INSIGHTS",
            "Insights"
        );

        pageContainer.add(
            createOverviewPanel(),
            "OVERVIEW"
        );

        pageContainer.add(
            createResultsPanel(),
            "RESULTS"
        );

        pageContainer.add(
            createChartPanel(
                "Execution Time by Maze Size",
                "Execution Time (ms)",
                CSV_TIME,
                false
            ),
            "EXECUTION"
        );

        pageContainer.add(
            createChartPanel(
                "Thread-Allocated Memory by Maze Size",
                "Allocated Memory (KB)",
                CSV_MEMORY,
                false
            ),
            "MEMORY"
        );

        pageContainer.add(
            createAnalysisPanel(),
            "COMPLEXITY"
        );

        pageContainer.add(
            createComparisonPanel(),
            "BFS_DFS"
        );

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS vs A* - Execution Time",
                "Execution Time (ms)",
                CSV_TIME,
                true
            ),
            "ALGORITHM"
        );

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS vs A* - Memory Allocation",
                "Allocated Memory (KB)",
                CSV_MEMORY,
                true
            ),
            "MEMORY_COMPARE"
        );

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS vs A* - Visited Cells",
                "Number of Visited Cells",
                CSV_VISITED,
                true
            ),
            "VISITED"
        );

        pageContainer.add(
            createInsightsPanel(),
            "INSIGHTS"
        );

        navigationPanel.add(
            Box.createVerticalStrut(5)
        );

        navigationPanel.revalidate();
        navigationPanel.repaint();

        updateSummaryCards();

        showPage(currentPage);
    }

    private void addNavigationItem(
        String key,
        String label
    ) {

        JButton button = new JButton(label);

        button.setFont(
            new Font("Arial", Font.BOLD, 11)
        );

        button.setForeground(TEXT);
        button.setBackground(PANEL);

        button.setHorizontalAlignment(
            SwingConstants.LEFT
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                38
            )
        );

        button.setPreferredSize(
            new Dimension(175, 38)
        );

        button.setBorder(
            BorderFactory.createEmptyBorder(
                0,
                13,
                0,
                5
            )
        );

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.addActionListener(
            e -> showPage(key)
        );

        navigationButtons.add(button);

        navigationPanel.add(button);

        navigationPanel.add(
            Box.createVerticalStrut(3)
        );
    }

    private void showPage(String key) {

        currentPage = key;

        pageLayout.show(pageContainer, key);

        String[] keys = {
            "OVERVIEW",
            "RESULTS",
            "EXECUTION",
            "MEMORY",
            "COMPLEXITY",
            "BFS_DFS",
            "ALGORITHM",
            "MEMORY_COMPARE",
            "VISITED",
            "INSIGHTS"
        };

        for (int i = 0; i < navigationButtons.size(); i++) {

            JButton button = navigationButtons.get(i);

            if (keys[i].equals(key)) {

                button.setBackground(
                    new Color(53, 91, 64)
                );

                button.setForeground(LIGHT_GREEN);

                button.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                            0,
                            3,
                            0,
                            0,
                            LIGHT_GREEN
                        ),
                        BorderFactory.createEmptyBorder(
                            0,
                            10,
                            0,
                            5
                        )
                    )
                );

            } else {

                button.setBackground(PANEL);
                button.setForeground(TEXT);

                button.setBorder(
                    BorderFactory.createEmptyBorder(
                        0,
                        13,
                        0,
                        5
                    )
                );
            }
        }

        pageContainer.revalidate();
        pageContainer.repaint();
    }

    // ==========================================
    // HEADER
    // ==========================================

    private JPanel createHeader() {

        JPanel wrapper = new JPanel(new BorderLayout());

        wrapper.setBackground(BACKGROUND);

        wrapper.setBorder(
            BorderFactory.createEmptyBorder(
                12,
                18,
                10,
                18
            )
        );

        JPanel header =
            new JPanel(new BorderLayout(10, 8));

        header.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
            new BoxLayout(
                titlePanel,
                BoxLayout.Y_AXIS
            )
        );

        JLabel title = new JLabel("MUSHROOM MAZE");

        title.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
            "ALGORITHM PERFORMANCE ANALYTICS"
        );

        subtitle.setFont(
            new Font("Arial", Font.PLAIN, 10)
        );

        subtitle.setForeground(LIGHT_GREEN);

        titlePanel.add(title);

        titlePanel.add(
            Box.createVerticalStrut(3)
        );

        titlePanel.add(subtitle);

        JPanel actions = new JPanel(
            new FlowLayout(
                FlowLayout.RIGHT,
                6,
                0
            )
        );

        actions.setOpaque(false);

        statusLabel = new JLabel("Ready");

        statusLabel.setForeground(MUTED);

        statusLabel.setFont(
            new Font("Arial", Font.PLAIN, 10)
        );

        JButton backButton = createButton(
            "BACK TO MENU",
            new Color(66, 91, 73)
        );

        exportButton = createButton(
            "EXPORT CSV",
            new Color(66, 91, 73)
        );

        runButton = createButton(
            "RUN BENCHMARK",
            GREEN
        );

        backButton.addActionListener(e -> {

            if (onBack != null) {
                onBack.run();
            }
        });

        exportButton.addActionListener(
            e -> exportCSV()
        );

        runButton.addActionListener(
            e -> runBenchmark()
        );

        actions.add(statusLabel);
        actions.add(backButton);
        actions.add(exportButton);
        actions.add(runButton);

        header.add(
            titlePanel,
            BorderLayout.WEST
        );

        header.add(
            actions,
            BorderLayout.EAST
        );

        wrapper.add(
            header,
            BorderLayout.NORTH
        );

        summaryPanel = new JPanel(
            new GridLayout(1, 5, 8, 0)
        );

        summaryPanel.setBackground(BACKGROUND);

        summaryPanel.setBorder(
            BorderFactory.createEmptyBorder(
                10,
                0,
                0,
                0
            )
        );

        updateSummaryCards();

        wrapper.add(
            summaryPanel,
            BorderLayout.CENTER
        );

        return wrapper;
    }

    private JButton createButton(
        String text,
        Color color
    ) {

        JButton button = new JButton(text);

        button.setFont(
            new Font("Arial", Font.BOLD, 10)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createEmptyBorder(
                9,
                11,
                9,
                11
            )
        );

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // ==========================================
    // SUMMARY CARDS
    // ==========================================

    private void updateSummaryCards() {

        if (summaryPanel == null) {
            return;
        }

        summaryPanel.removeAll();

        TreeSet<Integer> sizes = new TreeSet<>();

        int bfsCount = 0;
        int dfsCount = 0;
        int aStarCount = 0;

        for (String[] row : results) {

            try {

                sizes.add(
                    Integer.parseInt(row[0])
                );

                if (row[1].equalsIgnoreCase("BFS")) {
                    bfsCount++;
                }

                if (row[1].equalsIgnoreCase("DFS")) {
                    dfsCount++;
                }

                if (row[1].equalsIgnoreCase("A*")) {
                    aStarCount++;
                }

            } catch (Exception ignored) {
            }
        }

        summaryPanel.add(
            createSummaryCard(
                "MAZE SIZES",
                String.valueOf(sizes.size()),
                "Different input sizes"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "BFS RECORDS",
                String.valueOf(bfsCount),
                "Benchmark results"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "DFS RECORDS",
                String.valueOf(dfsCount),
                "Benchmark results"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "A* RECORDS",
                String.valueOf(aStarCount),
                "Benchmark results"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "TOTAL RECORDS",
                String.valueOf(results.size()),
                "Available measurements"
            )
        );

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel createSummaryCard(
        String heading,
        String value,
        String description
    ) {

        JPanel card = new JPanel();

        card.setBackground(CARD);

        card.setLayout(
            new BoxLayout(
                card,
                BoxLayout.Y_AXIS
            )
        );

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(57, 78, 63)
                ),
                BorderFactory.createEmptyBorder(
                    7,
                    10,
                    7,
                    10
                )
            )
        );

        JLabel headingLabel = new JLabel(heading);

        headingLabel.setForeground(MUTED);

        headingLabel.setFont(
            new Font("Arial", Font.BOLD, 9)
        );

        JLabel valueLabel = new JLabel(value);

        valueLabel.setForeground(LIGHT_GREEN);

        valueLabel.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        JLabel descriptionLabel =
            new JLabel(description);

        descriptionLabel.setForeground(MUTED);

        descriptionLabel.setFont(
            new Font("Arial", Font.PLAIN, 9)
        );

        card.add(headingLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(1));
        card.add(descriptionLabel);

        return card;
    }

    // ==========================================
    // OVERVIEW
    // ==========================================

    private JScrollPane createOverviewPanel() {

        JPanel panel = createBasePanel();

        JPanel welcome = new JPanel(
            new BorderLayout(10, 10)
        );

        welcome.setBackground(
            new Color(42, 75, 52)
        );

        welcome.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(75, 125, 82)
                ),
                BorderFactory.createEmptyBorder(
                    16,
                    17,
                    16,
                    17
                )
            )
        );

        JLabel welcomeTitle = new JLabel(
            "Welcome to Performance Analytics"
        );

        welcomeTitle.setForeground(Color.WHITE);

        welcomeTitle.setFont(
            new Font("Arial", Font.BOLD, 19)
        );

        JTextArea welcomeDescription =
            new JTextArea(
                "Explore how Breadth-First Search (BFS), "
                + "Depth-First Search (DFS), and A* Search "
                + "perform when solving different sizes "
                + "of the Mushroom Maze."
            );

        welcomeDescription.setEditable(false);
        welcomeDescription.setLineWrap(true);
        welcomeDescription.setWrapStyleWord(true);

        welcomeDescription.setBackground(
            new Color(42, 75, 52)
        );

        welcomeDescription.setForeground(
            new Color(225, 239, 225)
        );

        welcomeDescription.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        welcome.add(
            welcomeTitle,
            BorderLayout.NORTH
        );

        welcome.add(
            welcomeDescription,
            BorderLayout.CENTER
        );

        welcome.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        panel.add(welcome);

        panel.add(
            Box.createVerticalStrut(18)
        );

        JLabel highlightsTitle =
            createSectionTitle("Performance Highlights");

        highlightsTitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        panel.add(highlightsTitle);

        TreeMap<Integer, double[]> data =
            getGraphData();

        String fastest = getBestAlgorithm(
            data,
            new int[] {
                BFS_TIME,
                DFS_TIME,
                ASTAR_TIME
            }
        );

        String memoryEfficient = getBestAlgorithm(
            data,
            new int[] {
                BFS_MEMORY,
                DFS_MEMORY,
                ASTAR_MEMORY
            }
        );

        JPanel highlightCards = new JPanel(
            new GridLayout(1, 2, 10, 0)
        );

        highlightCards.setBackground(BACKGROUND);

        highlightCards.add(
            createSummaryCard(
                "FASTEST ALGORITHM",
                fastest,
                "Based on average execution time"
            )
        );

        highlightCards.add(
            createSummaryCard(
                "LOWER MEMORY ALLOCATION",
                memoryEfficient,
                "Based on average allocated memory"
            )
        );

        highlightCards.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                100
            )
        );

        highlightCards.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        panel.add(highlightCards);

        panel.add(
            Box.createVerticalStrut(18)
        );

        addTextSection(
            panel,
            "Benchmark Configuration",
            "MAZE SIZES\n"
            + "5x5, 10x10, 20x20, 30x30, and 50x50.\n\n"
            + "NUMBER OF RUNS\n"
            + "5 runs for every maze size.\n\n"
            + "ALGORITHMS\n"
            + "Breadth-First Search (BFS), Depth-First Search (DFS), "
            + "and A* Search.\n\n"
            + "TESTING METHOD\n"
            + "All three algorithms use the same generated maze "
            + "within each benchmark trial. Their execution order "
            + "is rotated across runs. The CSV stores the average "
            + "result for each algorithm and maze size."
        );

        addTextSection(
            panel,
            "Available Analytics",
            "EXECUTION TIME\n"
            + "Compare how long each algorithm takes to solve the maze.\n\n"
            + "MEMORY ALLOCATION\n"
            + "Compare memory allocated by each algorithm's thread.\n\n"
            + "VISITED CELLS\n"
            + "Analyze how many cells each algorithm explores.\n\n"
            + "ALGORITHM COMPARISON\n"
            + "Compare BFS, DFS, and A* using charts and tables.\n\n"
            + "COMPLEXITY ANALYSIS\n"
            + "Understand the theoretical time and space complexity."
        );

        addTextSection(
            panel,
            "How to Use This Dashboard",
            "1. Use the navigation bar on the left to select a section.\n\n"
            + "2. Click RUN BENCHMARK to generate updated results.\n\n"
            + "3. Review the graphs, tables, and performance insights.\n\n"
            + "4. Click EXPORT CSV to save your benchmark results."
        );

        String dataStatus;

        if (results.isEmpty()) {

            dataStatus =
                "No benchmark data is currently available. "
                + "Click RUN BENCHMARK to generate your first results.";

        } else {

            dataStatus =
                "The dashboard currently contains "
                + results.size()
                + " benchmark records loaded from "
                + CSV_FILE + ".";
        }

        addTextSection(
            panel,
            "Benchmark Data Status",
            dataStatus
        );

        return wrapScroll(panel);
    }

    // ==========================================
    // LOAD CSV
    // ==========================================

    private boolean loadCSV() {

        results.clear();

        File file = new File(CSV_FILE);

        if (!file.exists()) {
            return false;
        }

        try (
            BufferedReader reader =
                new BufferedReader(
                    new FileReader(file)
                )
        ) {

            String header = reader.readLine();

            if (header == null) {
                return false;
            }

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values =
                    line.split(",", -1);

                if (values.length < 5) {
                    continue;
                }

                try {

                    Integer.parseInt(
                        values[0].trim()
                    );

                    String algorithm =
                        values[1].trim();

                    if (!isSupportedAlgorithm(algorithm)) {
                        continue;
                    }

                    results.add(values);

                } catch (NumberFormatException ignored) {
                }
            }

            return !results.isEmpty();

        } catch (IOException e) {

            return false;
        }
    }

    private boolean isSupportedAlgorithm(
        String algorithm
    ) {

        return algorithm.equalsIgnoreCase("BFS")
            || algorithm.equalsIgnoreCase("DFS")
            || algorithm.equalsIgnoreCase("A*");
    }

    // ==========================================
    // RUN BENCHMARK
    // ==========================================

    private void runBenchmark() {

        runButton.setEnabled(false);
        exportButton.setEnabled(false);

        statusLabel.setText(
            "Running benchmark..."
        );

        SwingWorker<Void, Void> worker =
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {

                    AlgorithmBenchmark.runBenchmark();

                    return null;
                }

                @Override
                protected void done() {

                    try {

                        get();

                        if (loadCSV()) {

                            buildPages();

                            updateSummaryCards();

                            statusLabel.setText(
                                "Benchmark completed"
                            );

                        } else {

                            statusLabel.setText(
                                "Could not load results"
                            );

                            JOptionPane.showMessageDialog(
                                PerformanceDashboard.this,
                                "The benchmark finished, but no valid CSV "
                                + "results could be loaded.",
                                "Benchmark Results",
                                JOptionPane.WARNING_MESSAGE
                            );
                        }

                    } catch (Exception ex) {

                        statusLabel.setText(
                            "Benchmark failed"
                        );

                        JOptionPane.showMessageDialog(
                            PerformanceDashboard.this,
                            ex.getMessage(),
                            "Benchmark Error",
                            JOptionPane.ERROR_MESSAGE
                        );

                    } finally {

                        runButton.setEnabled(true);
                        exportButton.setEnabled(true);

                        pageContainer.revalidate();
                        pageContainer.repaint();
                    }
                }
            };

        worker.execute();
    }

    // ==========================================
    // EXPORT CSV
    // ==========================================

    private void exportCSV() {

        File source = new File(CSV_FILE);

        if (!source.exists()) {

            JOptionPane.showMessageDialog(
                this,
                "There is no benchmark CSV to export.",
                "Export",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(
            "Export Mushroom Maze Benchmark"
        );

        chooser.setSelectedFile(
            new File(CSV_FILE)
        );

        if (chooser.showSaveDialog(this) ==
            JFileChooser.APPROVE_OPTION) {

            File destination =
                chooser.getSelectedFile();

            if (destination.exists()) {

                int answer = JOptionPane.showConfirmDialog(
                    this,
                    "This file already exists. Replace it?",
                    "Confirm Export",
                    JOptionPane.YES_NO_OPTION
                );

                if (answer != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try (
                InputStream input =
                    new FileInputStream(source);

                OutputStream output =
                    new FileOutputStream(destination)
            ) {

                byte[] buffer = new byte[8192];

                int length;

                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                statusLabel.setText("CSV exported");

                JOptionPane.showMessageDialog(
                    this,
                    "Benchmark results exported successfully.",
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ==========================================
    // RESULTS TABLE
    // ==========================================

    private JPanel createResultsPanel() {

        JPanel panel = new JPanel(
            new BorderLayout(10, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(
                15,
                15,
                15,
                15
            )
        );

        panel.add(
            createSectionTitle("Benchmark Results"),
            BorderLayout.NORTH
        );

        String[] columns = {
            "Maze Size",
            "Algorithm",
            "Average Time (ms)",
            "Average Visited Cells",
            "Average Thread-Allocated Memory (KB)"
        };

        DefaultTableModel model =
            new DefaultTableModel(columns, 0) {

                @Override
                public boolean isCellEditable(
                    int row,
                    int column
                ) {
                    return false;
                }
            };

        for (String[] result : results) {

            model.addRow(new Object[] {
                result[0] + "x" + result[0],
                result[1],
                result[2],
                result[3],
                result[4].equalsIgnoreCase("Unavailable")
                    ? "Unavailable"
                    : result[4]
            });
        }

        JTable table = new JTable(model);

        styleTable(table);

        TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);

        scroll.getViewport().setBackground(PANEL);

        scroll.setBorder(
            BorderFactory.createLineBorder(
                new Color(57, 78, 63)
            )
        );

        panel.add(
            scroll,
            BorderLayout.CENTER
        );

        JLabel note = new JLabel(
            "Each row contains the average of five benchmark runs. "
            + "Select a column heading to sort the results."
        );

        note.setForeground(MUTED);

        note.setFont(
            new Font("Arial", Font.PLAIN, 10)
        );

        panel.add(note, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable table) {

        table.setRowHeight(30);

        table.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        table.setBackground(PANEL);
        table.setForeground(TEXT);

        table.setGridColor(
            new Color(55, 73, 61)
        );

        table.setSelectionBackground(
            new Color(65, 110, 74)
        );

        table.setSelectionForeground(Color.WHITE);

        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 11)
        );

        table.getTableHeader().setBackground(
            new Color(49, 91, 54)
        );

        table.getTableHeader().setForeground(
            Color.WHITE
        );

        table.getTableHeader().setReorderingAllowed(false);
    }

    // ==========================================
    // CHART PANELS
    // ==========================================

    private JPanel createChartPanel(
        String title,
        String yLabel,
        int valueIndex,
        boolean grouped
    ) {

        JPanel panel = new JPanel(
            new BorderLayout()
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(
                12,
                12,
                12,
                12
            )
        );

        panel.add(
            createSectionTitle(title),
            BorderLayout.NORTH
        );

        ChartPanel chart = new ChartPanel(
            title,
            yLabel,
            valueIndex,
            grouped
        );

        panel.add(
            chart,
            BorderLayout.CENTER
        );

        return panel;
    }

    // ==========================================
    // COMPLEXITY ANALYSIS
    // ==========================================

    private JScrollPane createAnalysisPanel() {

        JPanel panel = createBasePanel();

        addTextSection(
            panel,
            "Breadth-First Search (BFS)",
            "BFS explores neighboring cells level by level using a Queue.\n\n"
            + "DATA STRUCTURE: Queue\n\n"
            + "TIME COMPLEXITY: O(V + E)\n"
            + "SPACE COMPLEXITY: O(V)\n\n"
            + "BFS guarantees the shortest path in an unweighted maze "
            + "when all edges have equal cost."
        );

        addTextSection(
            panel,
            "Depth-First Search (DFS)",
            "DFS explores one route deeply before backtracking. "
            + "It uses a Stack in this implementation.\n\n"
            + "DATA STRUCTURE: Stack\n\n"
            + "TIME COMPLEXITY: O(V + E)\n"
            + "SPACE COMPLEXITY: O(V)\n\n"
            + "DFS does not guarantee the shortest path."
        );

        addTextSection(
            panel,
            "A* Search",
            "A* selects the next cell using its estimated total cost:\n\n"
            + "f(n) = g(n) + h(n)\n\n"
            + "g(n) represents the cost from the starting cell to the "
            + "current cell.\n\n"
            + "h(n) estimates the remaining distance to the exit. "
            + "This implementation uses Manhattan distance.\n\n"
            + "DATA STRUCTURE: Priority Queue\n\n"
            + "TYPICAL TIME COMPLEXITY: O((V + E) log V)\n"
            + "TYPICAL SPACE COMPLEXITY: O(V + E)\n\n"
            + "For a grid maze with a bounded number of neighbors per cell, "
            + "E is proportional to V. The priority queue helps A* select "
            + "the cell with the lowest estimated total cost."
        );

        addTextSection(
            panel,
            "Understanding V and E",
            "V represents the number of vertices or cells that can be explored.\n\n"
            + "E represents the connections between neighboring cells.\n\n"
            + "In a grid maze, each cell can connect to up to four neighboring cells."
        );

        addTextSection(
            panel,
            "Effect of Input Size",
            "Larger mazes generally contain more cells and possible connections.\n\n"
            + "This can increase the amount of work required by all three algorithms.\n\n"
            + "Actual measurements depend on maze structure, search order, "
            + "the data structures used, and the execution environment."
        );

        addTextSection(
            panel,
            "Important Comparison",
            "BFS is useful when finding the shortest path in an unweighted maze.\n\n"
            + "DFS explores deeply but does not guarantee the shortest solution.\n\n"
            + "A* uses a heuristic to guide its search toward the goal. "
            + "Its effectiveness depends partly on the quality of the heuristic.\n\n"
            + "Theoretical complexity describes general growth, while "
            + "benchmark results show observed performance on the tested mazes."
        );

        return wrapScroll(panel);
    }

    // ==========================================
    // THREE-ALGORITHM SUMMARY
    // ==========================================

    private JPanel createComparisonPanel() {

        JPanel panel = new JPanel(
            new BorderLayout(10, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(
                15,
                15,
                15,
                15
            )
        );

        panel.add(
            createSectionTitle(
                "BFS vs DFS vs A* Summary"
            ),
            BorderLayout.NORTH
        );

        String[] columns = {
            "Maze Size",
            "Fastest Algorithm",
            "Fewest Visited Cells",
            "Lowest Memory Allocation",
            "Time Difference"
        };

        DefaultTableModel model =
            new DefaultTableModel(columns, 0) {

                @Override
                public boolean isCellEditable(
                    int row,
                    int column
                ) {
                    return false;
                }
            };

        TreeMap<Integer, double[]> data =
            getGraphData();

        for (Map.Entry<Integer, double[]> entry :
            data.entrySet()) {

            double[] values = entry.getValue();

            String fastest = getBestAlgorithm(
                values,
                new int[] {
                    BFS_TIME,
                    DFS_TIME,
                    ASTAR_TIME
                }
            );

            String fewestVisited = getBestAlgorithm(
                values,
                new int[] {
                    BFS_VISITED,
                    DFS_VISITED,
                    ASTAR_VISITED
                }
            );

            String lowestMemory = getBestAlgorithm(
                values,
                new int[] {
                    BFS_MEMORY,
                    DFS_MEMORY,
                    ASTAR_MEMORY
                }
            );

            String difference =
                getTimeDifference(values);

            int size = entry.getKey();

            model.addRow(new Object[] {
                size + "x" + size,
                fastest,
                fewestVisited,
                lowestMemory,
                difference
            });
        }

        JTable table = new JTable(model);

        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);

        scroll.getViewport().setBackground(PANEL);

        panel.add(
            scroll,
            BorderLayout.CENTER
        );

        JTextArea explanation = createTextArea(
            "HOW TO READ THIS TABLE\n\n"
            + "Fastest Algorithm: The algorithm with the lowest "
            + "average execution time.\n\n"
            + "Fewest Visited Cells: The algorithm that explored "
            + "the fewest cells.\n\n"
            + "Lowest Memory Allocation: The algorithm with the "
            + "lowest measured thread-allocated memory.\n\n"
            + "Time Difference: The percentage difference between "
            + "the fastest and slowest valid execution times.\n\n"
            + "Tie means the compared algorithms recorded equal values. "
            + "Unavailable means no valid measurement was available."
        );

        explanation.setRows(7);

        panel.add(
            explanation,
            BorderLayout.SOUTH
        );

        return panel;
    }

    // ==========================================
    // INSIGHTS
    // ==========================================

    private JScrollPane createInsightsPanel() {

        JPanel panel = createBasePanel();

        TreeMap<Integer, double[]> data =
            getGraphData();

        addTextSection(
            panel,
            "Overall Execution Time",
            buildOverallComparison(
                data,
                new int[] {
                    BFS_TIME,
                    DFS_TIME,
                    ASTAR_TIME
                },
                "execution time",
                "ms"
            )
        );

        addTextSection(
            panel,
            "Overall Memory Allocation",
            buildOverallComparison(
                data,
                new int[] {
                    BFS_MEMORY,
                    DFS_MEMORY,
                    ASTAR_MEMORY
                },
                "memory allocation",
                "KB"
            )
        );

        addTextSection(
            panel,
            "Overall Visited Cells",
            buildOverallComparison(
                data,
                new int[] {
                    BFS_VISITED,
                    DFS_VISITED,
                    ASTAR_VISITED
                },
                "visited cells",
                "cells"
            )
        );

        addTextSection(
            panel,
            "Execution Time by Maze Size",
            buildMetricInterpretation(
                data,
                new int[] {
                    BFS_TIME,
                    DFS_TIME,
                    ASTAR_TIME
                },
                "ms",
                "execution time"
            )
        );

        addTextSection(
            panel,
            "Memory Allocation by Maze Size",
            buildMetricInterpretation(
                data,
                new int[] {
                    BFS_MEMORY,
                    DFS_MEMORY,
                    ASTAR_MEMORY
                },
                "KB",
                "memory allocation"
            )
        );

        addTextSection(
            panel,
            "Visited Cells by Maze Size",
            buildMetricInterpretation(
                data,
                new int[] {
                    BFS_VISITED,
                    DFS_VISITED,
                    ASTAR_VISITED
                },
                "cells",
                "visited cells"
            )
        );

        addTextSection(
            panel,
            "Performance Trend",
            buildTrend(data)
        );

        addTextSection(
            panel,
            "Complexity Interpretation",
            "BFS and DFS have O(V + E) time complexity and O(V) "
            + "auxiliary space complexity in their graph-search implementations.\n\n"
            + "A* uses a priority queue to select the next cell according "
            + "to its estimated total cost. Its typical time complexity "
            + "with a binary-heap priority queue is O((V + E) log V), "
            + "with O(V + E) space in a general graph-search setting.\n\n"
            + "The benchmark measurements show observed behavior for the "
            + "tested mazes. They do not replace theoretical complexity analysis."
        );

        addTextSection(
            panel,
            "Memory Measurement Note",
            "The benchmark records thread-allocated memory when supported "
            + "by the Java runtime.\n\n"
            + "This measures memory allocated by the thread during algorithm "
            + "execution. It is not the same as peak live memory or the total "
            + "memory used by the entire application.\n\n"
            + "If memory measurement is unavailable, the dashboard excludes "
            + "that measurement from memory comparisons and charts."
        );

        return wrapScroll(panel);
    }

    // ==========================================
    // GET BEST ALGORITHM
    // ==========================================

    private String getBestAlgorithm(
        TreeMap<Integer, double[]> data,
        int[] indexes
    ) {

        double[] totals = new double[3];

        int[] counts = new int[3];

        for (double[] values : data.values()) {

            for (int i = 0; i < 3; i++) {

                double value = values[indexes[i]];

                if (Double.isFinite(value)) {

                    totals[i] += value;
                    counts[i]++;
                }
            }
        }

        double[] averages = {
            Double.NaN,
            Double.NaN,
            Double.NaN
        };

        for (int i = 0; i < 3; i++) {

            if (counts[i] > 0) {
                averages[i] = totals[i] / counts[i];
            }
        }

        return getBestAlgorithm(
            averages,
            new int[] {0, 1, 2}
        );
    }

    private String getBestAlgorithm(
        double[] values,
        int[] indexes
    ) {

        double bestValue = Double.POSITIVE_INFINITY;

        int bestIndex = -1;

        boolean tie = false;

        for (int i = 0; i < indexes.length; i++) {

            double value = values[indexes[i]];

            if (!Double.isFinite(value)) {
                continue;
            }

            if (value < bestValue) {

                bestValue = value;
                bestIndex = i;
                tie = false;

            } else if (Double.compare(value, bestValue) == 0) {

                tie = true;
            }
        }

        if (bestIndex == -1) {
            return "Unavailable";
        }

        if (tie) {
            return "Tie";
        }

        return ALGORITHMS[bestIndex];
    }

    // ==========================================
    // TIME DIFFERENCE
    // ==========================================

    private String getTimeDifference(double[] values) {

        double[] times = {
            values[BFS_TIME],
            values[DFS_TIME],
            values[ASTAR_TIME]
        };

        double fastest = Double.POSITIVE_INFINITY;
        double slowest = Double.NEGATIVE_INFINITY;

        int validCount = 0;

        for (double time : times) {

            if (!Double.isFinite(time)) {
                continue;
            }

            fastest = Math.min(fastest, time);
            slowest = Math.max(slowest, time);

            validCount++;
        }

        if (validCount < 2) {
            return "Unavailable";
        }

        if (slowest == 0) {
            return "0.00%";
        }

        return String.format(
            Locale.US,
            "%.2f%%",
            ((slowest - fastest) / slowest) * 100
        );
    }

    // ==========================================
    // OVERALL COMPARISON
    // ==========================================

    private String buildOverallComparison(
        TreeMap<Integer, double[]> data,
        int[] indexes,
        String metric,
        String unit
    ) {

        if (data.isEmpty()) {
            return "No benchmark data is available.";
        }

        double[] totals = new double[3];

        int[] counts = new int[3];

        for (double[] values : data.values()) {

            for (int i = 0; i < 3; i++) {

                double value = values[indexes[i]];

                if (Double.isFinite(value)) {

                    totals[i] += value;
                    counts[i]++;
                }
            }
        }

        StringBuilder output =
            new StringBuilder();

        output.append(
            "Average " + metric + " across available maze sizes:\n\n"
        );

        for (int i = 0; i < 3; i++) {

            output.append(ALGORITHMS[i])
                .append(": ");

            if (counts[i] == 0) {

                output.append("Unavailable\n");

            } else {

                output.append(
                    String.format(
                        Locale.US,
                        "%.4f %s",
                        totals[i] / counts[i],
                        unit
                    )
                );

                output.append("\n");
            }
        }

        output.append("\nBest result: ");

        output.append(
            getBestAlgorithm(data, indexes)
        );

        output.append(
            "\n\nLower values indicate less "
        );

        output.append(metric);

        output.append(".");

        return output.toString();
    }

    // ==========================================
    // METRIC INTERPRETATION
    // ==========================================

    private String buildMetricInterpretation(
        TreeMap<Integer, double[]> data,
        int[] indexes,
        String unit,
        String metric
    ) {

        if (data.isEmpty()) {
            return "No benchmark data is available.";
        }

        StringBuilder output =
            new StringBuilder();

        for (Map.Entry<Integer, double[]> entry :
            data.entrySet()) {

            int size = entry.getKey();

            double[] values = entry.getValue();

            output.append(size)
                .append("x")
                .append(size)
                .append(" Maze\n");

            for (int i = 0; i < 3; i++) {

                double value = values[indexes[i]];

                output.append(ALGORITHMS[i])
                    .append(": ");

                if (Double.isFinite(value)) {

                    output.append(
                        String.format(
                            Locale.US,
                            "%.2f %s",
                            value,
                            unit
                        )
                    );

                } else {

                    output.append("Unavailable");
                }

                output.append("\n");
            }

            output.append("Lowest ")
                .append(metric)
                .append(": ")
                .append(
                    getBestAlgorithm(values, indexes)
                );

            output.append("\n\n");
        }

        return output.toString();
    }

    // ==========================================
    // TREND ANALYSIS
    // ==========================================

    private String buildTrend(
        TreeMap<Integer, double[]> data
    ) {

        if (data.size() < 2) {

            return "At least two maze sizes are required "
                + "to compare performance trends.";
        }

        Map.Entry<Integer, double[]> first =
            data.firstEntry();

        Map.Entry<Integer, double[]> last =
            data.lastEntry();

        StringBuilder output =
            new StringBuilder();

        output.append("Smallest maze: ")
            .append(first.getKey())
            .append("x")
            .append(first.getKey())
            .append("\n");

        output.append("Largest maze: ")
            .append(last.getKey())
            .append("x")
            .append(last.getKey())
            .append("\n\n");

        int[] timeIndexes = {
            BFS_TIME,
            DFS_TIME,
            ASTAR_TIME
        };

        for (int i = 0; i < 3; i++) {

            appendTrend(
                output,
                ALGORITHMS[i],
                first.getValue()[timeIndexes[i]],
                last.getValue()[timeIndexes[i]]
            );
        }

        output.append(
            "\nThis compares only the smallest and largest "
            + "recorded maze sizes. It describes observed "
            + "measurements, not a guaranteed trend."
        );

        return output.toString();
    }

    private void appendTrend(
        StringBuilder output,
        String algorithm,
        double first,
        double last
    ) {

        output.append(algorithm)
            .append(": ");

        if (!Double.isFinite(first) ||
            !Double.isFinite(last)) {

            output.append("Insufficient data.\n\n");

            return;
        }

        String direction;

        if (last > first) {
            direction = "increased";
        } else if (last < first) {
            direction = "decreased";
        } else {
            direction = "remained equal";
        }

        output.append(direction)
            .append(" from ");

        output.append(
            String.format(
                Locale.US,
                "%.4f ms to %.4f ms.\n\n",
                first,
                last
            )
        );
    }

    // ==========================================
    // GRAPH DATA
    // ==========================================

    private TreeMap<Integer, double[]> getGraphData() {

        TreeMap<Integer, double[]> data =
            new TreeMap<>();

        for (String[] row : results) {

            try {

                int size = Integer.parseInt(
                    row[0].trim()
                );

                int algorithmIndex;

                String algorithm =
                    row[1].trim();

                if (algorithm.equalsIgnoreCase("BFS")) {

                    algorithmIndex = 0;

                } else if (
                    algorithm.equalsIgnoreCase("DFS")
                ) {

                    algorithmIndex = 1;

                } else if (
                    algorithm.equalsIgnoreCase("A*")
                ) {

                    algorithmIndex = 2;

                } else {

                    continue;
                }

                double[] values = data.computeIfAbsent(
                    size,
                    key -> new double[] {
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN,
                        Double.NaN
                    }
                );

                double time =
                    parseValue(row, CSV_TIME);

                double visited =
                    parseValue(row, CSV_VISITED);

                double memory =
                    parseValue(row, CSV_MEMORY);

                // Execution time: indexes 0, 1, 2
                values[algorithmIndex] = time;

                // Memory: indexes 3, 4, 5
                values[algorithmIndex + 3] = memory;

                // Visited cells: indexes 6, 7, 8
                values[algorithmIndex + 6] = visited;

            } catch (Exception ignored) {
            }
        }

        return data;
    }

    private double parseValue(
        String[] row,
        int index
    ) {

        try {

            if (index >= row.length) {
                return Double.NaN;
            }

            String value =
                row[index].trim();

            if (value.isEmpty() ||
                value.equalsIgnoreCase("Unavailable")) {

                return Double.NaN;
            }

            double number =
                Double.parseDouble(value);

            return Double.isFinite(number) && number >= 0
                ? number
                : Double.NaN;

        } catch (Exception e) {

            return Double.NaN;
        }
    }

    // ==========================================
    // GENERAL UI HELPERS
    // ==========================================

    private JPanel createBasePanel() {

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
            )
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(
                15,
                15,
                15,
                15
            )
        );

        return panel;
    }

    private JScrollPane wrapScroll(JPanel panel) {

        JScrollPane scroll =
            new JScrollPane(panel);

        scroll.setBorder(null);

        scroll.getViewport().setBackground(
            BACKGROUND
        );

        scroll.getVerticalScrollBar()
            .setUnitIncrement(18);

        return scroll;
    }

    private JLabel createSectionTitle(
        String title
    ) {

        JLabel label = new JLabel(title);

        label.setForeground(TEXT);

        label.setFont(
            new Font("Arial", Font.BOLD, 19)
        );

        label.setBorder(
            BorderFactory.createEmptyBorder(
                5,
                5,
                12,
                5
            )
        );

        return label;
    }

    private void addTextSection(
        JPanel panel,
        String heading,
        String description
    ) {

        JPanel card = new JPanel(
            new BorderLayout(8, 8)
        );

        card.setBackground(CARD);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(57, 78, 63)
                ),
                BorderFactory.createEmptyBorder(
                    12,
                    14,
                    12,
                    14
                )
            )
        );

        card.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                500
            )
        );

        JLabel title = new JLabel(heading);

        title.setForeground(LIGHT_GREEN);

        title.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        JTextArea text =
            createTextArea(description);

        card.add(
            title,
            BorderLayout.NORTH
        );

        card.add(
            text,
            BorderLayout.CENTER
        );

        card.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        panel.add(card);

        panel.add(
            Box.createVerticalStrut(12)
        );
    }

    private JTextArea createTextArea(
        String content
    ) {

        JTextArea text =
            new JTextArea(content);

        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        text.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        text.setForeground(TEXT);
        text.setBackground(CARD);

        text.setBorder(
            BorderFactory.createEmptyBorder(
                5,
                2,
                5,
                2
            )
        );

        return text;
    }

    // ==========================================
    // CUSTOM CHART
    // ==========================================

    class ChartPanel extends JPanel {

        private final String title;
        private final String yLabel;
        private final int valueIndex;
        private final boolean grouped;

        ChartPanel(
            String title,
            String yLabel,
            int valueIndex,
            boolean grouped
        ) {

            this.title = title;
            this.yLabel = yLabel;
            this.valueIndex = valueIndex;
            this.grouped = grouped;

            setBackground(PANEL);

            setPreferredSize(
                new Dimension(800, 520)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int left = 100;
            int right = width - 45;
            int top = 85;
            int bottom = height - 105;

            if (right <= left || bottom <= top) {

                g2.dispose();

                return;
            }

            // TITLE

            g2.setColor(TEXT);

            g2.setFont(
                new Font("Arial", Font.BOLD, 19)
            );

            FontMetrics fm =
                g2.getFontMetrics();

            g2.drawString(
                title,
                (width - fm.stringWidth(title)) / 2,
                38
            );

            // GET GRAPH DATA

            TreeMap<Integer, double[]> values =
                getGraphData();

            int[] indexes =
                getIndexesForMetric();

            // FIND MAXIMUM VALUE

            double max = 0;

            for (double[] row : values.values()) {

                for (int index : indexes) {

                    if (Double.isFinite(row[index])) {

                        max = Math.max(
                            max,
                            row[index]
                        );
                    }
                }
            }

            if (values.isEmpty() || max == 0) {

                drawCenteredMessage(
                    g2,
                    "No valid benchmark data available.",
                    width,
                    height
                );

                g2.dispose();

                return;
            }

            max *= 1.15;

            // GRID AND Y-AXIS LABELS

            g2.setFont(
                new Font("Arial", Font.PLAIN, 11)
            );

            for (int i = 0; i <= 5; i++) {

                int y = bottom -
                    (int)(
                        (bottom - top) * i / 5.0
                    );

                g2.setColor(
                    new Color(57, 73, 63)
                );

                g2.drawLine(
                    left,
                    y,
                    right,
                    y
                );

                double labelValue =
                    max * i / 5.0;

                g2.setColor(MUTED);

                String label = String.format(
                    Locale.US,
                    "%.2f",
                    labelValue
                );

                g2.drawString(
                    label,
                    15,
                    y + 4
                );
            }

            // AXES

            g2.setColor(MUTED);

            g2.drawLine(
                left,
                top,
                left,
                bottom
            );

            g2.drawLine(
                left,
                bottom,
                right,
                bottom
            );

            g2.drawString(
                yLabel,
                15,
                65
            );

            g2.drawString(
                "Maze Size",
                width / 2 - 25,
                height - 25
            );

            int count = values.size();

            int position = 0;

            int groupWidth =
                (right - left) / count;

            // ==========================================
            // GROUPED BAR CHART
            // ==========================================

            if (grouped) {

                int barWidth = Math.max(
                    5,
                    Math.min(
                        24,
                        groupWidth / 5
                    )
                );

                for (Map.Entry<Integer, double[]> entry :
                    values.entrySet()) {

                    int center = left
                        + groupWidth * position
                        + groupWidth / 2;

                    double[] row =
                        entry.getValue();

                    drawBar(
                        g2,
                        center - barWidth - 2,
                        bottom,
                        top,
                        row[indexes[0]],
                        max,
                        barWidth,
                        BFS_COLOR
                    );

                    drawBar(
                        g2,
                        center,
                        bottom,
                        top,
                        row[indexes[1]],
                        max,
                        barWidth,
                        DFS_COLOR
                    );

                    drawBar(
                        g2,
                        center + barWidth + 2,
                        bottom,
                        top,
                        row[indexes[2]],
                        max,
                        barWidth,
                        ASTAR_COLOR
                    );

                    drawMazeLabel(
                        g2,
                        entry.getKey(),
                        center,
                        bottom
                    );

                    position++;
                }

            }

            // ==========================================
            // LINE CHART
            // ==========================================

            else {

                drawLine(
                    g2,
                    values,
                    indexes[0],
                    BFS_COLOR,
                    left,
                    right,
                    top,
                    bottom,
                    max
                );

                drawLine(
                    g2,
                    values,
                    indexes[1],
                    DFS_COLOR,
                    left,
                    right,
                    top,
                    bottom,
                    max
                );

                drawLine(
                    g2,
                    values,
                    indexes[2],
                    ASTAR_COLOR,
                    left,
                    right,
                    top,
                    bottom,
                    max
                );

                for (int size : values.keySet()) {

                    int x = count == 1
                        ? (left + right) / 2
                        : left + (int)(
                            (right - left) * position /
                            (double)(count - 1)
                        );

                    drawMazeLabel(
                        g2,
                        size,
                        x,
                        bottom
                    );

                    position++;
                }
            }

            drawLegend(
                g2,
                width,
                height
            );

            g2.dispose();
        }

        // ==========================================
        // GET INDEXES FOR SELECTED CHART
        // ==========================================

        private int[] getIndexesForMetric() {

            if (valueIndex == CSV_TIME) {

                return new int[] {
                    BFS_TIME,
                    DFS_TIME,
                    ASTAR_TIME
                };
            }

            if (valueIndex == CSV_MEMORY) {

                return new int[] {
                    BFS_MEMORY,
                    DFS_MEMORY,
                    ASTAR_MEMORY
                };
            }

            return new int[] {
                BFS_VISITED,
                DFS_VISITED,
                ASTAR_VISITED
            };
        }

        // ==========================================
        // DRAW BAR
        // ==========================================

        private void drawBar(
            Graphics2D g2,
            int x,
            int bottom,
            int top,
            double value,
            double max,
            int barWidth,
            Color color
        ) {

            if (!Double.isFinite(value)) {
                return;
            }

            int barHeight = (int)(
                (value / max) * (bottom - top)
            );

            barHeight = Math.max(
                0,
                barHeight
            );

            g2.setColor(color);

            g2.fillRoundRect(
                x,
                bottom - barHeight,
                barWidth,
                barHeight,
                5,
                5
            );
        }

        // ==========================================
        // DRAW LINE
        // ==========================================

        private void drawLine(
            Graphics2D g2,
            TreeMap<Integer, double[]> values,
            int algorithmIndex,
            Color color,
            int left,
            int right,
            int top,
            int bottom,
            double max
        ) {

            int count = values.size();

            int position = 0;

            int previousX = -1;
            int previousY = -1;

            g2.setColor(color);

            g2.setStroke(
                new BasicStroke(3f)
            );

            for (double[] row : values.values()) {

                double value =
                    row[algorithmIndex];

                int x = count == 1
                    ? (left + right) / 2
                    : left + (int)(
                        (right - left) * position /
                        (double)(count - 1)
                    );

                position++;

                if (!Double.isFinite(value)) {

                    previousX = -1;

                    continue;
                }

                int y = bottom - (int)(
                    (value / max) * (bottom - top)
                );

                if (previousX != -1) {

                    g2.drawLine(
                        previousX,
                        previousY,
                        x,
                        y
                    );
                }

                g2.fillOval(
                    x - 5,
                    y - 5,
                    10,
                    10
                );

                previousX = x;
                previousY = y;
            }
        }

        // ==========================================
        // DRAW MAZE LABEL
        // ==========================================

        private void drawMazeLabel(
            Graphics2D g2,
            int size,
            int x,
            int bottom
        ) {

            g2.setColor(MUTED);

            String label =
                size + "x" + size;

            FontMetrics fm =
                g2.getFontMetrics();

            g2.drawString(
                label,
                x - fm.stringWidth(label) / 2,
                bottom + 23
            );
        }

        // ==========================================
        // DRAW THREE-ALGORITHM LEGEND
        // ==========================================

        private void drawLegend(
            Graphics2D g2,
            int width,
            int height
        ) {

            int y = height - 55;

            g2.setFont(
                new Font("Arial", Font.BOLD, 12)
            );

            int startX = width / 2 - 110;

            drawLegendItem(
                g2,
                "BFS",
                BFS_COLOR,
                startX,
                y
            );

            drawLegendItem(
                g2,
                "DFS",
                DFS_COLOR,
                startX + 85,
                y
            );

            drawLegendItem(
                g2,
                "A*",
                ASTAR_COLOR,
                startX + 170,
                y
            );
        }

        private void drawLegendItem(
            Graphics2D g2,
            String label,
            Color color,
            int x,
            int y
        ) {

            g2.setColor(color);

            g2.fillRoundRect(
                x,
                y - 9,
                13,
                13,
                4,
                4
            );

            g2.setColor(TEXT);

            g2.drawString(
                label,
                x + 19,
                y + 2
            );
        }

        // ==========================================
        // DRAW EMPTY CHART MESSAGE
        // ==========================================

        private void drawCenteredMessage(
            Graphics2D g2,
            String message,
            int width,
            int height
        ) {

            g2.setColor(MUTED);

            g2.setFont(
                new Font("Arial", Font.PLAIN, 15)
            );

            FontMetrics fm =
                g2.getFontMetrics();

            g2.drawString(
                message,
                (width - fm.stringWidth(message)) / 2,
                height / 2
            );
        }
    }
}