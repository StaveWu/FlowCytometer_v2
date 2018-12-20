package application.dashboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashBoardApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Dashboard dashboard = new Dashboard();

        primaryStage.setScene(new Scene(dashboard));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
