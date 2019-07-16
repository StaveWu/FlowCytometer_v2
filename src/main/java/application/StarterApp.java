package application;

import application.utils.Resource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication
public class StarterApp extends Application {

    private Parent root;

    private Stage imageStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root);
        primaryStage.setTitle("Starter");
        primaryStage.setScene(scene);
        primaryStage.show();
        imageStage.hide();
    }

    @Override
    public void init() throws Exception {
        super.init();
        Platform.runLater(() -> {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(Objects.requireNonNull(Resource.class.getClassLoader()
                    .getResourceAsStream(String.format("icons/%s", "firstshow.gif")))));

            BorderPane pane = new BorderPane();
            pane.setCenter(imageView);

            Scene scene = new Scene(pane);
            scene.setFill(Color.TRANSPARENT);

            imageStage = new Stage();
            imageStage.initStyle(StageStyle.TRANSPARENT);
            imageStage.setScene(scene);
            imageStage.show();
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            imageStage.setX((primScreenBounds.getWidth() - imageStage.getWidth()) / 2);
            imageStage.setY((primScreenBounds.getHeight() - imageStage.getHeight()) / 2);
        });
        SpringApplicationBuilder builder = new SpringApplicationBuilder(StarterApp.class);
        ConfigurableApplicationContext context = builder.run(
                getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(Resource.getFXML("starter.fxml"));
        loader.setControllerFactory(context::getBean);
        root = loader.load();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
