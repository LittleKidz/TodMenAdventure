package model.entity;

import model.skill.Skill;
import model.skill.SkillEffect;

/**
 * Knight — the most durable character with high HP and defense.
 * Skill: Slash — attacks twice per turn.
 */
public class KnightPlayer extends Player {

    /**
     * Creates a Knight with default stats.
     * HP 120, ATK 18, DEF 7, Skill: Slash (MULTI_HIT).
     */
    public KnightPlayer() {
        super("Knight", 120, 120, 18, 7, new Skill("Slash", SkillEffect.MULTI_HIT));
    }

    /**
     * Calculates the Knight's attack value using attackPower directly.
     *
     * @return Attack value
     */
    @Override
    public int calculateAttack() {
        return attackPower;
    }

    /**
     * Returns a short description of the Knight for the UI.
     *
     * @return Description text
     */
    @Override
    public String getDescription() {
        return "Knight HP สูง ป้องกันดี โจมตีแรงขึ้นเมื่อ HP ต่ำ";
    }
}
