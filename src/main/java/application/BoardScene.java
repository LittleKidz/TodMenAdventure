package application;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.map.Cell;
import model.map.CellType;
import model.map.MapGrid;

/**
 * Map phase screen displaying the 11x11 grid along with HP bars for both players
 * and an inventory panel on each side.
 * Whenever the model changes, GameController calls methods here to update the UI.
 */
public class BoardScene {

    /** Size of each tile on the map in pixels. */
    private static final int    TILE_SIZE    = 50;

    /** Size of player/goblin sprites in pixels. */
    private static final double ENTITY_SIZE  = 65.0;

    /** X offset to centre the sprite within a tile. */
    private static final double ENTITY_X_OFF = -7.5;

    /** Y offset to centre the sprite within a tile. */
    private static final double ENTITY_Y_OFF = -10.0;

    /** Main JavaFX Scene for this screen. */
    private Scene      scene;

    /** Root container that stacks the background and layout. */
    private StackPane  mainRoot;

    /** Main layout. */
    private BorderPane rootPane;

    /** StackPane layering the gridPane and entityPane. */
    private StackPane mapStack;

    /** Grid containing all tiles. */
    private GridPane  gridPane;

    /** Layer for placing player and goblin sprites on top of the grid. */
    private Pane      entityPane;

    /** HP bar for P1. */
    private ProgressBar p1HpBar;

    /** HP bar for P2. */
    private ProgressBar p2HpBar;

    /** Stat text for P1. */
    private Text p1StatText;

    /** Stat text for P2. */
    private Text p2StatText;

    /** Inventory panel for P1 on the left side. */
    private VBox p1InventoryBox;

    /** Inventory panel for P2 on the right side. */
    private VBox p2InventoryBox;

    /** Sprite for P1 on the map. */
    private ImageView p1ImageView;

    /** Sprite for P2 on the map. */
    private ImageView p2ImageView;

    /** Text displaying the current turn. */
    private Text turnText;

    /** Text displaying the current round. */
    private Text roundText;

    /** Temporary notification text that fades out automatically after 2.5 seconds. */
    private Text notificationText;

    /** Image for normal tiles. */
    private Image normalImg;

    /** Image for rock tiles. */
    private Image stoneImg;

    /** Image for tree tiles. */
    private Image treeImg;

    /** Image for river tiles. */
    private Image riverImg;

    /** Image for chest (item) tiles. */
    private Image chestImg;

    /** Image for lava (shrinking zone) tiles. */
    private Image lavaImg;

    /** VS image for the top panel. */
    private Image vsImg;

    /** Inventory slot image. */
    private Image inventoryImg;

    /** Background image for the map phase. */
    private Image bgImg;

    /** Walking sound effect. */
    private AudioClip   walkSound;

    /** BGM media player. */
    private MediaPlayer bgmPlayer;

    /** Name of P1. */
    private final String p1Name;

    /** Name of P2. */
    private final String p2Name;

    /** Class of P1 (e.g. "knight"). */
    private final String p1Class;

    /** Class of P2. */
    private final String p2Class;

