package application;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;

import model.battle.BattleAction;
import model.battle.BattleManager;
import model.battle.BattleResult;
import model.entity.*;
import model.game.*;
import model.item.*;

/**
 * Mediator between the Model (GameManager) and the View (Scenes).
 * Implements GameEventListener to receive events from GameManager and instructs Scenes to update the UI.
 * Manages keyboard input for the map phase and action buttons in the battle phase.
 */
public class GameController implements GameEventListener {

    /** The main JavaFX Stage of the application. */
    private final Stage stage;

    /** GameManager that controls all game logic. */
    private GameManager gm;

    /** current Main menu scene */
    // View objects
    private MainMenuScene mainMenu;

    /** Current map phase screen ({@code null} if not yet created). */
    private BoardScene    board;

    /** Current battle phase screen ({@code null} if not in battle). */
    private BattleScene   battle;

    /** Class of P1 used to load sprites in the View. */
    private String  p1Class = "knight";

    /** Class of P2 used to load sprites in the View. */
    private String  p2Class = "archer";

    /** Whether it is P1's turn to move. */
    private boolean p1TurnToMove = true;

    /** Last direction P1 moved. */
    private String  lastP1Dir = "right";

    /** Last direction P2 moved. */
    private String  lastP2Dir = "left";

    /** Whether the current battle is PvP, used to determine if P2's input must be waited for. */
    private boolean isPvP = false;

    /** Lock that prevents repeated key presses while an animation is playing. */
    private boolean moveLock = false;

    /**
     * Creates a GameController and registers a listener with the GameManager.
     *
     * @param stage The main Stage of the application
     */
    public GameController(Stage stage) {
        this.stage = stage;
        this.gm    = new GameManager();
        this.gm.setListener(this);
    }

    /**
     * Initialises the application, configures the Stage, and shows the Main Menu.
     */
    public void initialize() {
        stage.setTitle("TodMen Adventure");
        stage.setResizable(false);
        stage.setWidth(1150);
        stage.setHeight(750);
        showMainMenu();
        stage.show();
        Platform.runLater(stage::centerOnScreen);
    }

    /**
     * Shows the Main Menu screen and binds the Start button.
     */
    private void showMainMenu() {
        board  = null;
        battle = null;

        mainMenu = new MainMenuScene();
        mainMenu.getStartBtn().setOnAction(e -> {
            stopBgm(mainMenu.getBgmPlayer());
            gm.startGame();
        });
        stage.setScene(mainMenu.getScene());
    }

    /**
     * Receives a state-change event and switches to the appropriate screen.
     *
     * @param state The new game state
     */
    @Override
    public void onStateChanged(GameState state) {
        Platform.runLater(() -> {
            switch (state) {
                case MAIN_MENU:        showMainMenu();        break;
                case CHARACTER_SELECT: showCharacterSelect(); break;
                case MAP_PHASE:        onEnterMapPhase();     break;
                case BATTLE_PHASE:     break;
                case GAME_OVER:        break;
            }
        });
    }

    /**
     * Receives a player-moved event, updates the animation on the BoardScene, and switches the turn.
     *
     * @param player The player who moved
     * @param row    New row
     * @param col    New column
     */
    @Override
    public void onPlayerMoved(Player player, int row, int col) {
        Platform.runLater(() -> {
            if (board == null) return;
            int    pNum = player.getPlayerNumber();
            String dir  = (pNum == 1) ? lastP1Dir : lastP2Dir;
            board.movePlayer(pNum, col, row, dir);

            p1TurnToMove = !p1TurnToMove;
            board.setTurnText(p1TurnToMove ? "P1 Turn (WASD)" : "P2 Turn (UHJK)");
            moveLock = false;
        });
    }

    /**
     * Receives an item-pickup event and updates the inventory panel and map.
     *
     * @param player The player who picked up the item
     */
    @Override
    public void onItemPickedUp(Player player) {
        Platform.runLater(() -> {
            if (board == null) return;
            board.updateInventory(player.getPlayerNumber(), itemNames(player));
            board.refreshMapFromModel(gm.getMap());
            board.showNotification(player.getName() + " picked up an item!");
        });
    }

