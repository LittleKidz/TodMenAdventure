package model.entity;

import model.map.Cell;
import model.map.CellType;
import model.skill.Skill;
import model.skill.SkillEffect;

/**
 * Reborn — a support character with moderate HP and defense but the lowest attack.
 * Special traits: larger inventory (5 slots) and can walk on water.
 * Skill: Heal — restores 30 HP.
 */
public class RebornPlayer extends Player {

    /**
     * Creates a Reborn with default stats.
     * HP 100, ATK 16, DEF 5, Skill: Heal (HEAL_SELF), 5-slot inventory.
     */
    public RebornPlayer() {
        super("Reborn", 100, 100, 16, 5, new Skill("Heal", SkillEffect.HEAL_SELF));
        this.maxInventorySize = 5;
    }

    /**
     * Calculates the Reborn's attack value using attackPower directly.
     *
     * @return Attack value
     */
    @Override
    public int calculateAttack() {
        return attackPower;
    }

    /**
     * Returns a short description of the Reborn for the UI.
     *
     * @return Description text
     */
    @Override
    public String getDescription() {
        return "นักบวชผู้รักษา Skill ฟื้นฟู HP 30 จุด";
    }

    /**
     * Reborn can walk on RIVER cells but cannot cross ROCK or TREE cells.
     *
     * @param cell Target cell to move to
     * @return {@code true} if movement is allowed
     */
    @Override
    public boolean canMoveTo(Cell cell) {
        return cell.getType() != CellType.ROCK && cell.getType() != CellType.TREE;
    }
}
