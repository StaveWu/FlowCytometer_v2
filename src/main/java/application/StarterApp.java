package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import utils.Resource;

@SpringBootApplication
public class StarterApp extends Application {

    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root);
        primaryStage.setTitle("Starter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(StarterApp.class);
        ConfigurableApplicationContext context = builder.run(
                getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(Resource.getFXML("starter.fxml"));
        loader.setControllerFactory(context::getBean);
        root = loader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
