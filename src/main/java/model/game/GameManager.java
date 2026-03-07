package model.game;

import model.battle.BattleAction;
import model.battle.BattleManager;
import model.battle.BattleResult;
import model.entity.CharacterType;
import model.entity.Entity;
import model.entity.Player;
import model.item.TomeItem;
import model.map.Cell;
import model.map.CellType;
import model.map.MapGrid;
import model.map.MapLoader;

/**
 * Central controller for the entire game, managing flow from start to finish.
 * The UI communicates with GameManager via method calls and receives events back through GameEventListener.
 *
 * <p>Flow ของเกม:</p>
 * <pre>
 * startGame() → selectCharacter(1) → selectCharacter(2)
 *   → [MAP_PHASE]    movePlayer(1/2, row, col) ซ้ำ ๆ
 *   → [BATTLE_PHASE] submitBattleAction(1/2, action) ซ้ำ ๆ
 *   → [GAME_OVER]
 * </pre>
 */
public class GameManager {

    /** Current game state. */
    private GameState state;

    /** Player 1. */
    private Player player1;

    /** Player 2. */
    private Player player2;

    /** Character type selected by player 1. */
    private CharacterType selectedType1;

    /** Character type selected by player 2. */
    private CharacterType selectedType2;

    /** Current map. */
    private MapGrid map;

    /** Shrinking zone system. */
    private ZoneManager zoneManager;

    /** Current battle manager (null if not in a battle). */
    private BattleManager battleManager;

    /** Current map round. */
    private int mapRound;

    /** Whether player 1 has moved this round. */
    private boolean p1MovedThisRound;

    /** Whether player 2 has moved this round. */
    private boolean p2MovedThisRound;

    /** The goblin cell currently being fought (used to remove the goblin after the battle). */
    private Cell pendingGoblinCell;

    /** Listener registered by the UI to receive events. */
    private GameEventListener listener;

    /**
     * Creates a new GameManager, starting at MAIN_MENU.
     */
    public GameManager() {
        this.state = GameState.MAIN_MENU;
    }

