package application.projectTree;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProjectTreeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(Resource.getFXML("project_tree.fxml"));
        Scene scene = new Scene(new ProjectTree());
        primaryStage.setTitle("Project Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
