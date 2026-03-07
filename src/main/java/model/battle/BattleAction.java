package model.battle;

/**
 * Actions available to players during the battle phase.
 */
public enum BattleAction {

    /** Normal attack. Damage equals calculateAttack() minus the target's defense. */
    ATTACK,

    /** Defend this turn. Incoming damage is halved. */
    DEFEND,

    /** Use the selected item from inventory. Falls back to a normal attack if inventory is empty. */
    USE_ITEM,

    /** Use the character's special skill. Falls back to a normal attack if the skill is on cooldown. */
    USE_SKILL
}
