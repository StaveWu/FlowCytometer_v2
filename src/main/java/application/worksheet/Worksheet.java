package application.worksheet;

import application.utils.ControlUtils;
import application.utils.Resource;
import application.utils.UiUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worksheet extends VBox {

    private static final Logger log = LoggerFactory.getLogger(Worksheet.class);

    public Worksheet() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("worksheet.fxml"));
            loader.setRoot(this);
            loader.setController(ControlUtils.getController(WorksheetController.class));
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "Worksheet加载失败",
                    e.getMessage()).showAndWait();
        }
    }
}
