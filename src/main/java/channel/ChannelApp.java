package channel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Resource;

public class ChannelApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(Resource.getFXML("channel.fxml"));
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
