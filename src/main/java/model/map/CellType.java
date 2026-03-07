package model.map;

/**
 * Types of cells on the 11x11 map.
 */
public enum CellType {
    /** Normal cell; walkable. */
    NORMAL,
    /** River; not walkable (Reborn can cross). */
    RIVER,
    /** Tree; not walkable (Archer can cross). */
    TREE,
    /** Rock; not walkable by any character. */
    ROCK,
    /** Contains an item; walkable and automatically picked up on entry. */
    ITEM,
    /** Contains a Goblin; entering starts a battle. */
    GOBLIN,
    /** Lava from the shrinking zone; entering deals 10 HP damage per round. */
    LAVA
}
