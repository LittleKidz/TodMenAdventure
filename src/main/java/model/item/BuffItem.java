package model.item;

import model.entity.Entity;

/**
 * Increases the user's attack power. The effect is removed after the battle ends (reset).
 */
public class BuffItem extends Item {

    /** Attack bonus to apply. */
    private final int attackBonus;

    /**
     * create BuffItem
     *
     * @param attackBonus Attack bonus to apply.
     */
    public BuffItem(int attackBonus) {
        super("Attack Elixir");
        this.attackBonus = attackBonus;
    }

    /**
     * Increases the target entity's attack power.
     *
     * @param target Entity to buff
     */
    @Override
    public void apply(Entity target) {
        target.setAttackPower(target.getAttackPower() + attackBonus);
    }

    /**
     * Returns a description of the effect for display in the UI.
     *
     * @return Effect description text
     */
    @Override
    public String getEffectDescription() {
        return "เพิ่ม ATK " + attackBonus + " จุด";
    }

    /**
     * get Attack bonus to apply.
     *
     * @return attackBonus
     */
    public int getAttackBonus() {
        return attackBonus;
    }
}
