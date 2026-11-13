
package Maze;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MazeGenerator {

    private static final Random random = new Random();

    private static final int[][] DIRECTIONS = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // ==========================================
    // GENERATE MAZE
    // ==========================================

    public static Maze generateMaze(int rows, int cols) {

        if (rows < 5 || cols < 5) {

            throw new IllegalArgumentException(
                "Maze dimensions must be at least 5."
            );
        }

        Maze maze = new Maze(rows, cols);

        // Fill the entire maze with walls
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                maze.setCell(row, col, Maze.WALL);
            }
        }

        // ==========================================
        // LOGICAL MAZE DIMENSIONS
        // ==========================================

        int logicalRows = (rows + 1) / 2;
        int logicalCols = (cols + 1) / 2;

        boolean[][] visited =
            new boolean[logicalRows][logicalCols];

        Deque<int[]> stack = new ArrayDeque<>();

        // Starting position
        stack.push(new int[]{0, 0});

        visited[0][0] = true;

        maze.setCell(0, 0, Maze.PATH);

        // ==========================================
        // RANDOMIZED DFS GENERATION
        // ==========================================

        while (!stack.isEmpty()) {

            int[] current = stack.peek();

            int currentRow = current[0];
            int currentCol = current[1];

            ArrayList<int[]> neighbors = new ArrayList<>();

            // Find unvisited neighboring cells
            for (int[] direction : DIRECTIONS) {

                int nextRow = currentRow + direction[0];
                int nextCol = currentCol + direction[1];

                if (nextRow >= 0 &&
                    nextRow < logicalRows &&
                    nextCol >= 0 &&
                    nextCol < logicalCols &&
                    !visited[nextRow][nextCol]) {

                    neighbors.add(
                        new int[]{nextRow, nextCol}
                    );
                }
            }

            if (!neighbors.isEmpty()) {

                // Randomly select an unvisited neighbor
                int randomIndex =
                    random.nextInt(neighbors.size());

                int[] next = neighbors.get(randomIndex);

                int nextRow = next[0];
                int nextCol = next[1];

                // Convert logical coordinates to maze coordinates
                int currentMazeRow = currentRow * 2;
                int currentMazeCol = currentCol * 2;

                int nextMazeRow = nextRow * 2;
                int nextMazeCol = nextCol * 2;

                // Calculate the wall between the two cells
                int wallRow =
                    (currentMazeRow + nextMazeRow) / 2;

                int wallCol =
                    (currentMazeCol + nextMazeCol) / 2;

                // Open the neighboring cell
                maze.setCell(
                    nextMazeRow,
                    nextMazeCol,
                    Maze.PATH
                );

                // Open the wall between the cells
                maze.setCell(
                    wallRow,
                    wallCol,
                    Maze.PATH
                );

                visited[nextRow][nextCol] = true;

                stack.push(next);

            } else {

                // Backtrack when no unvisited neighbors remain
                stack.pop();
            }
        }

        // ==========================================
        // CONNECT EXIT TO THE MAZE
        // ==========================================

        int lastLogicalRow =
            (logicalRows - 1) * 2;

        int lastLogicalCol =
            (logicalCols - 1) * 2;

        // Connect the bottom boundary for even row counts
        if (rows % 2 == 0) {

            maze.setCell(
                rows - 1,
                lastLogicalCol,
                Maze.PATH
            );
        }

        // Connect the right boundary for even column counts
        if (cols % 2 == 0) {

            maze.setCell(
                rows - 1,
                cols - 1,
                Maze.PATH
            );
        }

        // Ensure start and exit are open
        maze.setCell(0, 0, Maze.PATH);

        maze.setCell(
            rows - 1,
            cols - 1,
            Maze.PATH
        );

        return maze;
    }
}
