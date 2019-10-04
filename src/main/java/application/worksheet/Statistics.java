package application.worksheet;

import application.chart.gate.KVData;
import application.utils.MathUtils;
import application.utils.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Statistics extends VBox implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(Statistics.class);

    @FXML
    private TableColumn<String, StatisticsRowObject> nameColumn;

    @FXML
    private TableColumn<Double, StatisticsRowObject> meanColumn;

    @FXML
    private TableColumn<Double, StatisticsRowObject> medianColumn;

    @FXML
    private TableColumn<Double, StatisticsRowObject> cvColumn;

    @FXML
    private TableView<StatisticsRowObject> tableView;

    public Statistics() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("statistics.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        meanColumn.setCellValueFactory(new PropertyValueFactory<>("mean"));
        medianColumn.setCellValueFactory(new PropertyValueFactory<>("median"));
        cvColumn.setCellValueFactory(new PropertyValueFactory<>("coefficientOfVariation"));
    }

    public void addData(List<KVData> dataList) {
        toRowObjects(dataList).forEach(tableView.getItems()::add);
    }

    private List<StatisticsRowObject> toRowObjects(List<KVData> dataList) {
        List<StatisticsRowObject> rowObjects = new ArrayList<>();
        if (dataList.isEmpty()) {
            return rowObjects;
        }
        HashMap<String, List<Float>> reshapedMap = new HashMap<>();
        dataList.get(0).getNames().forEach(name -> reshapedMap.put(name, new ArrayList<>()));
        dataList.forEach(kvData -> {
            for (String name :
                    kvData.getNames()) {
                reshapedMap.get(name).add(kvData.getValueByName(name));
            }
        });
        for (String name :
                reshapedMap.keySet()) {
            List<Float> floatData = reshapedMap.get(name);
            double[] data = new double[floatData.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = floatData.get(i).doubleValue();
            }
            rowObjects.add(new StatisticsRowObject(name,
                    MathUtils.getMean(data),
                    MathUtils.getMedian(data),
                    MathUtils.getCoefficientOfVariation(data)));
        }
        return rowObjects;
    }

}
