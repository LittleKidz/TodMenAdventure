package model.map;

import model.entity.Goblin;
import model.item.BuffItem;
import model.item.DefenseItem;
import model.item.HealItem;
import model.item.Item;

import java.util.Random;

/**
 * Loads one of 10 preset maps, randomly selected for each game.
 * Uses a shuffle bag to avoid repetition until all 10 maps have been used.
 *
 * <p>MAP_DATA:</p>
 * <pre>
 * 0 = NORMAL  1 = RIVER  2 = TREE
 * 3 = ROCK    4 = ITEM   5 = GOBLIN
 * </pre>
 */
public class MapLoader {

    /** Total number of available maps. */
    private static final int MAP_COUNT = 10;

    /** Shared random instance for the shuffle bag and item type selection. */
    private static final Random RANDOM = new Random();

    /** Shuffle bag used to select maps without repetition. */
    private static final int[] shuffleBag = new int[MAP_COUNT];

    /** Current index within the shuffle bag. */
    private static int bagIndex = MAP_COUNT;

    /** Data for all 10 map layouts. */
    private static final int[][][] MAP_DATA = {
            // ── MAP 1: Scattered
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,0,3,0,0,4,0,0,3,0,0},
                    {0,2,0,0,1,0,1,0,0,2,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,5,4,0,0,0,0,0,4,5,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,2,0,0,1,0,1,0,0,2,0},
                    {0,0,3,0,0,4,0,0,3,0,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 2: River Gaps
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,2,0,0,4,0,0,2,0,0},
                    {0,2,0,0,5,0,5,0,0,2,0},
                    {0,0,0,3,0,1,0,3,0,0,0},
                    {0,0,5,0,0,0,0,0,5,0,0},
                    {0,4,0,1,0,3,0,1,0,4,0},
                    {0,0,5,0,0,0,0,0,5,0,0},
                    {0,0,0,3,0,1,0,3,0,0,0},
                    {0,2,0,0,5,0,5,0,0,2,0},
                    {0,0,2,0,0,4,0,0,2,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 3: Open Center
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,3,0,0,4,0,4,0,0,3,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,0,2,0,0,4,0,0,2,0,0},
                    {0,4,0,0,1,0,1,0,0,4,0},
                    {0,0,5,4,0,0,0,4,5,0,0},
                    {0,4,0,0,1,0,1,0,0,4,0},
                    {0,0,2,0,0,4,0,0,2,0,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,3,0,0,4,0,4,0,0,3,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 4: Zigzag
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,2,0,4,0,2,0,0,0},
                    {0,0,5,0,3,0,3,0,5,0,0},
                    {0,2,0,0,0,0,0,0,0,2,0},
                    {0,0,3,0,1,0,1,0,3,0,0},
                    {0,4,0,0,0,5,0,0,0,4,0},
                    {0,0,3,0,1,0,1,0,3,0,0},
                    {0,2,0,0,0,0,0,0,0,2,0},
                    {0,0,5,0,3,0,3,0,5,0,0},
                    {0,0,0,2,0,4,0,2,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 5: Diamond
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,2,4,2,0,0,0,0},
                    {0,0,3,0,0,5,0,0,3,0,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,2,0,0,0,4,0,0,0,2,0},
                    {0,4,5,0,4,0,4,0,5,4,0},
                    {0,2,0,0,0,4,0,0,0,2,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,0,3,0,0,5,0,0,3,0,0},
                    {0,0,0,0,2,4,2,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 6: Four Zones
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,1,0,5,0,5,0,1,0,0},
                    {0,1,0,4,0,2,0,4,0,1,0},
                    {0,0,4,0,0,3,0,0,4,0,0},
                    {0,5,0,0,5,0,5,0,0,5,0},
                    {0,0,2,3,0,4,0,3,2,0,0},
                    {0,5,0,0,5,0,5,0,0,5,0},
                    {0,0,4,0,0,3,0,0,4,0,0},
                    {0,1,0,4,0,2,0,4,0,1,0},
                    {0,0,1,0,5,0,5,0,1,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 7: Corridor
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,2,0,3,0,4,0,3,0,2,0},
                    {0,0,4,0,0,5,0,0,4,0,0},
                    {0,3,0,0,2,0,2,0,0,3,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,4,5,0,5,4,5,0,5,4,0},
                    {0,0,0,2,0,5,0,2,0,0,0},
                    {0,3,0,0,2,0,2,0,0,3,0},
                    {0,0,4,0,0,5,0,0,4,0,0},
                    {0,2,0,3,0,4,0,3,0,2,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 8: Sparse
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,3,5,3,0,0,0,0},
                    {0,0,2,0,0,0,0,0,2,0,0},
                    {0,0,0,1,0,4,0,1,0,0,0},
                    {0,3,0,0,0,2,0,0,0,3,0},
                    {0,5,0,4,5,0,5,4,0,5,0},
                    {0,3,0,0,0,2,0,0,0,3,0},
                    {0,0,0,1,0,4,0,1,0,0,0},
                    {0,0,2,0,0,0,0,0,2,0,0},
                    {0,0,0,0,3,5,3,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 9: Ring
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,2,0,0,5,0,0,2,0,0},
                    {0,2,0,3,0,4,0,3,0,2,0},
                    {0,0,3,0,1,0,1,0,3,0,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,5,4,0,0,5,0,0,4,5,0},
                    {0,0,0,1,0,0,0,1,0,0,0},
                    {0,0,3,0,1,0,1,0,3,0,0},
                    {0,2,0,3,0,4,0,3,0,2,0},
                    {0,0,2,0,0,5,0,0,2,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            },
            // ── MAP 10: Wide Paths
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,3,0,2,0,3,0,0,0},
                    {0,0,4,0,5,0,5,0,4,0,0},
                    {0,3,0,0,0,1,0,0,0,3,0},
                    {0,0,5,0,5,0,5,0,5,0,0},
                    {0,2,0,1,0,4,0,1,0,2,0},
                    {0,0,5,0,5,0,5,0,5,0,0},
                    {0,3,0,0,0,1,0,0,0,3,0},
                    {0,0,4,0,5,0,5,0,4,0,0},
                    {0,0,0,3,0,2,0,3,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0}
            }
    };

    /**
     * Randomly loads one of the 10 maps, ensuring no repetition until all maps have been used.
     *
     * @return A ready-to-use MapGrid
     */
    public static MapGrid loadRandomMap() {
        return buildMap(MAP_DATA[nextMapIndex()]);
    }

    /**
     * Picks the next map index from the shuffle bag.
     * Refills and reshuffles the bag when exhausted.
     *
     * @return Index of the map to load (0-9)
     */
    private static int nextMapIndex() {
        if (bagIndex >= MAP_COUNT) {
            for (int i = 0; i < MAP_COUNT; i++) shuffleBag[i] = i;
            for (int i = MAP_COUNT - 1; i > 0; i--) {
                int j = RANDOM.nextInt(i + 1);
                int tmp = shuffleBag[i]; shuffleBag[i] = shuffleBag[j]; shuffleBag[j] = tmp;
            }
            bagIndex = 0;
        }
        return shuffleBag[bagIndex++];
    }

    /**
     * Loads the map at the specified index, intended for testing.
     *
     * @param index Map index (0-9)
     * @return A ready-to-use MapGrid
     * @throws IllegalArgumentException if the index is not in the range 0-9
     */
    public static MapGrid loadMap(int index) {
        if (index < 0 || index >= MAP_COUNT)
            throw new IllegalArgumentException("Map index ต้องอยู่ระหว่าง 0-9");
        return buildMap(MAP_DATA[index]);
    }

    /**
     * Builds a MapGrid from a raw int[][] data array,
     * placing random items (HealItem, BuffItem, or DefenseItem) and Goblins in the designated cells.
     *
     * @param data Raw map data as int[][]
     * @return A MapGrid with all items and goblins placed
     */
    private static MapGrid buildMap(int[][] data) {
        MapGrid grid = new MapGrid();
        for (int r = 0; r < MapGrid.SIZE; r++) {
            for (int c = 0; c < MapGrid.SIZE; c++) {
                CellType type = CellType.values()[data[r][c]];
                grid.getCell(r, c).setType(type);

                if (type == CellType.ITEM) {
                    Item item;
                    int rand = RANDOM.nextInt(3);
                    if      (rand == 0) item = new HealItem(20);
                    else if (rand == 1) item = new BuffItem(3);
                    else                item = new DefenseItem(3);
                    grid.getCell(r, c).setItem(item);
                } else if (type == CellType.GOBLIN) {
                    grid.getCell(r, c).setGoblin(new Goblin());
                }
            }
        }
        return grid;
    }
}
