package application.projecttree;

import application.starter.FCMRunTimeConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProjectTreeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FCMRunTimeConfig.getInstance().setRootDir("E:\\04文档\\陈宇欣\\流式细胞仪\\软件项目树测试");
        Scene scene = new Scene(new ProjectTree());
        primaryStage.setTitle("Project Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
