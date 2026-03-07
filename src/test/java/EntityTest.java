import model.entity.KnightPlayer;
import model.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Entity — takeDamage, heal, isAlive, reset stats
 */
public class EntityTest {

    private Player entity;

    @BeforeEach
    void setUp() {
        entity = new KnightPlayer(); // HP=120, ATK=18, DEF=7
    }

    // ─── takeDamage ─────────────────────────────────────────────

    @Test
    void takeDamageReducesCurrentHp() {
        entity.takeDamage(20); // actual = 20 - 7 = 13
        assertEquals(107, entity.getCurrentHp());
    }

    @Test
    void takeDamageDefenseReducesDamage() {
        int before = entity.getCurrentHp();
        entity.takeDamage(10); // 10 - 7 def = 3 actual
        assertEquals(before - 3, entity.getCurrentHp());
    }

    @Test
    void takeDamageDamageCannotGoBelowZero() {
        entity.takeDamage(9999);
        assertEquals(0, entity.getCurrentHp());
    }

    @Test
    void takeDamageDamageLesThanDefenseNoDamage() {
        entity.takeDamage(5); // 5 - 7 def = 0 actual (ไม่รับ damage)
        assertEquals(entity.getMaxHp(), entity.getCurrentHp());
    }

    // ─── heal ────────────────────────────────────────────────────

    @Test
    void healRestoresCurrentHp() {
        entity.takeDamage(30); // HP = 120 - 23 = 97
        int hpAfterDamage = entity.getCurrentHp();
        entity.heal(10);
        assertEquals(hpAfterDamage + 10, entity.getCurrentHp());
    }

    @Test
    void healCannotExceedMaxHp() {
        entity.heal(9999);
        assertEquals(entity.getMaxHp(), entity.getCurrentHp());
    }

    @Test
    void healFromZeroHp() {
        entity.takeDamage(9999);
        entity.heal(50);
        assertEquals(50, entity.getCurrentHp());
    }

    // ─── isAlive ─────────────────────────────────────────────────

    @Test
    void isAliveTrueWhenHpAboveZero() {
        assertTrue(entity.isAlive());
    }

    @Test
    void isAliveFalseWhenHpIsZero() {
        entity.takeDamage(9999);
        assertFalse(entity.isAlive());
    }

    // ─── resetAttackPower / resetDefense ─────────────────────────

    @Test
    void resetAttackPowerRestoresOriginalValue() {
        int base = entity.getAttackPower();
        entity.setAttackPower(base + 10);
        entity.resetAttackPower();
        assertEquals(base, entity.getAttackPower());
    }

    @Test
    void resetDefenseRestoresOriginalValue() {
        int base = entity.getDefense();
        entity.setDefense(base + 5);
        entity.resetDefense();
        assertEquals(base, entity.getDefense());
    }

    @Test
    void setBaseAttackPowerChangesResetTarget() {
        entity.setAttackPower(100);
        entity.setBaseAttackPower(100);
        entity.setAttackPower(999);
        entity.resetAttackPower();
        assertEquals(100, entity.getAttackPower());
    }

    @Test
    void setBaseDefenseChangesResetTarget() {
        entity.setDefense(50);
        entity.setBaseDefense(50);
        entity.setDefense(999);
        entity.resetDefense();
        assertEquals(50, entity.getDefense());
    }
}
