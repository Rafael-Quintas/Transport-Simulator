package pt.pa;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.pa.view.MapView;

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
        // Boilerplate. Need your own class, parametrized
        TransportMap map = new TransportMap();

        MapView view = new MapView(map.getGraph());

        map.positionVertex(view.getSmartGraph());

        Scene scene = new Scene(view, 1024, 782);

        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setTitle("Projeto PA 2024/25 - Maps");
        stage.setScene(scene);
        stage.show();
    }
}
