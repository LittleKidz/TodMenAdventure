package model.entity;

import model.battle.BattleAction;
import model.interfaces.AIControllable;

/**
 * In-game enemy controlled by AI.
 * Decides to attack or defend based on its remaining HP.
 */
public class Goblin extends Entity implements AIControllable {

    /** Whether the Goblin is currently defending (halves incoming damage). */
    protected boolean isDefending;

    /**
     * Creates a Goblin with default stats.
     * HP 60, ATK 15, DEF 2.
     */
    public Goblin() {
        this.name = "Goblin";
        this.maxHp = 60;
        this.currentHp = 60;
        this.attackPower = 15;
        this.defense = 2;
    }

    /**
     * Calculates the Goblin's attack value using attackPower directly.
     *
     * @return Attack value
     */
    @Override
    public int calculateAttack() {
        return attackPower;
    }

    /**
     * Returns a short description of the Goblin for the UI.
     *
     * @return Description text
     */
    @Override
    public String getDescription() {
        return "ก็อบลินดุร้าย";
    }

    /**
     * Takes damage, halving it first if the Goblin is currently defending.
     *
     * @param dmg Raw damage before reduction
     */
    @Override
    public void takeDamage(int dmg) {
        int actual = isDefending
                ? Math.max(0, dmg / 2 - defense)
                : Math.max(0, dmg - defense);
        currentHp = Math.max(0, currentHp - actual);
    }

    /**
     * AI decides what to do this turn.
     * If HP is below 30%, it chooses to defend; otherwise it always attacks.
     *
     * @param target The opposing player (used as a reference; the Goblin does not inspect the target's stats)
     * @return The action the Goblin will perform
     */
    @Override
    public BattleAction decideAction(Entity target) {
        if (currentHp < maxHp * 0.3) {
            return BattleAction.DEFEND;
        }
        return BattleAction.ATTACK;
    }

    /** @return {@code true} if currently defending */
    public boolean isDefending()         { return isDefending; }

    /**
     * Sets the defending state.
     *
     * @param d {@code true} to start defending
     */
    public void setDefending(boolean d)  { this.isDefending = d; }
}
