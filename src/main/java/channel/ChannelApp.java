package channel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChannelApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Channel channel = new Channel();
        Scene scene = new Scene(channel);
        primaryStage.setTitle("Channel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
