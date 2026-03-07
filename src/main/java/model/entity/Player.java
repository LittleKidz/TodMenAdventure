package model.entity;

import model.item.Item;
import model.map.Cell;
import model.skill.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all 4 player characters (Knight, Archer, Reborn, Alien).
 * Manages inventory, skills, movement, and the defend system.
 * calculateAttack() is left to subclasses to override because the values differ.
 */
public abstract class Player extends Entity {

    /** Row position on the map. */
    protected int row;

    /** Column position on the map. */
    protected int col;

    /** List of items currently held. */
    protected List<Item> inventory;

    /** Maximum number of items that can be held; default is 4. */
    protected int maxInventorySize = 4;

    /** The character's special skill. */
    protected Skill skill;

    /** Whether the player is currently defending (halves incoming damage this turn). */
    protected boolean isDefending;

    /** Player number (1 or 2). */
    protected int playerNumber;

    /** Index of the selected item in the inventory. */
    protected int selectedItemIndex = 0;

    /**
     * Creates a player with the specified starting stats.
     *
     * @param name        Character name
     * @param maxHp       Maximum HP
     * @param currentHp   Starting HP
     * @param attackPower Starting attack power
     * @param defense     Starting defense
     * @param skill       The character's skill
     */
    public Player(String name, int maxHp, int currentHp, int attackPower, int defense, Skill skill) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.attackPower = attackPower;
        this.baseAttackPower = attackPower;
        this.defense = defense;
        this.baseDefense = defense;
        this.skill = skill;
        this.inventory = new ArrayList<>();
        this.isDefending = false;
    }

    /**
     * Takes damage, halving it first if the player is currently defending.
     * The defending state is always reset to false after damage is applied.
     *
     * @param dmg Raw damage before reduction
     */
    @Override
    public void takeDamage(int dmg) {
        if (isDefending) {
            dmg = dmg / 2;
        }
        super.takeDamage(dmg);
        isDefending = false;
    }

    /**
     * Checks whether this character can move to the given cell.
     * By default, only walkable cells (not ROCK, RIVER, or TREE) are allowed.
     * Subclasses such as Archer or Reborn override this to add exceptions.
     *
     * @param cell Target cell
     * @return {@code true} if movement is allowed
     */
    public boolean canMoveTo(Cell cell) {
        return cell.isWalkable();
    }

    /**
     * Checks whether moving from (fromRow, fromCol) to (toRow, toCol) is valid.
     * By default, only straight-line moves of exactly 1 step are allowed (no diagonal).
     * Alien overrides this to permit diagonal movement.
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow   Destination row
     * @param toCol   Destination column
     * @return {@code true} if the move is valid
     */
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow);
        int dc = Math.abs(toCol - fromCol);
        return dr + dc == 1;
    }

    /**
     * Moves the character to a new position on the map.
     *
     * @param newRow New row
     * @param newCol New column
     */
    public void move(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    /**
     * Picks up an item and adds it to the inventory if there is space.
     *
     * @param item Item to pick up
     * @return {@code true} if picked up successfully, {@code false} if inventory is full
     */
    public boolean pickUpItem(Item item) {
        if (inventory.size() >= maxInventorySize) {
            return false;
        }
        inventory.add(item);
        return true;
    }

    /**
     * Uses the first item in the inventory and removes it.
     *
     * @return {@code true} if successful, {@code false} if the inventory is empty
     */
    public boolean useFirstItem() {
        if (!inventory.isEmpty()) {
            Item item = inventory.get(0);
            item.apply(this);
            inventory.remove(0);
            return true;
        }
        return false;
    }

    // ─── Getters / Setters ───────────────────────────────────────

    /** @return Current row on the map */
    public int getRow()                  { return row; }

    /** @return Current column on the map */
    public int getCol()                  { return col; }

    /** @return All items currently in the inventory */
    public List<Item> getInventory()     { return inventory; }

    /** @return The character's skill */
    public Skill getSkill()              { return skill; }

    /** @return {@code true} if the player is defending this turn */
    public boolean isDefending()         { return isDefending; }

    /**
     * Sets the defending state.
     *
     * @param d {@code true} to start defending
     */
    public void setDefending(boolean d)  { this.isDefending = d; }

    /** @return Player number (1 or 2). */
    public int getPlayerNumber()         { return playerNumber; }

    /**
     * Sets the player number.
     *
     * @param n Player number (1 or 2)
     */
    public void setPlayerNumber(int n)   { this.playerNumber = n; }

    /** @return Maximum number of items that can be held */
    public int getMaxInventorySize()     { return maxInventorySize; }

    /** @return {@code true} if the inventory is full */
    public boolean isInventoryFull()     { return inventory.size() >= maxInventorySize; }

    /** @return Index of the currently selected item */
    public int getSelectedItemIndex()            { return selectedItemIndex; }

    /**
     * Selects an item by its index in the inventory.
     *
     * @param index Index of the item in the inventory
     */
    public void setSelectedItemIndex(int index)  { this.selectedItemIndex = index; }
}
