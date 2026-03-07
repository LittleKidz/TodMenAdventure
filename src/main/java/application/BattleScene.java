package application;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.function.Consumer;

/**
 * Battle phase screen displaying sprites for both sides, HP bars, and action buttons.
 * GameController calls methods here to play animations and update the UI after each turn.
 */
public class BattleScene {

    /** X position of fighter 1's sprite */
    private final double F1_POS_X = 110;

    /** Y position of fighter 1's sprite */
    private final double F1_POS_Y = 180;

    /** X position of fighter 2's sprite */
    private final double F2_POS_X = 740;

    /** Y position of fighter 2's sprite */
    private final double F2_POS_Y = 180;

    /** Main JavaFX Scene for this screen. */
    private Scene      scene;

    /** Root container. */
    private StackPane  mainRoot;

    /** Main layout. */
    private BorderPane rootPane;

    /** HBox holding the action buttons at the bottom */
    private HBox       actionMenuBox;

    /** HP bar for fighter 1. */
    private ProgressBar f1HpBar;

    /** HP bar for fighter 2. */
    private ProgressBar f2HpBar;

    /** Stat text for fighter 1 */
    private Text f1StatText;

    /** Stat text for fighter 2 */
    private Text f2StatText;

    /** Sprite for fighter 1 */
    private ImageView f1ImgView;

    /** Sprite for fighter 2 */
    private ImageView f2ImgView;

    /** Battle log text displayed at the center of the screen */
    private Text battleLogText;

    /** Text indicating whose turn it is */
    private Text turnIndicatorText;

    /** Attack Button */
    private Button attackBtn;

    /** Use Skill button */
    private Button skillBtn;

    /** Use Item button */
    private Button itemBtn;

    /** Defend button */
    private Button defendBtn;

    /** fighter 1 name */
    private String f1Name;

    /** Class of fighter 1, e.g. "knight", "archer" */
    private String f1Class;

    /** Current HP of fighter 1 */
    private double f1Hp;

    /** Maximum HP of fighter 1 */
    private double f1MaxHp;

    /** Attack power of fighter 1 */
    private int f1Atk;

    /** Defense of fighter 1 */
    private int f1Def;

    /** Fighter 2 name */
    private String f2Name;

    /** Class of fighter 2, e.g. "goblin", "alien" */
    private String f2Class;

    /** Current HP of fighter 2 */
    private double f2Hp;

    /** Maximum HP of fighter 2 */
    private double f2MaxHp;

    /** Attack power of fighter 2 */
    private int f2Atk;

    /** Defense of fighter 2 */
    private int f2Def;

    /** background battle scene */
    private Image bgImg;

    /** VS image displayed at the center of the screen */
    private Image vsImg;

    /** Inventory slot image */
    private Image inventoryImg;

    /** Sound effect played on attack */
    private AudioClip atkSound;

    /** Sound effect played when drinking a potion */
    private AudioClip potionSound;

    /** BGM media player for the battle */
    private MediaPlayer bgmPlayer;

    /**
     * Creates the Battle Scene with data for both fighters.
     *
     * @param f1Name   Name of fighter 1
     * @param f1Class  Class of fighter 1
     * @param f1Hp     Current HP of fighter 1
     * @param f1MaxHp  Maximum HP of fighter 1
     * @param f1Atk    ATK of fighter 1
     * @param f1Def    DEF of fighter 1
     * @param f2Name   Name of fighter 2
     * @param f2Class  Class of fighter 2
     * @param f2Hp     Current HP of fighter 2
     * @param f2MaxHp  Maximum HP of fighter 2
     * @param f2Atk    ATK of fighter 2
     * @param f2Def    DEF of fighter 2
     */
    public BattleScene(String f1Name, String f1Class,
                       double f1Hp, double f1MaxHp, int f1Atk, int f1Def,
                       String f2Name, String f2Class,
                       double f2Hp, double f2MaxHp, int f2Atk, int f2Def) {
        this.f1Name = f1Name; this.f1Class = f1Class;
        this.f1Hp   = f1Hp;   this.f1MaxHp = f1MaxHp;
        this.f1Atk  = f1Atk;  this.f1Def   = f1Def;
        this.f2Name = f2Name; this.f2Class = f2Class;
        this.f2Hp   = f2Hp;   this.f2MaxHp = f2MaxHp;
        this.f2Atk  = f2Atk;  this.f2Def   = f2Def;
        buildUI();
    }

