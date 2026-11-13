
package Algorithms;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

public class BenchmarkGraphs {

    private static final String CSV_FILE =
        "MushroomMaze_Benchmark.csv";

    private static final TreeMap<Integer, double[]> timeData =
        new TreeMap<>();

    private static final TreeMap<Integer, double[]> memoryData =
        new TreeMap<>();

    public static void main(String[] args) {

        if (!loadCSV()) {
            return;
        }

        showGraphs();
    }

    // Read benchmark results
    private static boolean loadCSV() {

        timeData.clear();
        memoryData.clear();

        try (BufferedReader reader =
            new BufferedReader(new FileReader(CSV_FILE))) {

            String line;

            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] values = line.split(",");

                if (values.length < 5) {
                    continue;
                }

                int size = Integer.parseInt(values[0]);

                String algorithm = values[1];

                double time = Double.parseDouble(values[2]);

                double memory;

                try {
                    memory = Double.parseDouble(values[4]);
                } catch (NumberFormatException e) {
                    memory = Double.NaN;
                }

                int index;

                if ("BFS".equalsIgnoreCase(algorithm)) {
                    index = 0;
                } else if ("DFS".equalsIgnoreCase(algorithm)) {
                    index = 1;
                } else {
                    continue;
                }

                timeData.putIfAbsent(size, new double[]{
                    Double.NaN, Double.NaN
                });

                memoryData.putIfAbsent(size, new double[]{
                    Double.NaN, Double.NaN
                });

                timeData.get(size)[index] = time;
                memoryData.get(size)[index] = memory;
            }

            if (timeData.isEmpty()) {
                System.out.println("No benchmark data found.");
                return false;
            }

            return true;

        } catch (IOException | NumberFormatException e) {

            System.out.println(
                "Could not read " + CSV_FILE
            );

            e.printStackTrace();
            return false;
        }
    }

    // Display graphs
    private static void showGraphs() {

        SwingUtilities.invokeLater(() -> {

            JFrame window = new JFrame(
                "Mushroom Maze - BFS vs DFS Performance"
            );

            window.setLayout(new GridLayout(2, 1, 10, 10));

            window.add(new GraphPanel(
                "Input Size vs. Execution Time",
                "Execution Time (ms)",
                timeData
            ));

            window.add(new GraphPanel(
                "Input Size vs. Memory Usage",
                "Allocated Memory (KB)",
                memoryData
            ));

            window.setSize(1000, 900);
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            saveGraph(
                new GraphPanel(
                    "Input Size vs. Execution Time",
                    "Execution Time (ms)",
                    timeData
                ),
                "MushroomMaze_Execution_Time.png"
            );

            saveGraph(
                new GraphPanel(
                    "Input Size vs. Memory Usage",
                    "Allocated Memory (KB)",
                    memoryData
                ),
                "MushroomMaze_Memory_Usage.png"
            );
        });
    }

    // Save graph as PNG
    private static void saveGraph(
        GraphPanel panel,
        String fileName
    ) {

        int width = 1000;
        int height = 500;

        panel.setSize(width, height);

        BufferedImage image = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = image.createGraphics();

        panel.paint(graphics);

        graphics.dispose();

        try {

            ImageIO.write(
                image,
                "png",
                new File(fileName)
            );

            System.out.println(
                "Graph saved: " + fileName
            );

        } catch (IOException e) {

            System.out.println(
                "Could not save graph: " + fileName
            );

            e.printStackTrace();
        }
    }

    // Graph drawing panel
    static class GraphPanel extends JPanel {

        private final String title;
        private final String yLabel;

        private final TreeMap<Integer, double[]> data;

        GraphPanel(
            String title,
            String yLabel,
            TreeMap<Integer, double[]> data
        ) {

            this.title = title;
            this.yLabel = yLabel;
            this.data = data;

            setBackground(Color.WHITE);
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

            int left = 90;
            int right = width - 45;
            int top = 75;
            int bottom = height - 85;

            // Title
            g2.setColor(Color.BLACK);

            g2.setFont(
                new Font("Arial", Font.BOLD, 21)
            );

            FontMetrics metrics = g2.getFontMetrics();

            g2.drawString(
                title,
                (width - metrics.stringWidth(title)) / 2,
                35
            );

            // Axis labels
            g2.setFont(new Font("Arial", Font.PLAIN, 13));

            g2.drawString(
                "Maze Size",
                width / 2 - 25,
                height - 20
            );

            g2.rotate(-Math.PI / 2);

            g2.drawString(
                yLabel,
                -(height + metrics.stringWidth(yLabel)) / 2,
                25
            );

            g2.rotate(Math.PI / 2);

            // Find maximum value
            double maxValue = 0;

            for (double[] values : data.values()) {

                for (double value : values) {

                    if (Double.isFinite(value)) {
                        maxValue = Math.max(maxValue, value);
                    }
                }
            }

            if (maxValue <= 0) {
                maxValue = 1;
            }

            maxValue *= 1.15;

            // Grid and Y-axis labels
            for (int i = 0; i <= 5; i++) {

                int y = bottom -
                    (int) ((bottom - top) * i / 5.0);

                double value = maxValue * i / 5.0;

                g2.setColor(new Color(225, 225, 225));

                g2.drawLine(left, y, right, y);

                g2.setColor(Color.BLACK);

                g2.drawString(
                    String.format("%.2f", value),
                    25,
                    y + 5
                );
            }

            // X-axis
            g2.setColor(Color.BLACK);

            g2.drawLine(left, top, left, bottom);
            g2.drawLine(left, bottom, right, bottom);

            int count = data.size();

            int position = 0;

            for (int size : data.keySet()) {

                int x = count == 1
                    ? (left + right) / 2
                    : left + (int) (
                        (right - left) * position /
                        (double) (count - 1)
                    );

                g2.drawString(
                    size + "x" + size,
                    x - 20,
                    bottom + 25
                );

                position++;
            }

            // Draw BFS and DFS lines
            drawSeries(g2, 0, new Color(40, 120, 220),
                left, right, top, bottom, maxValue);

            drawSeries(g2, 1, new Color(230, 120, 40),
                left, right, top, bottom, maxValue);

            // Legend
            int legendY = height - 48;

            g2.setColor(new Color(40, 120, 220));
            g2.fillOval(width / 2 - 75, legendY - 9, 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawString("BFS", width / 2 - 57, legendY + 2);

            g2.setColor(new Color(230, 120, 40));
            g2.fillOval(width / 2 + 5, legendY - 9, 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawString("DFS", width / 2 + 23, legendY + 2);

            g2.dispose();
        }

        // Draw one algorithm's line
        private void drawSeries(
            Graphics2D g2,
            int seriesIndex,
            Color color,
            int left,
            int right,
            int top,
            int bottom,
            double maxValue
        ) {

            int count = data.size();
            int position = 0;

            int previousX = -1;
            int previousY = -1;

            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.5f));

            for (Map.Entry<Integer, double[]> entry :
                data.entrySet()) {

                double value = entry.getValue()[seriesIndex];

                int x = count == 1
                    ? (left + right) / 2
                    : left + (int) (
                        (right - left) * position /
                        (double) (count - 1)
                    );

                position++;

                if (!Double.isFinite(value)) {
                    previousX = -1;
                    previousY = -1;
                    continue;
                }

                int y = bottom - (int) (
                    (value / maxValue) * (bottom - top)
                );

                if (previousX != -1) {
                    g2.drawLine(
                        previousX,
                        previousY,
                        x,
                        y
                    );
                }

                g2.fillOval(x - 5, y - 5, 10, 10);

                previousX = x;
                previousY = y;
            }
        }
    }
}
