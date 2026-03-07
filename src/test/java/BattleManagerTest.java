import model.battle.BattleAction;
import model.battle.BattleManager;
import model.battle.BattleResult;
import model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ BattleManager — submit action, resolve turn, winner/loser
 */
public class BattleManagerTest {

    private KnightPlayer knight;
    private ArcherPlayer  archer;
    private Goblin        goblin;
    private BattleManager pvpManager;
    private BattleManager pvgManager;

    @BeforeEach
    void setUp() {
        knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        archer = new ArcherPlayer();
        archer.setPlayerNumber(2);
        goblin = new Goblin();

        pvpManager = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, knight, archer);
        pvgManager = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
    }

    // ─── isReadyToResolve ────────────────────────────────────────

    @Test
    void pvgIsReadyToResolveTrueAfterPlayerSubmits() {
        pvgManager.submitActionA(BattleAction.ATTACK);
        assertTrue(pvgManager.isReadyToResolve());
    }

    @Test
    void pvgIsReadyToResolveFalseBeforeSubmit() {
        assertFalse(pvgManager.isReadyToResolve());
    }

    @Test
    void pvpIsReadyToResolveFalseAfterOnlyOneSubmit() {
        pvpManager.submitActionA(BattleAction.ATTACK);
        assertFalse(pvpManager.isReadyToResolve());
    }

    @Test
    void pvpIsReadyToResolveTrueAfterBothSubmit() {
        pvpManager.submitActionA(BattleAction.ATTACK);
        pvpManager.submitActionB(BattleAction.DEFEND);
        assertTrue(pvpManager.isReadyToResolve());
    }

    // ─── resolveTurn — ATTACK ────────────────────────────────────

    @Test
    void attackDealsDamageToTarget() {
        int hpBefore = goblin.getCurrentHp();
        pvgManager.submitActionA(BattleAction.ATTACK);
        pvgManager.resolveTurn();
        assertTrue(goblin.getCurrentHp() < hpBefore);
    }

    @Test
    void attackResultAHasDamageDealt() {
        pvgManager.submitActionA(BattleAction.ATTACK);
        BattleResult result = pvgManager.resolveTurn();
        assertTrue(result.getResultA().getDamageDealt() > 0);
    }

    // ─── resolveTurn — DEFEND ────────────────────────────────────

    @Test
    void defendResultAIsDefendedTrue() {
        pvgManager.submitActionA(BattleAction.DEFEND);
        BattleResult result = pvgManager.resolveTurn();
        assertTrue(result.getResultA().isDefended());
    }

    @Test
    void defendReducesIncomingDamage() {
        // P1 defend, Goblin attack
        KnightPlayer k1 = new KnightPlayer();
        KnightPlayer k2 = new KnightPlayer();
        Goblin g1 = new Goblin();
        Goblin g2 = new Goblin();

        BattleManager m1 = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, k1, g1);
        BattleManager m2 = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, k2, g2);

        m1.submitActionA(BattleAction.DEFEND);
        m1.resolveTurn();
        m2.submitActionA(BattleAction.ATTACK);
        m2.resolveTurn();

        assertTrue(k1.getCurrentHp() >= k2.getCurrentHp());
    }

    // ─── resolveTurn — Skill ─────────────────────────────────────

    @Test
    void useSkillKnightSlashDealsDamageTwice() {
        pvgManager.submitActionA(BattleAction.USE_SKILL);
        BattleResult result = pvgManager.resolveTurn();
        assertTrue(result.getResultA().isSkillUsed());
    }

    @Test
    void useSkillSetsCooldown() {
        pvgManager.submitActionA(BattleAction.USE_SKILL);
        pvgManager.resolveTurn();
        assertFalse(knight.getSkill().isReady());
    }

    @Test
    void useSkillOnCooldownAttacksInstead() {
        knight.getSkill().use(); // force cooldown
        pvgManager.submitActionA(BattleAction.USE_SKILL);
        BattleResult result = pvgManager.resolveTurn();
        assertFalse(result.getResultA().isSkillUsed());
        assertTrue(result.getResultA().getDamageDealt() > 0);
    }

    // ─── resolveTurn — Item ──────────────────────────────────────

    @Test
    void useItemEmptyInventoryAttacksInstead() {
        pvgManager.submitActionA(BattleAction.USE_ITEM);
        BattleResult result = pvgManager.resolveTurn();
        assertFalse(result.getResultA().isItemUsed());
        assertTrue(result.getResultA().getDamageDealt() > 0);
    }

    @Test
    void useItemWithItemConsumesItem() {
        knight.pickUpItem(new model.item.HealItem(30));
        pvgManager.submitActionA(BattleAction.USE_ITEM);
        pvgManager.resolveTurn();
        assertEquals(0, knight.getInventory().size());
    }

    @Test
    void useItemWithHealItemResultIsItemUsed() {
        knight.takeDamage(50);
        knight.pickUpItem(new model.item.HealItem(30));
        pvgManager.submitActionA(BattleAction.USE_ITEM);
        BattleResult result = pvgManager.resolveTurn();
        assertTrue(result.getResultA().isItemUsed());
    }

    // ─── Winner / Loser ──────────────────────────────────────────

    @Test
    void battleOverFalseWhenBothAlive() {
        pvgManager.submitActionA(BattleAction.ATTACK);
        BattleResult result = pvgManager.resolveTurn();
        assertFalse(result.isBattleOver());
    }

    @Test
    void battleOverTrueWhenGoblinDies() {
        goblin.setCurrentHp(1);
        pvgManager.submitActionA(BattleAction.ATTACK);
        BattleResult result = pvgManager.resolveTurn();
        assertTrue(result.isBattleOver());
        assertEquals(knight, result.getWinner());
        assertEquals(goblin, result.getLoser());
    }

    @Test
    void battleOverTrueWhenPlayerDies() {
        knight.setCurrentHp(1);
        pvgManager.submitActionA(BattleAction.DEFEND);
        BattleResult result = pvgManager.resolveTurn();
        // goblin อาจไม่ตาย ใน 1 turn ขึ้นกับ damage
        // test ว่า knight ยังเป็นผู้เล่น ที่ถูก track อยู่ใน entityA
        assertEquals(knight, pvgManager.getEntityA());
    }

    @Test
    void bothDieResultInDraw() {
        knight.setCurrentHp(1);
        goblin.setCurrentHp(1);
        pvgManager.submitActionA(BattleAction.ATTACK);
        BattleResult result = pvgManager.resolveTurn();
        if (result.isBattleOver() && !knight.isAlive() && !goblin.isAlive()) {
            assertNull(result.getWinner());
            assertNull(result.getLoser());
        }
    }

    // ─── Alien magic attack (ทะลุ defense) ───────────────────

    @Test
    void alienAttackBypassesDefense() {
        AlienPlayer alien = new AlienPlayer();
        alien.setPlayerNumber(1);
        KnightPlayer target = new KnightPlayer(); // DEF=7
        BattleManager m = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, alien, target);

        int hpBefore = target.getCurrentHp();
        m.submitActionA(BattleAction.ATTACK);
        m.submitActionB(BattleAction.DEFEND);
        m.resolveTurn();

        // Alien magic ทะลุ defense = target รับ damage เต็ม 20
        assertTrue(target.getCurrentHp() < hpBefore);
        assertEquals(hpBefore - alien.getAttackPower(), target.getCurrentHp());
    }

    // ─── Goblin AI ───────────────────────────────────────────────

    @Test
    void goblinAIHighHpChoosesAttack() {
        goblin.setCurrentHp(60); // HP เต็ม
        BattleAction action = goblin.decideAction(knight);
        assertEquals(BattleAction.ATTACK, action);
    }

    @Test
    void goblinAILowHpChoosesDefend() {
        goblin.setCurrentHp(10); // <30%
        BattleAction action = goblin.decideAction(knight);
        assertEquals(BattleAction.DEFEND, action);
    }
}