    /** Builds all UI components for the battle screen. */
    private void buildUI() {
        mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: #000000; -fx-font-family: 'Noto Sans Thai';");

        loadImages();
        loadSounds();

        if (bgImg != null) {
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(1150); bgView.setFitHeight(750); bgView.setPreserveRatio(false);
            mainRoot.getChildren().add(bgView);
        }

        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: transparent;");
        rootPane.setTop(createTopPanel());
        rootPane.setCenter(createCenterBattle());

        VBox bottomBox = new VBox(5);
        bottomBox.setAlignment(Pos.CENTER);

        turnIndicatorText = new Text();
        setTurnIndicator(true, f1Name);

        bottomBox.getChildren().addAll(turnIndicatorText, createBattleLog(), createActionMenuContainer());
        rootPane.setBottom(bottomBox);

        mainRoot.getChildren().add(rootPane);
        scene = new Scene(mainRoot, 1150, 750);
    }

    /** Loads background and UI images. */
    private void loadImages() {
        try { bgImg        = new Image(getClass().getResourceAsStream("/etc/fight_scene.jpg")); } catch(Exception e){}
        try { vsImg        = new Image(getClass().getResourceAsStream("/etc/vs.png"));          } catch(Exception e){}
        try { inventoryImg = new Image(getClass().getResourceAsStream("/etc/inventory.png"));   } catch(Exception e){}
    }

