
package Algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import Maze.Maze;

public class BFS {

    private static final int[][] DIRECTIONS = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // ==========================================
    // DEFAULT BFS - WITH QUEUE SNAPSHOTS
    // ==========================================

    public static SearchResult findPath(Maze maze) {

        return findPath(maze, true);
    }

    // ==========================================
    // BFS WITH OPTIONAL QUEUE SNAPSHOTS
    // ==========================================

    public static SearchResult findPath(
        Maze maze,
        boolean captureSnapshots
    ) {

        int rows = maze.getRows();
        int cols = maze.getCols();

        boolean[][] visited = new boolean[rows][cols];

        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];

        // Initialize parent arrays
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                parentRow[row][col] = -1;
                parentCol[row][col] = -1;
            }
        }

        Queue<Point> queue = new LinkedList<>();

        ArrayList<Point> visitedOrder = new ArrayList<>();

        ArrayList<ArrayList<Point>> queueSnapshots =
            new ArrayList<>();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int exitRow = maze.getExitRow();
        int exitCol = maze.getExitCol();

        // Add starting position
        queue.add(new Point(startRow, startCol));

        visited[startRow][startCol] = true;

        boolean found = false;

        // ==========================================
        // BFS TRAVERSAL
        // ==========================================

        while (!queue.isEmpty()) {

            Point current = queue.poll();

            int currentRow = current.x;
            int currentCol = current.y;

            visitedOrder.add(
                new Point(currentRow, currentCol)
            );

            // Check if exit is reached
            if (currentRow == exitRow &&
                currentCol == exitCol) {

                found = true;

                if (captureSnapshots) {
                    queueSnapshots.add(copyQueue(queue));
                }

                break;
            }

            // Explore neighboring cells
            for (int[] direction : DIRECTIONS) {

                int nextRow = currentRow + direction[0];
                int nextCol = currentCol + direction[1];

                if (maze.isPath(nextRow, nextCol) &&
                    !visited[nextRow][nextCol]) {

                    visited[nextRow][nextCol] = true;

                    parentRow[nextRow][nextCol] = currentRow;
                    parentCol[nextRow][nextCol] = currentCol;

                    queue.add(
                        new Point(nextRow, nextCol)
                    );
                }
            }

            // Save Queue after processing current cell
            if (captureSnapshots) {

                queueSnapshots.add(copyQueue(queue));
            }
        }

        // ==========================================
        // RECONSTRUCT SHORTEST PATH
        // ==========================================

        ArrayList<Point> path = new ArrayList<>();

        if (found) {

            int currentRow = exitRow;
            int currentCol = exitCol;

            while (currentRow != -1 && currentCol != -1) {

                path.add(
                    new Point(currentRow, currentCol)
                );

                int previousRow =
                    parentRow[currentRow][currentCol];

                int previousCol =
                    parentCol[currentRow][currentCol];

                currentRow = previousRow;
                currentCol = previousCol;
            }

            // Reverse path to get start-to-exit order
            Collections.reverse(path);
        }

        return new SearchResult(
            found,
            visitedOrder,
            path,
            queueSnapshots
        );
    }

    // ==========================================
    // COPY QUEUE SNAPSHOT
    // ==========================================

    private static ArrayList<Point> copyQueue(
        Queue<Point> queue
    ) {

        ArrayList<Point> snapshot = new ArrayList<>();

        for (Point point : queue) {

            snapshot.add(
                new Point(point.x, point.y)
            );
        }

        return snapshot;
    }

    // ==========================================
    // SEARCH RESULT
    // ==========================================

    public static class SearchResult {

        private boolean found;

        private ArrayList<Point> visitedOrder;

        private ArrayList<Point> path;

        private ArrayList<ArrayList<Point>> queueSnapshots;

        public SearchResult(
            boolean found,
            ArrayList<Point> visitedOrder,
            ArrayList<Point> path,
            ArrayList<ArrayList<Point>> queueSnapshots
        ) {

            this.found = found;
            this.visitedOrder = visitedOrder;
            this.path = path;
            this.queueSnapshots = queueSnapshots;
        }

        // Backward-compatible constructor
        public SearchResult(
            boolean found,
            ArrayList<Point> visitedOrder,
            ArrayList<Point> path
        ) {

            this(
                found,
                visitedOrder,
                path,
                new ArrayList<>()
            );
        }

        public boolean isFound() {
            return found;
        }

        public ArrayList<Point> getVisitedOrder() {
            return visitedOrder;
        }

        public ArrayList<Point> getPath() {
            return path;
        }

        public ArrayList<ArrayList<Point>> getQueueSnapshots() {
            return queueSnapshots;
        }
    }
}
