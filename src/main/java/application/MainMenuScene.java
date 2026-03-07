package application;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Main menu screen shown at game start. Displays a background image, plays music, and shows a single Start button.
 * Pressing Start notifies GameController to begin a new game.
 */
public class MainMenuScene {

    /** Main JavaFX Scene for this screen. */
    private Scene scene;

    /** Root layout for this screen. */
    private StackPane mainRoot;

    /** Start button that enters the game. */
    private Button startBtn;

    /** Looping background music player. */
    private MediaPlayer bgmPlayer;

    /**
     * Creates the Main Menu screen with a background image, music, and a Start button.
     * The button fades in slowly when the screen opens.
     */
    public MainMenuScene() {
        mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: #000000;");

        try {
            Image bgImg = new Image(getClass().getResourceAsStream("/etc/start_game.jpg"));
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(1150);
            bgView.setFitHeight(750);
            bgView.setPreserveRatio(false);
            mainRoot.getChildren().add(bgView);
        } catch (Exception e) {
            System.out.println("Warning: Missing /etc/start_game.jpg");
        }

        try {
            Media bgmMedia = new Media(getClass().getResource("/etc/song_background.mp3").toExternalForm());
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setVolume(0.3);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {
            System.out.println("Warning: Missing /etc/song_background.mp3");
        }

        startBtn = new Button();
        try {
            ImageView startImgView = new ImageView(new Image(getClass().getResourceAsStream("/etc/start.png")));
            startImgView.setFitWidth(280);
            startImgView.setPreserveRatio(true);
            startBtn.setGraphic(startImgView);
        } catch (Exception e) {
            startBtn.setText("START GAME");
            startBtn.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: white;");
        }

        startBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        StackPane.setAlignment(startBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startBtn, new Insets(0, 0, 65, 0));
        startBtn.setOnMouseEntered(e -> startBtn.setOpacity(0.7));
        startBtn.setOnMouseExited(e -> startBtn.setOpacity(1.0));
        mainRoot.getChildren().add(startBtn);

        startBtn.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2.5), startBtn);
        fadeIn.setToValue(1.0);
        fadeIn.setDelay(Duration.seconds(1));
        fadeIn.play();

        scene = new Scene(mainRoot, 1150, 750);
    }

    /**
     * Returns the Scene for this screen to be set on the Stage.
     *
     * @return The Main Menu Scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Returns the background music MediaPlayer for stopping before switching screens.
     *
     * @return The BGM MediaPlayer
     */
    public MediaPlayer getBgmPlayer() {
        return bgmPlayer;
    }

    /**
     * Returns the Start button for GameController to bind an event handler.
     *
     * @return The Start button
     */
    public Button getStartBtn() {
        return startBtn;
    }
}