    /** Loads sound effects and BGM. */
    private void loadSounds() {
        try { atkSound    = new AudioClip(getClass().getResource("/battlescene/atk_sound.mp3").toExternalForm());    } catch(Exception e){}
        try { potionSound = new AudioClip(getClass().getResource("/battlescene/drink_potion.mp3").toExternalForm()); } catch(Exception e){}
        try {
            Media m = new Media(getClass().getResource("/battlescene/opening_battle_scene.mp3").toExternalForm());
            bgmPlayer = new MediaPlayer(m);
            bgmPlayer.setVolume(0.3);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch(Exception e){}
    }

    /**
     * Resolves the resource path for a character sprite based on class name and action.
     *
     * @param cls    Class name
     * @param action Action name, e.g. "stand", "atk", "skill"
     * @return Resource path for the image
     */
    private String charImg(String cls, String action) {
        String f = cls.toLowerCase();
        if (f.equals("swordsman"))  f = "knight";
        if (f.equals("reincarnated") || f.equals("newborn")) f = "reborn";
        if (f.equals("monster"))    f = "goblin";
        return "/character/" + f + "/" + action + ".png";
    }

    /**
     * Creates the top panel showing HP bars and stats for both fighters.
     *
     * @return The top panel as a BorderPane
     */
    private BorderPane createTopPanel() {
        BorderPane top = new BorderPane();
        top.setPadding(new Insets(15, 30, 15, 30));
        top.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: #444; -fx-border-width: 0 0 2 0;");
        top.setLeft(createFighterProfile(f1Name, f1Class, f1Hp, f1MaxHp, f1Atk, f1Def, true));
        top.setRight(createFighterProfile(f2Name, f2Class, f2Hp, f2MaxHp, f2Atk, f2Def, false));
        if (vsImg != null) {
            ImageView vs = new ImageView(vsImg); vs.setFitHeight(60); vs.setPreserveRatio(true);
            BorderPane.setAlignment(vs, Pos.CENTER);
            top.setCenter(vs);
        }
        return top;
    }

    /**
     * Creates a profile card for one fighter in the top panel.
     *
     * @param name   Display name
     * @param cls    Character class
     * @param hp     Current HP
     * @param maxHp  Maximum HP
     * @param atk    Attack power
     * @param def    Defense
     * @param isLeft {@code true} if this is fighter 1 (left side)
     * @return The profile card as an HBox
     */
    private HBox createFighterProfile(String name, String cls, double hp, double maxHp, int atk, int def, boolean isLeft) {
        HBox box = new HBox(15); box.setAlignment(Pos.CENTER);

        ImageView ci = new ImageView();
        try { ci.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "stand")))); } catch(Exception e){}
        ci.setFitWidth(70); ci.setFitHeight(70); ci.setPreserveRatio(true);

        VBox info = new VBox(5); info.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Text nm = new Text(name + " (" + cls + ")");
        nm.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: white;");

        ProgressBar bar = new ProgressBar(maxHp > 0 ? hp / maxHp : 0);
        bar.setPrefWidth(350); bar.setPrefHeight(18);
        bar.setStyle("-fx-accent: #ff4444; -fx-control-inner-background: #555;");

        Text st = new Text("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        st.setStyle("-fx-font-size: 14px; -fx-fill: #cccccc;");

        if (isLeft)  { f1HpBar = bar; f1StatText = st; }
        else         { f2HpBar = bar; f2StatText = st; }

        info.getChildren().addAll(nm, bar, st);
        if (isLeft) box.getChildren().addAll(ci, info);
        else        box.getChildren().addAll(info, ci);
        return box;
    }

    /**
     * Creates the center pane containing sprites for both fighters facing each other.
     *
     * @return The center pane with both sprites
     */
    private Pane createCenterBattle() {
        Pane pane = new Pane();

        f1ImgView = new ImageView();
        try { f1ImgView.setImage(new Image(getClass().getResourceAsStream(charImg(f1Class, "stand")))); } catch(Exception e){}
        f1ImgView.setFitHeight(280); f1ImgView.setPreserveRatio(true);
        f1ImgView.setStyle("-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.6),15,0.5,0,10);");
        f1ImgView.setLayoutX(F1_POS_X); f1ImgView.setLayoutY(F1_POS_Y);

        f2ImgView = new ImageView();
        try { f2ImgView.setImage(new Image(getClass().getResourceAsStream(charImg(f2Class, "stand")))); } catch(Exception e){}
        f2ImgView.setFitHeight(280); f2ImgView.setPreserveRatio(true);
        f2ImgView.setStyle("-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.6),15,0.5,0,10);");
        f2ImgView.setScaleX(-1);
        f2ImgView.setLayoutX(F2_POS_X); f2ImgView.setLayoutY(F2_POS_Y);

        pane.getChildren().addAll(f1ImgView, f2ImgView);
        return pane;
    }

    /**
     * Creates the battle log box that displays the result of the latest turn.
     *
     * @return The battle log as a StackPane
     */
    private StackPane createBattleLog() {
        StackPane log = new StackPane();
        log.setPadding(new Insets(10, 20, 10, 20));
        log.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;");
        log.setMinHeight(50);
        battleLogText = new Text("");
        battleLogText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #ffcc00;");
        log.getChildren().add(battleLogText);
        return log;
    }

    /**
     * Creates the action menu container with all 4 buttons (attack, defend, item, skill).
     *
     * @return The action menu as an HBox
     */
    private HBox createActionMenuContainer() {
        actionMenuBox = new HBox(30);
        actionMenuBox.setPadding(new Insets(10, 30, 20, 30));
        actionMenuBox.setAlignment(Pos.CENTER);
        actionMenuBox.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-border-color: #555; -fx-border-width: 3 0 0 0;");
        actionMenuBox.setMinHeight(120);

        attackBtn = createImgBtn("/battlescene/atk_btn.png");
        defendBtn = createImgBtn("/battlescene/def_btn.png");
        itemBtn   = createImgBtn("/battlescene/item_btn.png");
        skillBtn  = createImgBtn("/battlescene/skill_btn.png");

        showMainActionMenu();
        return actionMenuBox;
    }

    /**
     * Creates an image-based button with a hover effect.
     *
     * @param path Resource path for the button image
     * @return A Button using an image instead of text
     */
    private Button createImgBtn(String path) {
        Button btn = new Button();
        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
            iv.setFitWidth(180); iv.setPreserveRatio(true);
            btn.setGraphic(iv);
        } catch(Exception e) { btn.setText("BTN"); }
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.7));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    // ── Public API ──────────────────────────────────────────────

    /** @return The Scene for the battle screen */
    public Scene getScene()           { return scene; }

    /** @return The BGM MediaPlayer for the battle */
    public MediaPlayer getBgmPlayer() { return bgmPlayer; }

    /** @return The Attack button */
    public Button getAttackBtn()      { return attackBtn; }

    /** @return Defend button */
    public Button getDefendBtn()      { return defendBtn; }

    /** @return Use Item button */
    public Button getItemBtn()        { return itemBtn; }

    /** @return Use Skill button */
    public Button getSkillBtn()       { return skillBtn; }

    /**
     * Shows all 4 main action buttons.
     */
    public void showMainActionMenu() {
        actionMenuBox.getChildren().setAll(attackBtn, defendBtn, itemBtn, skillBtn);
    }

    /**
     * Hides all action buttons while an animation is playing.
     */
    public void disableAllActionButtons() {
        actionMenuBox.getChildren().clear();
    }

    /**
     * Updates the turn indicator text with the appropriate color for the active side.
     *
     * @param isF1  {@code true} if it is fighter 1's turn
     * @param name  Name of the active fighter
     */
    public void setTurnIndicator(boolean isF1, String name) {
        if (turnIndicatorText == null) return;
        turnIndicatorText.setText("▶ " + name + "'s Turn ◀");
        turnIndicatorText.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: "
                + (isF1 ? "#00ffcc" : "#ff6666")
                + "; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.8),4,0.5,2,2);");
    }

    /**
     * Sets the battle log text.
     *
     * @param msg Message to display
     */
    public void setLogText(String msg) {
        if (battleLogText != null) battleLogText.setText(msg);
    }

    /**
     * Updates the HP bar and stat text for the specified fighter.
     *
     * @param isF1  {@code true} to update fighter 1
     * @param hp    Current HP
     * @param maxHp Maximum HP
     * @param atk   Attack power
     * @param def   Defense
     */
    public void updateHP(boolean isF1, double hp, double maxHp, int atk, int def) {
        if (isF1) {
            if (f1HpBar != null)    f1HpBar.setProgress(maxHp > 0 ? hp / maxHp : 0);
            if (f1StatText != null) f1StatText.setText("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        } else {
            if (f2HpBar != null)    f2HpBar.setProgress(maxHp > 0 ? hp / maxHp : 0);
            if (f2StatText != null) f2StatText.setText("HP: " + (int)hp + "/" + (int)maxHp + "   ATK: " + atk + "   DEF: " + def);
        }
    }

    /**
     * Displays a message indicating the fighter is taking a defensive stance.
     *
     * @param name Name of the defending fighter
     */
    public void playDefendEffect(String name) {
        setLogText(name + " takes a defensive stance!");
    }

    /**
     * Plays a normal attack animation: swaps the sprite, shakes the target, then invokes the callback when done.
     *
     * @param isF1          {@code true} if fighter 1 is attacking
     * @param actionType    Action name shown in the log
     * @param attackerName  Name of the attacker
     * @param defenderName  Name of the defender
     * @param dmgAmount     Damage dealt
     * @param onFinished    Callback invoked after the animation finishes
     */
    public void playActionAnimation(boolean isF1, String actionType,
                                    String attackerName, String defenderName,
                                    int dmgAmount, Runnable onFinished) {
        ImageView atk = isF1 ? f1ImgView : f2ImgView;
        ImageView def = isF1 ? f2ImgView : f1ImgView;
        String    cls = isF1 ? f1Class   : f2Class;

        if (atkSound != null) atkSound.play();
        try { atk.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "atk")))); } catch(Exception e){}
        setLogText(attackerName + " used " + actionType + "! " + defenderName + " took " + dmgAmount + " damage.");

        TranslateTransition shake = new TranslateTransition(Duration.millis(50), def);
        shake.setByX(isF1 ? 15 : -15); shake.setCycleCount(6); shake.setAutoReverse(true);
        def.setOpacity(0.5); shake.play();

        PauseTransition delay = new PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> {
            def.setOpacity(1.0);
            try { atk.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "stand")))); } catch(Exception ex){}
            if (onFinished != null) onFinished.run();
        });
        delay.play();
    }

    /**
     * Plays a skill animation, handling attack skills and heal skills separately.
     *
     * @param isF1          {@code true} if fighter 1 is using the skill
     * @param skillName     Name of the skill
     * @param attackerName  Name of the skill user
     * @param defenderName  Name of the target
     * @param dmgAmount     Damage dealt (ignored for heal skills)
     * @param onFinished    Callback invoked after the animation finishes
     */
    public void playSkillAnimation(boolean isF1, String skillName,
                                   String attackerName, String defenderName,
                                   int dmgAmount, Runnable onFinished) {
        ImageView atk = isF1 ? f1ImgView : f2ImgView;
        ImageView def = isF1 ? f2ImgView : f1ImgView;
        String    cls = isF1 ? f1Class   : f2Class;

        if (atkSound != null) atkSound.play();
        try { atk.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "skill")))); } catch(Exception e){}

        boolean isHealSkill = cls.toLowerCase().equals("reborn")
                || skillName.toLowerCase().contains("heal");

        if (isHealSkill) {
            setLogText(attackerName + " กำลังร่ายเวทย์ " + skillName + " ใส่ตัวเอง!");
            TranslateTransition floatAnim = new TranslateTransition(Duration.millis(150), atk);
            floatAnim.setByY(-10); floatAnim.setCycleCount(4); floatAnim.setAutoReverse(true);
            floatAnim.play();

            PauseTransition delay = new PauseTransition(Duration.millis(600));
            delay.setOnFinished(e -> {
                try { atk.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "stand")))); } catch(Exception ex){}
                if (onFinished != null) onFinished.run();
            });
            delay.play();
        } else {
            setLogText(attackerName + " กำลังร่าย " + skillName + " โจมตีใส่ " + defenderName + "!");
            TranslateTransition shake = new TranslateTransition(Duration.millis(50), def);
            shake.setByX(isF1 ? 15 : -15); shake.setCycleCount(6); shake.setAutoReverse(true);
            def.setOpacity(0.5); shake.play();

            PauseTransition delay = new PauseTransition(Duration.millis(600));
            delay.setOnFinished(e -> {
                def.setOpacity(1.0);
                try { atk.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "stand")))); } catch(Exception ex){}
                if (onFinished != null) onFinished.run();
            });
            delay.play();
        }
    }

    /**
     * Plays an item-use animation: switches to the potion-drinking pose, then invokes the callback.
     *
     * @param isF1       {@code true} if fighter 1 is using the item
     * @param itemName   Name of the item
     * @param onFinished Callback invoked after the animation finishes
     */
    public void playItemAnimation(boolean isF1, String itemName, Runnable onFinished) {
        ImageView userImg = isF1 ? f1ImgView : f2ImgView;
        String    cls     = isF1 ? f1Class   : f2Class;
        String    name    = isF1 ? f1Name    : f2Name;

        if (potionSound != null) potionSound.play();
        try { userImg.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "potion")))); } catch(Exception e){}
        setLogText(name + " used " + itemName + "!");

        PauseTransition delay = new PauseTransition(Duration.millis(800));
        delay.setOnFinished(e -> {
            try { userImg.setImage(new Image(getClass().getResourceAsStream(charImg(cls, "stand")))); } catch(Exception ex){}
            if (onFinished != null) onFinished.run();
        });
        delay.play();
    }

    /**
     * Switches the action menu to display item selection slots from the inventory.
     * If the inventory is empty, a warning message is shown instead.
     *
     * @param items         Array of item names in the inventory
     * @param onItemClicked Callback that receives the index of the selected item
     */
    public void openItemSelectionMenu(String[] items, Consumer<Integer> onItemClicked) {
        boolean hasItem = false;
        if (items != null) for (String it : items) if (it != null && !it.trim().isEmpty()) { hasItem = true; break; }

        if (!hasItem) { setLogText("Inventory is empty!"); return; }

        actionMenuBox.getChildren().clear();

        Button back = new Button("BACK");
        back.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-pref-height: 65;");
        back.setOnAction(e -> showMainActionMenu());
        actionMenuBox.getChildren().add(back);

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].trim().isEmpty()) continue;
            StackPane slot = new StackPane(); slot.setStyle("-fx-cursor: hand;");
            if (inventoryImg != null) {
                ImageView bg = new ImageView(inventoryImg); bg.setFitWidth(65); bg.setFitHeight(65); bg.setPreserveRatio(true);
                slot.getChildren().add(bg);
            }
            try {
                ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("/potion_spell/" + items[i] + ".png")));
                iv.setFitWidth(45); iv.setFitHeight(45); iv.setPreserveRatio(true);
                slot.getChildren().add(iv);
            } catch(Exception e){}

            slot.setOnMouseEntered(e -> slot.setOpacity(0.7));
            slot.setOnMouseExited(e  -> slot.setOpacity(1.0));
            final int idx = i;
            slot.setOnMouseClicked(e -> { if (onItemClicked != null) onItemClicked.accept(idx); });
            actionMenuBox.getChildren().add(slot);
        }
    }
}
