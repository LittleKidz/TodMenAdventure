package model.item;

import model.entity.Entity;

/**
 * Restores HP to the user by a specified amount, capped at maxHp.
 */
public class HealItem extends Item {

    /** Amount of HP to restore. */
    private final int healAmount;

    /**
     * create HealItem
     *
     * @param healAmount Amount of HP to restore.
     */
    public HealItem(int healAmount) {
        super("Heal Potion");
        this.healAmount = healAmount;
    }

    /**
     * Restores HP to the target entity by healAmount.
     *
     * @param target Entity to heal
     */
    @Override
    public void apply(Entity target) {
        target.heal(healAmount);
    }

    /**
     * Returns a description of the effect for display in the UI.
     *
     * @return Effect description text
     */
    @Override
    public String getEffectDescription() {
        return "ฟื้นฟู HP " + healAmount;
    }

    /**
     * Returns the amount of HP this item restores.
     *
     * @return Heal amount
     */
    public int getHealAmount() {
        return healAmount;
    }
}