    /**
     * Receives a round-end event on the map, updates stats and the round counter.
     *
     * @param round The round that just ended
     */
    @Override
    public void onRoundEnd(int round) {
        Platform.runLater(() -> {
            if (board == null) return;
            refreshStats();
            board.setRoundText("Round " + round);
        });
    }

    /**
     * Receives a zone-shrink event, redraws the map, and shows a notification.
     *
     * @param newRadius New radius of the safe zone
     */
    @Override
    public void onZoneShrunk(int newRadius) {
        Platform.runLater(() -> {
            if (board == null) return;
            board.refreshMapFromModel(gm.getMap());
            board.showNotification("☠ Zone shrinks! Radius: " + newRadius);
        });
    }

    /**
     * Receives a tome-applied event after the player defeats a Goblin, updates stats, and shows a notification.
     *
     * @param player     The player who received the tome
     * @param effectDesc Description of the stat boost
     */
    @Override
    public void onTomeApplied(Player player, String effectDesc) {
        Platform.runLater(() -> {
            if (board == null) return;
            refreshStats();
            board.showNotification(player.getName() + " received: " + effectDesc + "!");
        });
    }

    /**
     * Receives a battle-start event, creates the BattleScene, and shows action buttons.
     *
     * @param entityA Entity on side A
     * @param entityB Entity on side B
     */
    @Override
    public void onBattleStart(Entity entityA, Entity entityB) {
        String f1Name  = entityA.getName(), f1Class = classOf(entityA);
        double f1Hp    = entityA.getCurrentHp(), f1Max = entityA.getMaxHp();
        int    f1Atk   = entityA.getAttackPower(), f1Def = entityA.getDefense();

        String f2Name  = entityB.getName(), f2Class = classOf(entityB);
        double f2Hp    = entityB.getCurrentHp(), f2Max = entityB.getMaxHp();
        int    f2Atk   = entityB.getAttackPower(), f2Def = entityB.getDefense();

        isPvP = gm.getBattleManager().getMode() == BattleManager.BattleMode.PLAYER_VS_PLAYER;

        Platform.runLater(() -> {
            stopBgm(board != null ? board.getBgmPlayer() : null);
            battle = new BattleScene(f1Name, f1Class, f1Hp, f1Max, f1Atk, f1Def,
                                     f2Name, f2Class, f2Hp, f2Max, f2Atk, f2Def);
            setupBattleButtonsForA();
            if (battle.getBgmPlayer() != null) battle.getBgmPlayer().play();
            stage.setScene(battle.getScene());
        });
    }

