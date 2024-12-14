package pt.pa;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por importar dados de arquivos CSV para inicializar objetos.
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class DataImporter {

    // Constantes para os índices das colunas do CSV "routes.csv"
    private static final int STOP_CODE_START = 0;
    private static final int STOP_CODE_END = 1;
    private static final int FIRST_DISTANCE = 2;
    private static final int FIRST_DURATION = 7;
    private static final int FIRST_COST = 12;

    // Constantes para os índices das colunas do CSV "stops.csv"
    private static final int STOP_CODE = 0;
    private static final int STOP_NAME = 1;
    private static final int LATITUDE = 2;
    private static final int LONGITUDE = 3;

    // Constantes para os índices das colunas do CSV "xy.csv"
    private static final int STOP_POSITION = 0;
    private static final int X = 1;
    private static final int Y = 2;

    /**
     * Carrega a lista de Stops a partir do arquivo CSV.
     *
     * @return uma lista de objetos {@link Stop} contendo as informações carregadas.
     */
    public static List<Stop> loadStops() {
        List<Stop> stops = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/dataset/stops.csv"))) {
            String[] nextLine;
            reader.readNext(); // Ignorar cabeçalho
            while ((nextLine = reader.readNext()) != null) {
                String stopCode = nextLine[STOP_CODE];
                String stopName = nextLine[STOP_NAME];
                double latitude = Double.parseDouble(nextLine[LATITUDE]);
                double longitude = Double.parseDouble(nextLine[LONGITUDE]);

                stops.add(new Stop(stopCode, stopName, latitude, longitude));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return stops;
    }


    /**
     * Carrega as GenericRoutes a partir do arquivo CSV e associa com as paragens fornecidas.
     *
     * @return uma lista de objetos {@link GenericRoute}.
     */
    public static List<GenericRoute> loadRoutes() {
        List<GenericRoute> genericRoutes = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/dataset/routes.csv"))) {
            String[] values;
            csvReader.readNext(); // Ignorar cabeçalho
            while ((values = csvReader.readNext()) != null) {
                List<Route> routes = new ArrayList<>();

                TransportType[] transportTypes = TransportType.values();
                for (int i = 0; i < transportTypes.length; i++) {
                    int distanceIndex = FIRST_DISTANCE + i;
                    int durationIndex = FIRST_DURATION + i;
                    int costIndex = FIRST_COST + i;

                    addRouteIfNotNull(routes, createRoute(transportTypes[i], values[distanceIndex], values[durationIndex], values[costIndex]));
                }

                genericRoutes.add(new GenericRoute(values[STOP_CODE_START], values[STOP_CODE_END], routes));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return genericRoutes;
    }
    /**
     * Cria uma instância de {@link Route} com base nos parâmetros fornecidos.
     * Retorna null se qualquer parâmetro estiver vazio.
     *
     * @param type Tipo de transporte ({@link TransportType}).
     * @param distanceStr Distância em formato String.
     * @param durationStr Duração em formato String.
     * @param costStr Custo em formato String.
     * @return Instância de {@link Route} ou null.
     */
    private static Route createRoute(TransportType type, String distanceStr, String durationStr, String costStr) {
        if (distanceStr.isEmpty() || durationStr.isEmpty() || costStr.isEmpty()) {
            return null; // Não cria a rota se existirem valores vazios
        }

        double distance = Double.parseDouble(distanceStr);
        int duration = Integer.parseInt(durationStr);
        double cost = Double.parseDouble(costStr);

        return new Route(type, distance, duration, cost);
    }

    /**
     * Adiciona uma Route à lista se não for nula.
     *
     * @param routes Lista de rotas.
     * @param route Rota a ser adicionada.
     */
    private static void addRouteIfNotNull(List<Route> routes, Route route) {
        if (route != null) {
            routes.add(route);
        }
    }

    /**
     * Carrega as coordenadas dos vértices no grafo e ajusta as suas posições no painel do SmartGraph.
     *
     * @param smartGraph Painel do SmartGraph ({@link SmartGraphPanel})
     * @param graph Grafo que contém os vértices ({@link Graph})
     */
    public static void loadCordinates(SmartGraphPanel<Stop, List<Route>> smartGraph, Graph<Stop, List<Route>> graph) {
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/dataset/xy.csv"))) {
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                List<Vertex<Stop>> list = (List<Vertex<Stop>>) graph.vertices();
                String stopCode = nextLine[STOP_POSITION];
                double x = Double.parseDouble(nextLine[X]);
                double y = Double.parseDouble(nextLine[Y]);

                for (Vertex<Stop> v : list) {
                    if (v.element().getStopCode().equals(stopCode)) {
                        smartGraph.setVertexPosition(v, x, y);
                    }
                }

            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
