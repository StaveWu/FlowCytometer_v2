package application.channel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import application.utils.Resource;
import application.utils.UiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Channel extends VBox implements Initializable {

    private ChannelModel model;

    @FXML
    private HBox channelsHBox;

    public Channel() {
        model = new ChannelModel();
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("channel.fxml"));
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
        model.getChannelInfos().stream().forEach(info -> addChannelCell(info));
    }

    private void addChannelCell(ChannelInfo info) {
        channelsHBox.getChildren().add(new ChannelCell(this, info));
    }

    @FXML
    protected void saveChannelInfos() {
        try {
            model.saveInfos();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "保存通道参数失败：" + e.getMessage());
        }
    }

    @FXML
    protected void newChannelCell() {
        ChannelInfo info = new ChannelInfo();
        model.addChannelInfo(info);
        addChannelCell(info);
    }

    public void removeChannelCell(ChannelCell cell) {
        model.removeChannelInfo(cell.getChannelInfo());
        channelsHBox.getChildren().remove(cell);
    }

}
