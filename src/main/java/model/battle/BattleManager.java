package model.battle;

import model.entity.Entity;
import model.entity.AlienPlayer;
import model.entity.Goblin;
import model.entity.Player;
import model.interfaces.AIControllable;
import model.interfaces.Applicable;
import model.item.Item;
import model.skill.Skill;

/**
 * Controls a turn-based battle between 2 entities.
 * Supports 2 modes: player vs. Goblin (AI) and player vs. player (PvP).
 * Both sides act simultaneously within a single turn.
 */
public class BattleManager {

    /**
     * Battle modes.
     */
    public enum BattleMode {
        /** Player fights a Goblin AI. */
        PLAYER_VS_GOBLIN,
        /** Players fight each other. */
        PLAYER_VS_PLAYER
    }

    /** The battle mode in use. */
    private final BattleMode mode;

    /** Entity on side A (Player 1 or the first to enter the battle). */
    private final Entity entityA;

    /** Entity on side B (Player 2 or the Goblin). */
    private final Entity entityB;

    /** Pending action chosen by side A, awaiting resolution. */
    private BattleAction pendingActionA;

    /** Pending action chosen by side B, awaiting resolution. */
    private BattleAction pendingActionB;

    /** Whether side A has submitted an action. */
    private boolean actionASubmitted;

    /** Whether side B has submitted an action. */
    private boolean actionBSubmitted;

    /**
     * Creates a BattleManager for a new battle.
     *
     * @param mode    Battle mode
     * @param entityA Entity on side A
     * @param entityB Entity on side B
     */
    public BattleManager(BattleMode mode, Entity entityA, Entity entityB) {
        this.mode = mode;
        this.entityA = entityA;
        this.entityB = entityB;
        resetFlags();
    }

    /**
     * Side A submits an action for this turn.
     *
     * @param action The chosen action
     */
    public void submitActionA(BattleAction action) {
        this.pendingActionA = action;
        this.actionASubmitted = true;
    }

    /**
     * Side B submits an action for this turn (PvP mode only).
     *
     * @param action The chosen action
     */
    public void submitActionB(BattleAction action) {
        this.pendingActionB = action;
        this.actionBSubmitted = true;
    }

    /**
     * Checks whether the turn is ready to be resolved.
     * In PvP both sides must submit; against a Goblin only side A's submission is required.
     *
     * @return {@code true} if the turn is ready to resolve
     */
    public boolean isReadyToResolve() {
        if (mode == BattleMode.PLAYER_VS_GOBLIN) return actionASubmitted;
        return actionASubmitted && actionBSubmitted;
    }

    /**
     * Resolves the turn — both sides act simultaneously.
     * The Goblin AI decides its action at this point. After resolution, all skills tick their cooldown.
     *
     * @return The result of this turn
     */
    public BattleResult resolveTurn() {
        if (mode == BattleMode.PLAYER_VS_GOBLIN) {
            AIControllable ai = (AIControllable) entityB;
            pendingActionB = ai.decideAction(entityA);
        }

        if (pendingActionA == BattleAction.DEFEND && entityA instanceof Player)
            ((Player) entityA).setDefending(true);
        if (pendingActionB == BattleAction.DEFEND && entityB instanceof Player)
            ((Player) entityB).setDefending(true);
        if (pendingActionB == BattleAction.DEFEND && entityB instanceof Goblin)
            ((Goblin) entityB).setDefending(true);

        ActionResult resA = processAction(entityA, pendingActionA, entityB);
        ActionResult resB = processAction(entityB, pendingActionB, entityA);

        if (entityA instanceof Player) ((Player) entityA).getSkill().tickCooldown();
        if (entityB instanceof Player) ((Player) entityB).getSkill().tickCooldown();

        BattleResult result = getBattleResult(resA, resB);
        resetFlags();
        return result;
    }

    /**
     * Evaluates the outcomes of both sides' actions and determines the winner/loser.
     *
     * @param resA ActionResult for side A
     * @param resB ActionResult for side B
     * @return A BattleResult summarising this turn
     */
    private BattleResult getBattleResult(ActionResult resA, ActionResult resB) {
        BattleResult result = new BattleResult();
        result.setResultA(resA);
        result.setResultB(resB);

        if (!entityA.isAlive() || !entityB.isAlive()) {
            result.setBattleOver(true);
            if (!entityA.isAlive() && !entityB.isAlive()) {
                result.setWinner(null);
                result.setLoser(null);
            } else if (entityA.isAlive()) {
                result.setWinner(entityA);
                result.setLoser(entityB);
            } else {
                result.setWinner(entityB);
                result.setLoser(entityA);
            }
        }
        return result;
    }

