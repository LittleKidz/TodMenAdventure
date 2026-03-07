package model.skill;

/**
 * Skill effect types available to each character.
 */
public enum SkillEffect {

    /** Knight Slash — attacks twice per turn. */
    MULTI_HIT,

    /** Alien Fireball — magic damage x1.5, fully bypasses defense. */
    MAGIC_DAMAGE,

    /** Archer Strike — attack that fully bypasses defense. */
    PIERCE,

    /** Reborn Heal — restores 30 HP. */
    HEAL_SELF
}
