package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import utils.Resource;

import java.util.Arrays;

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
