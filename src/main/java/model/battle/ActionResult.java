package model.battle;

/**
 * Stores the result of one entity's action in a single turn.
 * A BattleResult contains two ActionResults — one for entityA and one for entityB.
 */
public class ActionResult {

    /** Name of the entity that performed this action. */
    private final String actorName;

    /** The chosen action. */
    private final BattleAction action;

    /** Total raw damage dealt to the target (before defense is applied). */
    private int damageDealt;

    /** HP restored during this turn. */
    private int healingDone;

    /** Whether a skill was used successfully. */
    private boolean skillUsed;

    /** Whether an item was used successfully. */
    private boolean itemUsed;

    /** Whether the entity defended this turn. */
    private boolean defended;

    /** Text describing what happened this turn, shown in the UI. */
    private String description;

    /**
     * create new ActionResult
     *
     * @param actorName ชื่อของ entity ที่ทำ action
     * @param action    The chosen action.
     */
    public ActionResult(String actorName, BattleAction action) {
        this.actorName = actorName;
        this.action = action;
    }

    // ─── Getters / Setters ───────────────────────────────────────

    /** @return Name of the entity that performed the action */
    public String getActorName()         { return actorName; }

    /** @return The chosen action. */
    public BattleAction getAction()      { return action; }

    /** @return Damage dealt */
    public int getDamageDealt()          { return damageDealt; }

    /** @param d Damage dealt */
    public void setDamageDealt(int d)    { this.damageDealt = d; }

    /** @return HP restored */
    public int getHealingDone()          { return healingDone; }

    /** @param h HP restored */
    public void setHealingDone(int h)    { this.healingDone = h; }

    /** @return {@code true} if a skill was used successfully */
    public boolean isSkillUsed()         { return skillUsed; }

    /** @param s {@code true} if a skill was used successfully */
    public void setSkillUsed(boolean s)  { this.skillUsed = s; }

    /** @return {@code true} if an item was used successfully */
    public boolean isItemUsed()          { return itemUsed; }

    /** @param i {@code true} if an item was used successfully */
    public void setItemUsed(boolean i)   { this.itemUsed = i; }

    /** @return {@code true} if the entity defended this turn */
    public boolean isDefended()          { return defended; }

    /** @param d {@code true} to indicate the entity defended */
    public void setDefended(boolean d)   { this.defended = d; }

    /** @return Description of what happened */
    public String getDescription()       { return description; }

    /** @param d Description text */
    public void setDescription(String d) { this.description = d; }
}
