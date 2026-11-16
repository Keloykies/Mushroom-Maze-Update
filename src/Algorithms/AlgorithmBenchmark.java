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
            "          BFS, DFS AND A* PERFORMANCE TEST"
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
    // BENCHMARK ALL THREE USING THE SAME MAZES
    // ==========================================

    private static void benchmarkMazeSize(int size) {

        long totalBFSTime = 0;
        long totalDFSTime = 0;
        long totalAStarTime = 0;

        long totalBFSMemory = 0;
        long totalDFSMemory = 0;
        long totalAStarMemory = 0;

        int totalBFSVisited = 0;
        int totalDFSVisited = 0;
        int totalAStarVisited = 0;

        boolean bfsMemoryAvailable = MEMORY_BEAN != null;
        boolean dfsMemoryAvailable = MEMORY_BEAN != null;
        boolean aStarMemoryAvailable = MEMORY_BEAN != null;

        long threadId =
            Thread.currentThread().getId();

        for (int i = 0; i < RUNS; i++) {

            // All three algorithms use the same generated maze
            Maze maze =
                MazeGenerator.generateMaze(size, size);

            // ==========================================
            // WARM-UP
            // ==========================================

            if (i == 0) {

                BFS.findPath(maze, false);
                DFS.findPath(maze, false);
                AStar.findPath(maze, false);
            }

            System.out.println(
                "Run " + (i + 1) + " of " + RUNS
            );

            // ==========================================
            // ROTATE EXECUTION ORDER
            // ==========================================

            // Order 1: BFS -> DFS -> A*
            if (i % 3 == 0) {

                long[] bfs = measureBFS(maze, threadId);
                totalBFSTime += bfs[0];
                totalBFSVisited += (int) bfs[1];

                if (bfs[2] >= 0) {
                    totalBFSMemory += bfs[2];
                } else {
                    bfsMemoryAvailable = false;
                }

                long[] dfs = measureDFS(maze, threadId);
                totalDFSTime += dfs[0];
                totalDFSVisited += (int) dfs[1];

                if (dfs[2] >= 0) {
                    totalDFSMemory += dfs[2];
                } else {
                    dfsMemoryAvailable = false;
                }

                long[] aStar = measureAStar(maze, threadId);
                totalAStarTime += aStar[0];
                totalAStarVisited += (int) aStar[1];

                if (aStar[2] >= 0) {
                    totalAStarMemory += aStar[2];
                } else {
                    aStarMemoryAvailable = false;
                }
            }

            // Order 2: DFS -> A* -> BFS
            else if (i % 3 == 1) {

                long[] dfs = measureDFS(maze, threadId);
                totalDFSTime += dfs[0];
                totalDFSVisited += (int) dfs[1];

                if (dfs[2] >= 0) {
                    totalDFSMemory += dfs[2];
                } else {
                    dfsMemoryAvailable = false;
                }

                long[] aStar = measureAStar(maze, threadId);
                totalAStarTime += aStar[0];
                totalAStarVisited += (int) aStar[1];

                if (aStar[2] >= 0) {
                    totalAStarMemory += aStar[2];
                } else {
                    aStarMemoryAvailable = false;
                }

                long[] bfs = measureBFS(maze, threadId);
                totalBFSTime += bfs[0];
                totalBFSVisited += (int) bfs[1];

                if (bfs[2] >= 0) {
                    totalBFSMemory += bfs[2];
                } else {
                    bfsMemoryAvailable = false;
                }
            }

            // Order 3: A* -> BFS -> DFS
            else {

                long[] aStar = measureAStar(maze, threadId);
                totalAStarTime += aStar[0];
                totalAStarVisited += (int) aStar[1];

                if (aStar[2] >= 0) {
                    totalAStarMemory += aStar[2];
                } else {
                    aStarMemoryAvailable = false;
                }

                long[] bfs = measureBFS(maze, threadId);
                totalBFSTime += bfs[0];
                totalBFSVisited += (int) bfs[1];

                if (bfs[2] >= 0) {
                    totalBFSMemory += bfs[2];
                } else {
                    bfsMemoryAvailable = false;
                }

                long[] dfs = measureDFS(maze, threadId);
                totalDFSTime += dfs[0];
                totalDFSVisited += (int) dfs[1];

                if (dfs[2] >= 0) {
                    totalDFSMemory += dfs[2];
                } else {
                    dfsMemoryAvailable = false;
                }
            }
        }

        // ==========================================
        // SAVE AVERAGE RESULTS
        // ==========================================

        printResults(
            size,
            "BFS",
            totalBFSTime,
            totalBFSVisited,
            totalBFSMemory,
            bfsMemoryAvailable
        );

        printResults(
            size,
            "DFS",
            totalDFSTime,
            totalDFSVisited,
            totalDFSMemory,
            dfsMemoryAvailable
        );

        printResults(
            size,
            "A*",
            totalAStarTime,
            totalAStarVisited,
            totalAStarMemory,
            aStarMemoryAvailable
        );
    }

    // ==========================================
    // MEASURE BFS
    // ==========================================

    private static long[] measureBFS(
        Maze maze,
        long threadId
    ) {

        long memoryBefore = getAllocatedMemory(threadId);

        long startTime = System.nanoTime();

        BFS.SearchResult result =
            BFS.findPath(maze, false);

        long endTime = System.nanoTime();

        long memoryAfter = getAllocatedMemory(threadId);

        long allocatedMemory = calculateAllocatedMemory(
            memoryBefore,
            memoryAfter
        );

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

        long memoryBefore = getAllocatedMemory(threadId);

        long startTime = System.nanoTime();

        DFS.SearchResult result =
            DFS.findPath(maze, false);

        long endTime = System.nanoTime();

        long memoryAfter = getAllocatedMemory(threadId);

        long allocatedMemory = calculateAllocatedMemory(
            memoryBefore,
            memoryAfter
        );

        return new long[] {
            endTime - startTime,
            result.getVisitedOrder().size(),
            allocatedMemory
        };
    }

    // ==========================================
    // MEASURE A*
    // ==========================================

    private static long[] measureAStar(
        Maze maze,
        long threadId
    ) {

        long memoryBefore = getAllocatedMemory(threadId);

        long startTime = System.nanoTime();

        AStar.SearchResult result =
            AStar.findPath(maze, false);

        long endTime = System.nanoTime();

        long memoryAfter = getAllocatedMemory(threadId);

        long allocatedMemory = calculateAllocatedMemory(
            memoryBefore,
            memoryAfter
        );

        return new long[] {
            endTime - startTime,
            result.getVisitedOrder().size(),
            allocatedMemory
        };
    }

    // ==========================================
    // CALCULATE ALLOCATED MEMORY
    // ==========================================

    private static long calculateAllocatedMemory(
        long before,
        long after
    ) {

        if (before < 0 || after < before) {
            return -1;
        }

        return after - before;
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