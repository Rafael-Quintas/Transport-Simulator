package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import com.sun.jdi.connect.Transport;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.converter.IntegerStringConverter;
import pt.pa.*;
import javafx.scene.chart.*;
import pt.pa.patterns.strategy.WeightCalculationStrategy;

import java.net.URISyntaxException;
import java.util.*;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A classe {@code MapView} é responsável pela GUI do sistema.
 * Exibe o grafo de transportes, as opções de interação e informações detalhadas sobre Stops e Routes.
 *
 * Implementa a interface {@link TransportMapUI} e é usada para conectar o Model ({@link TransportMap})
 * e o Controller ({@link TransportMapController}).
 *
 * <h3>Funcionalidades principais:</h3>
 * <ul>
 *   <li>Exibição do grafo de transporte com suporte a interatividade.</li>
 *   <li>Visualização de detalhes de Stops e Routes.</li>
 *   <li>Opções para calcular caminhos, centralidade e personalização de trajetos.</li>
 *   <li>Integração com o Controller para gerir eventos do utilizador.</li>
 * </ul>
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class MapView extends BorderPane implements TransportMapUI {

    private TransportMap model;
    private SmartGraphPanel<Stop, List<Route>> graphView;
    private Graph<Stop, List<Route>> graph;

    private Label stopCodeLabel;
    private Label stopNameLabel;
    private Label latitudeLabel;
    private Label longitudeLabel;
    private Label calculateLabel;
    private VBox totalRoutes;
    private VBox busRoutes;
    private VBox trainRoutes;
    private VBox boatRoutes;
    private VBox walkRoutes;
    private VBox bicycleRoutes;
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
    private SmartGraphEdge<List<Route>, Stop> currentEdge;
    private TransportMapController controller;

    /**
     * Construtor que inicializa a interface do mapa de transportes com base no modelo fornecido.
     *
     * @param map modelo de transporte ({@link TransportMap}).
     */
    public MapView(TransportMap map, Logger logger) {
        try {
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            if (css != null) {
                this.model = map;
                this.graph = map.getGraph();
                this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());
                controller = new TransportMapController(map, this, logger);

                // Configurações do painel do grafo
                graphView.setStyle("-fx-background-color: #ffffff;");
                graphView.setMinSize(0, 0);
                graphView.setPrefSize(1024, 720);
            }

            doLayout();
            setTriggers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Retorna o painel gráfico do grafo.
     *
     * @return uma instância de {@link SmartGraphPanel}.
     */
    public SmartGraphPanel<Stop, List<Route>> getSmartGraph() {
        return this.graphView;
    }

    /**
     * Define os triggers de interação para a interface gráficas
     */
    @Override
    public void setTriggers() {
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
            this.currentEdge = edge;
            //controller.doShowEdgeDetails(edge);
            showEdgeDetails(edge);
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


    /**
     * Configura o layout principal da UI.
     *
     * Este método organiza os elementos da interface, incluindo:
     * O menu superior com as opções de interação (botões e dropdowns).
     * A área central que contem o grafo e o visualizador de informações.
     */
    private void doLayout() {
        // Configurar o menu superior no topo
        HBox topMenu = createTopMenu();
        this.setTop(topMenu);

        // Configurar o mapa e visualizer na região central
        StackPane mapArea = new StackPane();
        mapArea.getChildren().add(graphView);

        // Ajustar tamanho do mapa com o visualizer
        VBox visualizer = createVisualizer();
        StackPane.setAlignment(visualizer, Pos.BOTTOM_LEFT); // Posicionar no canto inferior esquerdo
        StackPane.setMargin(visualizer, new Insets(10)); // Margem no canto inferior esquerdo
        mapArea.getChildren().add(visualizer);

        // Limitar o tamanho do mapa à cena
        mapArea.prefWidthProperty().bind(Bindings.min(this.widthProperty(), 1024));
        mapArea.prefHeightProperty().bind(Bindings.min(this.heightProperty(), 720));

        this.setCenter(mapArea); // Configurar o mapa na área central
    }

    /**
     * Cria o menu superior da GUI.
     *
     * inclui:
     * Dropdowns para selecionar a origem, destino e critério de cálculo.
     * Um menu suspenso para selecionar os tipos de transporte.
     * Botões para calcular custos, mostrar as 5 Stops mais centrais, exibir paragens a N Routes de distância e ativar o modo de caminho personalizado.
     *
     * @return {@link HBox} que contém o menu superior.
     */
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

        // Buttons alinhados ao lado do Calculate Cost
        topFiveButton = new Button("Top 5");
        stopsNRoutesButton = new Button("Stops N Routes Away");
        centralityButton = new Button("Centrality");
        customPathButton = new Button("Custom Path");

        // Estilos
        topFiveButton.setStyle(dropdownFX);
        stopsNRoutesButton.setStyle(dropdownFX);
        centralityButton.setStyle(dropdownFX);
        customPathButton.setStyle(dropdownFX);
        topFiveButton.setPrefWidth(120);
        customPathButton.setPrefWidth(120);
        stopsNRoutesButton.setPrefWidth(160);
        centralityButton.setPrefWidth(160);


        // Organizar os buttons em duas secções com 2 buttons em cada uma
        HBox firstButtons = new HBox(10, stopsNRoutesButton, topFiveButton);
        HBox secondButtons = new HBox(10, centralityButton, customPathButton);
        VBox alignedButtons = new VBox(10, firstButtons, secondButtons);

        // Layout Final
        HBox comboBoxRow = new HBox(10, originDropdown, destinationDropdown, criteriaDropdown, transportDropdown, calculateCostButton, alignedButtons);
        comboBoxRow.setAlignment(Pos.CENTER_LEFT);

        topMenu.getChildren().add(comboBoxRow);
        return topMenu;
    }

    /**
     * Cria o painel visualizador da UI.
     *
     * Exibe:
     * Informações gerais sobre o grafo, como número de Stops, Routes possíveis.
     * Detalhes da Stop selecionada, incluindo código, nome, latitude e longitude.
     *
     * @return {@link VBox} que contém o painel visualizador.
     */
    private VBox createVisualizer() {
        VBox visualizerPane = new VBox(10);
        visualizerPane.setPadding(new Insets(10));
        visualizerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-radius: 10px;");
        visualizerPane.setMaxWidth(280);
        visualizerPane.setMaxHeight(200);

        String labelStyle = "-fx-text-fill: white;";

        // Secção do Visualizer
        Label visualizerLabel = new Label("Visualizer");
        visualizerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " + labelStyle);

        Label stopsLabel = new Label("Number of Stops: " + graph.numVertices());
        stopsLabel.setStyle(labelStyle);

        Label isolatedStopsLabel = new Label("Number of Isolated Stops: " + model.numberOfIsolatedStops());
        isolatedStopsLabel.setStyle(labelStyle);

        Label nonIsolatedStopsLabel = new Label("Number of Non-Isolated Stops: " + model.numberOfNonIsolatedStops());
        nonIsolatedStopsLabel.setStyle(labelStyle);

        Label routesLabel = new Label("Number of Routes: " + graph.numEdges());
        routesLabel.setStyle(labelStyle);

        Label possibleRoutesTitle = new Label("Number of Possible Routes:");
        possibleRoutesTitle.setStyle(labelStyle);

        // Labels e dados para cada transport type
        totalRoutes = createTransportRoute("Total", model.numberOfPossibleRoutes(), labelStyle);
        busRoutes = createTransportRoute("Bus", model.numberOfRoutesByTransport(TransportType.BUS), labelStyle);
        trainRoutes = createTransportRoute("Train", model.numberOfRoutesByTransport(TransportType.TRAIN), labelStyle);
        boatRoutes = createTransportRoute("Boat", model.numberOfRoutesByTransport(TransportType.BOAT), labelStyle);
        walkRoutes = createTransportRoute("Walk", model.numberOfRoutesByTransport(TransportType.WALK), labelStyle);
        bicycleRoutes = createTransportRoute("Bicycle", model.numberOfRoutesByTransport(TransportType.BICYCLE), labelStyle);

        // Organizer o número de routes para transportes num layout horizontal
        HBox transportRoutes = new HBox(15, totalRoutes, busRoutes, trainRoutes, boatRoutes, walkRoutes, bicycleRoutes);

        calculateLabel = new Label("Total Path Cost: ");
        calculateLabel.setStyle(labelStyle);

        // Secção do Visualizer de Stops
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
                visualizerLabel, calculateLabel, stopsLabel, isolatedStopsLabel, nonIsolatedStopsLabel, routesLabel, possibleRoutesTitle,
                transportRoutes, stopVisualizerLabel, stopCodeLabel, stopNameLabel, latitudeLabel, longitudeLabel
        );

        return visualizerPane;
    }

    public void updateVisualizer() {
        // Atualiza o número total de Stops
        stopCodeLabel.setText("Number of Stops: " + graph.numVertices());

        // Atualiza o número de Stops isolados e não isolados
        latitudeLabel.setText("Number of Isolated Stops: " + model.numberOfIsolatedStops());
        longitudeLabel.setText("Number of Non-Isolated Stops: " + model.numberOfNonIsolatedStops());

        // Atualiza o número total de Routes
        stopNameLabel.setText("Number of Routes: " + graph.numEdges());

        // Atualiza as informações de Routes por tipo de transporte
        updateTransportRoute(totalRoutes, "Total", model.numberOfPossibleRoutes());
        updateTransportRoute(busRoutes, "Bus", model.numberOfRoutesByTransport(TransportType.BUS));
        updateTransportRoute(trainRoutes, "Train", model.numberOfRoutesByTransport(TransportType.TRAIN));
        updateTransportRoute(boatRoutes, "Boat", model.numberOfRoutesByTransport(TransportType.BOAT));
        updateTransportRoute(walkRoutes, "Walk", model.numberOfRoutesByTransport(TransportType.WALK));
        updateTransportRoute(bicycleRoutes, "Bicycle", model.numberOfRoutesByTransport(TransportType.BICYCLE));
    }

    /**
     * Exibe os detalhes de uma Stop na interface.
     *
     * @param numberOfRoutes Stop selecionada ({@link Stop}).
     */

    private VBox createTransportRoute(String text, int numberOfRoutes, String labelStyle) {
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-weight: bold; " + labelStyle);

        Label numberLabel = new Label(String.valueOf(numberOfRoutes));
        numberLabel.setStyle(labelStyle);

        VBox transportRoute = new VBox(5, textLabel, numberLabel);
        transportRoute.setAlignment(Pos.CENTER);
        return transportRoute;
    }

    private void updateTransportRoute(VBox routeBox, String text, int numberOfRoutes) {
        Label textLabel = (Label) routeBox.getChildren().get(0); // Assume que o primeiro filho é o texto
        Label numberLabel = (Label) routeBox.getChildren().get(1); // Assume que o segundo filho é o número

        textLabel.setText(text);
        numberLabel.setText(String.valueOf(numberOfRoutes));
    }


    /**
     * Exibe os detalhes de uma Stop na interface.
     *
     * @param stop Stop selecionada ({@link Stop}).
     */
    public void showVertexDetails(Stop stop) {
        stopCodeLabel.setText("Stop Code: " + stop.getStopCode());
        stopNameLabel.setText("Stop Name: " + stop.getStopName());
        latitudeLabel.setText("Latitude: " + stop.getLatitude());
        longitudeLabel.setText("Longitude: " + stop.getLongitude());
    }

    /**
     * Exibe os detalhes de uma Route numa nova janela, com opções para remover completamente a rota do grafo
     * e selecionar quais tipos de transporte estão ativos individualmente.
     *
     * @param edge a lista de Routes associadas a uma aresta ({@link Route}).
     */
    public void showEdgeDetails(SmartGraphEdge<List<Route>, Stop> edge) {
        Stage stage = new Stage();
        stage.setTitle("Informações da Rota");

        List<Route> routes = edge.getUnderlyingEdge().element();

        // Criar tabela
        TableView<Route> table = new TableView<>();
        table.setEditable(true);

        // Configurar as colunas
        TableColumn<Route, String> transportTypeColumn = new TableColumn<>("Transport Type");
        transportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportType"));

        TableColumn<Route, Double> distanceColumn = new TableColumn<>("Distance");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Coluna "Duration" que será editável apenas para bicicletas
        TableColumn<Route, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        durationColumn.setCellFactory(column -> {
            return new TextFieldTableCell<Route, Integer>(new IntegerStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    TextField textField = (TextField) getGraphic();

                    // Adiciona um filtro para aceitar apenas números
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("\\d*")) {
                            textField.setText(oldValue);
                        }
                    });
                }
            };
        });

        durationColumn.setEditable(true);

        durationColumn.setOnEditCommit(event -> {
            Route route = event.getRowValue();
            try {
                int newDuration = event.getNewValue();

                // Chama o controlador para alterar a duração
                controller.doChangeBicycleRouteDuration(route, newDuration);
                table.refresh();

                showNotification("Bicycle route duration updated successfully!");
            } catch (IllegalArgumentException e) {
                showWarning("Invalid duration: " + e.getMessage());
                table.refresh();
            }
        });

        TableColumn<Route, Double> costColumn = new TableColumn<>("Sustainability");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("sustainability"));

        TableColumn<Route, Boolean> activeColumn = new TableColumn<>("Active");
        activeColumn.setCellValueFactory(cellData -> {
            SimpleBooleanProperty activeProperty = new SimpleBooleanProperty(cellData.getValue().getState());

            // Adicionar listener para monitorar mudanças na checkbox
            activeProperty.addListener((observable, oldValue, newValue) -> {
                Route route = cellData.getValue();

                controller.doDisableRoute(edge, List.of(route));
                refreshTable(table, routes);
                updateVisualizer();
            });

            return activeProperty;
        });

        activeColumn.setCellFactory(column -> new CheckBoxTableCell<Route, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    Route route = getTableRow().getItem();
                    if (route != null) {
                        setDisable(!route.getState()); // Bloqueia checkbox se a rota estiver desativada
                    }
                }
            }
        });

        table.getColumns().addAll(transportTypeColumn, distanceColumn, durationColumn, costColumn, activeColumn);

        // Populate the table with initial routes
        refreshTable(table, routes);

        // Button to deactivate all routes
        Button deactivateAllButton = new Button("Deactivate all routes");
        deactivateAllButton.setOnAction(event -> {
            controller.doDisableRoute(edge, routes);
            refreshTable(table, routes);
            updateVisualizer();
        });

        // Button to undo changes
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> {
            controller.undo(); // Undo changes in the controller
            refreshGraphView();
            refreshTableAfterUndo(edge, table, routes);
            updateVisualizer();
        });

        VBox vbox = new VBox(10, deactivateAllButton, undoButton, table);
        vbox.setPadding(new Insets(10));

        stage.setScene(new Scene(vbox, 600, 400));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Refreshes the table based on the current state of the routes.
     */
    private void refreshTable(TableView<Route> table, List<Route> routes) {
        table.getItems().setAll(routes); // Replace the table's items with the updated list
        table.refresh(); // Force refresh to ensure UI consistency
    }

    private void refreshGraphView() {
        try {
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            if (smartgraphProperties != null && css != null) {
                this.graph = model.getGraph();
                // Recriar o painel gráfico com o grafo atualizado
                this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());

                // Redefinir os triggers para o novo gráfico
                setTriggers();

                // Substituir o gráfico atual na interface
                StackPane mapArea = (StackPane) this.getCenter();
                mapArea.getChildren().set(0, graphView);

                model.positionVertex(graphView);
            } else {
                throw new RuntimeException("Failed to load graph properties or CSS.");
            }
        } catch (Exception e) {
            showWarning("Failed to refresh graph view: " + e.getMessage());
        }
    }

    private void refreshTableAfterUndo(SmartGraphEdge<List<Route>, Stop> edge, TableView<Route> table, List<Route> routes) {
        Vertex<Stop>[] adjacentStops = edge.getUnderlyingEdge().vertices();

        // Encontrar as rotas correspondentes no grafo
        List<Route> updatedRoutes = findRoutesByStops(adjacentStops);

        if (updatedRoutes != null) {
            refreshTable(table, updatedRoutes);
            routes.clear();
            routes.addAll(updatedRoutes);
        } else {
            System.out.println("Aresta correspondente não encontrada no grafo.");
        }
    }

    private List<Route> findRoutesByStops(Vertex<Stop>[] adjacentStops) {
        String stopName1 = adjacentStops[0].element().getStopName();
        String stopName2 = adjacentStops[1].element().getStopName();

        for (Edge<List<Route>, Stop> e : model.getGraph().edges()) {
            Vertex<Stop>[] vertices = e.vertices();
            String graphStopName1 = vertices[0].element().getStopName();
            String graphStopName2 = vertices[1].element().getStopName();

            if ((stopName1.equals(graphStopName1) && stopName2.equals(graphStopName2)) ||
                    (stopName1.equals(graphStopName2) && stopName2.equals(graphStopName1))) {
                return e.element();
            }
        }

        return null;
    }


    /**
     * Exibe uma tabela com os detalhes de centralidade de todas as paragens.
     */
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
            return new SimpleStringProperty(vertex.element().getStopName());
        });

        // Coluna para Centralidade
        TableColumn<Map.Entry<Vertex<Stop>, Integer>, Integer> centralityColumn = new TableColumn<>("Centrality");
        centralityColumn.setCellValueFactory(cellData -> {
            Integer centralityValue = cellData.getValue().getValue();
            return new SimpleIntegerProperty(centralityValue).asObject();
        });

        table.getColumns().addAll(stopNameColumn, centralityColumn);

        // Obter os dados de centralidade e adicionar à tabela
        LinkedHashMap<Vertex<Stop>, Integer> centralityMap = model.centrality();
        table.getItems().addAll(centralityMap.entrySet());

        // Configurar a cena e exibir o estágio
        stage.setScene(new Scene(table, 218, 400));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Exibe um gráfico de barras com as 5 Stops de maior centralidade.
     */
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

    /**
     * Cria um popup para calcular e exibir os Stops a N Routes de distância de um Stop inicial.
     */
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

            try {
                int N = Integer.parseInt(inputNumber);
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

    /**
     * Exibe uma tabela com os Stops retornadas pelo cálculo de N rotas de distância.
     *
     * @param stops a lista de Stops encontrados ({@link Stop}).
     */
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

    /**
     * Configura o estilo e o comportamento de um {@link ComboBox}.
     *
     * @param comboBox ComboBox a ser configurado.
     * @param prefWidth largura preferencial do ComboBox.
     */
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

    /**
     * Obtém o dropdown de seleção do Stop de origem.
     *
     * @return uma instância de {@link ComboBox}.
     */
    public ComboBox<String> getOriginDropdown() {
        return originDropdown;
    }

    /**
     * Obtém o dropdown de seleção da Stop de destino.
     *
     * @return uma instância de {@link ComboBox}.
     */
    public ComboBox<String> getDestinationDropdown() {
        return destinationDropdown;
    }

    /**
     * Obtém o dropdown de critérios de cálculo.
     *
     * @return uma instância de {@link ComboBox}.
     */
    public ComboBox<String> getCriteriaDropdown() {
        return criteriaDropdown;
    }

    /**
     * Retorna o estado do modo de seleção do Path personalizado.
     *
     * @return {@code true} se o modo estiver ativo; caso contrário, {@code false}.
     */
    public boolean getIsSelectingCustomPath() {
        return isSelectingCustomPath;
    }

    /**
     * Retorna a lista de Stops no Path personalizado.
     *
     * @return lista de vértices ({@link Vertex}).
     */
    public List<Vertex<Stop>> getCustomPath() {
        return customPath;
    }

    /**
     * Obtém os tipos de transporte selecionados no dropdown.
     *
     * @return lista de tipos de transporte ({@link TransportType}).
     */
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

    /**
     * Adiciona um Stop ao Path personalizado.
     *
     * @param vertex vértice a ser adicionado ({@link Vertex}).
     */
    public void addToCustomPath(Vertex<Stop> vertex) {
        customPath.add(vertex);
    }

    /**
     * Atualiza o custo acumulado do Path personalizado.
     *
     * @param cost custo a ser adicionado.
     */
    public void updateCurrentCustomPathCost(double cost) {
        currentCustomPathCost += cost;
        updateCostLabel("Total Path Cost: " + Math.round(currentCustomPathCost * 100.0) / 100.0);
    }

    /**
     * Dá o custo acumulado do Path personalizado.
     */
    public void resetCurrentCustomPathCost() {
        currentCustomPathCost = 0.0;
        updateCostLabel("Total Path Cost: " + Math.round(currentCustomPathCost * 100.0) / 100.0);
    }

    /**
     * Atualiza o campo de custo na interface.
     *
     * @param costText texto a ser exibido na secção do custo.
     */
    public void updateCostLabel(String costText) {
        calculateLabel.setText(costText);
    }

    /**
     * Destaca um Path específico no grafo.
     *
     * @param stopsInPath lista de Stops no Path.
     * @param transportTypes tipos de transporte disponíveis.
     * @param strategy estratégia de cálculo de peso.
     */
    public void highlightPath(List<Vertex<Stop>> stopsInPath, List<TransportType> transportTypes, WeightCalculationStrategy strategy) {

        clearHighlights();

        for (int i = 0; i < stopsInPath.size() - 1; i++) {
            Vertex<Stop> start = stopsInPath.get(i);
            Vertex<Stop> end = stopsInPath.get(i + 1);

            // Encontrar a aresta correspondente
            model.getGraph().incidentEdges(start).stream()
                    .filter(edge -> !edge.element().isEmpty())
                    .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                    .findFirst()
                    .ifPresent(edge -> {
                        Route bestRoute = null;
                        double minWeight = Double.POSITIVE_INFINITY;

                        // Determinar a rota com o menor peso usando a estratégia
                        for (Route route : edge.element()) {
                            if (transportTypes.contains(route.getTransportType()) && route.getState()) {
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

    /**
     * Remove os destaques aplicados no grafo.
     */
    public void clearHighlights() {
        model.getGraph().edges().forEach(edge -> {
            var graphicalEdge = graphView.getStylableEdge(edge);
            if (graphicalEdge != null) {
                // Remove quaisquer classes de estilo aplicadas
                graphicalEdge.setStyleClass("edge");
            }
        });
    }

    /**
     * Destaca a aresta entre duas paragens no grafo.
     *
     * @param start vértice de início.
     * @param end vértice de destino.
     * @param strategy estratégia de cálculo de peso.
     */
    public void highlightEdge(Vertex<Stop> start, Vertex<Stop> end, WeightCalculationStrategy strategy) {
        model.getGraph().incidentEdges(start).stream()
                .filter(edge -> !edge.element().isEmpty())
                .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                .findFirst()
                .ifPresent(edge -> {
                    Route bestRoute = null;
                    double minWeight = Double.POSITIVE_INFINITY;

                    // Determinar a rota com o menor peso usando a estratégia, sem verificar transportTypes
                    for (Route route : edge.element()) {
                        if (route.getState()) {
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


    ///////////////////////////////////////////////////////////////////////////////////
    // Error Reporting
    /**
     * Exibe um alerta de erro com uma mensagem específica.
     *
     * @param message mensagem de erro a ser exibida.
     */
    public void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning Notification");
        alert.setHeaderText("An error has occurred. Description below:");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Exibe uma notificação de sucesso ou informação com uma mensagem específica.
     *
     * @param message mensagem de notificação a ser exibida.
     */
    public void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Ainda não implementado
    @Override
    public void update(Object obj) {

    }
}