    /**
     * Receives the result of each battle turn, plays the appropriate animation,
     * then updates the HP bars after the animation finishes.
     *
     * @param result Result of this turn
     */
    @Override
    public void onBattleResult(BattleResult result) {
        BattleManager bm = gm.getBattleManager();
        if (bm == null) return;

        Entity eA = bm.getEntityA(), eB = bm.getEntityB();
        double eAHp  = eA.getCurrentHp(), eAMax = eA.getMaxHp();
        int    eAAtk = eA.getAttackPower(), eADef = eA.getDefense();
        double eBHp  = eB.getCurrentHp(), eBMax = eB.getMaxHp();
        int    eBAtk = eB.getAttackPower(), eBDef = eB.getDefense();

        String  logA = result.getResultA() != null ? result.getResultA().getDescription() : "";
        String  logB = result.getResultB() != null ? result.getResultB().getDescription() : "";
        boolean over = result.isBattleOver();

        Platform.runLater(() -> {
            if (battle == null) return;

            if (over) {
                battle.updateHP(true,  eAHp, eAMax, eAAtk, eADef);
                battle.updateHP(false, eBHp, eBMax, eBAtk, eBDef);
                battle.setLogText(logA.isEmpty() ? logB : logA);
                return;
            }

            battle.setLogText(logA);

            PauseTransition t1 = new PauseTransition(Duration.seconds(1.2));
            t1.setOnFinished(e -> {
                if (!logB.isEmpty()) {
                    if (!isPvP) {
                        BattleAction goblinAction = result.getResultB().getAction();
                        if (goblinAction == BattleAction.DEFEND) {
                            battle.updateHP(true,  eAHp, eAMax, eAAtk, eADef);
                            battle.updateHP(false, eBHp, eBMax, eBAtk, eBDef);
                            battle.setLogText(logB);
                            PauseTransition t2 = new PauseTransition(Duration.seconds(1.2));
                            t2.setOnFinished(e2 -> {
                                battle.setLogText("");
                                if (gm.getBattleManager() != null) setupBattleButtonsForA();
                            });
                            t2.play();
                        } else {
                            battle.playActionAnimation(false, "Attack", eB.getName(), eA.getName(), eB.getAttackPower(), () -> {
                                battle.updateHP(true,  eAHp, eAMax, eAAtk, eADef);
                                battle.updateHP(false, eBHp, eBMax, eBAtk, eBDef);
                                battle.setLogText(logB);
                                PauseTransition t2 = new PauseTransition(Duration.seconds(1.2));
                                t2.setOnFinished(e2 -> {
                                    battle.setLogText("");
                                    if (gm.getBattleManager() != null) setupBattleButtonsForA();
                                });
                                t2.play();
                            });
                        }
                    } else {
                        battle.updateHP(true,  eAHp, eAMax, eAAtk, eADef);
                        battle.updateHP(false, eBHp, eBMax, eBAtk, eBDef);
                        battle.setLogText(logB);
                        PauseTransition t2 = new PauseTransition(Duration.seconds(1.2));
                        t2.setOnFinished(e2 -> {
                            battle.setLogText("");
                            if (gm.getBattleManager() != null) setupBattleButtonsForA();
                        });
                        t2.play();
                    }
                } else {
                    battle.updateHP(true,  eAHp, eAMax, eAAtk, eADef);
                    battle.updateHP(false, eBHp, eBMax, eBAtk, eBDef);
                    battle.setLogText("");
                    if (gm.getBattleManager() != null) setupBattleButtonsForA();
                }
            });
            t1.play();
        });
    }

    /**
     * Receives a game-over event, shows the GameOverScene, and binds its buttons.
     *
     * @param winner The winner, or {@code null} in case of a draw
     */
    @Override
    public void onGameOver(Player winner) {
        Player p1 = gm.getPlayer1();
        Player p2 = gm.getPlayer2();

        Platform.runLater(() -> {
            stopBgm(battle != null ? battle.getBgmPlayer() : null);
            stopBgm(board  != null ? board.getBgmPlayer()  : null);

            String winName, winClass, loseName, loseClass;
            if (winner == null) {
                winName = loseName = "Draw";
                winClass = p1Class; loseClass = p2Class;
            } else if (winner.getPlayerNumber() == 1) {
                winName  = "Player 1"; winClass  = p1Class;
                loseName = "Player 2"; loseClass = p2Class;
            } else {
                winName  = "Player 2"; winClass  = p2Class;
                loseName = "Player 1"; loseClass = p1Class;
            }

            GameOverScene gameOver = new GameOverScene(winName, winClass, loseName, loseClass);
            gameOver.getMainMenuBtn().setOnAction(e -> {
                stopBgm(gameOver.getBgmPlayer());
                gm = new GameManager();
                gm.setListener(this);
                board = null; battle = null;
                showMainMenu();
            });
            gameOver.getExitBtn().setOnAction(e -> stage.close());
            stage.setScene(gameOver.getScene());
        });
    }

    /**
     * Shows the character selection screen and sets the callback for when selection is complete.
     */
    private void showCharacterSelect() {
        CharacterSelectScene charSelect = new CharacterSelectScene();
        charSelect.setOnGameStart((c1, c2) -> {
            p1Class = c1.toLowerCase();
            p2Class = c2.toLowerCase();
            gm.selectCharacter(1, toType(c1));
            gm.selectCharacter(2, toType(c2));
        });
        stage.setScene(charSelect.getScene());
    }

