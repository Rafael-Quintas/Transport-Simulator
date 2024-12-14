package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import pt.pa.*;
import javafx.scene.chart.*;
import pt.pa.patterns.strategy.WeightCalculationStrategy;
import java.util.*;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapView extends BorderPane implements TransportMapUI {

    private TransportMap model;
    private SmartGraphPanel<Stop, List<Route>> graphView;
    private Graph<Stop, List<Route>> graph;

    private Label stopCodeLabel;
    private Label stopNameLabel;
    private Label latitudeLabel;
    private Label longitudeLabel;
    private Label calculateLabel;
    private Button centralityButton;
    private Button topFiveButton;
    private Button calculateCostButton;
    private Button stopsNRoutesButton;
    private Button customPathButton;
    private ComboBox<String> originDropdown;
    private ComboBox<String> destinationDropdown;
    private ComboBox<String> criteriaDropdown;
    private MenuButton transportDropdown;
    private boolean isSelectingCustomPath = false;
    private List<Vertex<Stop>> customPath = new ArrayList<>();
    private double currentCustomPathCost = 0.0;


    public MapView(TransportMap map) {
        try {
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            if (css != null) {
                this.model = map;
                this.graph = map.getGraph();
                this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());

                // Configurações do painel do grafo
                graphView.setStyle("-fx-background-color: #ffffff;");
                graphView.setMinSize(0, 0);
                graphView.setPrefSize(1024, 720);

            }

            doLayout();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public SmartGraphPanel<Stop, List<Route>> getSmartGraph() {
        return this.graphView;
    }

    @Override
    public void setTriggers(TransportMapController controller) {
        originDropdown.setOnAction(event -> controller.triggerLog("Origin Dropdown" + originDropdown.getValue()));
        destinationDropdown.setOnAction(event -> controller.triggerLog("Destination Dropdown " + destinationDropdown.getValue()));
        criteriaDropdown.setOnAction(event -> controller.triggerLog("Criteria Dropdown " + criteriaDropdown.getValue()));

        graphView.setVertexDoubleClickAction(vertex -> {
            // Sempre mostrar os detalhes do vértice no visualizador
            controller.doShowVertexDetails(vertex);

            // Se o modo customPath estiver ativado, adicionar ao caminho personalizado
            if (isSelectingCustomPath) {
                controller.doShowCustomPath(vertex);
            }
        });

        graphView.setEdgeDoubleClickAction(edge -> {
            controller.doShowEdgeDetails(edge);
        });

        centralityButton.setOnAction(event -> controller.doShowCentralityDetails());

        topFiveButton.setOnAction(event -> controller.doShowTopFive());

        calculateCostButton.setOnAction(event -> controller.doShowLeastCostRoute());

        stopsNRoutesButton.setOnAction(event -> controller.doShowStopsNRoutesAway());

        customPathButton.setOnAction(event -> {
            if (this.criteriaDropdown.getValue() == null) {
                showWarning("Please select a criteria before activating Custom Path.");
                return;
            }

            isSelectingCustomPath = !isSelectingCustomPath;
            customPath.clear();
            resetCurrentCustomPathCost(); // Reseta o custo acumulado
            clearHighlights();

            // Desabilitar ou habilitar o ComboBox de critérios
            criteriaDropdown.setDisable(isSelectingCustomPath);

            if (isSelectingCustomPath) {
                showNotification("Custom Path is activated. Criteria selection is locked.");
            } else {
                showNotification("Custom Path mode deactivated. Criteria selection is now enabled.");
            }
        });
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
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold; " +
                "-fx-alignment: center;";

        // Dropdowns
        originDropdown = new ComboBox<>();
        originDropdown.setPromptText("Origin");
        originDropdown.setStyle(dropdownFX);
        configureComboBox(originDropdown, 80);

        destinationDropdown = new ComboBox<>();
        destinationDropdown.setPromptText("Destination");
        destinationDropdown.setStyle(dropdownFX);
        configureComboBox(destinationDropdown, 120);

        for (Vertex<Stop> vertex : graph.vertices()) {
            String stopName = vertex.element().getStopName();
            originDropdown.getItems().add(stopName);
            destinationDropdown.getItems().add(stopName);
        }

        criteriaDropdown = new ComboBox<>();
        criteriaDropdown.setPromptText("Criteria");
        criteriaDropdown.getItems().addAll("Distance", "Duration", "Sustainability");
        criteriaDropdown.setStyle(dropdownFX);
        configureComboBox(criteriaDropdown, 80);

        transportDropdown = new MenuButton("Transport Type");
        for (TransportType transportType : TransportType.values()) {
            CheckMenuItem item = new CheckMenuItem(transportType.toString());
            transportDropdown.getItems().add(item);
        }
        transportDropdown.setStyle(dropdownFX);

        // Button: Calculate Cost
        calculateCostButton = new Button("Calculate Cost");
        calculateCostButton.setStyle(dropdownFX);
        calculateCostButton.setPrefWidth(120);

        // Buttons: Aligned next to Calculate Cost
        topFiveButton = new Button("Top 5");
        stopsNRoutesButton = new Button("Stops N Routes Away");
        centralityButton = new Button("Centrality");
        customPathButton = new Button("Custom Path");

        // Apply uniform style and size
        topFiveButton.setStyle(dropdownFX);
        stopsNRoutesButton.setStyle(dropdownFX);
        centralityButton.setStyle(dropdownFX);
        customPathButton.setStyle(dropdownFX);

        // Set uniform size for all buttons
        topFiveButton.setPrefWidth(120);
        customPathButton.setPrefWidth(120);
        stopsNRoutesButton.setPrefWidth(160);
        centralityButton.setPrefWidth(160);


        // Organize buttons into two rows with two buttons per row
        HBox firstButtons = new HBox(10, stopsNRoutesButton, topFiveButton);
        HBox secondButtons = new HBox(10, centralityButton, customPathButton);

        // VBox to stack the two rows of buttons
        VBox alignedButtons = new VBox(10, firstButtons, secondButtons);

        // Main layout: ComboBox row + Calculate Cost + Buttons
        HBox comboBoxRow = new HBox(10, originDropdown, destinationDropdown, criteriaDropdown, transportDropdown, calculateCostButton, alignedButtons);
        comboBoxRow.setAlignment(Pos.CENTER_LEFT);

        topMenu.getChildren().add(comboBoxRow);
        return topMenu;
    }

    private VBox createVisualizerPane() {
        VBox visualizerPane = new VBox(10);
        visualizerPane.setPadding(new Insets(10));
        visualizerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-radius: 10px;");
        visualizerPane.setMaxWidth(250);
        visualizerPane.setMaxHeight(200);

        String labelStyle = "-fx-text-fill: white;";

        // Visualizer Section
        Label visualizerLabel = new Label("Visualizer");
        visualizerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " + labelStyle);

        Label stopsLabel = new Label("Number of Stops: " + graph.numVertices());
        stopsLabel.setStyle(labelStyle);

        Label isolatedStopsLabel = new Label("Number of Isolated Stops: " + model.numberOfIsolatedStops());
        isolatedStopsLabel.setStyle(labelStyle);

        Label nonIsolatedStopsLabel = new Label("Number of Non-Isolated Stops: " + model.numberOfNonIsolatedStops());
        nonIsolatedStopsLabel.setStyle(labelStyle);

        Label RoutesLabel = new Label("Number of Routes: " + graph.numEdges());
        RoutesLabel.setStyle(labelStyle);

        Label PossibleRoutesLabel = new Label("Number of Possible Routes: " + model.numberOfPossibleRoutes());
        PossibleRoutesLabel.setStyle(labelStyle);

        calculateLabel = new Label("Cost: ");
        calculateLabel.setStyle(labelStyle);

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
                visualizerLabel, stopsLabel, isolatedStopsLabel, nonIsolatedStopsLabel, RoutesLabel, PossibleRoutesLabel, calculateLabel,
                stopVisualizerLabel, stopCodeLabel, stopNameLabel, latitudeLabel, longitudeLabel
        );

        return visualizerPane;
    }

    public void showVertexDetails(Stop stop) {
        stopCodeLabel.setText("Stop Code: " + stop.getStopCode());
        stopNameLabel.setText("Stop Name: " + stop.getStopName());
        latitudeLabel.setText("Latitude: " + stop.getLatitude());
        longitudeLabel.setText("Longitude: " + stop.getLongitude());
    }

    public void showEdgeDetails(List<Route> routes) {
        Stage stage = new Stage();
        stage.setTitle("Informações da Rota");

        // Criar tabela
        TableView<Route> table = new TableView<>();
        table.setEditable(false);

        // Coluna para Tipo de Transporte
        TableColumn<Route, String> transportTypeColumn = new TableColumn<>("Transport Type");
        transportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportType"));

        // Coluna para Distância
        TableColumn<Route, Double> distanceColumn = new TableColumn<>("Distance");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Coluna para Duração
        TableColumn<Route, Double> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Coluna para Custo
        TableColumn<Route, Double> costColumn = new TableColumn<>("Sustainability");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("sustainability"));

        table.getColumns().addAll(transportTypeColumn, distanceColumn, durationColumn, costColumn);

        // Adicionar dados à tabela
        table.getItems().addAll(routes);

        stage.setScene(new javafx.scene.Scene(table, 334, 200));
        stage.setResizable(false);
        stage.show();
    }

    public void showCentralityDetails() {
        Stage stage = new Stage();
        stage.setTitle("Centrality Details");

        // Criar tabela
        TableView<Map.Entry<Vertex<Stop>, Integer>> table = new TableView<>();
        table.setEditable(false);

        // Coluna para Nome da Parada
        TableColumn<Map.Entry<Vertex<Stop>, Integer>, String> stopNameColumn = new TableColumn<>("Stop Name");
        stopNameColumn.setCellValueFactory(cellData -> {
            Vertex<Stop> vertex = cellData.getValue().getKey();
            return new javafx.beans.property.SimpleStringProperty(vertex.element().getStopName());
        });

        // Coluna para Centralidade
        TableColumn<Map.Entry<Vertex<Stop>, Integer>, Integer> centralityColumn = new TableColumn<>("Centrality");
        centralityColumn.setCellValueFactory(cellData -> {
            Integer centralityValue = cellData.getValue().getValue();
            return new javafx.beans.property.SimpleIntegerProperty(centralityValue).asObject();
        });

        table.getColumns().addAll(stopNameColumn, centralityColumn);

        // Obter os dados de centralidade e adicionar à tabela
        LinkedHashMap<Vertex<Stop>, Integer> centralityMap = model.centrality();
        table.getItems().addAll(centralityMap.entrySet());

        // Configurar a cena e exibir o estágio
        stage.setScene(new javafx.scene.Scene(table, 218, 400));
        stage.setResizable(false);
        stage.show();
    }

    public void showTopFiveCentralityChart() {
        Stage stage = new Stage();
        stage.setTitle("Top 5 Stops by Centrality");

        // Criar um eixo X para os nomes das paradas
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Stop Name");

        // Criar um eixo Y para os valores de centralidade
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Centrality");

        // Criar o gráfico de barras
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 5 Stops by Centrality");

        // Adicionar os dados ao gráfico
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        // Obter os top 5 stops
        List<Map.Entry<Vertex<Stop>, Integer>> topFive = model.topFiveCentrality();
        for (Map.Entry<Vertex<Stop>, Integer> entry : topFive) {
            String stopName = entry.getKey().element().getStopName();
            Integer centrality = entry.getValue();
            dataSeries.getData().add(new XYChart.Data<>(stopName, centrality));
        }

        barChart.getData().add(dataSeries);
        barChart.setLegendVisible(false);

        // Definir as cores personalizadas para cada barra
        String[] barColors = { "#FF5733", "#33FF57", "#3357FF", "#FFC300", "#DAF7A6" };

        // Configurar estilos após a renderização
        stage.setOnShown(event -> {
            for (int i = 0; i < dataSeries.getData().size(); i++) {
                XYChart.Data<String, Number> data = dataSeries.getData().get(i);
                String color = barColors[i % barColors.length];
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        });

        // Configurar a cena e exibir o estágio
        VBox layout = new VBox(barChart);
        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout, 600, 400));
        stage.setResizable(false);
        stage.show();
    }

    public void createStopsNRoutesAwayPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Stops N Routes Away");

        // Layout principal
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Dropdown para selecionar um Stop
        ComboBox<String> stopDropdown = new ComboBox<>();
        stopDropdown.setPromptText("Select a Stop");
        stopDropdown.setPrefWidth(200);

        // Popula o dropdown com os nomes dos Stops
        for (Vertex<Stop> vertex : model.getGraph().vertices()) {
            stopDropdown.getItems().add(vertex.element().getStopName());
        }

        // Campo para número de rotas
        TextField numberField = new TextField();
        numberField.setPromptText("Enter number of routes (N)");
        numberField.setPrefWidth(50);

        // Botão para executar a ação
        Button findButton = new Button("Find Stops");
        findButton.setPrefWidth(200);

        // Adiciona comportamento ao botão
        findButton.setOnAction(event -> {
            String selectedStopName = stopDropdown.getValue();
            String inputNumber = numberField.getText();

            // Obtém o vértice e chama o método do modelo
            Vertex<Stop> selectedVertex = model.getGraph().vertices().stream()
                    .filter(v -> v.element().getStopName().equals(selectedStopName))
                    .findFirst()
                    .orElse(null);

            int N = Integer.parseInt(inputNumber);

            try {
                List<Stop> stops = model.getStopsNRoutesAway(selectedVertex, N);
                popupStage.close();
                showStopsTable(stops);
            } catch (IllegalArgumentException e) {
                showWarning(e.getMessage());
            }
        });

        // Adiciona elementos ao layout
        layout.getChildren().addAll(stopDropdown, numberField, findButton);
        popupStage.setScene(new Scene(layout, 300, 200));
        popupStage.setResizable(false);
        popupStage.show();
    }

    public void showStopsTable(List<Stop> stops) {
        Stage stage = new Stage();
        stage.setTitle("Stops N Routes Away");

        // Cria tabela
        TableView<Stop> table = new TableView<>();
        table.setEditable(false);

        // Coluna para o código da Stop
        TableColumn<Stop, String> codeColumn = new TableColumn<>("Stop Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("stopCode"));

        // Coluna para o nome da Stop
        TableColumn<Stop, String> nameColumn = new TableColumn<>("Stop Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("stopName"));

        // Coluna para a latitude
        TableColumn<Stop, Double> latitudeColumn = new TableColumn<>("Latitude");
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));

        // Coluna para a longitude
        TableColumn<Stop, Double> longitudeColumn = new TableColumn<>("Longitude");
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        table.getColumns().addAll(codeColumn, nameColumn, latitudeColumn, longitudeColumn);

        // Adiciona dados à tabela
        table.getItems().addAll(stops);

        // Configura a cena e exibe o estágio
        stage.setScene(new Scene(new VBox(table), 400, 300));
        stage.setResizable(false);
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

    public ComboBox<String> getOriginDropdown() {
        return originDropdown;
    }

    public ComboBox<String> getDestinationDropdown() {
        return destinationDropdown;
    }

    public ComboBox<String> getCriteriaDropdown() {
        return criteriaDropdown;
    }

    public boolean getIsSelectingCustomPath() { return isSelectingCustomPath; }

    public List<Vertex<Stop>> getCustomPath() { return customPath; }


    public List<TransportType> getSelectedTransportTypes() {
        return transportDropdown.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem)
                .map(item -> (CheckMenuItem) item)
                .filter(CheckMenuItem::isSelected)
                .map(CheckMenuItem::getText)
                .map(displayName -> {
                    for (TransportType type : TransportType.values()) {
                        if (type.toString().equals(displayName)) {
                            return type;
                        }
                    }
                    return null; // Este caso não deve ocorrer, mas está aqui por segurança
                })
                .filter(Objects::nonNull) // Remove qualquer possível valor nulo
                .toList();
    }

    public void addToCustomPath(Vertex<Stop> vertex) {
        customPath.add(vertex);
    }

    public void updateCurrentCustomPathCost(double cost) {
        currentCustomPathCost += cost;
        updateCostLabel("Cost: " + currentCustomPathCost);
    }

    public void resetCurrentCustomPathCost() {
        currentCustomPathCost = 0.0;
        updateCostLabel("Cost: " + currentCustomPathCost);
    }


    public void updateCostLabel(String costText) {
        calculateLabel.setText(costText);
    }

    public void highlightPath(List<Vertex<Stop>> stopsInPath, List<TransportType> transportTypes, WeightCalculationStrategy strategy) {

        clearHighlights();

        for (int i = 0; i < stopsInPath.size() - 1; i++) {
            Vertex<Stop> start = stopsInPath.get(i);
            Vertex<Stop> end = stopsInPath.get(i + 1);

            // Encontrar a aresta correspondente
            model.getGraph().incidentEdges(start).stream()
                    .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                    .findFirst()
                    .ifPresent(edge -> {
                        Route bestRoute = null;
                        double minWeight = Double.POSITIVE_INFINITY;

                        // Determinar a rota com o menor peso usando a estratégia
                        for (Route route : edge.element()) {
                            if (transportTypes.contains(route.getTransportType())) {
                                double weight = strategy.calculateWeight(route);
                                if (weight < minWeight) {
                                    minWeight = weight;
                                    bestRoute = route;
                                }
                            }
                        }

                        // Se a melhor rota foi encontrada, aplica o estilo correspondente
                        if (bestRoute != null) {
                            var graphicalEdge = graphView.getStylableEdge(edge);
                            if (graphicalEdge != null) {
                                // Aplica a classe CSS correspondente
                                graphicalEdge.setStyleClass("edge-" + bestRoute.getTransportType().name().toLowerCase());
                            }
                        }
                    });
        }
    }

    public void clearHighlights() {
        model.getGraph().edges().forEach(edge -> {
            var graphicalEdge = graphView.getStylableEdge(edge);
            if (graphicalEdge != null) {
                // Remove quaisquer classes de estilo aplicadas
                graphicalEdge.setStyleClass("edge");
            }
        });
    }

    public void highlightEdge(Vertex<Stop> start, Vertex<Stop> end, WeightCalculationStrategy strategy) {
        model.getGraph().incidentEdges(start).stream()
                .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                .findFirst()
                .ifPresent(edge -> {
                    Route bestRoute = null;
                    double minWeight = Double.POSITIVE_INFINITY;

                    // Determinar a rota com o menor peso usando a estratégia, sem verificar transportTypes
                    for (Route route : edge.element()) {
                        double weight = strategy.calculateWeight(route);
                        if (weight < minWeight) {
                            minWeight = weight;
                            bestRoute = route;
                        }
                    }

                    // Se a melhor rota foi encontrada, aplica o estilo correspondente
                    if (bestRoute != null) {
                        var graphicalEdge = graphView.getStylableEdge(edge);
                        if (graphicalEdge != null) {
                            // Aplica a classe CSS correspondente
                            graphicalEdge.setStyleClass("edge-" + bestRoute.getTransportType().name().toLowerCase());
                        }
                    }
                });
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Error Reporting -> First method taken from PA´s laboratory.
    public void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning Notification");
        alert.setHeaderText("An error has occurred. Description below:");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update(Object obj) {

    }
}