    /**
     * Creates the Board Scene with the initial map and player stat data.
     *
     * @param p1Class  Class of P1
     * @param p2Class  Class of P2.
     * @param initialMap  Initial map to display
     * @param p1Name   Name of P1.
     * @param p2Name   Name of P2.
     * @param p1Hp     Current HP of P1
     * @param p1MaxHp  Maximum HP of P1
     * @param p1Atk    ATK of P1
     * @param p1Def    DEF of P1
     * @param p2Hp     Current HP of P2
     * @param p2MaxHp  Maximum HP of P2
     * @param p2Atk    ATK of P2
     * @param p2Def    DEF of P2
     */
    public BoardScene(String p1Class, String p2Class,
                      MapGrid initialMap,
                      String p1Name, String p2Name,
                      double p1Hp, double p1MaxHp, int p1Atk, int p1Def,
                      double p2Hp, double p2MaxHp, int p2Atk, int p2Def) {
        this.p1Class = p1Class;
        this.p2Class = p2Class;
        this.p1Name  = p1Name;
        this.p2Name  = p2Name;

        mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: #000000; -fx-font-family: 'Noto Sans Thai';");

        loadImages();
        loadSounds();
        if (bgmPlayer != null) bgmPlayer.play();

        if (bgImg != null) {
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(1150); bgView.setFitHeight(750);
            bgView.setPreserveRatio(false); bgView.setOpacity(0.75);
            mainRoot.getChildren().add(bgView);
        }

        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: transparent;");

        mapStack = new StackPane();
        mapStack.setMaxSize(MapGrid.SIZE * TILE_SIZE, MapGrid.SIZE * TILE_SIZE);

        gridPane = new GridPane();
        gridPane.setHgap(0); gridPane.setVgap(0);

        entityPane = new Pane();
        entityPane.setPrefSize(MapGrid.SIZE * TILE_SIZE, MapGrid.SIZE * TILE_SIZE);

        initPlayerSprites(p1Class, p2Class);
        renderFromModel(initialMap);

        mapStack.getChildren().addAll(gridPane, entityPane);
        rootPane.setCenter(mapStack);
        rootPane.setTop(createTopPanel(p1Name, p1Class, p1Hp, p1MaxHp, p1Atk, p1Def,
                p2Name, p2Class, p2Hp, p2MaxHp, p2Atk, p2Def));

        p1InventoryBox = createInventoryPanel(p1Name + " Items", p1Class, new String[0]);
        p2InventoryBox = createInventoryPanel(p2Name + " Items", p2Class, new String[0]);
        rootPane.setLeft(p1InventoryBox);
        rootPane.setRight(p2InventoryBox);
        rootPane.setBottom(createBottomBar());

        mainRoot.getChildren().add(rootPane);

        notificationText = new Text("");
        notificationText.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #ffe066;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 6, 0.5, 0, 2);");
        notificationText.setVisible(false);
        StackPane.setAlignment(notificationText, Pos.CENTER);
        mainRoot.getChildren().add(notificationText);

        scene = new Scene(mainRoot, 1150, 750);
    }

    /** Loads all tile and UI images. */
    private void loadImages() {
        try { normalImg    = new Image(getClass().getResourceAsStream("/map/grass.png")); } catch(Exception e){}
        try { stoneImg     = new Image(getClass().getResourceAsStream("/map/rock.png"));  } catch(Exception e){}
        try { treeImg      = new Image(getClass().getResourceAsStream("/map/tree.png"));  } catch(Exception e){}
        try { riverImg     = new Image(getClass().getResourceAsStream("/map/water.png")); } catch(Exception e){}
        try { lavaImg      = new Image(getClass().getResourceAsStream("/map/lava.png"));  } catch(Exception e){}
        try { chestImg     = new Image(getClass().getResourceAsStream("/map/chest.png")); } catch(Exception e){}
        try { vsImg        = new Image(getClass().getResourceAsStream("/etc/vs.png"));          } catch(Exception e){}
        try { inventoryImg = new Image(getClass().getResourceAsStream("/etc/inventory.png"));   } catch(Exception e){}
        try { bgImg        = new Image(getClass().getResourceAsStream("/etc/background.png"));  } catch(Exception e){}
    }

