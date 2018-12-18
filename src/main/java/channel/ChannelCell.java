package channel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import utils.Resource;

import java.io.IOException;

public class ChannelCell extends VBox {

    @FXML
    private TextField voltageTextField;

    @FXML
    private TextField thresholdTextField;

    public ChannelCell() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("channel_cell.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
