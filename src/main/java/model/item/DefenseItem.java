package model.item;

import model.entity.Entity;

/**
 * Increases the user's defense. The effect is removed after the battle ends (reset).
 */
public class DefenseItem extends Item {

    /** Defense bonus to apply. */
    private final int defBonus;

    /**
     * create DefenseItem
     *
     * @param defBonus Defense bonus to apply.
     */
    public DefenseItem(int defBonus) {
        super("Defense Elixir");
        this.defBonus = defBonus;
    }

    /**
     * Increases the target entity's defense.
     *
     * @param target Entity to buff
     */
    @Override
    public void apply(Entity target) {
        target.setDefense(target.getDefense() + defBonus);
    }

    /**
     * Returns a description of the effect for display in the UI.
     *
     * @return Effect description text
     */
    @Override
    public String getEffectDescription() {
        return "เพิ่ม DEF " + defBonus + " จุด";
    }
}
