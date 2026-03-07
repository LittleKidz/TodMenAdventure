package model.game;

import model.entity.Player;
import model.map.Cell;
import model.map.CellType;
import model.map.MapGrid;

/**
 * Shrinking Zone system. Every 8 rounds the safe zone shrinks by 1 cell.
 * Players standing on lava take 10 HP damage every round.
 */
public class ZoneManager {

    /**
     * Number of rounds before the zone shrinks by 1 cell.
     * For example, with SHRINK_INTERVAL = 8, the zone shrinks on rounds 8, 16, 24, ...
     */
    private static final int SHRINK_INTERVAL = 8;

    /**
     * Amount of HP a player loses per round when standing on lava.
     * Applied at the end of every round via applyZoneDamage().
     */
    private static final int ZONE_DAMAGE = 10;

    /** Current radius of the safe zone (starts at 5, i.e. half the map size). */
    private int currentRadius;

    /**
     * Creates a ZoneManager with the initial radius set to half the map size.
     */
    public ZoneManager() {
        this.currentRadius = MapGrid.SIZE / 2;
    }

    /**
     * Called at the end of every round to check whether the zone should shrink.
     * If so, the radius is decremented and newly exposed cells are converted to lava.
     *
     * @param round Current round number
     * @param map   Map whose cells will be updated
     * @return {@code true} if the zone shrank this round
     */
    public boolean onRoundEnd(int round, MapGrid map) {
        if (round > 0 && round % SHRINK_INTERVAL == 0 && currentRadius > 0) {
            currentRadius--;
            convertNewlyExposedCellsToLava(map);
            return true;
        }
        return false;
    }

    /**
     * Converts all cells outside the safe zone to lava
     * and removes any items or goblins on those cells.
     *
     * @param map Map to update
     */
    private void convertNewlyExposedCellsToLava(MapGrid map) {
        for (int r = 0; r < MapGrid.SIZE; r++) {
            for (int c = 0; c < MapGrid.SIZE; c++) {
                if (isOutsideZone(r, c)) {
                    Cell cell = map.getCell(r, c);
                    cell.setType(CellType.LAVA);
                    cell.setItem(null);
                    cell.setGoblin(null);
                }
            }
        }
    }

    /**
     * Checks whether a cell is outside the safe zone using Chebyshev distance from the map centre.
     *
     * @param row Row of the cell
     * @param col Column of the cell
     * @return {@code true} if the cell is outside the safe zone
     */
    public boolean isOutsideZone(int row, int col) {
        int center = MapGrid.SIZE / 2;
        return Math.abs(row - center) > currentRadius
            || Math.abs(col - center) > currentRadius;
    }

    /**
     * Applies zone damage to any player standing on lava.
     *
     * @param p1  Player 1
     * @param p2  Player 2
     * @param map Current map
     * @return {@code true} if at least one player took damage
     */
    public boolean applyZoneDamage(Player p1, Player p2, MapGrid map) {
        boolean damaged = false;
        if (map.getCell(p1.getRow(), p1.getCol()).getType() == CellType.LAVA) {
            p1.setCurrentHp(Math.max(0, p1.getCurrentHp() - ZONE_DAMAGE));
            damaged = true;
        }
        if (map.getCell(p2.getRow(), p2.getCol()).getType() == CellType.LAVA) {
            p2.setCurrentHp(Math.max(0, p2.getCurrentHp() - ZONE_DAMAGE));
            damaged = true;
        }
        return damaged;
    }

    /** @return Current safe zone radius */
    public int getCurrentRadius() { return currentRadius; }

    /** @return Damage per round when standing on lava */
    public int getZoneDamage()    { return ZONE_DAMAGE; }
}
