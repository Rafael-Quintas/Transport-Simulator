package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pt.pa.Stop;
import pt.pa.Route;
import pt.pa.TransportType;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MapView extends BorderPane {

    private SmartGraphPanel<Stop, List<Route>> graphView;
    private Graph<Stop, List<Route>> graph;

    private Label stopCodeLabel;
    private Label stopNameLabel;
    private Label latitudeLabel;
    private Label longitudeLabel;

    public MapView(Graph<Stop, List<Route>> graph) {
        try {
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            if (css != null) {
                this.graph = graph;
                this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());

                // Configurações do painel do grafo
                graphView.setStyle("-fx-background-color: #ffffff;");
                graphView.setMinSize(0, 0);
                graphView.setPrefSize(1024, 720);

                addEventHandlers();
            }

            // Ajustar layout geral
            doLayout();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public SmartGraphPanel<Stop, List<Route>> getSmartGraph() {
        return this.graphView;
    }

    private void doLayout() {
        // Configurar o menu superior no topo
        HBox topMenu = createTopMenu();
        this.setTop(topMenu);

        // Configurar o mapa e visualizer na região central
        StackPane mapArea = new StackPane();
        mapArea.getChildren().add(graphView);

        // Ajustar tamanho do mapa com o visualizer
        VBox visualizer = createVisualizerPane();
        StackPane.setAlignment(visualizer, Pos.BOTTOM_LEFT); // Posicionar no canto inferior esquerdo
        StackPane.setMargin(visualizer, new Insets(10)); // Margem no canto inferior esquerdo
        mapArea.getChildren().add(visualizer);

        // Limitar o tamanho do mapa à cena
        mapArea.prefWidthProperty().bind(Bindings.min(this.widthProperty(), 1024));
        mapArea.prefHeightProperty().bind(Bindings.min(this.heightProperty(), 720));

        this.setCenter(mapArea); // Configurar o mapa na área central
    }

    private HBox createTopMenu() {
        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        topMenu.setStyle("-fx-background-color: #212121; -fx-border-color: #000000;");

        String dropdownFX = "-fx-background-color: rgba(255, 255, 255); " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 15px; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 5px; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: black;"   +
                "-fx-font-weight: bold; " +
                "-fx-alignment: center;";

        // Dropdowns
        ComboBox<String> originDropdown = new ComboBox<>();
        originDropdown.setPromptText("Origin");
        originDropdown.setStyle(dropdownFX);
        configureComboBox(originDropdown, 80);


        ComboBox<String> destinationDropdown = new ComboBox<>();
        destinationDropdown.setPromptText("Destination");
        destinationDropdown.setStyle(dropdownFX);
        configureComboBox(destinationDropdown, 100);

        for (Vertex<Stop> vertex : graph.vertices()) {
            String stopName = vertex.element().getStopName();
            originDropdown.getItems().add(stopName);
            destinationDropdown.getItems().add(stopName);
        }

        ComboBox<String> criteriaDropdown = new ComboBox<>();
        criteriaDropdown.setPromptText("Criteria");
        criteriaDropdown.getItems().addAll("Distance", "Duration", "Sustainability");
        criteriaDropdown.setStyle(dropdownFX);
        configureComboBox(criteriaDropdown, 80);

        ComboBox<String> transportDropdown = new ComboBox<>();
        transportDropdown.setPromptText("Transport Type");
        for (TransportType transportType : TransportType.values()) {
            transportDropdown.getItems().add(transportType.toString());
        }
        transportDropdown.setStyle(dropdownFX);
        configureComboBox(transportDropdown, 120);

        // Buttons
        Button calculateCostButton = new Button("Calculate Cost");
        Button top5Button = new Button("Top 5");
        Button logsButton = new Button("Logs");
        Button stopsButton = new Button("Stops N Routes Away");

        // Estilo dos botões
        top5Button.setStyle("-fx-background-color: #fbc02d; -fx-text-fill: #000000;");
        calculateCostButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: #ffffff;");

        // Adicionar elementos ao menu superior
        topMenu.getChildren().addAll(
                originDropdown, destinationDropdown, criteriaDropdown, transportDropdown,
                calculateCostButton, top5Button, logsButton, stopsButton
        );

        return topMenu;
    }

    private VBox createVisualizerPane() {
        VBox visualizerPane = new VBox(10);
        visualizerPane.setPadding(new Insets(10));
        visualizerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-radius: 10px;");
        visualizerPane.setMaxWidth(200);
        visualizerPane.setMaxHeight(150);

        String labelStyle = "-fx-text-fill: white;";

        // Visualizer Section
        Label visualizerLabel = new Label("Visualizer");
        visualizerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " + labelStyle);

        Label numberOfStopsLabel = new Label("Number of Stops:");
        numberOfStopsLabel.setStyle(labelStyle);

        Label numberOfRoutesLabel = new Label("Number of Routes:");
        numberOfRoutesLabel.setStyle(labelStyle);

        Label centralityLabel = new Label("Centrality:");
        centralityLabel.setStyle(labelStyle);

        // Stop Visualizer Section
        Label stopVisualizerLabel = new Label("Stop Visualizer");
        stopVisualizerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " + labelStyle);

        // Inicializar os rótulos de detalhes da paragem
        stopCodeLabel = new Label("Stop Code:");
        stopCodeLabel.setStyle(labelStyle);

        stopNameLabel = new Label("Stop Name:");
        stopNameLabel.setStyle(labelStyle);

        latitudeLabel = new Label("Latitude:");
        latitudeLabel.setStyle(labelStyle);

        longitudeLabel = new Label("Longitude:");
        longitudeLabel.setStyle(labelStyle);

        // Adicionar elementos ao painel
        visualizerPane.getChildren().addAll(
                visualizerLabel, numberOfStopsLabel, numberOfRoutesLabel, centralityLabel,
                stopVisualizerLabel, stopCodeLabel, stopNameLabel, latitudeLabel, longitudeLabel
        );

        return visualizerPane;
    }

    private void addEventHandlers() {
        graphView.setVertexDoubleClickAction(vertex -> {
            Stop stop = (Stop) vertex.getUnderlyingVertex().element();
            showVertexDetails(stop);
        });

        graphView.setEdgeDoubleClickAction(edge -> {
            List<Route> routes = edge.getUnderlyingEdge().element();
            showEdgeDetails(routes);
        });
    }

    private void showVertexDetails(Stop stop) {
        stopCodeLabel.setText("Stop Code: " + stop.getStopCode());
        stopNameLabel.setText("Stop Name: " + stop.getStopName());
        latitudeLabel.setText("Latitude: " + stop.getLatitude());
        longitudeLabel.setText("Longitude: " + stop.getLongitude());
    }


    private void showEdgeDetails(List<Route> routes) {
        Stage stage = new Stage();
        stage.setTitle("Informações da Rota");

        // Criar tabela
        TableView<Route> table = new TableView<>();
        table.setEditable(false);

        // Coluna para Tipo de Transporte
        TableColumn<Route, String> transportTypeColumn = new TableColumn<>("Meio de Transporte");
        transportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportType"));

        // Coluna para Distância
        TableColumn<Route, Double> distanceColumn = new TableColumn<>("Distância");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Coluna para Duração
        TableColumn<Route, Double> durationColumn = new TableColumn<>("Duração");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Coluna para Custo
        TableColumn<Route, Double> costColumn = new TableColumn<>("Sustentabilidade");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("sustainability"));

        table.getColumns().addAll(transportTypeColumn, distanceColumn, durationColumn, costColumn);

        // Adicionar dados à tabela
        table.getItems().addAll(routes);

        stage.setScene(new javafx.scene.Scene(table, 600, 400));
        stage.show();
    }

    private void configureComboBox(ComboBox<String> comboBox, int prefWidth) {
        // Configurar o cellFactory para os itens do ComboBox
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-pref-width: " + prefWidth + "px;");
            }
        });

        // Configurar o botão principal do ComboBox para corresponder ao tamanho
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-pref-width: " + prefWidth + "px;");
            }
        });
    }


}
