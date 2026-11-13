
package Algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BenchmarkResults {

    private static final String CSV_FILE =
        "MushroomMaze_Benchmark.csv";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> showResults());
    }

    private static void showResults() {

        String[] columns = {
            "Maze Size",
            "Algorithm",
            "Average Time (ms)",
            "Visited Cells",
            "Memory (KB)"
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

        try (BufferedReader reader =
            new BufferedReader(new FileReader(CSV_FILE))) {

            String line;

            // Skip CSV header
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] values = line.split(",");

                if (values.length >= 5) {

                    model.addRow(values);
                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                null,
                "Cannot find benchmark CSV file.\nRun AlgorithmBenchmark first.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JTable table = new JTable(model);

        table.setRowHeight(30);

        table.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setBackground(
            new Color(55, 125, 65)
        );

        table.getTableHeader().setForeground(Color.WHITE);

        table.setSelectionBackground(
            new Color(210, 235, 210)
        );

        table.setAutoResizeMode(
            JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        JScrollPane scrollPane =
            new JScrollPane(table);

        JLabel title = new JLabel(
            "MUSHROOM MAZE - BENCHMARK RESULTS",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        title.setBorder(
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        JLabel subtitle = new JLabel(
            "BFS and DFS Performance Comparison",
            SwingConstants.CENTER
        );

        subtitle.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.add(title);
        heading.add(subtitle);

        JFrame window = new JFrame("Benchmark Results");

        window.setLayout(new BorderLayout(10, 10));

        window.add(heading, BorderLayout.NORTH);
        window.add(scrollPane, BorderLayout.CENTER);

        window.setSize(950, 450);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }
}
