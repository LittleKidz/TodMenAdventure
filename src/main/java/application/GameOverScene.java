package application;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Game Over screen displaying the winner and loser with animations.
 * Supports both a normal result and a draw.
 * Provides a Main Menu button and an Exit button.
 */
public class GameOverScene {

    /** X position of the left (loser) box. */
    private final double LEFT_POS_X = 100;

    /** Y position of the left box. */
    private final double LEFT_POS_Y = 100;

    /** X position of the right (winner) box. */
    private final double RIGHT_POS_X = 700;

    /** Y position of the right box. */
    private final double RIGHT_POS_Y = 100;

    /** Main JavaFX Scene for this screen. */
    private Scene scene;

    /** Root container. */
    private StackPane mainRoot;

    /** Main layout. */
    private BorderPane rootPane;

    /** Button to return to the Main Menu. */
    private Button mainMenuBtn;

    /** Button to exit the game. */
    private Button exitBtn;

    /** BGM media player. */
    private MediaPlayer bgmPlayer;

    /**
     * Creates the Game Over screen displaying the battle result.
     * If winnerName is "Draw", a draw result is shown instead.
     *
     * @param winnerName  Name of the winner (or "Draw" for a draw)
     * @param winnerClass Class of the winner
     * @param loserName   Name of the loser
     * @param loserClass  Class of the loser
     */
    public GameOverScene(String winnerName, String winnerClass, String loserName, String loserClass) {
        mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: #000000;");

        try {
            Image bgImg = new Image(getClass().getResourceAsStream("/etc/end_game.jpg"));
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(1150); bgView.setFitHeight(750);
            bgView.setPreserveRatio(false); bgView.setOpacity(0.5);
            mainRoot.getChildren().add(bgView);
        } catch (Exception e) {}

        try {
            Media bgmMedia = new Media(getClass().getResource("/etc/song_background.mp3").toExternalForm());
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setVolume(0.2);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {}

        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: transparent;");

        Pane centerPane = new Pane();
        boolean isDraw = "Draw".equalsIgnoreCase(winnerName);

        if (isDraw) {
            Pane p1Box = createPlayerResult("Player 1", winnerClass, null, false, false);
            p1Box.setLayoutX(LEFT_POS_X); p1Box.setLayoutY(LEFT_POS_Y);

            Pane p2Box = createPlayerResult("Player 2", loserClass, null, false, true);
            p2Box.setLayoutX(RIGHT_POS_X); p2Box.setLayoutY(RIGHT_POS_Y);

            ImageView drawImgView = new ImageView();
            try {
                drawImgView.setImage(new Image(getClass().getResourceAsStream("/etc/draw.png")));
                drawImgView.setFitWidth(350); drawImgView.setPreserveRatio(true);
            } catch (Exception e) {}
            drawImgView.setLayoutX(400); drawImgView.setLayoutY(100);

            TranslateTransition drawAnim = new TranslateTransition(Duration.seconds(2), drawImgView);
            drawAnim.setByY(-15); drawAnim.setCycleCount(TranslateTransition.INDEFINITE); drawAnim.setAutoReverse(true);
            drawAnim.play();

            centerPane.getChildren().addAll(p1Box, p2Box, drawImgView);
        } else {
            Pane loserBox = createPlayerResult(loserName, loserClass, "/etc/defeat.png", false, false);
            loserBox.setLayoutX(LEFT_POS_X); loserBox.setLayoutY(LEFT_POS_Y);

            Pane winnerBox = createPlayerResult(winnerName, winnerClass, "/etc/victory.png", true, true);
            winnerBox.setLayoutX(RIGHT_POS_X); winnerBox.setLayoutY(RIGHT_POS_Y);

            centerPane.getChildren().addAll(loserBox, winnerBox);
        }

        rootPane.setCenter(centerPane);

        HBox bottomButtonBox = new HBox(40);
        bottomButtonBox.setAlignment(Pos.CENTER);
        bottomButtonBox.setPadding(new Insets(0, 0, 15, 0));

        mainMenuBtn = createImageButton("/etc/mainmenu.png");
        exitBtn     = createImageButton("/etc/exitgame.png");
        bottomButtonBox.getChildren().addAll(mainMenuBtn, exitBtn);
        rootPane.setBottom(bottomButtonBox);

        centerPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), centerPane);
        fadeIn.setToValue(1.0); fadeIn.play();

