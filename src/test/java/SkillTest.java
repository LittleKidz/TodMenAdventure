import model.entity.KnightPlayer;
import model.entity.Player;
import model.skill.Skill;
import model.skill.SkillEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Skill — cooldown, isReady, use, apply (HEAL_SELF)
 */
public class SkillTest {

    private Skill slashSkill;
    private Skill healSkill;
    private Player player;

    @BeforeEach
    void setUp() {
        slashSkill = new Skill("Slash", SkillEffect.MULTI_HIT);
        healSkill  = new Skill("Heal", SkillEffect.HEAL_SELF);
        player     = new KnightPlayer();
    }

    // ─── isReady ─────────────────────────────────────────────────

    @Test
    void isReadyTrueAtStart() {
        assertTrue(slashSkill.isReady());
    }

    @Test
    void isReadyFalseAfterUse() {
        slashSkill.use();
        assertFalse(slashSkill.isReady());
    }

    // ─── use ─────────────────────────────────────────────────────

    @Test
    void useSetsCooldownTo3() {
        slashSkill.use();
        assertEquals(3, slashSkill.getRemainingCooldown());
    }

    @Test
    void useThrowsWhenNotReady() {
        slashSkill.use();
        assertThrows(IllegalStateException.class, () -> slashSkill.use());
    }

    // ─── tickCooldown ────────────────────────────────────────────

    @Test
    void tickCooldownDecreasesByOne() {
        slashSkill.use();
        slashSkill.tickCooldown();
        assertEquals(2, slashSkill.getRemainingCooldown());
    }

    @Test
    void tickCooldownDoesNotGoBelowZero() {
        slashSkill.tickCooldown(); // ยังไม่ได้ use
        assertEquals(0, slashSkill.getRemainingCooldown());
    }

    @Test
    void isReadyTrueAfter3Ticks() {
        slashSkill.use();
        slashSkill.tickCooldown();
        slashSkill.tickCooldown();
        slashSkill.tickCooldown();
        assertTrue(slashSkill.isReady());
    }

    @Test
    void isReadyFalseAfter2Ticks() {
        slashSkill.use();
        slashSkill.tickCooldown();
        slashSkill.tickCooldown();
        assertFalse(slashSkill.isReady());
    }

    @Test
    void canUseAgainAfterCooldown() {
        slashSkill.use();
        slashSkill.tickCooldown();
        slashSkill.tickCooldown();
        slashSkill.tickCooldown();
        assertDoesNotThrow(() -> slashSkill.use());
    }

    // ─── apply (HEAL_SELF) ────────────────────────────────────────

    @Test
    void applyHealSelfRestores30Hp() {
        player.takeDamage(80);
        int hpBefore = player.getCurrentHp();
        healSkill.apply(player);
        assertEquals(Math.min(player.getMaxHp(), hpBefore + 30), player.getCurrentHp());
    }

    @Test
    void applyNonHealEffectDoesNothing() {
        int hpBefore = player.getCurrentHp();
        slashSkill.apply(player); // MULTI_HIT ไม่ควร heal
        assertEquals(hpBefore, player.getCurrentHp());
    }

    // ─── getters ─────────────────────────────────────────────────

    @Test
    void getSkillNameReturnsCorrectName() {
        assertEquals("Slash", slashSkill.getSkillName());
    }

    @Test
    void getEffectReturnsCorrectEffect() {
        assertEquals(SkillEffect.MULTI_HIT, slashSkill.getEffect());
        assertEquals(SkillEffect.HEAL_SELF, healSkill.getEffect());
    }
}
