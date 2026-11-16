
package Algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import Maze.Maze;

public class AStar {

    private static final int[][] DIRECTIONS = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // ==========================================
    // DEFAULT A* - WITH OPEN LIST SNAPSHOTS
    // ==========================================

    public static SearchResult findPath(Maze maze) {
        return findPath(maze, true);
    }

    // ==========================================
    // A* WITH OPTIONAL SNAPSHOTS
    // ==========================================

    public static SearchResult findPath(
        Maze maze,
        boolean captureSnapshots
    ) {

        int rows = maze.getRows();
        int cols = maze.getCols();

        int[][] gScore = new int[rows][cols];

        boolean[][] visited = new boolean[rows][cols];

        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                gScore[row][col] = Integer.MAX_VALUE;

                parentRow[row][col] = -1;
                parentCol[row][col] = -1;
            }
        }

        PriorityQueue<Node> openList =
            new PriorityQueue<>(
                Comparator.comparingInt(node -> node.fScore)
            );

        ArrayList<Point> visitedOrder = new ArrayList<>();

        ArrayList<ArrayList<Point>> openListSnapshots =
            new ArrayList<>();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int exitRow = maze.getExitRow();
        int exitCol = maze.getExitCol();

        gScore[startRow][startCol] = 0;

        openList.add(
            new Node(
                startRow,
                startCol,
                0,
                heuristic(startRow, startCol, exitRow, exitCol)
            )
        );

        boolean found = false;

        // ==========================================
        // A* SEARCH
        // ==========================================

        while (!openList.isEmpty()) {

            Node current = openList.poll();

            int currentRow = current.row;
            int currentCol = current.col;

            // Skip outdated entries
            if (visited[currentRow][currentCol]) {
                continue;
            }

            visited[currentRow][currentCol] = true;

            visitedOrder.add(
                new Point(currentRow, currentCol)
            );

            // Check if exit is reached
            if (currentRow == exitRow &&
                currentCol == exitCol) {

                found = true;

                if (captureSnapshots) {
                    openListSnapshots.add(copyOpenList(openList));
                }

                break;
            }

            // Explore neighboring cells
            for (int[] direction : DIRECTIONS) {

                int nextRow = currentRow + direction[0];
                int nextCol = currentCol + direction[1];

                if (!maze.isPath(nextRow, nextCol) ||
                    visited[nextRow][nextCol]) {

                    continue;
                }

                int tentativeGScore =
                    gScore[currentRow][currentCol] + 1;

                if (tentativeGScore < gScore[nextRow][nextCol]) {

                    gScore[nextRow][nextCol] = tentativeGScore;

                    parentRow[nextRow][nextCol] = currentRow;
                    parentCol[nextRow][nextCol] = currentCol;

                    int hScore = heuristic(
                        nextRow,
                        nextCol,
                        exitRow,
                        exitCol
                    );

                    int fScore = tentativeGScore + hScore;

                    openList.add(
                        new Node(
                            nextRow,
                            nextCol,
                            tentativeGScore,
                            fScore
                        )
                    );
                }
            }

            if (captureSnapshots) {
                openListSnapshots.add(copyOpenList(openList));
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

            Collections.reverse(path);
        }

        return new SearchResult(
            found,
            visitedOrder,
            path,
            openListSnapshots
        );
    }

    // ==========================================
    // MANHATTAN DISTANCE HEURISTIC
    // ==========================================

    private static int heuristic(
        int row,
        int col,
        int exitRow,
        int exitCol
    ) {

        return Math.abs(row - exitRow)
             + Math.abs(col - exitCol);
    }

    // ==========================================
    // COPY PRIORITY QUEUE SNAPSHOT
    // ==========================================

    private static ArrayList<Point> copyOpenList(
        PriorityQueue<Node> openList
    ) {

        ArrayList<Node> sortedNodes =
            new ArrayList<>(openList);

        sortedNodes.sort(
            Comparator.comparingInt(node -> node.fScore)
        );

        ArrayList<Point> snapshot = new ArrayList<>();

        for (Node node : sortedNodes) {

            snapshot.add(
                new Point(node.row, node.col)
            );
        }

        return snapshot;
    }

    // ==========================================
    // NODE CLASS
    // ==========================================

    private static class Node {

        int row;
        int col;

        int gScore;
        int fScore;

        Node(
            int row,
            int col,
            int gScore,
            int fScore
        ) {

            this.row = row;
            this.col = col;

            this.gScore = gScore;
            this.fScore = fScore;
        }
    }

    // ==========================================
    // SEARCH RESULT
    // ==========================================

    public static class SearchResult {

        private boolean found;

        private ArrayList<Point> visitedOrder;

        private ArrayList<Point> path;

        private ArrayList<ArrayList<Point>> openListSnapshots;

        public SearchResult(
            boolean found,
            ArrayList<Point> visitedOrder,
            ArrayList<Point> path,
            ArrayList<ArrayList<Point>> openListSnapshots
        ) {

            this.found = found;
            this.visitedOrder = visitedOrder;
            this.path = path;
            this.openListSnapshots = openListSnapshots;
        }

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

        public ArrayList<ArrayList<Point>> getOpenListSnapshots() {
            return openListSnapshots;
        }
    }
}
