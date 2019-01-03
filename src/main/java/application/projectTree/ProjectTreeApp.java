package application.projectTree;

import application.starter.FCMRunTimeConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProjectTreeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FCMRunTimeConfig.getInstance().setRootDir("E:\\01安装包\\eclipse-SDK-4.7.3a-win32-x86_64");
        Scene scene = new Scene(new ProjectTree());
        primaryStage.setTitle("Project Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
