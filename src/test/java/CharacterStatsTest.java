import model.entity.*;
import model.map.Cell;
import model.map.CellType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ stats และความสามารถพิเศษของตัวละครแต่ละตัว
 * Knight, Archer, Reborn, Alien, Goblin
 */
public class CharacterStatsTest {

    // ─── Knight ──────────────────────────────────────────────────

    @Test
    void knightStatsCorrect() {
        KnightPlayer k = new KnightPlayer();
        assertEquals("Knight", k.getName());
        assertEquals(120, k.getMaxHp());
        assertEquals(18, k.getAttackPower());
        assertEquals(7, k.getDefense());
    }

    @Test
    void knightCalculateAttackReturnsAttackPower() {
        KnightPlayer k = new KnightPlayer();
        assertEquals(18, k.calculateAttack());
    }

    @Test
    void knightCannotMoveDiagonally() {
        KnightPlayer k = new KnightPlayer();
        assertFalse(k.isValidMove(5, 5, 6, 6));
        assertFalse(k.isValidMove(5, 5, 4, 4));
    }

    @Test
    void knightCannotMoveToRiver() {
        KnightPlayer k = new KnightPlayer();
        assertFalse(k.canMoveTo(new Cell(0, 0, CellType.RIVER)));
    }

    @Test
    void knightCannotMoveToRock() {
        KnightPlayer k = new KnightPlayer();
        assertFalse(k.canMoveTo(new Cell(0, 0, CellType.ROCK)));
    }

    @Test
    void knightCannotMoveToTree() {
        KnightPlayer k = new KnightPlayer();
        assertFalse(k.canMoveTo(new Cell(0, 0, CellType.TREE)));
    }

    @Test
    void knightCanMoveToNormal() {
        KnightPlayer k = new KnightPlayer();
        assertTrue(k.canMoveTo(new Cell(0, 0, CellType.NORMAL)));
    }

    // ─── Archer ──────────────────────────────────────────────────

    @Test
    void archerStatsCorrect() {
        ArcherPlayer a = new ArcherPlayer();
        assertEquals("Archer", a.getName());
        assertEquals(85, a.getMaxHp());
        assertEquals(24, a.getAttackPower());
        assertEquals(3, a.getDefense());
    }

    @Test
    void archerCanMoveToTree() {
        ArcherPlayer a = new ArcherPlayer();
        assertTrue(a.canMoveTo(new Cell(0, 0, CellType.TREE)));
    }

    @Test
    void archerCannotMoveToRock() {
        ArcherPlayer a = new ArcherPlayer();
        assertFalse(a.canMoveTo(new Cell(0, 0, CellType.ROCK)));
    }

    @Test
    void archerCannotMoveToRiver() {
        ArcherPlayer a = new ArcherPlayer();
        assertFalse(a.canMoveTo(new Cell(0, 0, CellType.RIVER)));
    }

    // ─── Reborn ──────────────────────────────────────────────────

    @Test
    void rebornStatsCorrect() {
        RebornPlayer r = new RebornPlayer();
        assertEquals("Reborn", r.getName());
        assertEquals(100, r.getMaxHp());
        assertEquals(16, r.getAttackPower());
        assertEquals(5, r.getDefense());
    }

    @Test
    void rebornInventorySizeIsFive() {
        RebornPlayer r = new RebornPlayer();
        assertEquals(5, r.getMaxInventorySize());
    }

    @Test
    void rebornCanMoveToRiver() {
        RebornPlayer r = new RebornPlayer();
        assertTrue(r.canMoveTo(new Cell(0, 0, CellType.RIVER)));
    }

    @Test
    void rebornCannotMoveToTree() {
        RebornPlayer r = new RebornPlayer();
        assertFalse(r.canMoveTo(new Cell(0, 0, CellType.TREE)));
    }

    @Test
    void rebornCannotMoveToRock() {
        RebornPlayer r = new RebornPlayer();
        assertFalse(r.canMoveTo(new Cell(0, 0, CellType.ROCK)));
    }

    // ─── Alien ───────────────────────────────────────────────────

    @Test
    void alienStatsCorrect() {
        AlienPlayer al = new AlienPlayer();
        assertEquals("Alien", al.getName());
        assertEquals(70, al.getMaxHp());
        assertEquals(20, al.getAttackPower());
        assertEquals(2, al.getDefense());
    }

    @Test
    void alienCanMoveDiagonally() {
        AlienPlayer al = new AlienPlayer();
        assertTrue(al.isValidMove(5, 5, 6, 6));
        assertTrue(al.isValidMove(5, 5, 4, 4));
        assertTrue(al.isValidMove(5, 5, 6, 4));
        assertTrue(al.isValidMove(5, 5, 4, 6));
    }

    @Test
    void alienCanMoveOrthogonally() {
        AlienPlayer al = new AlienPlayer();
        assertTrue(al.isValidMove(5, 5, 5, 6));
        assertTrue(al.isValidMove(5, 5, 6, 5));
    }

    @Test
    void alienCannotMoveMoreThanOneStep() {
        AlienPlayer al = new AlienPlayer();
        assertFalse(al.isValidMove(5, 5, 7, 7));
        assertFalse(al.isValidMove(5, 5, 5, 7));
    }

    @Test
    void alienCannotStayInPlace() {
        AlienPlayer al = new AlienPlayer();
        assertFalse(al.isValidMove(5, 5, 5, 5));
    }

    // ─── Goblin ──────────────────────────────────────────────────

    @Test
    void goblinStatsCorrect() {
        Goblin g = new Goblin();
        assertEquals("Goblin", g.getName());
        assertEquals(60, g.getMaxHp());
        assertEquals(15, g.getAttackPower());
        assertEquals(2, g.getDefense());
    }

    @Test
    void goblinNotDefendingTakesNormalDamage() {
        Goblin g = new Goblin();
        g.takeDamage(10); // 10 - 2 def = 8 actual
        assertEquals(52, g.getCurrentHp());
    }

    @Test
    void goblinDefendingReducesDamage() {
        Goblin g = new Goblin();
        g.setDefending(true);
        g.takeDamage(20); // 20/2=10, 10-2def=8 actual
        assertEquals(52, g.getCurrentHp());
    }

    @Test
    void goblinDefendingDamageIsLessThanNormal() {
        Goblin g1 = new Goblin();
        Goblin g2 = new Goblin();
        g1.setDefending(true);
        g1.takeDamage(30);
        g2.takeDamage(30);
        assertTrue(g1.getCurrentHp() > g2.getCurrentHp());
    }
}
