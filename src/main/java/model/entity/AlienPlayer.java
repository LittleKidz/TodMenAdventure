package model.entity;

import model.skill.Skill;
import model.skill.SkillEffect;

/**
 * Alien — a magic-oriented character with very low HP and defense, but attacks bypass defense.
 * Special trait: can move diagonally, unlike other characters who can only move in straight lines.
 * Skill: Fireball — magic damage x1.5, fully bypasses defense.
 */
public class AlienPlayer extends Player {

    /**
     * Creates an Alien with default stats.
     * HP 70, ATK 20, DEF 2, Skill: Fireball (MAGIC_DAMAGE).
     */
    public AlienPlayer() {
        super("Alien", 70, 70, 20, 2, new Skill("Fireball", SkillEffect.MAGIC_DAMAGE));
    }

    /**
     * Calculates the Alien's attack value using attackPower directly.
     * (Defense bypass is handled by BattleManager during action processing.)
     *
     * @return Attack value
     */
    @Override
    public int calculateAttack() {
        return attackPower;
    }

    /**
     * Returns a short description of the Alien for the UI.
     *
     * @return Description text
     */
    @Override
    public String getDescription() {
        return "Alien โจมตี magic ทะลุ defense แต่บอบบางมาก";
    }

    /**
     * Alien can also move diagonally, unlike the default which only allows straight-line moves.
     * The rule is at most 1 step in any direction, and staying in place is not allowed.
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow   Destination row
     * @param toCol   Destination column
     * @return {@code true} if this move is valid
     */
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow);
        int dc = Math.abs(toCol - fromCol);
        return dr <= 1 && dc <= 1 && !(dr == 0 && dc == 0);
    }
}
