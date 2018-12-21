package application.worksheet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WorksheetApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Worksheet worksheet = new Worksheet();

        primaryStage.setScene(new Scene(worksheet));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
