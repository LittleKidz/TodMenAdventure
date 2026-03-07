package model.game;

import model.battle.BattleResult;
import model.entity.Entity;
import model.entity.Player;

/**
 * Interface that the UI must implement to receive events from GameManager.
 * Separating the Model from the UI allows game logic to be tested without a real screen.
 */
public interface GameEventListener {

    /**
     * Notifies when the GameState changes.
     *
     * @param newState The new state
     */
    void onStateChanged(GameState newState);

    /**
     * Notifies when a battle starts; the UI should create a BattleScene.
     *
     * @param entityA Entity on side A
     * @param entityB Entity on side B
     */
    void onBattleStart(Entity entityA, Entity entityB);

    /**
     * Notifies the UI of each turn's result so it can update HP bars and messages.
     *
     * @param result Result of this turn
     */
    void onBattleResult(BattleResult result);

    /**
     * Notifies when a player successfully moves.
     *
     * @param player The player who moved
     * @param row    New row
     * @param col    New column
     */
    void onPlayerMoved(Player player, int row, int col);

    /**
     * Notifies when the zone shrinks.
     *
     * @param newRadius New radius of the safe zone
     */
    void onZoneShrunk(int newRadius);

    /**
     * Notifies when the game ends.
     *
     * @param winner The winner, or {@code null} in case of a draw
     */
    void onGameOver(Player winner);

    /**
     * Notifies when a player picks up an item.
     *
     * @param player The player who picked up the item
     */
    void onItemPickedUp(Player player);

    /**
     * Notifies when a map round ends.
     *
     * @param round The current round number
     */
    void onRoundEnd(int round);

    /**
     * Notifies when a TomeItem is applied to the winner after defeating a Goblin.
     *
     * @param player            The player who received the tome
     * @param effectDescription Description of the stat boost
     */
    void onTomeApplied(Player player, String effectDescription);
}
