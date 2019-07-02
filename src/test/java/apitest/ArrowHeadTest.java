package apitest;

import application.chart.ArrowHead;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ArrowHeadTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane box = new AnchorPane();
        final Scene scene = new Scene(box,400, 400);
        scene.setFill(null);

        ArrowHead arrowHead = new ArrowHead(100, 100, 110, 200);
        box.getChildren().add(arrowHead);

        Button button = new Button("change");
        button.setOnAction(value ->{
            arrowHead.setStart(100, 100);
            arrowHead.setEnd(215, 235);
        });

        box.getChildren().add(button);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
