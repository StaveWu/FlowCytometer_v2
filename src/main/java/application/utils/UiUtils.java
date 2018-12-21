package application.utils;

import javafx.scene.control.Alert;

public class UiUtils {

    public static Alert getAlert(Alert.AlertType type, String header, String content) {
        Alert res = new Alert(type);
        res.setHeaderText(header);
        res.setContentText(content);
        return res;
    }
}
