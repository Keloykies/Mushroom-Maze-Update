
package Algorithms;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

import Maze.Maze;

public class DFS {

    private static final int[][] DIRECTIONS = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // ==========================================
    // DEFAULT DFS - WITH STACK SNAPSHOTS
    // ==========================================

    public static SearchResult findPath(Maze maze) {

        return findPath(maze, true);
    }

    // ==========================================
    // DFS WITH OPTIONAL STACK SNAPSHOTS
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

        Deque<Point> stack = new ArrayDeque<>();

        ArrayList<Point> visitedOrder = new ArrayList<>();

        ArrayList<ArrayList<Point>> stackSnapshots =
            new ArrayList<>();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int exitRow = maze.getExitRow();
        int exitCol = maze.getExitCol();

        // Add starting position
        stack.push(new Point(startRow, startCol));

        visited[startRow][startCol] = true;

        boolean found = false;

        // ==========================================
        // DFS TRAVERSAL
        // ==========================================

        while (!stack.isEmpty()) {

            Point current = stack.pop();

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
                    stackSnapshots.add(copyStack(stack));
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

                    stack.push(
                        new Point(nextRow, nextCol)
                    );
                }
            }

            // Save Stack after processing current cell
            if (captureSnapshots) {

                stackSnapshots.add(copyStack(stack));
            }
        }

        // ==========================================
        // RECONSTRUCT PATH
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
            stackSnapshots
        );
    }

    // ==========================================
    // COPY STACK SNAPSHOT
    // ==========================================

    private static ArrayList<Point> copyStack(
        Deque<Point> stack
    ) {

        ArrayList<Point> snapshot = new ArrayList<>();

        // ArrayDeque iteration starts at the top
        for (Point point : stack) {

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

        private ArrayList<ArrayList<Point>> stackSnapshots;

        public SearchResult(
            boolean found,
            ArrayList<Point> visitedOrder,
            ArrayList<Point> path,
            ArrayList<ArrayList<Point>> stackSnapshots
        ) {

            this.found = found;
            this.visitedOrder = visitedOrder;
            this.path = path;
            this.stackSnapshots = stackSnapshots;
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

        public ArrayList<ArrayList<Point>> getStackSnapshots() {
            return stackSnapshots;
        }
    }
}
