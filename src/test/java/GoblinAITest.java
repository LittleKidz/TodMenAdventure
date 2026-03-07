import model.battle.BattleAction;
import model.battle.BattleManager;
import model.battle.BattleResult;
import model.entity.Goblin;
import model.entity.KnightPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Goblin  — decideAction และ behavior
 */
public class GoblinAITest {

    private Goblin goblin;
    private KnightPlayer knight;

    @BeforeEach
    void setUp() {
        goblin = new Goblin();
        knight = new KnightPlayer();
        knight.setPlayerNumber(1);
    }

    // ─── decideAction ────────────────────────────────────────────

    @Test
    void decideActionFullHpReturnsAttack() {
        goblin.setCurrentHp(60); // HP เต็ม
        assertEquals(BattleAction.ATTACK, goblin.decideAction(knight));
    }

    @Test
    void decideActionAbove30percentReturnsAttack() {
        goblin.setCurrentHp(20); // 20/60 = 33% > 30%
        assertEquals(BattleAction.ATTACK, goblin.decideAction(knight));
    }

    @Test
    void decideActionBelow30percentReturnsDefend() {
        goblin.setCurrentHp(17); // 17/60 = 28% < 30%
        assertEquals(BattleAction.DEFEND, goblin.decideAction(knight));
    }

    @Test
    void decideActionExactly30percentReturnsAttack() {
        goblin.setCurrentHp(18); // 18/60 = 30% พอดี (ไม่ < 30%)
        assertEquals(BattleAction.ATTACK, goblin.decideAction(knight));
    }

    @Test
    void decideActionHpIs1ReturnsDefend() {
        goblin.setCurrentHp(1);
        assertEquals(BattleAction.DEFEND, goblin.decideAction(knight));
    }

    // ─── Goblin defend ใน battle ─────────────────────────────────

    @Test
    void goblinDefendsWhenLowHpInBattle() {
        goblin.setCurrentHp(10); // < 30%
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
        bm.submitActionA(BattleAction.ATTACK);
        BattleResult result = bm.resolveTurn();
        // Goblin ควรเลือก DEFEND
        assertTrue(result.getResultB().isDefended());
    }

    @Test
    void goblinAttacksWhenHighHpInBattle() {
        goblin.setCurrentHp(60); // HP เต็ม
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
        bm.submitActionA(BattleAction.DEFEND);
        BattleResult result = bm.resolveTurn();
        // Goblin ควรเลือก ATTACK
        assertTrue(result.getResultB().getDamageDealt() > 0);
    }

    // ─── Goblin takeDamage ───────────────────────────────────────

    @Test
    void goblinTakeDamageNormalDamageApplied() {
        goblin.takeDamage(10); // 10 - 2 def = 8
        assertEquals(52, goblin.getCurrentHp());
    }

    @Test
    void goblinTakeDamageDefenseReduces() {
        goblin.takeDamage(3); // 3 - 2 def = 1
        assertEquals(59, goblin.getCurrentHp());
    }

    @Test
    void goblinTakeDamageCannotGoBelowZero() {
        goblin.takeDamage(9999);
        assertEquals(0, goblin.getCurrentHp());
    }

    @Test
    void goblinDefendingHalfDamage() {
        goblin.setDefending(true);
        goblin.takeDamage(20); // 20/2=10, 10-2def=8
        assertEquals(52, goblin.getCurrentHp());
    }

    @Test
    void goblinNotDefendingFullDamage() {
        goblin.takeDamage(20); // 20-2def=18
        assertEquals(42, goblin.getCurrentHp());
    }
}
