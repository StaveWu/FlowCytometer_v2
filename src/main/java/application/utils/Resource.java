package application.utils;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class Resource {

    private static final String BASE_URL = "../../../resources";

    public static URL getFXML(String name) {
        return Resource.class.getResource(String.format("%s/fxml/%s", BASE_URL, name));
    }

    public static Node getIcon(String name) {
        return new ImageView(new Image(Resource.class
                .getResourceAsStream(String.format("%s/icons/%s", BASE_URL, name))));
    }

    public static String getStyle(String name) {
        return Resource.class.getResource(String.format("%s/fxml/%s", BASE_URL, name))
                .toExternalForm();
    }

}