    /** Load Walking sound effect and BGM */
    private void loadSounds() {
        try { walkSound = new AudioClip(getClass().getResource("/etc/walk.mp3").toExternalForm()); } catch(Exception e){}
        try {
            Media m = new Media(getClass().getResource("/etc/song.mp3").toExternalForm());
            bgmPlayer = new MediaPlayer(m);
            bgmPlayer.setVolume(0.2);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch(Exception e){}
    }

    /**
     * create sprite for P1 and P2 on map
     *
     * @param p1Class class of P1 for loading image
     * @param p2Class Class of P2 for loading image
     */
    private void initPlayerSprites(String p1Class, String p2Class) {
        p1ImageView = new ImageView();
        try { p1ImageView.setImage(new Image(getClass().getResourceAsStream(dirImg(p1Class, "stand")))); } catch(Exception e){}
        p1ImageView.setFitWidth(ENTITY_SIZE); p1ImageView.setFitHeight(ENTITY_SIZE); p1ImageView.setPreserveRatio(true);
        p1ImageView.setTranslateX(0 * TILE_SIZE + ENTITY_X_OFF);
        p1ImageView.setTranslateY(0 * TILE_SIZE + ENTITY_Y_OFF);

        p2ImageView = new ImageView();
        try { p2ImageView.setImage(new Image(getClass().getResourceAsStream(dirImg(p2Class, "stand")))); } catch(Exception e){}
        p2ImageView.setFitWidth(ENTITY_SIZE); p2ImageView.setFitHeight(ENTITY_SIZE); p2ImageView.setPreserveRatio(true);
        p2ImageView.setTranslateX(10 * TILE_SIZE + ENTITY_X_OFF);
        p2ImageView.setTranslateY(10 * TILE_SIZE + ENTITY_Y_OFF);
    }

    /**
     * Re-renders the entire map from the given model, used when the map changes (item pickup, zone shrink).
     *
     * @param map Current map from the model
     */
    private void renderFromModel(MapGrid map) {
        gridPane.getChildren().clear();
        entityPane.getChildren().clear();
        entityPane.getChildren().add(p1ImageView);
        entityPane.getChildren().add(p2ImageView);

        for (int row = 0; row < MapGrid.SIZE; row++) {
            for (int col = 0; col < MapGrid.SIZE; col++) {
                Cell cell  = map.getCell(row, col);
                CellType t = cell.getType();

                StackPane base = new StackPane();
                base.setPrefSize(TILE_SIZE, TILE_SIZE);

                ImageView baseImg = new ImageView();
                baseImg.setFitWidth(TILE_SIZE); baseImg.setFitHeight(TILE_SIZE); baseImg.setPreserveRatio(false);
                if      (t == CellType.RIVER) baseImg.setImage(riverImg);
                else if (t == CellType.LAVA)  baseImg.setImage(lavaImg);
                else                          baseImg.setImage(normalImg);
                base.getChildren().add(baseImg);

                if      (t == CellType.ROCK && stoneImg != null) base.getChildren().add(overlay(stoneImg));
                else if (t == CellType.TREE && treeImg  != null) base.getChildren().add(overlay(treeImg));
                else if (t == CellType.ITEM && chestImg != null) base.getChildren().add(overlay(chestImg));

                gridPane.add(base, col, row);

                if (t == CellType.GOBLIN && cell.getGoblin() != null) {
                    ImageView gv = new ImageView();
                    try { gv.setImage(new Image(getClass().getResourceAsStream("/character/goblin/stand.png"))); } catch(Exception e){}
                    gv.setFitWidth(ENTITY_SIZE); gv.setFitHeight(ENTITY_SIZE); gv.setPreserveRatio(true);
                    gv.setTranslateX(col * TILE_SIZE + ENTITY_X_OFF);
                    gv.setTranslateY(row * TILE_SIZE + ENTITY_Y_OFF);
                    entityPane.getChildren().add(gv);
                }
            }
        }
    }

    /**
     * Creates a tile-sized ImageView to overlay on a base tile (e.g. rock, tree).
     *
     * @param img Image to overlay
     * @return A resized ImageView
     */
    private ImageView overlay(Image img) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(TILE_SIZE); iv.setFitHeight(TILE_SIZE); iv.setPreserveRatio(false);
        return iv;
    }

    /**
     * Creates the top panel showing HP bars and stats for both players.
     */
    private BorderPane createTopPanel(String n1, String c1, double hp1, double maxHp1, int atk1, int def1,
                                      String n2, String c2, double hp2, double maxHp2, int atk2, int def2) {
        BorderPane top = new BorderPane();
        top.setPadding(new Insets(15, 30, 10, 30));
        top.setStyle("-fx-background-color: transparent;");
        top.setLeft(createProfile(n1, c1, hp1, maxHp1, atk1, def1, true));
        top.setRight(createProfile(n2, c2, hp2, maxHp2, atk2, def2, false));

        if (vsImg != null) {
            ImageView vs = new ImageView(vsImg);
            vs.setFitHeight(55); vs.setPreserveRatio(true);
            BorderPane.setAlignment(vs, Pos.CENTER);
            top.setCenter(vs);
        }
        return top;
    }

