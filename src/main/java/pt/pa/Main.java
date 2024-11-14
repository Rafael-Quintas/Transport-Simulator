package pt.pa;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
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
        Graph graph = new GraphEdgeList();

        MapView view = new MapView(graph);

        Scene scene = new Scene(view, 1024, 720);

        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setTitle("Projeto PA 2024/25 - Maps");
        stage.setScene(scene);
        stage.show();
    }
}
