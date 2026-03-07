package application;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.function.BiConsumer;

/**
 * Character selection screen for both players.
 * P1 selects first and confirms with Enter, then P2 selects.
 * Supports both keyboard (A/D to move, Enter to confirm) and mouse click.
 */
public class CharacterSelectScene {

    /** Width of each character image. */
    private final double CHAR_WIDTH = 180;

    /** Spacing between characters. */
    private final double CHAR_SPACING = 30;

    /** Width of the selection arrow. */
    private final double ARROW_WIDTH = 60;

    /** Y position of the arrow. */
    private final double ARROW_Y_POS = 320;

    /** Main JavaFX Scene for this screen. */
    private Scene scene;

    /** Root layout for this screen. */
    private StackPane mainRoot;

    /** Main layout. */
    private BorderPane rootPane;

    /** Class names for all 4 characters. */
    private final String[] characterClasses = {"Knight", "Archer", "Reborn", "Alien"};

    /** Stat descriptions for each character shown in the info box. */
    private final String[] characterInfos = {
            "Knight\nHP: 120  ATK: 18  DEF: 7\nSkill: Slash\nเดิน: ปกติ",
            "Archer\nHP: 85  ATK: 24  DEF: 3\nSkill: Strike\nเดิน: ผ่านต้นไม้ได้",
            "Reborn\nHP: 100  ATK: 16  DEF: 5\nSkill: Heal\nเดิน: ผ่านแม่น้ำได้",
            "Alien\nHP: 70  ATK: 20  DEF: 2\nSkill: Fireball\nเดิน: ทแยงได้"
    };

    /** ImageViews for all 4 character images. */
    private ImageView[] characterImageViews;

    /** Wrapper pane for P1's selection arrow. */
    private Pane p1ArrowWrapper;

    /** Wrapper pane for P2's selection arrow. */
    private Pane p2ArrowWrapper;

    /** Confirm button for P1. */
    private Button confirmP1Btn;

    /** Confirm button for P2. */
    private Button confirmP2Btn;

    /** Container for the confirm buttons at the bottom. */
    private StackPane bottomButtonContainer;

    /** Background music player. */
    private MediaPlayer bgmPlayer;

    /** Callback invoked when both players have confirmed their characters; receives P1's and P2's classes. */
    private BiConsumer<String, String> onGameStartHandler;

    /** Box displaying the stat info of the currently highlighted character. */
    private javafx.scene.layout.VBox infoBox;

    /** Stat text inside the info box. */
    private javafx.scene.text.Text infoText;

    /** Whether it is currently P1's turn to select. */
    private boolean isP1Turn = true;

    /** Index of the character currently selected by P1 (0-3). */
    private int p1SelectedIndex = 0;

    /** Index of the character currently selected by P2 (0-3). */
    private int p2SelectedIndex = 0;

