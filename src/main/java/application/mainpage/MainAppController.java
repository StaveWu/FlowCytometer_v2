package application.mainpage;

import application.channel.ChannelController;
import application.projectTree.ProjectTree;
import application.starter.StarterController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainAppController {

    private static final Logger log = LoggerFactory.getLogger(MainAppController.class);

    @FXML
    private MenuBar menuBar;

    @FXML
    private ProjectTree projectTree;

    @Autowired
    private ChannelController channelController;

    private StarterController parentController;

    public void setParentController(StarterController parentController) {
        this.parentController = parentController;
    }

    @FXML
    protected void closeProject() {
        saveProjectInformation();
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

    public void saveProjectInformation() {
        if (channelController != null) { // may start by manual, which would cause autowired failed
            channelController.saveChannelInformation();
        }
    }
}
