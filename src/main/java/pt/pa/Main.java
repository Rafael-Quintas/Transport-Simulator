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
 * A classe {@code Main} é o ponto de entrada principal para a aplicação JavaFX.
 * Inicializa o Model, a View e o Controller, e configura a GUI.
 *
 * A aplicação é uma solução para o Projeto PA 2024/25, que simula um mapa de transporte.
 * Esta classe gere também o sistema de registo (logging) para rastrear as ações do utilizador.
 *
 * @author amfs
 */
public class Main extends Application {

    /**
     * Ponto de entrada padrão da aplicação.
     * Este método inicia a aplicação JavaFX.
     *
     * @param args argumentos da linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método de inicialização da aplicação JavaFX. Configura o Model, View e Controller,
     * e inicializa a GUI.
     *
     * @param primaryStage o estágio principal da aplicação.
     */
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

    /**
     * Configura o logger para registar ações do utilizador num arquivo.
     *
     * O logger grava os logs num arquivo chamado "userLogs.log".
     *
     * @param logger instância de {@link Logger} a ser configurada.
     */
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
