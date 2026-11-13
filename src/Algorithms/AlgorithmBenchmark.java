
package Algorithms;

import java.lang.management.ManagementFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import com.sun.management.ThreadMXBean;

import Maze.Maze;
import Maze.MazeGenerator;

public class AlgorithmBenchmark {

    private static final int[] MAZE_SIZES = {
        5, 10, 20, 30, 50
    };

    private static final int RUNS = 5;

    private static final ThreadMXBean MEMORY_BEAN =
        getMemoryBean();

    private static StringBuilder csvData =
        new StringBuilder();

    // ==========================================
    // GET MEMORY MEASUREMENT SUPPORT
    // ==========================================

    private static ThreadMXBean getMemoryBean() {

        try {

            java.lang.management.ThreadMXBean bean =
                ManagementFactory.getThreadMXBean();

            if (bean instanceof ThreadMXBean) {

                ThreadMXBean threadBean =
                    (ThreadMXBean) bean;

                if (threadBean.isThreadAllocatedMemorySupported()) {

                    if (!threadBean.isThreadAllocatedMemoryEnabled()) {

                        threadBean.setThreadAllocatedMemoryEnabled(true);
                    }

                    return threadBean;
                }
            }

        } catch (UnsupportedOperationException |
                 SecurityException e) {

            System.out.println(
                "Thread memory measurement is unavailable."
            );
        }

        return null;
    }

    // ==========================================
    // RUN ALL BENCHMARK TESTS
    // ==========================================

    public static void runBenchmark() {

        csvData = new StringBuilder();

        csvData.append(
            "Maze Size,Algorithm,Average Time (ms),Visited Cells,Memory (KB)\n"
        );

        System.out.println(
            "\n=============================================================="
        );

        System.out.println(
            "             BFS AND DFS PERFORMANCE TEST"
        );

        System.out.println(
            "=============================================================="
        );

        System.out.printf(
            "%-10s %-10s %-18s %-18s %-18s%n",
            "Size",
            "Algorithm",
            "Average Time",
            "Visited Cells",
            "Memory (KB)"
        );

        for (int size : MAZE_SIZES) {

            System.out.println(
                "\nTesting Maze Size: " + size + "x" + size
            );

            benchmarkMazeSize(size);
        }

        System.out.println(
            "\n=============================================================="
        );

        System.out.println(
            "                  TEST COMPLETED"
        );

        System.out.println(
            "=============================================================="
        );

        saveCSV();
    }

    // ==========================================
    // BENCHMARK BFS AND DFS USING THE SAME MAZES
    // ==========================================

    private static void benchmarkMazeSize(int size) {

        long totalBFSTime = 0;
        long totalDFSTime = 0;

        long totalBFSMemory = 0;
        long totalDFSMemory = 0;

        int totalBFSVisited = 0;
        int totalDFSVisited = 0;

        boolean bfsMemoryAvailable =
            MEMORY_BEAN != null;

        boolean dfsMemoryAvailable =
            MEMORY_BEAN != null;

        long threadId =
            Thread.currentThread().getId();

        for (int i = 0; i < RUNS; i++) {

            // Generate one maze for this trial.
            // Both algorithms use the exact same maze.

            Maze maze =
                MazeGenerator.generateMaze(size, size);

            // ==========================================
            // WARM-UP
            // ==========================================

            if (i == 0) {

                // Disable snapshots during warm-up
                BFS.findPath(maze, false);

                DFS.findPath(maze, false);
            }

            System.out.println(
                "Run " + (i + 1) + " of " + RUNS
            );

            // ==========================================
            // ALTERNATE EXECUTION ORDER
            // ==========================================

            if (i % 2 == 0) {

                // BFS first
                long[] bfsResult =
                    measureBFS(maze, threadId);

                totalBFSTime += bfsResult[0];
                totalBFSVisited += (int) bfsResult[1];

                if (bfsResult[2] >= 0) {

                    totalBFSMemory += bfsResult[2];

                } else {

                    bfsMemoryAvailable = false;
                }

                // DFS second
                long[] dfsResult =
                    measureDFS(maze, threadId);

                totalDFSTime += dfsResult[0];
                totalDFSVisited += (int) dfsResult[1];

                if (dfsResult[2] >= 0) {

                    totalDFSMemory += dfsResult[2];

                } else {

                    dfsMemoryAvailable = false;
                }

            } else {

                // DFS first
                long[] dfsResult =
                    measureDFS(maze, threadId);

                totalDFSTime += dfsResult[0];
                totalDFSVisited += (int) dfsResult[1];

                if (dfsResult[2] >= 0) {

                    totalDFSMemory += dfsResult[2];

                } else {

                    dfsMemoryAvailable = false;
                }

                // BFS second
                long[] bfsResult =
                    measureBFS(maze, threadId);

                totalBFSTime += bfsResult[0];
                totalBFSVisited += (int) bfsResult[1];

                if (bfsResult[2] >= 0) {

                    totalBFSMemory += bfsResult[2];

                } else {

                    bfsMemoryAvailable = false;
                }
            }
        }

        // ==========================================
        // SAVE AVERAGE BFS RESULTS
        // ==========================================

        printResults(
            size,
            "BFS",
            totalBFSTime,
            totalBFSVisited,
            totalBFSMemory,
            bfsMemoryAvailable
        );

        // ==========================================
        // SAVE AVERAGE DFS RESULTS
        // ==========================================

        printResults(
            size,
            "DFS",
            totalDFSTime,
            totalDFSVisited,
            totalDFSMemory,
            dfsMemoryAvailable
        );
    }

