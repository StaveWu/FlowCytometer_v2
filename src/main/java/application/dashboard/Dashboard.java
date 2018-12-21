package application.dashboard;

import application.utils.ApplicationContextUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationContext;
import application.utils.Resource;

import java.io.IOException;

public class Dashboard extends VBox {

    private final ApplicationContext springContext = ApplicationContextUtils.getContext();

    public Dashboard() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("dash_board.fxml"));
        loader.setRoot(this);
        loader.setController(springContext == null? new DashboardController() // manual constructor use to debug in small view
                : springContext.getBean(DashboardController.class));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
