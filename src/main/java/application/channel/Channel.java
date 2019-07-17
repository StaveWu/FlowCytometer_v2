package application.channel;

import application.utils.ControlUtils;
import application.utils.Resource;
import application.utils.UiUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Channel extends VBox {

    private static final Logger log = LoggerFactory.getLogger(Channel.class);

    public Channel() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("channel.fxml"));
            loader.setRoot(this);
            loader.setController(ControlUtils.getController(ChannelController.class));
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "Channel加载失败",
                    e.getMessage()).showAndWait();
        }
    }
}
