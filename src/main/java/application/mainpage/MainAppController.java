package application.mainpage;

import application.projectTree.ProjectTree;
import application.starter.StarterController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainAppController {

    private static final Logger log = LoggerFactory.getLogger(MainAppController.class);

    @FXML
    private MenuBar menuBar;

    @FXML
    private ProjectTree projectTree;

    private StarterController parentController;

    public void setParentController(StarterController parentController) {
        this.parentController = parentController;
    }

    @FXML
    protected void closeProject() {
        log.info("try showing application.starter...");
        if (parentController != null) {
            parentController.show();
            closeSelf();
        } else {
            Platform.exit();
            log.info("show failure, application.starter is null.");
        }
    }

    private void closeSelf() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }
}
