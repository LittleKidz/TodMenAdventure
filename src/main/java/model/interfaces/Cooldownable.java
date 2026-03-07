package model.interfaces;

/**
 * Interface for skills that have a cooldown system.
 * Enforces a consistent contract for checking, ticking, and using cooldowns.
 */
public interface Cooldownable {

    /**
     * Checks whether the skill is ready to use.
     *
     * @return {@code true} if cooldown is 0
     */
    boolean isReady();

    /**
     * Decrements the cooldown by 1; called at the end of every turn.
     */
    void tickCooldown();

    /**
     * Uses the skill and resets the cooldown.
     *
     * @throws IllegalStateException if the skill is not yet ready
     */
    void use();

    /**
     * Returns the number of turns remaining before the skill can be used again.
     *
     * @return Remaining turns
     */
    int getRemainingCooldown();
}