    /**
     * Creates a profile card for one player (avatar + name + HP bar + stats).
     *
     * @param name   Player name
     * @param cls    Character class
     * @param hp     Current HP
     * @param maxHp  Maximum HP
     * @param atk    Attack power
     * @param def    Defense
     * @param isLeft {@code true} if this is P1 (left side)
     * @return The profile card as an HBox
     */
    private HBox createProfile(String name, String cls, double hp, double maxHp, int atk, int def, boolean isLeft) {
        HBox box = new HBox(15); box.setAlignment(Pos.CENTER);

        ImageView ci = new ImageView();
        try { ci.setImage(new Image(getClass().getResourceAsStream(dirImg(cls, "stand")))); } catch(Exception e){}
        ci.setFitWidth(65); ci.setFitHeight(65); ci.setPreserveRatio(true);

        VBox info = new VBox(4);
        info.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Text nm = new Text(name + " (" + cls + ")");
        nm.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: white;");

        ProgressBar bar = new ProgressBar(hp / maxHp);
        bar.setPrefWidth(360); bar.setPrefHeight(16);
        bar.setStyle("-fx-accent: #ff4444; -fx-control-inner-background: #555;");

        Text st = new Text("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        st.setStyle("-fx-font-size: 13px; -fx-fill: #cccccc;");

        if (isLeft) { p1HpBar = bar; p1StatText = st; }
        else        { p2HpBar = bar; p2StatText = st; }

        info.getChildren().addAll(nm, bar, st);
        if (isLeft) box.getChildren().addAll(ci, info);
        else        box.getChildren().addAll(info, ci);
        return box;
    }

    /**
     * Creates the bottom bar displaying the current turn and round.
     *
     * @return The bottom bar as an HBox
     */
    private HBox createBottomBar() {
        HBox bar = new HBox(40);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(6, 20, 10, 20));
        bar.setStyle("-fx-background-color: rgba(0,0,0,0.55);");

        turnText  = new Text("P1 Turn (WASD)");
        turnText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #00ffcc;");

        roundText = new Text("Round 0");
        roundText.setStyle("-fx-font-size: 16px; -fx-fill: #aaaaaa;");

        bar.getChildren().addAll(turnText, roundText);
        return bar;
    }

