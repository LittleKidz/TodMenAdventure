import model.battle.BattleAction;
import model.battle.BattleManager;
import model.battle.BattleResult;
import model.entity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Skill effect แต่ละตัวใน battle จริง
 * MULTI_HIT (Knight), PIERCE (Archer), MAGIC_DAMAGE (Alien), HEAL_SELF (Reborn)
 */
public class SkillEffectInBattleTest {

    // ─── Knight — MULTI_HIT (Slash) ──────────────────────────────

    @Test
    void knightSlashSkillUsedFlagTrue() {
        KnightPlayer knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
        bm.submitActionA(BattleAction.USE_SKILL);
        BattleResult result = bm.resolveTurn();
        assertTrue(result.getResultA().isSkillUsed());
    }

    @Test
    void knightSlashDealsDamageToGoblin() {
        KnightPlayer knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
        int hpBefore = goblin.getCurrentHp();
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.resolveTurn();
        assertTrue(goblin.getCurrentHp() < hpBefore);
    }

    @Test
    void knightSlashSetsCooldown() {
        KnightPlayer knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.resolveTurn();
        assertFalse(knight.getSkill().isReady());
    }

    // ─── Archer — PIERCE (Strike) ────────────────────────────────

    @Test
    void archerPierceSkillUsedFlagTrue() {
        ArcherPlayer archer = new ArcherPlayer();
        archer.setPlayerNumber(1);
        KnightPlayer target = new KnightPlayer(); // DEF=7
        target.setPlayerNumber(2);
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, archer, target);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.submitActionB(BattleAction.DEFEND);
        BattleResult result = bm.resolveTurn();
        assertTrue(result.getResultA().isSkillUsed());
    }

    @Test
    void archerPierceBypassesDefense() {
        ArcherPlayer archer = new ArcherPlayer(); // ATK=24
        archer.setPlayerNumber(1);
        KnightPlayer target = new KnightPlayer(); // DEF=7, HP=120
        target.setPlayerNumber(2);
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, archer, target);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.submitActionB(BattleAction.DEFEND);
        bm.resolveTurn();
        // Pierce ทะลุ defense ทั้งหมด = รับ 24 damage เต็ม (แต่ target defend ด้วย = 24/2=12)
        assertTrue(target.getCurrentHp() < 120);
    }

    // ─── Alien — MAGIC_DAMAGE (Fireball) ────────────────────────

    @Test
    void alienFireballSkillUsedFlagTrue() {
        AlienPlayer alien = new AlienPlayer();
        alien.setPlayerNumber(1);
        KnightPlayer target = new KnightPlayer();
        target.setPlayerNumber(2);
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, alien, target);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.submitActionB(BattleAction.DEFEND);
        BattleResult result = bm.resolveTurn();
        assertTrue(result.getResultA().isSkillUsed());
    }

    @Test
    void alienFireballDealsMagicDamage() {
        AlienPlayer alien = new AlienPlayer(); // ATK=20, Fireball=20*1.5=30
        alien.setPlayerNumber(1);
        KnightPlayer target = new KnightPlayer(); // HP=120
        target.setPlayerNumber(2);
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_PLAYER, alien, target);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.submitActionB(BattleAction.DEFEND);
        bm.resolveTurn();
        // Fireball = 20 * 1.5 = 30 magic damage (ทะลุ defense ทั้งหมด แต่ target defend = 30/2=15)
        assertTrue(target.getCurrentHp() < 120);
    }

    // ─── Reborn — HEAL_SELF (Mend) ───────────────────────────────

    @Test
    void rebornHealSkillUsedFlagTrue() {
        RebornPlayer reborn = new RebornPlayer();
        reborn.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, reborn, goblin);
        bm.submitActionA(BattleAction.USE_SKILL);
        BattleResult result = bm.resolveTurn();
        assertTrue(result.getResultA().isSkillUsed());
    }

    @Test
    void rebornHealRestoresHp() {
        RebornPlayer reborn = new RebornPlayer();
        reborn.setPlayerNumber(1);
        reborn.takeDamage(80); // ทำให้ HP ต่ำก่อน
        int hpBefore = reborn.getCurrentHp();
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, reborn, goblin);
        bm.submitActionA(BattleAction.USE_SKILL);
        bm.resolveTurn();
        assertTrue(reborn.getCurrentHp() > hpBefore);
    }

    @Test
    void rebornHealHealingDoneIs30() {
        RebornPlayer reborn = new RebornPlayer();
        reborn.setPlayerNumber(1);
        reborn.takeDamage(80);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, reborn, goblin);
        bm.submitActionA(BattleAction.USE_SKILL);
        BattleResult result = bm.resolveTurn();
        assertEquals(30, result.getResultA().getHealingDone());
    }

    // ─── Skill cooldown tick ─────────────────────────────────────

    @Test
    void skillCooldownTicksEveryTurn() {
        KnightPlayer knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);

        bm.submitActionA(BattleAction.USE_SKILL);
        bm.resolveTurn(); // cooldown = 2

        bm.submitActionA(BattleAction.ATTACK);
        bm.resolveTurn(); // cooldown = 1

        assertEquals(1, knight.getSkill().getRemainingCooldown());
    }

    @Test
    void skillReadyAfterCooldownExpires() {
        KnightPlayer knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        Goblin goblin = new Goblin();
        goblin.setCurrentHp(9999); // ให้ goblin ไม่ตาย
        goblin.setMaxHp(9999);
        BattleManager bm = new BattleManager(BattleManager.BattleMode.PLAYER_VS_GOBLIN, knight, goblin);

        bm.submitActionA(BattleAction.USE_SKILL); bm.resolveTurn(); // CD=2
        bm.submitActionA(BattleAction.ATTACK);    bm.resolveTurn(); // CD=1
        bm.submitActionA(BattleAction.ATTACK);    bm.resolveTurn(); // CD=0

        assertTrue(knight.getSkill().isReady());
    }
}