    // ==========================================
    // MEASURE BFS
    // ==========================================

    private static long[] measureBFS(
        Maze maze,
        long threadId
    ) {

        long memoryBefore =
            getAllocatedMemory(threadId);

        long startTime = System.nanoTime();

        // Disable queue snapshots during benchmarking
        BFS.SearchResult result =
            BFS.findPath(maze, false);

        long endTime = System.nanoTime();

        long memoryAfter =
            getAllocatedMemory(threadId);

        long allocatedMemory = -1;

        if (memoryBefore >= 0 &&
            memoryAfter >= memoryBefore) {

            allocatedMemory =
                memoryAfter - memoryBefore;
        }

        return new long[] {
            endTime - startTime,
            result.getVisitedOrder().size(),
            allocatedMemory
        };
    }

    // ==========================================
    // MEASURE DFS
    // ==========================================

    private static long[] measureDFS(
        Maze maze,
        long threadId
    ) {

        long memoryBefore =
            getAllocatedMemory(threadId);

        long startTime = System.nanoTime();

        // Disable stack snapshots during benchmarking
        DFS.SearchResult result =
            DFS.findPath(maze, false);

        long endTime = System.nanoTime();

        long memoryAfter =
            getAllocatedMemory(threadId);

        long allocatedMemory = -1;

        if (memoryBefore >= 0 &&
            memoryAfter >= memoryBefore) {

            allocatedMemory =
                memoryAfter - memoryBefore;
        }

        return new long[] {
            endTime - startTime,
            result.getVisitedOrder().size(),
            allocatedMemory
        };
    }

    // ==========================================
    // GET ALLOCATED MEMORY
    // ==========================================

    private static long getAllocatedMemory(
        long threadId
    ) {

        if (MEMORY_BEAN == null) {

            return -1;
        }

        return MEMORY_BEAN.getThreadAllocatedBytes(threadId);
    }

    // ==========================================
    // DISPLAY AND SAVE RESULTS
    // ==========================================

    private static void printResults(
        int size,
        String algorithm,
        long totalTime,
        int totalVisited,
        long totalMemory,
        boolean memoryAvailable
    ) {

        double averageTime =
            (totalTime / (double) RUNS) / 1_000_000.0;

        double averageVisited =
            totalVisited / (double) RUNS;

        String memoryResult;

        if (!memoryAvailable) {

            memoryResult = "Unavailable";

        } else {

            double averageMemory =
                (totalMemory / (double) RUNS) / 1024.0;

            memoryResult = String.format(
                Locale.US,
                "%.2f",
                averageMemory
            );
        }

        System.out.printf(
            Locale.US,
            "%-10s %-10s %-18.4f %-18.2f %-18s%n",
            size + "x" + size,
            algorithm,
            averageTime,
            averageVisited,
            memoryResult
        );

        // Add results to CSV
        csvData.append(String.format(
            Locale.US,
            "%d,%s,%.4f,%.2f,%s%n",
            size,
            algorithm,
            averageTime,
            averageVisited,
            memoryResult
        ));
    }

    // ==========================================
    // SAVE CSV FILE
    // ==========================================

    private static void saveCSV() {

        try (
            FileWriter writer =
                new FileWriter("MushroomMaze_Benchmark.csv")
        ) {

            writer.write(csvData.toString());

            System.out.println(
                "\nCSV file successfully saved!"
            );

            System.out.println(
                "File: MushroomMaze_Benchmark.csv"
            );

        } catch (IOException e) {

            System.out.println(
                "Could not save CSV file."
            );

            e.printStackTrace();
        }
    }
}
