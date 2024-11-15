package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.scene.layout.BorderPane;
import pt.pa.*;
import pt.pa.utils.PropertiesUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MapView extends BorderPane {

    // For cohesion purposes, please parametrize me
    private SmartGraphPanel<Stop, List<Route>> graphView;

    // For cohesion purposes, please parametrize me
    private Graph<Stop, List<Route>> graph;

    public MapView(Graph<Stop, List<Route>> graph) {

        try {
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            if (css != null) {
                this.graph = graph;
                this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());
                graphView.setMaxHeight(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.height")));
                graphView.setMaxWidth(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.width")));
            }

            doLayout();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private void doLayout() {
        setCenter(this.graphView);
    }

}
