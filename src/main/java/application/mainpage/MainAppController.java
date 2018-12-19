package application.mainpage;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import application.projectTree.ProjectTree;
import application.starter.StarterController;


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
        log.info("try showing application.starter...");
        if (parentController != null) {
            parentController.show();
            closeSelf();
        } else {
            log.info("show failure, application.starter is null.");
        }
    }

    private void closeSelf() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }
}
