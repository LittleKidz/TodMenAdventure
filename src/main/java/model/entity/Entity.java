package model.entity;

/**
 * Base class for all entities in the game. Both Player and Goblin extend this class
 * because they share the same core attributes: HP, attack, defense, and basic behaviour.
 * Common logic is centralised here to avoid duplication.
 */
public abstract class Entity {

    /** Character name. */
    protected String name;

    /** Maximum HP. */
    protected int maxHp;

    /** Current HP. */
    protected int currentHp;

    /** Current attack power. */
    protected int attackPower;

    /** Current defense. */
    protected int defense;

    /** Base attack power (used to reset after leaving a battle). */
    protected int baseAttackPower;

    /** Base defense (used to reset after leaving a battle). */
    protected int baseDefense;

    /**
     * Takes damage after subtracting defense. If damage is less than defense, no HP is lost.
     * HP cannot go below 0.
     *
     * @param dmg Raw damage before defense is applied
     */
    public void takeDamage(int dmg) {
        int actual = Math.max(0, dmg - defense);
        currentHp = Math.max(0, currentHp - actual);
    }

    /**
     * Restores HP by the specified amount, capped at maxHp.
     *
     * @param amount Amount of HP to restore
     */
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /**
     * Checks whether the entity is still alive.
     *
     * @return {@code true} if HP is greater than 0
     */
    public boolean isAlive() {
        return currentHp > 0;
    }

    /**
     * Calculates the effective attack value. Each subclass overrides this because the logic differs.
     *
     * @return Attack value to use in combat
     */
    public abstract int calculateAttack();

    /**
     * Returns a short description of the character for display in the UI.
     *
     * @return Character description text
     */
    public abstract String getDescription();

    // ─── Getters / Setters ───────────────────────────────────────

    /** @return Character name. */
    public String getName()             { return name; }

    /** @return Maximum HP. */
    public int getMaxHp()               { return maxHp; }

    /** @return Current HP. */
    public int getCurrentHp()           { return currentHp; }

    /** @return Current attack power. */
    public int getAttackPower()         { return attackPower; }

    /** @return Current defense. */
    public int getDefense()             { return defense; }

    /**
     * Sets HP directly, bypassing takeDamage — used for effects such as magic attacks.
     *
     * @param hp New HP value (minimum 0)
     */
    public void setCurrentHp(int hp)    { this.currentHp = Math.max(0, hp); }

    /**
     * Sets the attack power.
     *
     * @param a New attack power
     */
    public void setAttackPower(int a)   { this.attackPower = a; }

    /**
     * ตั้ง Maximum HP.
     *
     * @param maxHp Maximum HP.ใหม่
     */
    public void setMaxHp(int maxHp)     { this.maxHp = maxHp; }

    /**
     * Sets the defense value.
     *
     * @param defense New defense value
     */
    public void setDefense(int defense) { this.defense = defense; }

    /**
     * Sets the base attack power used when resetting.
     *
     * @param v Base attack power
     */
    public void setBaseAttackPower(int v)   { this.baseAttackPower = v; }

    /**
     * Resets attack power to its base value, used when leaving a battle.
     */
    public void resetAttackPower()    { this.attackPower = baseAttackPower; }

    /**
     * Sets the base defense used when resetting.
     *
     * @param v Base defense
     */
    public void setBaseDefense(int v)   { this.baseDefense = v; }

    /**
     * Resets defense to its base value, used when leaving a battle.
     */
    public void resetDefense()    { this.defense = baseDefense; }
}
