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
 */
public class DataImporter {

    /**
     * Carrega a lista de stops a partir do arquivo CSV.
     *
     * @return uma lista de objetos {@link Stop} contendo as informações carregadas.
     */
    public static List<Stop> loadStops() {
        List<Stop> stops = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/dataset/stops.csv"))) {
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                String stopCode = nextLine[0];
                String stopName = nextLine[1];
                double latitude = Double.parseDouble(nextLine[2]);
                double longitude = Double.parseDouble(nextLine[3]);

                Stop stop = new Stop(stopCode, stopName, latitude, longitude);
                stops.add(stop);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return stops;
    }


    /**
     * Carrega as rotas GenericRoutes a partir do arquivo CSV e associa com as paragens fornecidas.
     *
     * @return uma lista de objetos {@link GenericRoute}.
     */
    public static List<GenericRoute> loadRoutes() {
        List<GenericRoute> genericRoutes = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/dataset/routes.csv"))) {
            String[] values;
            csvReader.readNext(); // Ignorar a primeira linha do ficheiro csv
            while ((values = csvReader.readNext()) != null) {
                List<Route> routes = new ArrayList<>();

                // Usar funções auxiliares para não ter routes nulas
                addRouteIfNotNull(routes, createRoute(TransportType.TRAIN, values[2], values[7], values[12]));
                addRouteIfNotNull(routes, createRoute(TransportType.BUS, values[3], values[8], values[13]));
                addRouteIfNotNull(routes, createRoute(TransportType.BOAT, values[4], values[9], values[14]));
                addRouteIfNotNull(routes, createRoute(TransportType.WALK, values[5], values[10], values[15]));
                addRouteIfNotNull(routes, createRoute(TransportType.BICYCLE, values[6], values[11], values[16]));

                genericRoutes.add(new GenericRoute(values[0], values[1], routes));
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
     * Adiciona uma rota à lista se não for nula.
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
                String stopCode = nextLine[0];
                double x = Double.parseDouble(nextLine[1]);
                double y = Double.parseDouble(nextLine[2]);

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
