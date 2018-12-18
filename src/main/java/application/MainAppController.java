package application;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projectTree.ProjectTree;
import starter.StarterController;


public class MainAppController {

    private static final Logger log = LoggerFactory.getLogger(MainAppController.class);

    @FXML
    private MenuBar menuBar;

    @FXML
    private ProjectTree projectTree;

    public StringProperty rootDirProperty() {
        return projectTree.rootDirProperty();
    }
    public String getRootDir() {
        return rootDirProperty().get();
    }
    public void setRootDir(String value) {
        rootDirProperty().setValue(value);
    }

    private StarterController parentController;

    public void setParentController(StarterController parentController) {
        this.parentController = parentController;
    }

    @FXML
    protected void closeProject() {
        log.info("try showing starter...");
        if (parentController != null) {
            parentController.show();
            closeSelf();
        } else {
            log.info("show failure, starter is null.");
        }
    }

    private void closeSelf() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }
}