    /**
     * Creates an inventory panel for one player showing slots up to maxInventorySize.
     *
     * @param title Panel title
     * @param cls   Player's class (determines the number of slots)
     * @param items Array of item names currently held
     * @return The inventory panel as a VBox
     */
    private VBox createInventoryPanel(String title, String cls, String[] items) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20)); box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: transparent;");

        Text t = new Text(title);
        t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #ffcc00;");
        box.getChildren().add(t);

        int slots = cls.toLowerCase().equals("reborn") ? 5 : 4;
        for (int i = 0; i < slots; i++) {
            StackPane slot = new StackPane();
            if (inventoryImg != null) {
                ImageView bg = new ImageView(inventoryImg);
                bg.setFitWidth(65); bg.setFitHeight(65); bg.setPreserveRatio(true);
                slot.getChildren().add(bg);
            }
            if (items != null && i < items.length && items[i] != null && !items[i].trim().isEmpty()) {
                try {
                    ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/potion_spell/" + items[i] + ".png")));
                    iv.setFitWidth(45); iv.setFitHeight(45); iv.setPreserveRatio(true);
                    slot.getChildren().add(iv);
                } catch(Exception e) {
                    Text ft = new Text(items[i]); ft.setStyle("-fx-fill: white; -fx-font-size: 10px;");
                    slot.getChildren().add(ft);
                }
            } else {
                Text et = new Text("Empty"); et.setStyle("-fx-fill: #999; -fx-font-size: 11px;");
                slot.getChildren().add(et);
            }
            box.getChildren().add(slot);
        }
        return box;
    }

    /**
     * Resolves the image path for a character based on class name and action.
     *
     * @param cls    Class name
     * @param action Action name, e.g. "stand", "atk"
     * @return Resource path for the image
     */
    private String dirImg(String cls, String action) {
        String f = cls.toLowerCase();
        if (f.equals("swordsman"))  f = "knight";
        if (f.equals("reincarnated") || f.equals("newborn")) f = "reborn";
        return "/character/" + f + "/" + action + ".png";
    }

    // ── Public API ──────────────────────────────────────────────

    /**
     * Returns the Scene for this screen.
     *
     * @return The Board Scene
     */
    public Scene getScene()           { return scene; }

    /**
     * Returns the BGM MediaPlayer.
     *
     * @return The MediaPlayer
     */
    public MediaPlayer getBgmPlayer() { return bgmPlayer; }

    /**
     * Moves a player's sprite to a new position with animation and sound.
     *
     * @param playerNum  Player number (1 or 2)
     * @param targetCol  Destination column
     * @param targetRow  Destination row
     * @param direction  Direction used to select the sprite, e.g. "up", "down", "left", "right"
     */
    public void movePlayer(int playerNum, int targetCol, int targetRow, String direction) {
        ImageView pv  = (playerNum == 1) ? p1ImageView : p2ImageView;
        String    cls = (playerNum == 1) ? p1Class : p2Class;
        if (pv == null) return;
        if (walkSound != null) walkSound.play();
        try { pv.setImage(new Image(getClass().getResourceAsStream(dirImg(cls, direction)))); } catch(Exception e){}

        TranslateTransition tt = new TranslateTransition(Duration.millis(280), pv);
        tt.setToX(targetCol * TILE_SIZE + ENTITY_X_OFF);
        tt.setToY(targetRow * TILE_SIZE + ENTITY_Y_OFF);
        tt.setOnFinished(e -> {
            try { pv.setImage(new Image(getClass().getResourceAsStream(dirImg(cls, "stand")))); } catch(Exception ex){}
        });
        tt.play();
    }

    /**
     * Updates the HP bar and stat text for a player.
     *
     * @param playerNum Player number (1 or 2)
     * @param hp        Current HP
     * @param maxHp     Maximum HP
     * @param atk       Attack power
     * @param def       Defense
     */
    public void updatePlayerStats(int playerNum, double hp, double maxHp, int atk, int def) {
        if (playerNum == 1) {
            if (p1HpBar != null)    p1HpBar.setProgress(hp / maxHp);
            if (p1StatText != null) p1StatText.setText("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        } else {
            if (p2HpBar != null)    p2HpBar.setProgress(hp / maxHp);
            if (p2StatText != null) p2StatText.setText("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        }
    }

    /**
     * Updates a player's inventory panel with the latest item list.
     *
     * @param playerNum Player number (1 or 2)
     * @param items     Array of current item names
     */
    public void updateInventory(int playerNum, String[] items) {
        if (playerNum == 1) {
            p1InventoryBox = createInventoryPanel(p1Name + " Items", p1Class, items);
            rootPane.setLeft(p1InventoryBox);
        } else {
            p2InventoryBox = createInventoryPanel(p2Name + " Items", p2Class, items);
            rootPane.setRight(p2InventoryBox);
        }
    }

    /**
     * Re-renders the entire map from the model, used when the map changes.
     *
     * @param map Current map from the model
     */
    public void refreshMapFromModel(MapGrid map) {
        renderFromModel(map);
    }

    /**
     * Updates the turn text at the bottom.
     *
     * @param text Text to display, e.g. "P1 Turn (WASD)"
     */
    public void setTurnText(String text) {
        if (turnText != null) {
            turnText.setText(text);
            boolean isP1 = text.startsWith("P1");
            turnText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: "
                    + (isP1 ? "#00ffcc" : "#ff8866") + ";");
        }
    }

    /**
     * Updates the round text at the bottom.
     *
     * @param text Text to display, e.g. "Round 3"
     */
    public void setRoundText(String text) {
        if (roundText != null) roundText.setText(text);
    }

    /**
     * Shows a temporary on-screen notification that disappears automatically after 2.5 seconds.
     *
     * @param msg Message to display
     */
    public void showNotification(String msg) {
        if (notificationText == null) return;
        notificationText.setText(msg);
        notificationText.setVisible(true);
        PauseTransition pt = new PauseTransition(Duration.seconds(2.5));
        pt.setOnFinished(e -> notificationText.setVisible(false));
        pt.play();
    }
}
