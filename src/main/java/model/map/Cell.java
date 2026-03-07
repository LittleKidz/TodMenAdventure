package model.map;

import model.entity.Goblin;
import model.item.Item;

/**
 * Represents a single cell on the 11x11 map, storing its type, item, goblin, and zone state.
 */
public class Cell {

    /** Row of this cell. */
    private final int row;

    /** Column of this cell. */
    private final int col;

    /** Type of this cell. */
    private CellType type;

    /** Item placed on this cell ({@code null} if none). */
    private Item item;

    /** Goblin on this cell ({@code null} if none). */
    private Goblin goblin;

    /** Whether this cell has been converted to lava by the shrinking zone. */
    private boolean inZone;

    /**
     * Creates a new Cell.
     *
     * @param row  Row
     * @param col  Column
     * @param type Initial type
     */
    public Cell(int row, int col, CellType type) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.inZone = false;
    }

    /**
     * Checks whether this cell is walkable by default.
     * ROCK, RIVER, and TREE are not walkable (character-specific exceptions are handled in canMoveTo).
     *
     * @return {@code true} if walkable
     */
    public boolean isWalkable() {
        return type != CellType.ROCK && type != CellType.RIVER && type != CellType.TREE;
    }

    // ─── Getters / Setters ───────────────────────────────────────

    /** @return Row of this cell. */
    public int getRow()                  { return row; }

    /** @return Column of this cell. */
    public int getCol()                  { return col; }

    /** @return Type of this cell. */
    public CellType getType()            { return type; }

    /** @param type New type */
    public void setType(CellType type)   { this.type = type; }

    /** @return Item on this cell, or {@code null} */
    public Item getItem()                { return item; }

    /** @param item Item to place ({@code null} to remove) */
    public void setItem(Item item)       { this.item = item; }

    /** @return Goblin on this cell, or {@code null} */
    public Goblin getGoblin()            { return goblin; }

    /** @param goblin Goblin to place ({@code null} to remove) */
    public void setGoblin(Goblin goblin) { this.goblin = goblin; }

    /** @return {@code true} if this cell is lava */
    public boolean isInZone()            { return inZone; }

    /** @param b {@code true} if this cell has entered the lava zone */
    public void setInZone(boolean b)     { this.inZone = b; }
}
