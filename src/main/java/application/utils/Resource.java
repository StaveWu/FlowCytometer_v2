package application.utils;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;

/**
 * A class to load fxml, css and icons from resource folder.
 */
public class Resource {

    public static URL getFXML(String name) {
        return Resource.class.getClassLoader().getResource(String.format("fxml/%s", name));
    }

    public static Node getIcon(String name) {
        return new ImageView(new Image(Objects.requireNonNull(Resource.class.getClassLoader()
                .getResourceAsStream(String.format("icons/%s", name)))));
    }

    public static String getStyle(String name) {
        return Objects.requireNonNull(Resource.class.getClassLoader()
                .getResource(String.format("fxml/%s", name)))
                .toExternalForm();
    }

}