    /**
     * Registers the listener that will receive events from GameManager.
     *
     * @param listener UI component implementing GameEventListener
     */
    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }

    /**
     * Starts a new game by loading a random map and navigating to the character selection screen.
     */
    public void startGame() {
        map = MapLoader.loadRandomMap();
        zoneManager = new ZoneManager();
        mapRound = 0;
        p1MovedThisRound = false;
        p2MovedThisRound = false;
        selectedType1 = null;
        selectedType2 = null;
        player1 = null;
        player2 = null;
        setState(GameState.CHARACTER_SELECT);
    }

    /**
     * Selects a character for the given player.
     * Once both players have selected, the game automatically enters MAP_PHASE.
     *
     * @param playerIndex Player number (1 or 2)
     * @param type        The chosen character type
     */
    public void selectCharacter(int playerIndex, CharacterType type) {
        if (playerIndex == 1) {
            selectedType1 = type;
        } else if (playerIndex == 2) {
            selectedType2 = type;
        }

        if (selectedType1 != null && selectedType2 != null) {
            player1 = selectedType1.createPlayer();
            player2 = selectedType2.createPlayer();
            player1.setPlayerNumber(1);
            player2.setPlayerNumber(2);
            player1.move(0, 0);
            player2.move(MapGrid.SIZE - 1, MapGrid.SIZE - 1);
            setState(GameState.MAP_PHASE);
        }
    }

    /**
     * Moves a player to the specified cell after validating the move.
     * Items are picked up automatically; landing on a Goblin or the other player starts a battle.
     *
     * @param playerIndex Player number (1 or 2)
     * @param newRow      Destination row
     * @param newCol      Destination column
     */
    public void movePlayer(int playerIndex, int newRow, int newCol) {
        if (state != GameState.MAP_PHASE) return;

        Player mover = (playerIndex == 1) ? player1 : player2;
        Player other = (playerIndex == 1) ? player2 : player1;

        if (!map.isValidPosition(newRow, newCol)) return;
        if (!mover.canMoveTo(map.getCell(newRow, newCol))) return;
        if (!mover.isValidMove(mover.getRow(), mover.getCol(), newRow, newCol)) return;

        mover.move(newRow, newCol);
        if (listener != null) listener.onPlayerMoved(mover, newRow, newCol);

        Cell cell = map.getCell(newRow, newCol);

        if (cell.getType() == CellType.ITEM && cell.getItem() != null) {
            boolean picked = mover.pickUpItem(cell.getItem());
            if (picked) {
                cell.setItem(null);
                cell.setType(CellType.NORMAL);
                if (listener != null) listener.onItemPickedUp(mover);
            }
        } else if (cell.getType() == CellType.GOBLIN && cell.getGoblin() != null) {
            pendingGoblinCell = cell;
            startBattle(mover, cell.getGoblin(), BattleManager.BattleMode.PLAYER_VS_GOBLIN);
            return;
        }

        if (newRow == other.getRow() && newCol == other.getCol()) {
            pendingGoblinCell = null;
            startBattle(player1, player2, BattleManager.BattleMode.PLAYER_VS_PLAYER);
            return;
        }

        if (playerIndex == 1) p1MovedThisRound = true;
        else                  p2MovedThisRound = true;

        if (p1MovedThisRound && p2MovedThisRound) {
            endMapRound();
        }
    }

    /**
     * Submits a battle action from a player. Once both sides have submitted, the turn resolves automatically.
     *
     * @param playerIndex Player number (1 or 2)
     * @param action      The chosen action
     */
    public void submitBattleAction(int playerIndex, BattleAction action) {
        if (state != GameState.BATTLE_PHASE || battleManager == null) return;

        if (playerIndex == 1) {
            battleManager.submitActionA(action);
        } else {
            battleManager.submitActionB(action);
        }

        if (battleManager.isReadyToResolve()) {
            BattleResult result = battleManager.resolveTurn();
            if (listener != null) listener.onBattleResult(result);
            if (result.isBattleOver()) {
                handleBattleOver(result);
            }
        }
    }

    /**
     * Starts a new battle between entityA and entityB.
     *
     * @param entityA Entity on side A
     * @param entityB Entity on side B
     * @param mode    PvP or vs Goblin mode
     */
    private void startBattle(Entity entityA, Entity entityB, BattleManager.BattleMode mode) {
        battleManager = new BattleManager(mode, entityA, entityB);
        setState(GameState.BATTLE_PHASE);
        if (listener != null) listener.onBattleStart(entityA, entityB);
    }

    /**
     * Handles the outcome after a battle ends: removes the goblin from the map and distributes a tome or triggers game over.
     *
     * @param result The battle result
     */
    private void handleBattleOver(BattleResult result) {
        Entity winner = result.getWinner();
        Entity loser  = result.getLoser();

        if (battleManager.getMode() == BattleManager.BattleMode.PLAYER_VS_GOBLIN) {
            if (pendingGoblinCell != null) {
                pendingGoblinCell.setGoblin(null);
                pendingGoblinCell.setType(CellType.NORMAL);
                pendingGoblinCell = null;
            }
            if (winner == null) {
                // ตายพร้อมกัน ผู้เล่นที่สู้ goblin แพ้
                Entity playerEntity = (battleManager.getEntityA() instanceof Player)
                        ? battleManager.getEntityA()
                        : battleManager.getEntityB();
                Player losingPlayer = (Player) playerEntity;
                Player winningPlayer = (losingPlayer == player1) ? player2 : player1;
                if (listener != null) listener.onGameOver(winningPlayer);
                setState(GameState.GAME_OVER);
            } else if (loser instanceof Player) {
                Player losingPlayer = (Player) loser;
                Player winningPlayer = (losingPlayer == player1) ? player2 : player1;
                if (listener != null) listener.onGameOver(winningPlayer);
                setState(GameState.GAME_OVER);
            } else {
                // ผู้เล่นชนะ goblin รับ tome แล้วกลับ map
                Player winnerPlayer = (Player) winner;
                TomeItem tome = new TomeItem();
                tome.apply(winnerPlayer);
                if (listener != null) listener.onTomeApplied(winnerPlayer, tome.getEffectDescription());
                winnerPlayer.resetAttackPower();
                winnerPlayer.resetDefense();
                battleManager = null;
                setState(GameState.MAP_PHASE);
            }
        } else {
            Player winnerPlayer = (winner instanceof Player) ? (Player) winner : null;
            if (listener != null) listener.onGameOver(winnerPlayer);
            setState(GameState.GAME_OVER);
        }
    }

    /**
     * Ends a map round: increments the round counter, checks for zone shrink, applies zone damage, and checks for deaths.
     */
    private void endMapRound() {
        p1MovedThisRound = false;
        p2MovedThisRound = false;
        mapRound++;

        boolean shrunk = zoneManager.onRoundEnd(mapRound, map);
        if (shrunk && listener != null) {
            listener.onZoneShrunk(zoneManager.getCurrentRadius());
        }

        zoneManager.applyZoneDamage(player1, player2, map);
        if (listener != null) listener.onRoundEnd(mapRound);
        checkGameOver();
    }

    /**
     * Checks whether any player has died from zone damage and handles game over if so.
     */
    private void checkGameOver() {
        boolean p1Dead = !player1.isAlive();
        boolean p2Dead = !player2.isAlive();

        if (p1Dead && p2Dead) {
            if (listener != null) listener.onGameOver(null);
            setState(GameState.GAME_OVER);
        } else if (p1Dead) {
            if (listener != null) listener.onGameOver(player2);
            setState(GameState.GAME_OVER);
        } else if (p2Dead) {
            if (listener != null) listener.onGameOver(player1);
            setState(GameState.GAME_OVER);
        }
    }

    /**
     * Changes the game state and notifies the listener.
     *
     * @param newState The new state
     */
    private void setState(GameState newState) {
        this.state = newState;
        if (listener != null) listener.onStateChanged(newState);
    }

    // ─── Getters ─────────────────────────────────────────────────

    /** @return Current game state. */
    public GameState getState()              { return state; }

    /** @return Player 1. */
    public Player getPlayer1()               { return player1; }

    /** @return Player 2. */
    public Player getPlayer2()               { return player2; }

    /** @return Current map. */
    public MapGrid getMap()                  { return map; }

    /** @return The ZoneManager for this game */
    public ZoneManager getZoneManager()      { return zoneManager; }

    /** @return Current BattleManager (null if not in a battle) */
    public BattleManager getBattleManager()  { return battleManager; }

    /** @return Current map round. */
    public int getMapRound()                 { return mapRound; }
}