    /**
     * Configures the map phase screen when entering MAP_PHASE.
     * If no BoardScene exists yet, it is created and keyboard listeners are set up.
     */
    private void onEnterMapPhase() {
        if (battle != null) stopBgm(battle.getBgmPlayer());
        Platform.runLater(()->{
            if (board.getBgmPlayer() != null) board.getBgmPlayer().play();
        });

        Player p1 = gm.getPlayer1();
        Player p2 = gm.getPlayer2();

        if (board == null) {
            board = new BoardScene(
                p1Class, p2Class, gm.getMap(),
                p1.getName(), p2.getName(),
                p1.getCurrentHp(), p1.getMaxHp(), p1.getAttackPower(), p1.getDefense(),
                p2.getCurrentHp(), p2.getMaxHp(), p2.getAttackPower(), p2.getDefense()
            );
            p1TurnToMove = true;
            setupKeyboard();
        }

        board.refreshMapFromModel(gm.getMap());
        board.setTurnText(p1TurnToMove ? "P1 Turn (WASD)" : "P2 Turn (UHJK)");
        refreshStats();
        stage.setScene(board.getScene());
        stage.requestFocus();
    }

    /**
     * Sets up keyboard listeners on the BoardScene.
     * P1 uses WASD (plus QEZX for Alien diagonals); P2 uses UHJK (plus YINM).
     */
    private void setupKeyboard() {
        board.getScene().setOnKeyPressed(ev -> {
            if (gm.getState() != GameState.MAP_PHASE) return;
            if (moveLock) return;

            int    nr, nc;
            String dir;

            if (p1TurnToMove) {
                Player p1 = gm.getPlayer1();
                int row = p1.getRow(), col = p1.getCol();
                boolean isAlien = p1 instanceof AlienPlayer;

                switch (ev.getCode()) {
                    case W: nr = row-1; nc = col;   dir = "up";    break;
                    case S: nr = row+1; nc = col;   dir = "down";  break;
                    case A: nr = row;   nc = col-1; dir = "left";  break;
                    case D: nr = row;   nc = col+1; dir = "right"; break;
                    case Q: if (!isAlien) return; nr = row-1; nc = col-1; dir = "up";   break;
                    case E: if (!isAlien) return; nr = row-1; nc = col+1; dir = "up";   break;
                    case Z: if (!isAlien) return; nr = row+1; nc = col-1; dir = "down"; break;
                    case X: if (!isAlien) return; nr = row+1; nc = col+1; dir = "down"; break;
                    default: return;
                }
                if (Math.abs(nr-row)==1 && Math.abs(nc-col)==1 && !isAlien) return;
                lastP1Dir = dir;
            } else {
                Player p2 = gm.getPlayer2();
                int row = p2.getRow(), col = p2.getCol();
                boolean isAlien = p2 instanceof AlienPlayer;

                switch (ev.getCode()) {
                    case U: nr = row-1; nc = col;   dir = "up";    break;
                    case J: nr = row+1; nc = col;   dir = "down";  break;
                    case H: nr = row;   nc = col-1; dir = "left";  break;
                    case K: nr = row;   nc = col+1; dir = "right"; break;
                    case Y: if (!isAlien) return; nr = row-1; nc = col-1; dir = "up";   break;
                    case I: if (!isAlien) return; nr = row-1; nc = col+1; dir = "up";   break;
                    case N: if (!isAlien) return; nr = row+1; nc = col-1; dir = "down"; break;
                    case M: if (!isAlien) return; nr = row+1; nc = col+1; dir = "down"; break;
                    default: return;
                }
                if (Math.abs(nr-row)==1 && Math.abs(nc-col)==1 && !isAlien) return;
                lastP2Dir = dir;
            }

            Player mover = p1TurnToMove ? gm.getPlayer1() : gm.getPlayer2();
            int prevRow = mover.getRow(), prevCol = mover.getCol();

            moveLock = true;
            gm.movePlayer(p1TurnToMove ? 1 : 2, nr, nc);

            if (mover.getRow() == prevRow && mover.getCol() == prevCol) {
                moveLock = false;
            }
        });
    }

