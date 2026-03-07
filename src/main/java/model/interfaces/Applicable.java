package model.interfaces;

import model.entity.Entity;

/**
 * Interface for objects that can apply an effect to an entity.
 * Both Item and Skill (HEAL_SELF) implement this interface,
 * allowing BattleManager to use them without knowing the concrete class.
 */
public interface Applicable {

    /**
     * Applies the effect to the target entity.
     *
     * @param target Entity to receive the effect
     */
    void apply(Entity target);

    /**
     * Returns a description of the effect for display in the UI.
     *
     * @return Effect description text
     */
    String getEffectDescription();
}
