package application.mainpage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.utils.Resource;

public class FlowCytometerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("main_app.fxml"));
        loader.setController(new MainAppController());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Flow Cytometer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