    /**
     * Binds battle action buttons for side A (Player 1, or the side-A entity in PvP).
     */
    private void setupBattleButtonsForA() {
        if (battle == null || gm.getBattleManager() == null) return;

        Entity eA = gm.getBattleManager().getEntityA();
        Entity eB = gm.getBattleManager().getEntityB();
        Player pA = (eA instanceof Player) ? (Player) eA : null;

        String skillLabel = pA != null
            ? pA.getSkill().getSkillName() + (pA.getSkill().isReady() ? "" : " [CD:" + pA.getSkill().getRemainingCooldown() + "]")
            : "Skill";

        battle.setTurnIndicator(true, eA.getName());
        battle.showMainActionMenu();

        battle.getAttackBtn().setOnAction(e -> {
            battle.disableAllActionButtons();
            battle.playActionAnimation(true, "Attack", eA.getName(), eB.getName(), eA.getAttackPower(),
                () -> submitA(BattleAction.ATTACK));
        });
        battle.getDefendBtn().setOnAction(e -> {
            battle.disableAllActionButtons();
            battle.playDefendEffect(eA.getName());
            pause(0.5, () -> submitA(BattleAction.DEFEND));
        });
        battle.getItemBtn().setOnAction(e -> {
            if (pA == null) return;
            String[] items = itemNames(pA);
            battle.openItemSelectionMenu(items, idx -> {
                battle.disableAllActionButtons();
                String name = (items != null && idx < items.length) ? items[idx] : "item";
                pA.setSelectedItemIndex(idx);
                battle.playItemAnimation(true, name, () -> submitA(BattleAction.USE_ITEM));
            });
        });
        battle.getSkillBtn().setOnAction(e -> {
            if (pA == null) return;
            if (!pA.getSkill().isReady()) {
                battle.setLogText(pA.getSkill().getSkillName() + " cooldown อีก " + pA.getSkill().getRemainingCooldown() + " turn!");
                return;
            }
            battle.disableAllActionButtons();
            boolean isHeal = pA.getSkill().getEffect() == model.skill.SkillEffect.HEAL_SELF;
            if (isHeal) {
                battle.playItemAnimation(true, skillLabel, () -> submitA(BattleAction.USE_SKILL));
            } else {
                battle.playSkillAnimation(true, skillLabel, eA.getName(), eB.getName(), eA.getAttackPower(),
                    () -> submitA(BattleAction.USE_SKILL));
            }
        });
    }

    /**
     * Submits side A's action to the GameManager.
     * In PvP mode, sets up side B's buttons if the turn has not been resolved yet.
     *
     * @param action The chosen action
     */
    private void submitA(BattleAction action) {
        gm.submitBattleAction(1, action);
        if (isPvP && gm.getBattleManager() != null && !gm.getBattleManager().isReadyToResolve()) {
            Platform.runLater(this::setupBattleButtonsForB);
        }
    }

    /**
     * Binds battle action buttons for side B (PvP only).
     */
    private void setupBattleButtonsForB() {
        if (battle == null || gm.getBattleManager() == null) return;

        Entity eA = gm.getBattleManager().getEntityA();
        Entity eB = gm.getBattleManager().getEntityB();
        Player pB = (eB instanceof Player) ? (Player) eB : null;

        String skillLabel = pB != null
            ? pB.getSkill().getSkillName() + (pB.getSkill().isReady() ? "" : " [CD:" + pB.getSkill().getRemainingCooldown() + "]")
            : "Skill";

        battle.setTurnIndicator(false, eB.getName());
        battle.showMainActionMenu();
        battle.setLogText("");

        battle.getAttackBtn().setOnAction(e -> {
            battle.disableAllActionButtons();
            battle.playActionAnimation(false, "Attack", eB.getName(), eA.getName(), eB.getAttackPower(),
                () -> gm.submitBattleAction(2, BattleAction.ATTACK));
        });
        battle.getDefendBtn().setOnAction(e -> {
            battle.disableAllActionButtons();
            battle.playDefendEffect(eB.getName());
            pause(0.5, () -> gm.submitBattleAction(2, BattleAction.DEFEND));
        });
        battle.getItemBtn().setOnAction(e -> {
            if (pB == null) return;
            String[] items = itemNames(pB);
            battle.openItemSelectionMenu(items, idx -> {
                battle.disableAllActionButtons();
                String name = (items != null && idx < items.length) ? items[idx] : "item";
                pB.setSelectedItemIndex(idx);
                battle.playItemAnimation(false, name, () -> gm.submitBattleAction(2, BattleAction.USE_ITEM));
            });
        });
        battle.getSkillBtn().setOnAction(e -> {
            if (pB == null) return;
            if (!pB.getSkill().isReady()) {
                battle.setLogText(pB.getSkill().getSkillName() + " cooldown อีก " + pB.getSkill().getRemainingCooldown() + " turn!");
                return;
            }
            battle.disableAllActionButtons();
            boolean isHeal = pB.getSkill().getEffect() == model.skill.SkillEffect.HEAL_SELF;
            if (isHeal) {
                battle.playItemAnimation(false, skillLabel, () -> gm.submitBattleAction(2, BattleAction.USE_SKILL));
            } else {
                battle.playSkillAnimation(false, skillLabel, eB.getName(), eA.getName(), eB.getAttackPower(),
                    () -> gm.submitBattleAction(2, BattleAction.USE_SKILL));
            }
        });
    }

