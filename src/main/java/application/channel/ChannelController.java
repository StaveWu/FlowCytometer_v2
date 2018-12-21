package application.channel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import application.utils.Resource;
import application.utils.UiUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ChannelController implements Initializable {

    private ChannelModel model;

    @FXML
    private HBox channelsHBox;

    public ChannelController() { }

    /**
     * using lazy load, to guarantee project root dir being set before
     * @return
     */
    private ChannelModel getModel() {
        if (model == null) {
            model = new ChannelModel();
        }
        return model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getModel().getChannelInfos().stream().forEach(info -> addChannelCell(info));
    }

    private void addChannelCell(ChannelInfo info) {
        channelsHBox.getChildren().add(new ChannelCell(this, info));
    }

    @FXML
    protected void saveChannelInfos() {
        try {
            getModel().saveInfos();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "保存通道参数失败：" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void newChannelCell() {
        ChannelInfo info = new ChannelInfo();
        getModel().addChannelInfo(info);
        addChannelCell(info);
    }

    public void removeChannelCell(ChannelCell cell) {
        getModel().removeChannelInfo(cell.getChannelInfo());
        channelsHBox.getChildren().remove(cell);
    }

}
