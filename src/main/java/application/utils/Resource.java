package application.utils;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class Resource {

    public static URL getFXML(String name) {
        return Resource.class.getResource(String.format("../../../resources/fxml/%s", name));
    }

    public static Node getIcon(String name) {
        return new ImageView(new Image(Resource.class
                .getResourceAsStream(String.format("../../../resources/icons/%s", name))));
    }

}
