package application.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import utils.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard extends VBox implements Initializable {

    @FXML
    private ComboBox<String> modeCombo;

    @FXML
    private ComboBox<String> connectionCombo;

    public Dashboard() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("dash_board.fxml"));
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
        modeCombo.getItems().add("按时间");
        modeCombo.getItems().add("按细胞个数");

        connectionCombo.getItems().add("串口");
        connectionCombo.getItems().add("USB");
        connectionCombo.getItems().add("通讯配置...");
    }
}
