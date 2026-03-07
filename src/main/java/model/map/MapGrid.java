package model.map;

/**
 * 11x11 map grid storing all Cell objects,
 * with helper methods for validating positions and movement.
 */
public class MapGrid {

    /** Map size (11x11). */
    public static final int SIZE = 11;

    /** 2D array holding all cells. */
    private final Cell[][] cells;

    /**
     * Creates an empty MapGrid where every cell is NORMAL.
     */
    public MapGrid() {
        cells = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                cells[r][c] = new Cell(r, c, CellType.NORMAL);
    }

    /**
     * Returns the Cell at the specified position.
     *
     * @param row Row (0-10)
     * @param col Column (0-10)
     * @return Cell at that position
     */
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    /**
     * Checks whether a position is within the map bounds.
     *
     * @param row Row to check
     * @param col Column to check
     * @return {@code true} if the position is within bounds
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Checks whether a cell is walkable (includes bounds checking).
     *
     * @param row Row
     * @param col Column
     * @return {@code true} if walkable
     */
    public boolean isWalkable(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        return cells[row][col].isWalkable();
    }

    /**
     * Checks whether a move is exactly 1 step in a straight line (no diagonal).
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow   Destination row
     * @param toCol   Destination column
     * @return {@code true} if the move is a single straight-line step
     */
    public boolean isAdjacentMove(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow);
        int dc = Math.abs(toCol - fromCol);
        return dr + dc == 1;
    }

    /**
     * Returns the full cell array (used by MapLoader to build the map).
     *
     * @return The complete Cell[][] of the map
     */
    public Cell[][] getCells() {
        return cells;
    }
}
