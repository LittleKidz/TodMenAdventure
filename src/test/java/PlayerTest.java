import model.entity.KnightPlayer;
import model.entity.Player;
import model.entity.RebornPlayer;
import model.item.HealItem;
import model.item.BuffItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ Player — inventory, defend, move, pickUp, useItem
 */
public class PlayerTest {

    private Player knight;
    private Player reborn;

    @BeforeEach
    void setUp() {
        knight = new KnightPlayer();
        knight.setPlayerNumber(1);
        reborn = new RebornPlayer();
        reborn.setPlayerNumber(2);
    }

    // ─── Inventory ───────────────────────────────────────────────

    @Test
    void pickUpItemAddsToInventory() {
        knight.pickUpItem(new HealItem(30));
        assertEquals(1, knight.getInventory().size());
    }

    @Test
    void pickUpItemReturnsTrueOnSuccess() {
        assertTrue(knight.pickUpItem(new HealItem(30)));
    }

    @Test
    void pickUpItemReturnsFalseWhenFull() {
        for (int i = 0; i < knight.getMaxInventorySize(); i++) {
            knight.pickUpItem(new HealItem(30));
        }
        assertFalse(knight.pickUpItem(new HealItem(30)));
    }

    @Test
    void pickUpItemInventoryNeverExceedsMax() {
        for (int i = 0; i < 10; i++) {
            knight.pickUpItem(new HealItem(30));
        }
        assertTrue(knight.getInventory().size() <= knight.getMaxInventorySize());
    }

    @Test
    void isInventoryFullTrueWhenAtMax() {
        for (int i = 0; i < knight.getMaxInventorySize(); i++) {
            knight.pickUpItem(new HealItem(30));
        }
        assertTrue(knight.isInventoryFull());
    }

    @Test
    void isInventoryFullFalseWhenNotFull() {
        knight.pickUpItem(new HealItem(30));
        assertFalse(knight.isInventoryFull());
    }

    @Test
    void knightDefaultInventorySizeIsFour() {
        assertEquals(4, knight.getMaxInventorySize());
    }

    @Test
    void rebornInventorySizeIsFive() {
        assertEquals(5, reborn.getMaxInventorySize());
    }

    // ─── useFirstItem ────────────────────────────────────────────

    @Test
    void useFirstItemRemovesItemFromInventory() {
        knight.pickUpItem(new HealItem(30));
        knight.useFirstItem();
        assertEquals(0, knight.getInventory().size());
    }

    @Test
    void useFirstItemAppliesHealEffect() {
        knight.takeDamage(50);
        int hpBefore = knight.getCurrentHp();
        knight.pickUpItem(new HealItem(30));
        knight.useFirstItem();
        assertEquals(Math.min(knight.getMaxHp(), hpBefore + 30), knight.getCurrentHp());
    }

    @Test
    void useFirstItemReturnsFalseWhenEmpty() {
        assertFalse(knight.useFirstItem());
    }

    @Test
    void useFirstItemReturnsTrueOnSuccess() {
        knight.pickUpItem(new HealItem(30));
        assertTrue(knight.useFirstItem());
    }

    // ─── Defend ──────────────────────────────────────────────────

    @Test
    void defendingReducesDamageByHalf() {
        int before = knight.getCurrentHp();
        knight.setDefending(true);
        knight.takeDamage(20); // defending: 20/2=10, 10-7def=3 actual
        assertTrue(knight.getCurrentHp() > before - 20 + 7); // รับน้อยกว่าปกติ
    }

    @Test
    void defendingResetsAfterTakingDamage() {
        knight.setDefending(true);
        knight.takeDamage(10);
        assertFalse(knight.isDefending());
    }

    @Test
    void notDefendingTakesFullDamage() {
        int before = knight.getCurrentHp();
        knight.takeDamage(20); // 20 - 7 def = 13
        assertEquals(before - 13, knight.getCurrentHp());
    }

    // ─── Move ────────────────────────────────────────────────────

    @Test
    void moveUpdatesRowAndCol() {
        knight.move(3, 5);
        assertEquals(3, knight.getRow());
        assertEquals(5, knight.getCol());
    }

    @Test
    void isValidMoveOrthogonalReturnsTrue() {
        assertTrue(knight.isValidMove(5, 5, 5, 6));
        assertTrue(knight.isValidMove(5, 5, 6, 5));
    }

    @Test
    void isValidMoveDiagonalReturnsFalse() {
        assertFalse(knight.isValidMove(5, 5, 6, 6));
    }

    // ─── selectedItemIndex ────────────────────────────────────────

    @Test
    void selectedItemIndexDefaultIsZero() {
        assertEquals(0, knight.getSelectedItemIndex());
    }

    @Test
    void selectedItemIndexCanBeChanged() {
        knight.setSelectedItemIndex(2);
        assertEquals(2, knight.getSelectedItemIndex());
    }
}
