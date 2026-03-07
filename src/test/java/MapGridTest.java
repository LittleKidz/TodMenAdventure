import model.entity.Goblin;
import model.item.HealItem;
import model.map.Cell;
import model.map.CellType;
import model.map.MapGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ทดสอบ MapGrid และ Cell — size, walkable, valid position, adjacent move
 */
public class MapGridTest {

    private MapGrid map;

    @BeforeEach
    void setUp() {
        map = new MapGrid();
    }

    // ─── MapGrid ─────────────────────────────────────────────────

    @Test
    void mapGridSizeIs11() {
        assertEquals(11, MapGrid.SIZE);
    }

    @Test
    void mapGridAllCellsInitiallyNormal() {
        for (int r = 0; r < MapGrid.SIZE; r++) {
            for (int c = 0; c < MapGrid.SIZE; c++) {
                assertEquals(CellType.NORMAL, map.getCell(r, c).getType());
            }
        }
    }

    @Test
    void mapGridGetCellReturnsCorrectCell() {
        Cell cell = map.getCell(3, 5);
        assertEquals(3, cell.getRow());
        assertEquals(5, cell.getCol());
    }

    @Test
    void isValidPositionInBoundsReturnsTrue() {
        assertTrue(map.isValidPosition(0, 0));
        assertTrue(map.isValidPosition(10, 10));
        assertTrue(map.isValidPosition(5, 5));
    }

    @Test
    void isValidPositionOutOfBoundsReturnsFalse() {
        assertFalse(map.isValidPosition(-1, 0));
        assertFalse(map.isValidPosition(0, -1));
        assertFalse(map.isValidPosition(11, 0));
        assertFalse(map.isValidPosition(0, 11));
    }

    @Test
    void isWalkableNormalCellReturnsTrue() {
        assertTrue(map.isWalkable(5, 5));
    }

    @Test
    void isWalkableRockCellReturnsFalse() {
        map.getCell(3, 3).setType(CellType.ROCK);
        assertFalse(map.isWalkable(3, 3));
    }

    @Test
    void isWalkableRiverCellReturnsFalse() {
        map.getCell(2, 2).setType(CellType.RIVER);
        assertFalse(map.isWalkable(2, 2));
    }

    @Test
    void isWalkableTreeCellReturnsFalse() {
        map.getCell(1, 1).setType(CellType.TREE);
        assertFalse(map.isWalkable(1, 1));
    }

    @Test
    void isWalkableOutOfBoundsReturnsFalse() {
        assertFalse(map.isWalkable(-1, 0));
        assertFalse(map.isWalkable(0, 11));
    }

    @Test
    void isAdjacentMoveOneStepReturnsTrue() {
        assertTrue(map.isAdjacentMove(5, 5, 5, 6));
        assertTrue(map.isAdjacentMove(5, 5, 6, 5));
        assertTrue(map.isAdjacentMove(5, 5, 5, 4));
        assertTrue(map.isAdjacentMove(5, 5, 4, 5));
    }

    @Test
    void isAdjacentMoveDiagonalReturnsFalse() {
        assertFalse(map.isAdjacentMove(5, 5, 6, 6));
        assertFalse(map.isAdjacentMove(5, 5, 4, 4));
    }

    @Test
    void isAdjacentMoveTwoStepsReturnsFalse() {
        assertFalse(map.isAdjacentMove(5, 5, 5, 7));
        assertFalse(map.isAdjacentMove(5, 5, 7, 5));
    }

    // ─── Cell ────────────────────────────────────────────────────

    @Test
    void cellIsWalkableNormalTypeReturnsTrue() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        assertTrue(cell.isWalkable());
    }

    @Test
    void cellIsWalkableRockTypeReturnsFalse() {
        Cell cell = new Cell(0, 0, CellType.ROCK);
        assertFalse(cell.isWalkable());
    }

    @Test
    void cellIsWalkableRiverTypeReturnsFalse() {
        Cell cell = new Cell(0, 0, CellType.RIVER);
        assertFalse(cell.isWalkable());
    }

    @Test
    void cellIsWalkableTreeTypeReturnsFalse() {
        Cell cell = new Cell(0, 0, CellType.TREE);
        assertFalse(cell.isWalkable());
    }

    @Test
    void cellIsWalkableItemTypeReturnsTrue() {
        Cell cell = new Cell(0, 0, CellType.ITEM);
        assertTrue(cell.isWalkable());
    }

    @Test
    void cellIsWalkableGoblinTypeReturnsTrue() {
        Cell cell = new Cell(0, 0, CellType.GOBLIN);
        assertTrue(cell.isWalkable());
    }

    @Test
    void cellSetItemStoresItem() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        HealItem item = new HealItem(30);
        cell.setItem(item);
        assertEquals(item, cell.getItem());
    }

    @Test
    void cellSetGoblinStoresGoblin() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        Goblin goblin = new Goblin();
        cell.setGoblin(goblin);
        assertEquals(goblin, cell.getGoblin());
    }

    @Test
    void cellInitiallyNoItemOrGoblin() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        assertNull(cell.getItem());
        assertNull(cell.getGoblin());
    }

    @Test
    void cellSetTypeChangesType() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        cell.setType(CellType.ROCK);
        assertEquals(CellType.ROCK, cell.getType());
    }

    @Test
    void cellInZoneInitiallyFalse() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        assertFalse(cell.isInZone());
    }

    @Test
    void cellSetInZoneUpdatesValue() {
        Cell cell = new Cell(0, 0, CellType.NORMAL);
        cell.setInZone(true);
        assertTrue(cell.isInZone());
    }
}
