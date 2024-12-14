package pt.pa;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.pa.view.MapView;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main class
 *
 * @author amfs
 */
public class Main extends Application {

    /**
     * The default entry point of the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Logger logger = Logger.getLogger("UserActionsLogger");
        configureLogger(logger);

        // Boilerplate. Need your own class, parametrized
        TransportMap map = new TransportMap();

        MapView view = new MapView(map);
        TransportMapController controller = new TransportMapController(map, view, logger);

        map.positionVertex(view.getSmartGraph());

        Scene scene = new Scene(view, 1024, 812);

        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setTitle("Projeto PA 2024/25 - Maps");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static void configureLogger(Logger logger) {
        try {
            FileHandler fileHandler = new FileHandler("userLogs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Error initializing the logger: " + e.getMessage());
        }
    }
}
