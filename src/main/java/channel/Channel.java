package channel;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import utils.Resource;

import java.io.IOException;

public class Channel extends ScrollPane {

    public Channel() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("channel.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
