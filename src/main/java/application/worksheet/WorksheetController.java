package application.worksheet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WorksheetController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);

    private static int delta = 0;

    public enum SheetStatus {
        CREATE_RECTANGLE_GATE,
        DEFAULT
    }

    private SheetStatus sheetStatus = SheetStatus.DEFAULT;

    @FXML
    private AnchorPane canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void createScatterChart() {
        ScatterChart<Number, Number> scatter = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        int loc = getDelta();
        scatter.setLayoutX(loc);
        scatter.setLayoutY(loc);
        scatter.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && sheetStatus == SheetStatus.CREATE_RECTANGLE_GATE) {
                System.out.println("create rect");
                setSheetStatus(SheetStatus.DEFAULT);
            }
        });
        canvas.getChildren().add(scatter);
    }

    @FXML
    protected void createRectGate() {
        log.info("sheet is on creating rectangle status");
        setSheetStatus(SheetStatus.CREATE_RECTANGLE_GATE);
    }

    public void setSheetStatus(SheetStatus sheetStatus) {
        this.sheetStatus = sheetStatus;
    }

    private int getDelta() {
        if (delta > 50) {
            delta = 10;
        } else {
            delta += 10;
        }
        return delta;
    }
}
