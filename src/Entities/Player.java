
package Entities;

import Maze.Maze;

public class Player {

    private int row;
    private int col;

    public Player(Maze maze) {

        if (maze == null) {
            throw new IllegalArgumentException("Maze cannot be null.");
        }

        row = maze.getStartRow();
        col = maze.getStartCol();
    }

    // POSITION GETTERS

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // MANUAL MOVEMENT

    public boolean moveUp(Maze maze) {
        return move(maze, -1, 0);
    }

    public boolean moveDown(Maze maze) {
        return move(maze, 1, 0);
    }

    public boolean moveLeft(Maze maze) {
        return move(maze, 0, -1);
    }

    public boolean moveRight(Maze maze) {
        return move(maze, 0, 1);
    }

    public boolean move(Maze maze, int rowChange, int colChange) {

        if (maze == null) {
            return false;
        }

        if (Math.abs(rowChange) + Math.abs(colChange) != 1) {
            return false;
        }

        int newRow = row + rowChange;
        int newCol = col + colChange;

        if (!maze.isPath(newRow, newCol)) {
            return false;
        }

        row = newRow;
        col = newCol;

        return true;
    }

    // SOLVER MOVEMENT

    public boolean moveTo(Maze maze, int targetRow, int targetCol) {

        if (maze == null || !maze.isPath(targetRow, targetCol)) {
            return false;
        }

        int rowChange = targetRow - row;
        int colChange = targetCol - col;

        // Allow staying on the same cell
        if (rowChange == 0 && colChange == 0) {
            return true;
        }

        // Only allow movement to an adjacent cell
        return move(maze, rowChange, colChange);
    }

    // EXIT DETECTION

    public boolean hasReachedExit(Maze maze) {

        if (maze == null) {
            return false;
        }

        return row == maze.getExitRow() &&
               col == maze.getExitCol();
    }

    // RESET

    public void reset(Maze maze) {

        if (maze == null) {
            return;
        }

        row = maze.getStartRow();
        col = maze.getStartCol();
    }
}
