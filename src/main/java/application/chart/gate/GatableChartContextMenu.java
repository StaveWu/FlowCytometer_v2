package application.chart.gate;

import application.chart.ChartSettings;
import application.utils.UiUtils;
import application.worksheet.Statistics;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class GatableChartContextMenu extends ContextMenu {

    private int chartId;
    private GatableChart<Number, Number> gatableChart;

    public GatableChartContextMenu(int chartId, GatableChart<Number, Number> gatableChart) {
        this.chartId = chartId;
        this.gatableChart = gatableChart;
        init();
    }

    private void init() {
        MenuItem createRectangleGateItem = new MenuItem("创建矩形圈门");
        createRectangleGateItem.setOnAction(event -> {
            gatableChart.removeGate();
            Gate gate = new RectangleGate();
            gatableChart.setGate(gate);
        });
        MenuItem createPolygonGateItem = new MenuItem("创建多边形圈门");
        createPolygonGateItem.setOnAction(event -> {
            gatableChart.removeGate();
            Gate gate = new PolygonGate();
            gatableChart.setGate(gate);
        });
        MenuItem deleteGateItem = new MenuItem("删除圈门");
        deleteGateItem.setOnAction(event -> {
            gatableChart.removeGate();
        });
        MenuItem settingsItem = new MenuItem("设置");
        settingsItem.setOnAction(event -> {
            if (gatableChart instanceof XYChart) {
                // pop up settings stage
                Stage stage = new Stage();
                stage.setTitle("图设置");
                stage.setScene(new Scene(new ChartSettings((XYChart<Number, Number>) gatableChart)));
                stage.show();
            }
        });
        MenuItem statisticsItem = new MenuItem("统计");
        statisticsItem.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle(gatableChart.getClass().getSimpleName() + " " + chartId);
            Statistics statistics = new Statistics();
            statistics.addData(gatableChart.getKVData());
            stage.setScene(new Scene(statistics));
            stage.show();
        });
        MenuItem exportKVDataItem = new MenuItem("导出数据");
        exportKVDataItem.setOnAction(event -> {
            List<KVData> dataList = gatableChart.getKVData();
            if (dataList.isEmpty()) {
                UiUtils.getAlert(Alert.AlertType.INFORMATION, null,
                        "当前图无数据可保存").showAndWait();
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save...");
            File file = chooser.showSaveDialog(this.getScene().getWindow());
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()),
                    Charset.forName("utf-8"))) {
                // write header
                List<String> names = dataList.get(0).getNames();
                writer.write(String.join("\t", names));
                writer.newLine();
                // write data
                for (KVData data :
                        dataList) {
                    List<String> values = names.stream()
                            .map(name -> String.valueOf(data.getValueByName(name)))
                            .collect(Collectors.toList());
                    writer.write(String.join("\t", values));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        getItems().add(createRectangleGateItem);
        getItems().add(createPolygonGateItem);
        getItems().add(deleteGateItem);
        getItems().add(settingsItem);
        getItems().add(statisticsItem);
        getItems().add(exportKVDataItem);
    }

}
