package model.entity;

import model.map.Cell;
import model.map.CellType;
import model.skill.Skill;
import model.skill.SkillEffect;

/**
 * Archer — the highest-attack character, but with relatively low HP and defense.
 * Special trait: can move through trees (TREE) but cannot cross rivers.
 * Skill: Strike — attack that fully bypasses defense.
 */
public class ArcherPlayer extends Player {

    /**
     * Creates an Archer with default stats.
     * HP 85, ATK 24, DEF 3, Skill: Strike (PIERCE).
     */
    public ArcherPlayer() {
        super("Archer", 85, 85, 24, 3, new Skill("Strike", SkillEffect.PIERCE));
    }

    /**
     * Calculates the Archer's attack value using attackPower directly.
     *
     * @return Attack value
     */
    @Override
    public int calculateAttack() {
        return attackPower;
    }

    /**
     * Returns a short description of the Archer for the UI.
     *
     * @return Description text
     */
    @Override
    public String getDescription() {
        return "นักธนูคล่องแคล่ว Skill ทะลุ defense ทั้งหมด";
    }

    /**
     * Archer can move through TREE cells but cannot cross ROCK or RIVER cells.
     *
     * @param cell Target cell to move to
     * @return {@code true} if movement is allowed
     */
    @Override
    public boolean canMoveTo(Cell cell) {
        return cell.getType() != CellType.ROCK
                && cell.getType() != CellType.RIVER;
    }
}