    /**
     * Updates the HP bars and inventories of both players on the BoardScene.
     */
    private void refreshStats() {
        if (board == null || gm.getPlayer1() == null) return;
        Player p1 = gm.getPlayer1(), p2 = gm.getPlayer2();
        board.updatePlayerStats(1, p1.getCurrentHp(), p1.getMaxHp(), p1.getAttackPower(), p1.getDefense());
        board.updatePlayerStats(2, p2.getCurrentHp(), p2.getMaxHp(), p2.getAttackPower(), p2.getDefense());
        board.updateInventory(1, itemNames(p1));
        board.updateInventory(2, itemNames(p2));
    }

    /**
     * Converts an entity to its class name string for sprite loading in the View.
     *
     * @param e Entity to convert
     * @return Class name as a String, e.g. "knight", "goblin"
     */
    private String classOf(Entity e) {
        if (e instanceof KnightPlayer) return "knight";
        if (e instanceof AlienPlayer)  return "alien";
        if (e instanceof ArcherPlayer) return "archer";
        if (e instanceof RebornPlayer) return "reborn";
        if (e instanceof Goblin)       return "goblin";
        return "knight";
    }

    /**
     * Converts a player's inventory to an array of item name strings for display in the View.
     *
     * @param player Player whose inventory to convert
     * @return Array of item names (length equals maxInventorySize; empty slots are "")
     */
    private String[] itemNames(Player player) {
        int size = player.getMaxInventorySize();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            if (i < player.getInventory().size()) {
                Item item = player.getInventory().get(i);
                if      (item instanceof HealItem)    names[i] = "hp potion";
                else if (item instanceof BuffItem)    names[i] = "atk potion";
                else if (item instanceof TomeItem)    names[i] = "spell";
                else if (item instanceof DefenseItem) names[i] = "def potion";
                else                                  names[i] = "";
            } else {
                names[i] = "";
            }
        }
        return names;
    }

    /**
     * Converts a class name string to the corresponding CharacterType enum value.
     *
     * @param name Class name, e.g. "Knight", "archer"
     * @return Matching CharacterType (default: KNIGHT)
     */
    private CharacterType toType(String name) {
        switch (name.toLowerCase()) {
            case "knight": return CharacterType.KNIGHT;
            case "archer": return CharacterType.ARCHER;
            case "reborn": return CharacterType.REBORN;
            case "alien":  return CharacterType.ALIEN;
            default:       return CharacterType.KNIGHT;
        }
    }

    /**
     * Waits for the specified delay then runs the given Runnable.
     *
     * @param seconds    Delay duration in seconds
     * @param onFinished Action to run after the delay
     */
    private void pause(double seconds, Runnable onFinished) {
        PauseTransition pt = new PauseTransition(Duration.seconds(seconds));
        pt.setOnFinished(e -> onFinished.run());
        pt.play();
    }

    /**
     * Safely stops BGM playback without throwing an exception if the player is null.
     *
     * @param player MediaPlayer to stop
     */
    private void stopBgm(javafx.scene.media.MediaPlayer player) {
        if (player != null) try { player.stop(); } catch (Exception ignored) {}
    }
}