        mainRoot.getChildren().add(rootPane);
        scene = new Scene(mainRoot, 1150, 750);
    }

    /**
     * Creates a result box for one player (name + sprite + Victory/Defeat banner).
     * The sprite floats if the player won, or is desaturated if they lost.
     *
     * @param name           Player name, e.g. "Player 1"
     * @param className      Player class used to load the sprite
     * @param titleImagePath Path to the Victory/Defeat banner ({@code null} for a draw)
     * @param isWinner       {@code true} if this player is the winner
     * @param isRightSide    {@code true} if placed on the right side (sprite is flipped)
     * @return The player result box as a Pane
     */
    private Pane createPlayerResult(String name, String className, String titleImagePath, boolean isWinner, boolean isRightSide) {
        Pane mainBox = new Pane();
        mainBox.setPrefSize(350, 480);

        String imgAction = "stand.png";
        if (titleImagePath != null) {
            imgAction = isWinner ? "win.png" : "dead.png";
        }

        ImageView charImgView = new ImageView();
        try {
            String folderName = getFolderByClass(className);
            charImgView.setImage(new Image(getClass().getResourceAsStream("/character/" + folderName + "/" + imgAction)));
        } catch (Exception e) {}
        charImgView.setFitHeight(220); charImgView.setPreserveRatio(true);
        charImgView.setScaleX(isRightSide ? -1 : 1);

        if (isWinner) {
            TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(2), charImgView);
            floatAnim.setByY(-15); floatAnim.setCycleCount(TranslateTransition.INDEFINITE); floatAnim.setAutoReverse(true);
            floatAnim.play();
        } else {
            ColorAdjust loserEffect = new ColorAdjust();
            loserEffect.setSaturation(-0.7); loserEffect.setBrightness(-0.3);
            charImgView.setEffect(loserEffect);
        }

        String displayClass = className.substring(0, 1).toUpperCase() + className.substring(1).toLowerCase();
        String pTag = name.contains("1") ? "P1" : name.contains("2") ? "P2" : name;
        String finalNameText = displayClass + "(" + pTag + ")";

        Text nameText = new Text(finalNameText);
        nameText.setStyle(isWinner
            ? "-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #ffcc00; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 5, 0.5, 2, 2);"
            : "-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #aaaaaa; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 5, 0.5, 2, 2);");

        VBox charAndNameBox = new VBox(-50);
        charAndNameBox.setAlignment(Pos.CENTER);
        charAndNameBox.setPrefWidth(350);
        charAndNameBox.getChildren().addAll(charImgView, nameText);
        charAndNameBox.setLayoutY(220);

        if (titleImagePath != null) {
            ImageView titleImgView = new ImageView();
            try {
                titleImgView.setImage(new Image(getClass().getResourceAsStream(titleImagePath)));
                titleImgView.setFitHeight(80); titleImgView.setPreserveRatio(true);
            } catch (Exception e) {}

            HBox titleBox = new HBox(titleImgView);
            titleBox.setAlignment(Pos.CENTER);
            titleBox.setPrefWidth(350);
            titleBox.setLayoutY(0);
            mainBox.getChildren().addAll(titleBox, charAndNameBox);
        } else {
            mainBox.getChildren().add(charAndNameBox);
        }
        return mainBox;
    }

    /**
     * Converts a class name to the corresponding sprite folder name, normalising aliases.
     *
     * @param className Class name to convert
     * @return Folder name under /character/
     */
    private String getFolderByClass(String className) {
        String folderName = className.toLowerCase();
        if (folderName.equals("swordsman")) folderName = "knight";
        if (folderName.equals("reincarnated") || folderName.equals("newborn")) folderName = "reborn";
        if (folderName.equals("monster")) folderName = "goblin";
        return folderName;
    }

    /**
     * Creates an image-based button with a hover effect.
     *
     * @param imagePath Resource path for the button image
     * @return A Button using an image instead of text
     */
    private Button createImageButton(String imagePath) {
        Button btn = new Button();
        try {
            ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imgView.setFitWidth(250); imgView.setPreserveRatio(true);
            btn.setGraphic(imgView);
        } catch (Exception e) {}
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.7));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    /** @return The Scene for the Game Over screen */
    public Scene getScene() { return scene; }

    /** @return The BGM MediaPlayer for stopping before switching screens */
    public MediaPlayer getBgmPlayer() { return bgmPlayer; }

    /** @return The Main Menu button for GameController to bind an event handler */
    public Button getMainMenuBtn() { return mainMenuBtn; }

    /** @return The Exit button for GameController to bind an event handler */
    public Button getExitBtn() { return exitBtn; }
}
