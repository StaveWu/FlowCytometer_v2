package application.dashboard;

import application.utils.ControlUtils;
import application.utils.Resource;
import application.utils.UiUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Dashboard extends VBox {

    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);

    public Dashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("dash_board.fxml"));
            loader.setRoot(this);
            loader.setController(ControlUtils.getController(DashboardController.class));
            loader.load();
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "Dashboard加载失败：" + e.getMessage()).showAndWait();
        }
    }
}
