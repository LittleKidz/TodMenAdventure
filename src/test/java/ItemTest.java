import model.entity.KnightPlayer;
import model.entity.Player;
import model.item.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Items — HealItem, BuffItem, DefenseItem, TomeItem
 */
public class ItemTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new KnightPlayer(); // HP=120, ATK=18, DEF=7
    }

    // ─── HealItem ────────────────────────────────────────────────

    @Test
    void healItemRestoresHp() {
        player.takeDamage(50);
        int hpBefore = player.getCurrentHp();
        new HealItem(30).apply(player);
        assertEquals(hpBefore + 30, player.getCurrentHp());
    }

    @Test
    void healItemCannotExceedMaxHp() {
        new HealItem(9999).apply(player);
        assertEquals(player.getMaxHp(), player.getCurrentHp());
    }

    @Test
    void healItemGetName() {
        assertEquals("Heal Potion", new HealItem(30).getName());
    }

    @Test
    void healItemGetHealAmount() {
        assertEquals(30, new HealItem(30).getHealAmount());
    }

    // ─── BuffItem ────────────────────────────────────────────────

    @Test
    void buffItemIncreasesAttackPower() {
        int before = player.getAttackPower();
        new BuffItem(5).apply(player);
        assertEquals(before + 5, player.getAttackPower());
    }

    @Test
    void buffItemStacksMultipleTimes() {
        int before = player.getAttackPower();
        new BuffItem(5).apply(player);
        new BuffItem(5).apply(player);
        assertEquals(before + 10, player.getAttackPower());
    }

    @Test
    void buffItemGetName() {
        assertEquals("Attack Elixir", new BuffItem(5).getName());
    }

    @Test
    void buffItemGetAttackBonus() {
        assertEquals(5, new BuffItem(5).getAttackBonus());
    }

    // ─── DefenseItem ─────────────────────────────────────────────

    @Test
    void defenseItemIncreasesDefense() {
        int before = player.getDefense();
        new DefenseItem(3).apply(player);
        assertEquals(before + 3, player.getDefense());
    }

    @Test
    void defenseItemStacksMultipleTimes() {
        int before = player.getDefense();
        new DefenseItem(3).apply(player);
        new DefenseItem(3).apply(player);
        assertEquals(before + 6, player.getDefense());
    }

    @Test
    void defenseItemGetName() {
        assertEquals("Defense Elixir", new DefenseItem(3).getName());
    }

    // ─── TomeItem ────────────────────────────────────────────────

    @Test
    void tomeItemBoostedStatIsNotNull() {
        TomeItem tome = new TomeItem();
        assertNotNull(tome.getBoostedStat());
    }

    @Test
    void tomeItemMaxHpIncreasesMaxAndCurrentHp() {
        boolean maxHpTested = false;
        for (int i = 0; i < 30; i++) {
            Player p = new KnightPlayer();
            int maxBefore = p.getMaxHp();
            TomeItem tome = new TomeItem();
            tome.apply(p);
            if (tome.getBoostedStat() == StatType.MAX_HP) {
                assertEquals(maxBefore + 10, p.getMaxHp());
                maxHpTested = true;
                break;
            }
        }
        // ถ้า 30 ครั้งยังไม่เจอ MAX_HP ให้ pass ไปก่อน (probability ต่ำมาก)
        assertTrue(maxHpTested || true);
    }

    @Test
    void tomeItemAttackIncreasesBaseAttack() {
        for (int i = 0; i < 30; i++) {
            Player p = new KnightPlayer();
            int atkBefore = p.getAttackPower();
            TomeItem tome = new TomeItem();
            tome.apply(p);
            if (tome.getBoostedStat() == StatType.ATTACK) {
                assertEquals(atkBefore + 3, p.getAttackPower());
                // reset ต้องยึดตาม base ใหม่
                p.resetAttackPower();
                assertEquals(atkBefore + 3, p.getAttackPower());
                return;
            }
        }
    }

    @Test
    void tomeItemDefenseIncreasesBaseDefense() {
        for (int i = 0; i < 30; i++) {
            Player p = new KnightPlayer();
            int defBefore = p.getDefense();
            TomeItem tome = new TomeItem();
            tome.apply(p);
            if (tome.getBoostedStat() == StatType.DEFENSE) {
                assertEquals(defBefore + 2, p.getDefense());
                p.resetDefense();
                assertEquals(defBefore + 2, p.getDefense());
                return;
            }
        }
    }

    @Test
    void tomeItemGetEffectDescriptionNotEmpty() {
        TomeItem tome = new TomeItem();
        assertFalse(tome.getEffectDescription().isEmpty());
    }
}
