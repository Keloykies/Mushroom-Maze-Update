
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

    private static final Color BACKGROUND = new Color(19, 29, 25);
    private static final Color PANEL = new Color(29, 43, 36);
    private static final Color CARD = new Color(38, 55, 45);
    private static final Color GREEN = new Color(75, 145, 91);
    private static final Color LIGHT_GREEN = new Color(164, 211, 153);
    private static final Color TEXT = new Color(235, 242, 235);
    private static final Color MUTED = new Color(164, 181, 168);
    private static final Color BFS_COLOR = new Color(75, 155, 235);
    private static final Color DFS_COLOR = new Color(235, 153, 76);

    private static final String CSV_FILE =
        "MushroomMaze_Benchmark.csv";

    // CSV column indexes
    private static final int CSV_TIME = 2;
    private static final int CSV_VISITED = 3;
    private static final int CSV_MEMORY = 4;

    // Graph data indexes
    private static final int BFS_TIME = 0;
    private static final int DFS_TIME = 1;
    private static final int BFS_MEMORY = 2;
    private static final int DFS_MEMORY = 3;
    private static final int BFS_VISITED = 4;
    private static final int DFS_VISITED = 5;

    // ==========================================
    // DATA AND UI COMPONENTS
    // ==========================================

    private final List<String[]> results = new ArrayList<>();
    private final List<JButton> navigationButtons = new ArrayList<>();

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
            new BoxLayout(navigationPanel, BoxLayout.Y_AXIS)
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

        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setBackground(PANEL);

        sidebar.setBorder(
            BorderFactory.createMatteBorder(
                0, 0, 0, 1,
                new Color(57, 78, 63)
            )
        );

        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setLayout(
            new BoxLayout(sidebarHeader, BoxLayout.Y_AXIS)
        );
        sidebarHeader.setBackground(PANEL);
        sidebarHeader.setBorder(
            BorderFactory.createEmptyBorder(18, 15, 15, 10)
        );

        JLabel heading = new JLabel("ANALYTICS");
        heading.setForeground(LIGHT_GREEN);
        heading.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel description = new JLabel("Dashboard sections");
        description.setForeground(MUTED);
        description.setFont(new Font("Arial", Font.PLAIN, 10));

        sidebarHeader.add(heading);
        sidebarHeader.add(Box.createVerticalStrut(5));
        sidebarHeader.add(description);

        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        JScrollPane navigationScroll = new JScrollPane(navigationPanel);

        navigationScroll.setBorder(null);
        navigationScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        navigationScroll.getViewport().setBackground(PANEL);
        navigationScroll.getVerticalScrollBar().setUnitIncrement(15);

        sidebar.add(navigationScroll, BorderLayout.CENTER);

        return sidebar;
    }

    // ==========================================
    // RIGHT CONTENT AREA
    // ==========================================

    private JPanel createContentArea() {

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);

        content.setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        content.add(pageContainer, BorderLayout.CENTER);

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
        addNavigationItem("BFS_DFS", "BFS vs DFS");
        addNavigationItem("ALGORITHM", "Algorithm Comparison");
        addNavigationItem("MEMORY_COMPARE", "Memory Comparison");
        addNavigationItem("VISITED", "Visited Cells");
        addNavigationItem("INSIGHTS", "Insights");

        pageContainer.add(createOverviewPanel(), "OVERVIEW");
        pageContainer.add(createResultsPanel(), "RESULTS");

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

        pageContainer.add(createAnalysisPanel(), "COMPLEXITY");
        pageContainer.add(createComparisonPanel(), "BFS_DFS");

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS - Execution Time",
                "Execution Time (ms)",
                CSV_TIME,
                true
            ),
            "ALGORITHM"
        );

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS - Memory Allocation",
                "Allocated Memory (KB)",
                CSV_MEMORY,
                true
            ),
            "MEMORY_COMPARE"
        );

        pageContainer.add(
            createChartPanel(
                "BFS vs DFS - Visited Cells",
                "Number of Visited Cells",
                CSV_VISITED,
                true
            ),
            "VISITED"
        );

        pageContainer.add(createInsightsPanel(), "INSIGHTS");

        navigationPanel.add(Box.createVerticalStrut(5));

        navigationPanel.revalidate();
        navigationPanel.repaint();

        updateSummaryCards();
        showPage(currentPage);
    }

    private void addNavigationItem(String key, String label) {

        JButton button = new JButton(label);

        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(TEXT);
        button.setBackground(PANEL);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        button.setPreferredSize(new Dimension(175, 38));

        button.setBorder(
            BorderFactory.createEmptyBorder(0, 13, 0, 5)
        );

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showPage(key));

        navigationButtons.add(button);

        navigationPanel.add(button);
        navigationPanel.add(Box.createVerticalStrut(3));
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

                button.setBackground(new Color(53, 91, 64));
                button.setForeground(LIGHT_GREEN);

                button.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                            0, 3, 0, 0, LIGHT_GREEN
                        ),
                        BorderFactory.createEmptyBorder(0, 10, 0, 5)
                    )
                );

            } else {

                button.setBackground(PANEL);
                button.setForeground(TEXT);

                button.setBorder(
                    BorderFactory.createEmptyBorder(0, 13, 0, 5)
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
            BorderFactory.createEmptyBorder(12, 18, 10, 18)
        );

        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(
            new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("MUSHROOM MAZE");
        title.setFont(new Font("Arial", Font.BOLD, 21));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
            "ALGORITHM PERFORMANCE ANALYTICS"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitle.setForeground(LIGHT_GREEN);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        actions.setOpaque(false);

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));

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

        exportButton.addActionListener(e -> exportCSV());
        runButton.addActionListener(e -> runBenchmark());

        actions.add(statusLabel);
        actions.add(backButton);
        actions.add(exportButton);
        actions.add(runButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        wrapper.add(header, BorderLayout.NORTH);

        summaryPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        summaryPanel.setBackground(BACKGROUND);

        summaryPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 0, 0, 0)
        );

        updateSummaryCards();

        wrapper.add(summaryPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createEmptyBorder(9, 11, 9, 11)
        );

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        for (String[] row : results) {

            try {

                sizes.add(Integer.parseInt(row[0]));

                if (row[1].equalsIgnoreCase("BFS")) {
                    bfsCount++;
                }

                if (row[1].equalsIgnoreCase("DFS")) {
                    dfsCount++;
                }

            } catch (Exception ignored) {
            }
        }

        summaryPanel.add(
            createSummaryCard(
                "MAZE SIZES TESTED",
                String.valueOf(sizes.size()),
                "Different input sizes"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "BFS RECORDS",
                String.valueOf(bfsCount),
                "Average benchmark results"
            )
        );

        summaryPanel.add(
            createSummaryCard(
                "DFS RECORDS",
                String.valueOf(dfsCount),
                "Average benchmark results"
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
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(57, 78, 63)
                ),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
            )
        );

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setForeground(MUTED);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 9));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(LIGHT_GREEN);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 21));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 9));

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

        JPanel welcome = new JPanel(new BorderLayout(10, 10));
        welcome.setBackground(new Color(42, 75, 52));

        welcome.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(75, 125, 82)
                ),
                BorderFactory.createEmptyBorder(16, 17, 16, 17)
            )
        );

        JLabel welcomeTitle = new JLabel(
            "Welcome to Performance Analytics"
        );
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 19));

        JTextArea welcomeDescription = new JTextArea(
            "Explore how Breadth-First Search (BFS) and "
            + "Depth-First Search (DFS) perform when solving "
            + "different sizes of the Mushroom Maze."
        );

        welcomeDescription.setEditable(false);
        welcomeDescription.setLineWrap(true);
        welcomeDescription.setWrapStyleWord(true);
        welcomeDescription.setBackground(new Color(42, 75, 52));
        welcomeDescription.setForeground(
            new Color(225, 239, 225)
        );
        welcomeDescription.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        welcome.add(welcomeTitle, BorderLayout.NORTH);
        welcome.add(welcomeDescription, BorderLayout.CENTER);

        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(welcome);
        panel.add(Box.createVerticalStrut(18));

        JLabel highlightsTitle = createSectionTitle(
            "Performance Highlights"
        );
        highlightsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(highlightsTitle);

        TreeMap<Integer, double[]> data = getGraphData();

        double[] timeAverages = getPairedAverages(
            data, BFS_TIME, DFS_TIME
        );

        double[] memoryAverages = getPairedAverages(
            data, BFS_MEMORY, DFS_MEMORY
        );

        String fasterAlgorithm = "No Data";
        String memoryEfficient = "No Data";

        if (timeAverages != null) {
            fasterAlgorithm = compare(
                timeAverages[0],
                timeAverages[1]
            );
        }

        if (memoryAverages != null) {
            memoryEfficient = compare(
                memoryAverages[0],
                memoryAverages[1]
            );
        }

        JPanel highlightCards = new JPanel(
            new GridLayout(1, 2, 10, 0)
        );
        highlightCards.setBackground(BACKGROUND);

        highlightCards.add(
            createSummaryCard(
                "FASTER ALGORITHM",
                fasterAlgorithm,
                "Based on paired average execution time"
            )
        );

        highlightCards.add(
            createSummaryCard(
                "LOWER MEMORY ALLOCATION",
                memoryEfficient,
                "Based on paired average allocation"
            )
        );

        highlightCards.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 100)
        );
        highlightCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(highlightCards);
        panel.add(Box.createVerticalStrut(18));

        addTextSection(
            panel,
            "Benchmark Configuration",
            "MAZE SIZES\n"
            + "5x5, 10x10, 20x20, 30x30, and 50x50.\n\n"
            + "NUMBER OF RUNS\n"
            + "5 runs for every maze size.\n\n"
            + "ALGORITHMS\n"
            + "Breadth-First Search (BFS) and Depth-First Search (DFS).\n\n"
            + "TESTING METHOD\n"
            + "Both algorithms use the same generated maze within each benchmark trial. "
            + "The CSV stores the average result for each algorithm and maze size."
        );

        addTextSection(
            panel,
            "Available Analytics",
            "EXECUTION TIME\n"
            + "View how long each algorithm takes to explore and solve the maze.\n\n"
            + "MEMORY ALLOCATION\n"
            + "Compare the memory allocated by each algorithm's thread during execution.\n\n"
            + "VISITED CELLS\n"
            + "Analyze how many cells each algorithm explores.\n\n"
            + "ALGORITHM COMPARISON\n"
            + "Compare BFS and DFS using tables and graphs.\n\n"
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
                + " average benchmark records loaded from "
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

        try (BufferedReader reader = new BufferedReader(
            new FileReader(file)
        )) {

            String header = reader.readLine();

            if (header == null) {
                return false;
            }

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",", -1);

                if (values.length < 5) {
                    continue;
                }

                try {

                    Integer.parseInt(values[0].trim());

                    String algorithm = values[1].trim();

                    if (!algorithm.equalsIgnoreCase("BFS")
                        && !algorithm.equalsIgnoreCase("DFS")) {
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

    // ==========================================
    // RUN BENCHMARK
    // ==========================================

    private void runBenchmark() {

        runButton.setEnabled(false);
        exportButton.setEnabled(false);

        statusLabel.setText("Running benchmark...");

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
                                "The benchmark finished, but no valid CSV results "
                                + "could be loaded.",
                                "Benchmark Results",
                                JOptionPane.WARNING_MESSAGE
                            );
                        }

                    } catch (Exception ex) {

                        statusLabel.setText("Benchmark failed");

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

            File destination = chooser.getSelectedFile();

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
                InputStream input = new FileInputStream(source);
                OutputStream output = new FileOutputStream(destination)
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

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
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
                public boolean isCellEditable(int row, int column) {
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

        panel.add(scroll, BorderLayout.CENTER);

        JLabel note = new JLabel(
            "Each row contains the average of five benchmark runs. "
            + "Select a column heading to sort the results."
        );

        note.setForeground(MUTED);
        note.setFont(new Font("Arial", Font.PLAIN, 10));

        panel.add(note, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable table) {

        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setGridColor(new Color(55, 73, 61));

        table.setSelectionBackground(new Color(65, 110, 74));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 11)
        );

        table.getTableHeader().setBackground(
            new Color(49, 91, 54)
        );

        table.getTableHeader().setForeground(Color.WHITE);
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
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

        panel.add(chart, BorderLayout.CENTER);

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
            + "TIME COMPLEXITY: O(V + E)\n"
            + "SPACE COMPLEXITY: O(V)\n\n"
            + "BFS guarantees the shortest path in an unweighted maze "
            + "when all edges have equal cost."
        );

        addTextSection(
            panel,
            "Depth-First Search (DFS)",
            "DFS explores one route deeply before backtracking. "
            + "It commonly uses a Stack or recursion.\n\n"
            + "TIME COMPLEXITY: O(V + E)\n"
            + "SPACE COMPLEXITY: O(V)\n\n"
            + "DFS does not guarantee the shortest path."
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
            + "This can increase the amount of work required by both algorithms.\n\n"
            + "Actual measurements depend on maze structure, the search order, "
            + "and the execution environment."
        );

        addTextSection(
            panel,
            "Important Comparison",
            "BFS is appropriate when the shortest path is important.\n\n"
            + "DFS can explore routes without guaranteeing the shortest solution.\n\n"
            + "Both algorithms have O(V + E) time complexity and O(V) "
            + "auxiliary space complexity."
        );

        return wrapScroll(panel);
    }

    // ==========================================
    // BFS VS DFS SUMMARY
    // ==========================================

    private JPanel createComparisonPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        panel.add(
            createSectionTitle("BFS vs DFS Summary"),
            BorderLayout.NORTH
        );

        String[] columns = {
            "Maze Size",
            "Faster Algorithm",
            "Fewer Visited Cells",
            "Lower Memory Allocation",
            "Time Difference"
        };

        DefaultTableModel model =
            new DefaultTableModel(columns, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        TreeMap<Integer, double[]> data = getGraphData();

        for (Map.Entry<Integer, double[]> entry : data.entrySet()) {

            double[] v = entry.getValue();

            String faster = compare(v[BFS_TIME], v[DFS_TIME]);
            String fewer = compare(v[BFS_VISITED], v[DFS_VISITED]);
            String lowerMemory = compare(
                v[BFS_MEMORY],
                v[DFS_MEMORY]
            );

            String difference = "Unavailable";

            if (Double.isFinite(v[BFS_TIME]) &&
                Double.isFinite(v[DFS_TIME])) {

                double high = Math.max(
                    v[BFS_TIME],
                    v[DFS_TIME]
                );

                double low = Math.min(
                    v[BFS_TIME],
                    v[DFS_TIME]
                );

                difference = high == 0
                    ? "0.00%"
                    : String.format(
                        Locale.US,
                        "%.2f%%",
                        (high - low) / high * 100
                    );
            }

            int size = entry.getKey();

            model.addRow(new Object[] {
                size + "x" + size,
                faster,
                fewer,
                lowerMemory,
                difference
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PANEL);

        panel.add(scroll, BorderLayout.CENTER);

        JTextArea explanation = createTextArea(
            "HOW TO READ THIS TABLE\n\n"
            + "Faster Algorithm: The algorithm with the lower average execution time.\n\n"
            + "Fewer Visited Cells: The algorithm that explored fewer cells.\n\n"
            + "Lower Memory Allocation: The algorithm with lower allocated memory.\n\n"
            + "Time Difference: The percentage difference between the faster "
            + "and slower execution times.\n\n"
            + "Unavailable means a measurement could not be obtained. "
            + "Tie means both algorithms recorded equal values."
        );

        explanation.setRows(7);
        panel.add(explanation, BorderLayout.SOUTH);

        return panel;
    }

    private String compare(double a, double b) {

        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            return "Unavailable";
        }

        if (a < b) return "BFS";
        if (b < a) return "DFS";

        return "Tie";
    }

    // ==========================================
    // INSIGHTS
    // ==========================================

    private JScrollPane createInsightsPanel() {

        JPanel panel = createBasePanel();

        TreeMap<Integer, double[]> data = getGraphData();

        addTextSection(
            panel,
            "Overall Execution Time",
            buildOverallComparison(
                data, BFS_TIME, DFS_TIME, "execution time"
            )
        );

        addTextSection(
            panel,
            "Overall Memory Allocation",
            buildOverallComparison(
                data, BFS_MEMORY, DFS_MEMORY, "memory allocation"
            )
        );

        addTextSection(
            panel,
            "Execution Time Interpretation",
            buildPairInterpretation(
                data, BFS_TIME, DFS_TIME, "ms", "execution time"
            )
        );

        addTextSection(
            panel,
            "Memory Allocation Interpretation",
            buildPairInterpretation(
                data, BFS_MEMORY, DFS_MEMORY,
                "KB", "memory allocation"
            )
        );

        addTextSection(
            panel,
            "Visited Cells Interpretation",
            buildPairInterpretation(
                data, BFS_VISITED, DFS_VISITED,
                "cells", "visited cells"
            )
        );

        addTextSection(
            panel,
            "Execution Time Trend",
            buildTrend(
                data, BFS_TIME, DFS_TIME,
                "ms", "execution time"
            )
        );

        addTextSection(
            panel,
            "Visited Cells Trend",
            buildTrend(
                data, BFS_VISITED, DFS_VISITED,
                "cells", "visited cells"
            )
        );

        addTextSection(
            panel,
            "Complexity Interpretation",
            "BFS and DFS both have O(V + E) time complexity "
            + "and O(V) auxiliary space complexity.\n\n"
            + "As the maze becomes larger, the number of cells and possible "
            + "connections increases.\n\n"
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
    // PAIRED AVERAGES
    // ==========================================

    private double[] getPairedAverages(
        TreeMap<Integer, double[]> data,
        int bfsIndex,
        int dfsIndex
    ) {

        double bfsTotal = 0;
        double dfsTotal = 0;

        int count = 0;

        for (double[] values : data.values()) {

            double bfs = values[bfsIndex];
            double dfs = values[dfsIndex];

            // Only compare maze sizes where both algorithms
            // have a valid measurement.
            if (Double.isFinite(bfs) &&
                Double.isFinite(dfs)) {

                bfsTotal += bfs;
                dfsTotal += dfs;
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        return new double[] {
            bfsTotal / count,
            dfsTotal / count
        };
    }

    // ==========================================
    // OVERALL COMPARISON
    // ==========================================

    private String buildOverallComparison(
        TreeMap<Integer, double[]> data,
        int bfsIndex,
        int dfsIndex,
        String metric
    ) {

        double[] averages = getPairedAverages(
            data,
            bfsIndex,
            dfsIndex
        );

        if (averages == null) {
            return "Not enough paired data to compare " + metric + ".";
        }

        double bfsAverage = averages[0];
        double dfsAverage = averages[1];

        String winner = compare(
            bfsAverage,
            dfsAverage
        );

        return "Based on the average values across maze sizes where "
            + "both algorithms have valid measurements, the algorithm "
            + "with lower " + metric + " is: " + winner + ".\n\n"
            + String.format(
                Locale.US,
                "BFS average: %.4f\nDFS average: %.4f",
                bfsAverage,
                dfsAverage
            );
    }

    // ==========================================
    // PAIR INTERPRETATION
    // ==========================================

    private String buildPairInterpretation(
        TreeMap<Integer, double[]> data,
        int bfsIndex,
        int dfsIndex,
        String unit,
        String metric
    ) {

        if (data.isEmpty()) {
            return "No benchmark data is available.";
        }

        StringBuilder text = new StringBuilder();

        for (Map.Entry<Integer, double[]> entry : data.entrySet()) {

            int size = entry.getKey();

            double bfs = entry.getValue()[bfsIndex];
            double dfs = entry.getValue()[dfsIndex];

            text.append(size)
                .append("x")
                .append(size)
                .append(" Maze\n");

            if (!Double.isFinite(bfs) ||
                !Double.isFinite(dfs)) {

                text.append(
                    "Not enough paired data to compare.\n\n"
                );

                continue;
            }

            String winner = compare(bfs, dfs);

            if (winner.equals("Tie")) {

                text.append(
                    "Both algorithms recorded equal values.\n"
                );

            } else {

                text.append(winner)
                    .append(" recorded lower ")
                    .append(metric)
                    .append(".\n");
            }

            text.append(
                String.format(
                    Locale.US,
                    "BFS: %.2f %s | DFS: %.2f %s\n\n",
                    bfs, unit, dfs, unit
                )
            );
        }

        return text.toString();
    }

    // ==========================================
    // TREND ANALYSIS
    // ==========================================

    private String buildTrend(
        TreeMap<Integer, double[]> data,
        int bfsIndex,
        int dfsIndex,
        String unit,
        String metric
    ) {

        if (data.size() < 2) {
            return "At least two maze sizes are required to compare the "
                + metric + " trend.";
        }

        Map.Entry<Integer, double[]> first = data.firstEntry();
        Map.Entry<Integer, double[]> last = data.lastEntry();

        StringBuilder text = new StringBuilder();

        text.append("Smallest maze: ")
            .append(first.getKey())
            .append("x")
            .append(first.getKey())
            .append("\n");

        text.append("Largest maze: ")
            .append(last.getKey())
            .append("x")
            .append(last.getKey())
            .append("\n\n");

        appendMetricTrend(
            text,
            "BFS",
            first.getValue()[bfsIndex],
            last.getValue()[bfsIndex],
            unit,
            metric
        );

        appendMetricTrend(
            text,
            "DFS",
            first.getValue()[dfsIndex],
            last.getValue()[dfsIndex],
            unit,
            metric
        );

        text.append(
            "\nThis interpretation compares the smallest and largest "
            + "recorded maze sizes only. It describes the observed "
            + "measurements, not a guaranteed trend."
        );

        return text.toString();
    }

    private void appendMetricTrend(
        StringBuilder text,
        String algorithm,
        double first,
        double last,
        String unit,
        String metric
    ) {

        text.append(algorithm).append(": ");

        if (!Double.isFinite(first) ||
            !Double.isFinite(last)) {

            text.append("Insufficient data.\n\n");
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

        text.append(metric)
            .append(" ")
            .append(direction)
            .append(" from ");

        text.append(
            String.format(
                Locale.US,
                "%.2f %s to %.2f %s.\n\n",
                first, unit, last, unit
            )
        );
    }

    // ==========================================
    // GRAPH DATA
    // ==========================================

    private TreeMap<Integer, double[]> getGraphData() {

        TreeMap<Integer, double[]> data = new TreeMap<>();

        for (String[] row : results) {

            try {

                int size = Integer.parseInt(row[0].trim());

                int algorithm;

                if (row[1].trim().equalsIgnoreCase("BFS")) {
                    algorithm = 0;
                } else if (
                    row[1].trim().equalsIgnoreCase("DFS")
                ) {
                    algorithm = 1;
                } else {
                    continue;
                }

                double[] values = data.computeIfAbsent(
                    size,
                    key -> new double[] {
                        Double.NaN, Double.NaN,
                        Double.NaN, Double.NaN,
                        Double.NaN, Double.NaN
                    }
                );

                double time = parseValue(row, CSV_TIME);
                double visited = parseValue(row, CSV_VISITED);
                double memory = parseValue(row, CSV_MEMORY);

                // Each CSV row already represents the average
                // of five benchmark runs.
                values[algorithm] = time;
                values[algorithm + 2] = memory;
                values[algorithm + 4] = visited;

            } catch (Exception ignored) {
            }
        }

        return data;
    }

    private double parseValue(String[] row, int index) {

        try {

            if (index >= row.length) {
                return Double.NaN;
            }

            String value = row[index].trim();

            if (value.isEmpty() ||
                value.equalsIgnoreCase("Unavailable")) {

                return Double.NaN;
            }

            double number = Double.parseDouble(value);

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
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        return panel;
    }

    private JScrollPane wrapScroll(JPanel panel) {

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);

        scroll.getViewport().setBackground(BACKGROUND);

        scroll.getVerticalScrollBar().setUnitIncrement(18);

        return scroll;
    }

    private JLabel createSectionTitle(String title) {

        JLabel label = new JLabel(title);

        label.setForeground(TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 19));

        label.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 12, 5)
        );

        return label;
    }

    private void addTextSection(
        JPanel panel,
        String heading,
        String description
    ) {

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(CARD);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(57, 78, 63)
                ),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );

        card.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 500)
        );

        JLabel title = new JLabel(heading);
        title.setForeground(LIGHT_GREEN);
        title.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea text = createTextArea(description);

        card.add(title, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(card);
        panel.add(Box.createVerticalStrut(12));
    }

    private JTextArea createTextArea(String content) {

        JTextArea text = new JTextArea(content);

        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setForeground(TEXT);
        text.setBackground(CARD);

        text.setBorder(
            BorderFactory.createEmptyBorder(5, 2, 5, 2)
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
            setPreferredSize(new Dimension(800, 520));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

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

            g2.setColor(TEXT);
            g2.setFont(new Font("Arial", Font.BOLD, 19));

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                title,
                (width - fm.stringWidth(title)) / 2,
                38
            );

            // All charts now use the same graph data source.
            TreeMap<Integer, double[]> values = getGraphData();

            // Convert CSV column to the matching graph data index.
            int bfsIndex;
            int dfsIndex;

            if (valueIndex == CSV_TIME) {
                bfsIndex = BFS_TIME;
                dfsIndex = DFS_TIME;
            } else if (valueIndex == CSV_MEMORY) {
                bfsIndex = BFS_MEMORY;
                dfsIndex = DFS_MEMORY;
            } else {
                bfsIndex = BFS_VISITED;
                dfsIndex = DFS_VISITED;
            }

            double max = 0;

            for (double[] pair : values.values()) {

                if (Double.isFinite(pair[bfsIndex])) {
                    max = Math.max(max, pair[bfsIndex]);
                }

                if (Double.isFinite(pair[dfsIndex])) {
                    max = Math.max(max, pair[dfsIndex]);
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

            g2.setFont(new Font("Arial", Font.PLAIN, 11));

            for (int i = 0; i <= 5; i++) {

                int y = bottom -
                    (int)((bottom - top) * i / 5.0);

                g2.setColor(new Color(57, 73, 63));
                g2.drawLine(left, y, right, y);

                double labelValue = max * i / 5.0;

                g2.setColor(MUTED);

                String label = String.format(
                    Locale.US,
                    "%.2f",
                    labelValue
                );

                g2.drawString(label, 15, y + 4);
            }

            // AXES

            g2.setColor(MUTED);

            g2.drawLine(left, top, left, bottom);
            g2.drawLine(left, bottom, right, bottom);

            g2.drawString(yLabel, 15, 65);

            g2.drawString(
                "Maze Size",
                width / 2 - 25,
                height - 25
            );

            int count = values.size();
            int position = 0;

            int groupWidth = (right - left) / count;

            if (grouped) {

                int barWidth = Math.max(
                    8,
                    Math.min(35, groupWidth / 4)
                );

                for (Map.Entry<Integer, double[]> entry :
                    values.entrySet()) {

                    int center = left +
                        groupWidth * position +
                        groupWidth / 2;

                    double[] pair = entry.getValue();

                    drawBar(
                        g2,
                        center - barWidth,
                        bottom,
                        top,
                        pair[bfsIndex],
                        max,
                        barWidth - 2,
                        BFS_COLOR
                    );

                    drawBar(
                        g2,
                        center + 2,
                        bottom,
                        top,
                        pair[dfsIndex],
                        max,
                        barWidth - 2,
                        DFS_COLOR
                    );

                    drawMazeLabel(
                        g2,
                        entry.getKey(),
                        center,
                        bottom
                    );

                    position++;
                }

            } else {

                drawLine(
                    g2,
                    values,
                    bfsIndex,
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
                    dfsIndex,
                    DFS_COLOR,
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

                    drawMazeLabel(g2, size, x, bottom);

                    position++;
                }
            }

            drawLegend(g2, width, height);

            g2.dispose();
        }

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

            barHeight = Math.max(0, barHeight);

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
            g2.setStroke(new BasicStroke(3f));

            for (double[] pair : values.values()) {

                double value = pair[algorithmIndex];

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
                    g2.drawLine(previousX, previousY, x, y);
                }

                g2.fillOval(x - 5, y - 5, 10, 10);

                previousX = x;
                previousY = y;
            }
        }

        private void drawMazeLabel(
            Graphics2D g2,
            int size,
            int x,
            int bottom
        ) {

            g2.setColor(MUTED);

            String label = size + "x" + size;

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                label,
                x - fm.stringWidth(label) / 2,
                bottom + 23
            );
        }

        private void drawLegend(
            Graphics2D g2,
            int width,
            int height
        ) {

            int y = height - 55;

            g2.setFont(new Font("Arial", Font.BOLD, 12));

            g2.setColor(BFS_COLOR);

            g2.fillRoundRect(
                width / 2 - 65,
                y - 9,
                13,
                13,
                4,
                4
            );

            g2.setColor(TEXT);

            g2.drawString("BFS", width / 2 - 45, y + 2);

            g2.setColor(DFS_COLOR);

            g2.fillRoundRect(
                width / 2 + 5,
                y - 9,
                13,
                13,
                4,
                4
            );

            g2.setColor(TEXT);

            g2.drawString("DFS", width / 2 + 25, y + 2);
        }

        private void drawCenteredMessage(
            Graphics2D g2,
            String message,
            int width,
            int height
        ) {

            g2.setColor(MUTED);
            g2.setFont(new Font("Arial", Font.PLAIN, 15));

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                message,
                (width - fm.stringWidth(message)) / 2,
                height / 2
            );
        }
    }
}