    /**
     * Processes one entity's action.
     * Alien attacks use magic damage that bypasses defense; all other entities use takeDamage normally.
     *
     * @param actor  Entity performing the action
     * @param action The chosen action
     * @param target The target entity
     * @return ActionResult describing the outcome of this action
     */
    private ActionResult processAction(Entity actor, BattleAction action, Entity target) {
        ActionResult res = new ActionResult(actor.getName(), action);

        switch (action) {
            case ATTACK: {
                if (actor instanceof AlienPlayer) {
                    int dmg = actor.calculateAttack();
                    target.setCurrentHp(Math.max(0, target.getCurrentHp() - dmg));
                    res.setDamageDealt(dmg);
                    res.setDescription(actor.getName() + " ใช้ magic โจมตี " + dmg + " damage (ทะลุ defense)");
                } else {
                    int dmg = actor.calculateAttack();
                    target.takeDamage(dmg);
                    res.setDamageDealt(dmg);
                    res.setDescription(actor.getName() + " โจมตี " + dmg + " damage");
                }
                break;
            }
            case DEFEND: {
                res.setDefended(true);
                res.setDescription(actor.getName() + " ป้องกัน (ลด damage ครึ่งหนึ่ง turn นี้)");
                break;
            }
            case USE_ITEM: {
                if (actor instanceof Player) {
                    Player p = (Player) actor;
                    if (!p.getInventory().isEmpty()) {
                        int idx = Math.min(p.getSelectedItemIndex(), p.getInventory().size() - 1);
                        Item item = p.getInventory().get(idx);
                        item.apply(actor);
                        p.getInventory().remove(idx);
                        p.setSelectedItemIndex(0);
                        res.setItemUsed(true);
                        res.setDescription(actor.getName() + " ใช้ " + item.getName()
                            + " — " + item.getEffectDescription());
                    } else {
                        int dmg = actor.calculateAttack();
                        target.takeDamage(dmg);
                        res.setDamageDealt(dmg);
                        res.setDescription(actor.getName() + " ไม่มี item! โจมตีธรรมดาแทน " + dmg + " damage");
                    }
                }
                break;
            }
            case USE_SKILL: {
                if (actor instanceof Player) {
                    Player p = (Player) actor;
                    Skill skill = p.getSkill();
                    if (skill.isReady()) {
                        applySkillEffect(p, skill, target, res);
                        skill.use();
                        res.setSkillUsed(true);
                    } else {
                        int dmg = actor.calculateAttack();
                        target.takeDamage(dmg);
                        res.setDamageDealt(dmg);
                        res.setDescription(actor.getName() + " skill ยัง cooldown อีก "
                            + skill.getRemainingCooldown() + " turn! โจมตีธรรมดาแทน " + dmg);
                    }
                }
                break;
            }
        }
        return res;
    }

    /**
     * Applies the skill's effect according to its SkillEffect type.
     *
     * @param actor  Player using the skill
     * @param skill  Skill to apply
     * @param target Target entity
     * @param res    ActionResult to update
     */
    private void applySkillEffect(Player actor, Skill skill, Entity target, ActionResult res) {
        switch (skill.getEffect()) {
            case MULTI_HIT: {
                int dmg1 = actor.calculateAttack();
                int dmg2 = actor.calculateAttack();
                target.takeDamage(dmg1);
                target.takeDamage(dmg2);
                int total = dmg1 + dmg2;
                res.setDamageDealt(total);
                res.setDescription(actor.getName() + " ใช้ Slash โจมตี 2 ครั้ง รวม " + total + " damage");
                break;
            }
            case MAGIC_DAMAGE: {
                int dmg = (int)(actor.calculateAttack() * 1.5);
                target.setCurrentHp(Math.max(0, target.getCurrentHp() - dmg));
                res.setDamageDealt(dmg);
                res.setDescription(actor.getName() + " ใช้ Fireball! " + dmg + " magic damage");
                break;
            }
            case PIERCE: {
                int dmg = actor.calculateAttack();
                target.setCurrentHp(Math.max(0, target.getCurrentHp() - dmg));
                res.setDamageDealt(dmg);
                res.setDescription(actor.getName() + " ใช้ Volley ทะลุ defense! " + dmg + " damage");
                break;
            }
            case HEAL_SELF: {
                Applicable applicable = skill;
                applicable.apply(actor);
                res.setHealingDone(30);
                res.setDescription(actor.getName() + " ใช้ Mend! ฟื้นฟู 30 HP");
                break;
            }
        }
    }

    /** Resets submission flags for both sides after the turn ends. */
    private void resetFlags() {
        actionASubmitted = false;
        actionBSubmitted = false;
        pendingActionA = null;
        pendingActionB = null;
    }

    // ─── Getters ─────────────────────────────────────────────────

    /** @return The battle mode */
    public BattleMode getMode()                  { return mode; }

    /** @return Entity on side A */
    public Entity getEntityA()                   { return entityA; }

    /** @return Entity on side B */
    public Entity getEntityB()                   { return entityB; }

    /** @return {@code true} if side A has submitted an action */
    public boolean isActionASubmitted()          { return actionASubmitted; }

    /** @return {@code true} if side B has submitted an action */
    public boolean isActionBSubmitted()          { return actionBSubmitted; }
}