    /**
     * Creates the character selection screen with all UI components and sets up keyboard/mouse listeners.
     */
    public CharacterSelectScene() {
        mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: #000000;");

        try {
            ImageView bgView = new ImageView(new Image(getClass().getResourceAsStream("/characterselectscene/background.png")));
            bgView.setFitWidth(1150);
            bgView.setFitHeight(750);
            bgView.setPreserveRatio(false);
            mainRoot.getChildren().add(bgView);
        } catch (Exception e) { }

        try {
            Media bgmMedia = new Media(getClass().getResource("/etc/song_background.mp3").toExternalForm());
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setVolume(0.2);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {}

        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: transparent;");
        rootPane.setTop(createTopChoose());
        rootPane.setCenter(createCenterCharacters());
        rootPane.setBottom(createBottomButtons());
        mainRoot.getChildren().add(rootPane);
        scene = new Scene(mainRoot, 1150, 750);
        setupController();
    }

    /**
     * Creates the top header section showing the "Choose your character" image.
     *
     * @return The header as an HBox
     */
    private HBox createTopChoose() {
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30, 0, 0, 0));
        try {
            ImageView chooseImg = new ImageView(new Image(getClass().getResourceAsStream("/characterselectscene/choose.png")));
            chooseImg.setFitHeight(100);
            chooseImg.setPreserveRatio(true);
            topBox.getChildren().add(chooseImg);
        } catch (Exception e) {}
        return topBox;
    }

    /**
     * Creates the center section displaying all 4 character images, selection arrows, and the stat info box.
     *
     * @return The center section as a Pane
     */
    private Pane createCenterCharacters() {
        StackPane centerStack = new StackPane();
        centerStack.setAlignment(Pos.CENTER);

        HBox characterBox = new HBox(CHAR_SPACING);
        characterBox.setAlignment(Pos.CENTER);
        characterImageViews = new ImageView[4];

        String[] charFiles = {"knight_select.png", "archer_select.png", "reborn_select.png", "alien_select.png"};
        for (int i = 0; i < 4; i++) {
            ImageView charImg = new ImageView();
            try { charImg.setImage(new Image(getClass().getResourceAsStream("/characterselectscene/" + charFiles[i]))); } catch (Exception e) {}
            charImg.setFitWidth(CHAR_WIDTH);
            charImg.setPreserveRatio(true);
            charImg.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0.5, 0, 5);");
            characterImageViews[i] = charImg;
            characterBox.getChildren().add(charImg);
        }

        Pane arrowPane = new Pane();
        arrowPane.setMaxSize(810, 400);

        p1ArrowWrapper = createArrowWrapper("/characterselectscene/selectP1.png");
        p2ArrowWrapper = createArrowWrapper("/characterselectscene/selectP2.png");
        p2ArrowWrapper.setVisible(false);

        infoText = new javafx.scene.text.Text(characterInfos[0]);
        infoText.setStyle("-fx-font-size: 13px; -fx-fill: #ffffff;");
        infoText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        infoBox = new javafx.scene.layout.VBox(infoText);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-background-radius: 8; -fx-padding: 8 16 8 16;");
        infoBox.setPrefWidth(230);
        infoBox.setMinWidth(230);
        infoBox.setMaxWidth(230);

        arrowPane.getChildren().addAll(p1ArrowWrapper, p2ArrowWrapper, infoBox);
        centerStack.getChildren().addAll(characterBox, arrowPane);
        return centerStack;
    }

    /**
     * Creates an arrow wrapper with a floating animation.
     *
     * @param path Path to the arrow image (P1 or P2)
     * @return A Pane containing the floating arrow
     */
    private Pane createArrowWrapper(String path) {
        ImageView arrowImg = new ImageView();
        try { arrowImg.setImage(new Image(getClass().getResourceAsStream(path))); } catch (Exception e) {}
        arrowImg.setFitWidth(ARROW_WIDTH);
        arrowImg.setPreserveRatio(true);

        TranslateTransition floatAnim = new TranslateTransition(Duration.millis(600), arrowImg);
        floatAnim.setByY(-15);
        floatAnim.setCycleCount(TranslateTransition.INDEFINITE);
        floatAnim.setAutoReverse(true);
        floatAnim.play();

        Pane wrapper = new Pane(arrowImg);
        wrapper.setLayoutY(ARROW_Y_POS);
        return wrapper;
    }

    /**
     * Creates the bottom Confirm button section; initially shows P1's button and switches to P2's after P1 confirms.
     *
     * @return A StackPane containing both confirm buttons
     */
    private StackPane createBottomButtons() {
        bottomButtonContainer = new StackPane();
        bottomButtonContainer.setPadding(new Insets(0, 0, 50, 0));

        confirmP1Btn = createImageButton("/characterselectscene/confirmP1.png");
        confirmP2Btn = createImageButton("/characterselectscene/confirmP2.png");
        confirmP2Btn.setVisible(false);

        bottomButtonContainer.getChildren().addAll(confirmP1Btn, confirmP2Btn);
        return bottomButtonContainer;
    }

    /**
     * Creates an image-based button with a hover effect.
     *
     * @param imagePath Path to the button image
     * @return A Button using an image instead of text
     */
    private Button createImageButton(String imagePath) {
        Button btn = new Button();
        try {
            ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imgView.setFitWidth(320);
            imgView.setPreserveRatio(true);
            btn.setGraphic(imgView);
        } catch (Exception e) { btn.setText("CONFIRM"); }
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.7));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    /**
     * Moves the arrows to the selected indices and highlights the pointed-to character.
     *
     * @param p1Index  Index selected by P1 (0-3)
     * @param p2Index  Index selected by P2 (0-3)
     * @param isP2Turn Whether it is P2's turn (if so, P2's arrow is also shown)
     */
    public void updateArrowPositions(int p1Index, int p2Index, boolean isP2Turn) {
        double p1TargetX = (CHAR_WIDTH + CHAR_SPACING) * p1Index + (CHAR_WIDTH / 2) - (ARROW_WIDTH / 2);
        double p2TargetX = (CHAR_WIDTH + CHAR_SPACING) * p2Index + (CHAR_WIDTH / 2) - (ARROW_WIDTH / 2);

        if (isP2Turn && p1Index == p2Index) {
            p1TargetX -= 35;
            p2TargetX += 35;
        }

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(150), p1ArrowWrapper);
        tt1.setToX(p1TargetX);
        tt1.play();

        if (isP2Turn) {
            TranslateTransition tt2 = new TranslateTransition(Duration.millis(150), p2ArrowWrapper);
            tt2.setToX(p2TargetX);
            tt2.play();
        }

        highlightSelectedCharacter(isP2Turn ? p2Index : p1Index);
    }

    /**
     * Enlarges the highlighted character and dims the others, and updates the info box.
     *
     * @param activeIndex Index of the currently highlighted character
     */
    private void highlightSelectedCharacter(int activeIndex) {
        for (int i = 0; i < 4; i++) {
            if (i == activeIndex) {
                characterImageViews[i].setScaleX(1.1);
                characterImageViews[i].setScaleY(1.1);
                characterImageViews[i].setStyle("-fx-effect: dropshadow(gaussian, rgba(255,200,0,0.8), 20, 0.5, 0, 0);");
            } else {
                characterImageViews[i].setScaleX(1.0);
                characterImageViews[i].setScaleY(1.0);
                characterImageViews[i].setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0.5, 0, 5);");
            }
        }
        infoText.setText(characterInfos[activeIndex]);
        double boxW = 220;
        double charCenterX = (CHAR_WIDTH + CHAR_SPACING) * activeIndex + (CHAR_WIDTH / 2);
        infoBox.setLayoutX(charCenterX - boxW / 2);
        infoBox.setLayoutY(ARROW_Y_POS - 130);
    }

    /**
     * Sets up keyboard listeners and character-confirmation logic.
     * P1 uses A/D to move and Enter to confirm; P2 uses the same keys in turn.
     * When P2 confirms, onGameStartHandler is called with both players' classes.
     */
    private void setupController() {
        updateArrowPositions(p1SelectedIndex, p2SelectedIndex, false);

        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            if (isP1Turn) {
                if (key == KeyCode.LEFT || key == KeyCode.A) {
                    p1SelectedIndex = Math.max(0, p1SelectedIndex - 1);
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, false);
                } else if (key == KeyCode.RIGHT || key == KeyCode.D) {
                    p1SelectedIndex = Math.min(3, p1SelectedIndex + 1);
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, false);
                } else if (key == KeyCode.ENTER || key == KeyCode.SPACE) {
                    confirmP1Btn.fire();
                }
            } else {
                if (key == KeyCode.LEFT || key == KeyCode.A) {
                    p2SelectedIndex = Math.max(0, p2SelectedIndex - 1);
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, true);
                } else if (key == KeyCode.RIGHT || key == KeyCode.D) {
                    p2SelectedIndex = Math.min(3, p2SelectedIndex + 1);
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, true);
                } else if (key == KeyCode.ENTER || key == KeyCode.SPACE) {
                    confirmP2Btn.fire();
                }
            }
        });

        for (int i = 0; i < 4; i++) {
            final int index = i;
            characterImageViews[i].setOnMouseClicked(e -> {
                if (isP1Turn) {
                    p1SelectedIndex = index;
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, false);
                } else {
                    p2SelectedIndex = index;
                    updateArrowPositions(p1SelectedIndex, p2SelectedIndex, true);
                }
            });
        }

        confirmP1Btn.setOnAction(e -> {
            isP1Turn = false;
            confirmP1Btn.setVisible(false);
            confirmP2Btn.setVisible(true);
            p2ArrowWrapper.setVisible(true);
            p2SelectedIndex = 0;
            updateArrowPositions(p1SelectedIndex, p2SelectedIndex, true);
        });

        confirmP2Btn.setOnAction(e -> {
            if (bgmPlayer != null) bgmPlayer.stop();
            if (onGameStartHandler != null) {
                onGameStartHandler.accept(characterClasses[p1SelectedIndex], characterClasses[p2SelectedIndex]);
            }
        });
    }

    /**
     * Returns the Scene for this screen.
     *
     * @return The character selection Scene
     */
    public Scene getScene() { return scene; }

    /**
     * Returns the BGM MediaPlayer for stopping before switching screens.
     *
     * @return The BGM MediaPlayer
     */
    public MediaPlayer getBgmPlayer() { return bgmPlayer; }

    /**
     * Sets the callback to be invoked when both players have confirmed their characters.
     *
     * @param handler BiConsumer that receives P1's class and P2's class respectively
     */
    public void setOnGameStart(BiConsumer<String, String> handler) {
        this.onGameStartHandler = handler;
    }
}
