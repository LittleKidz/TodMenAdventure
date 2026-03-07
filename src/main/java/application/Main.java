package application;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Application entry point. Loads fonts and delegates the Stage to GameController.
 */
public class Main extends Application {

    /**
     * Automatically called by JavaFX after launch(). Loads the Noto Sans Thai font and creates the GameController.
     *
     * @param primaryStage The primary Stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(Main.class.getResourceAsStream("/fonts/NotoSansThai-Regular.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/NotoSansThai-Bold.ttf"), 14);
        GameController controller = new GameController(primaryStage);
        controller.initialize();
    }

    /**
     * Program entry point. Calls the JavaFX launch method to start the application lifecycle.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
