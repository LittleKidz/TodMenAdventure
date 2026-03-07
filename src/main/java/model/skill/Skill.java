package model.skill;

import model.entity.Entity;
import model.interfaces.Applicable;
import model.interfaces.Cooldownable;

/**
 * A character's special skill that can be used once before entering a 3-turn cooldown.
 * Implements Cooldownable for cooldown management and Applicable for the HEAL_SELF effect.
 */
public class Skill implements Cooldownable, Applicable {

    /** Number of turns to wait after using the skill. */
    private static final int COOLDOWN_TURNS = 3;

    /** Skill name shown in the UI. */
    private final String skillName;

    /** Type of skill effect. */
    private final SkillEffect effect;

    /** Turns remaining before the skill can be used again. */
    private int remainingCooldown;

    /**
     * Creates a new Skill.
     *
     * @param skillName Skill name
     * @param effect    Type of skill effect
     */
    public Skill(String skillName, SkillEffect effect) {
        this.skillName = skillName;
        this.effect = effect;
        this.remainingCooldown = 0;
    }

    /**
     * Checks whether the skill is ready to use.
     *
     * @return {@code true} if the cooldown has expired
     */
    @Override
    public boolean isReady() {
        return remainingCooldown == 0;
    }

    /**
     * Decrements the cooldown by 1 after each turn; called automatically by BattleManager.
     */
    @Override
    public void tickCooldown() {
        if (remainingCooldown > 0) remainingCooldown--;
    }

    /**
     * Uses the skill and resets the cooldown. Throws an exception if the skill is not yet ready.
     *
     * @throws IllegalStateException if the skill is not ready
     */
    @Override
    public void use() {
        if (!isReady()) {
            throw new IllegalStateException(
                skillName + " ยังไม่พร้อม เหลืออีก " + remainingCooldown + " turn"
            );
        }
        remainingCooldown = COOLDOWN_TURNS;
    }

    /**
     * Returns the number of turns remaining before the skill can be used.
     *
     * @return Remaining turns (0 = ready)
     */
    @Override
    public int getRemainingCooldown() {
        return remainingCooldown;
    }

    /**
     * Applies the skill's effect; used exclusively for HEAL_SELF.
     * Other effects (MULTI_HIT, MAGIC_DAMAGE, PIERCE) are handled by BattleManager.
     *
     * @param target Entity receiving the effect
     */
    @Override
    public void apply(Entity target) {
        if (effect == SkillEffect.HEAL_SELF) {
            target.heal(30);
        }
    }

    /**
     * Returns a description of the effect for display in the UI.
     *
     * @return Effect description text
     */
    @Override
    public String getEffectDescription() {
        switch (effect) {
            case MULTI_HIT:    return "โจมตี 2 ครั้ง";
            case MAGIC_DAMAGE: return "Fireball magic damage x1.5 ทะลุ defense";
            case PIERCE:       return "โจมตีทะลุ defense ทั้งหมด";
            case HEAL_SELF:    return "ฟื้นฟู HP 30 จุด";
            default:           return "";
        }
    }

    /** @return Skill name */
    public String getSkillName() { return skillName; }

    /** @return Type of skill effect */
    public SkillEffect getEffect() { return effect; }
}
