
package Maze;

public class Maze {

    // ==========================================
    // CELL TYPES
    // ==========================================

    public static final int PATH = 0;
    public static final int WALL = 1;

    // ==========================================
    // MAZE VARIABLES
    // ==========================================

    private final int rows;
    private final int cols;

    private final int[][] grid;

    private final int startRow;
    private final int startCol;

    private final int exitRow;
    private final int exitCol;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public Maze(int rows, int cols) {

        if (rows <= 0 || cols <= 0) {

            throw new IllegalArgumentException(
                "Maze dimensions must be greater than zero."
            );
        }

        this.rows = rows;
        this.cols = cols;

        grid = new int[rows][cols];

        startRow = 0;
        startCol = 0;

        exitRow = rows - 1;
        exitCol = cols - 1;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }

    // ==========================================
    // CELL ACCESS
    // ==========================================

    public int getCell(int row, int col) {

        return grid[row][col];
    }

    public void setCell(int row, int col, int value) {

        if (value != PATH && value != WALL) {

            throw new IllegalArgumentException(
                "Invalid cell value. Use PATH or WALL."
            );
        }

        grid[row][col] = value;
    }

    // ==========================================
    // PATH CHECK
    // ==========================================

    public boolean isPath(int row, int col) {

        if (row < 0 || row >= rows ||
            col < 0 || col >= cols) {

            return false;
        }

        return grid[row][col] == PATH;
    }
}
