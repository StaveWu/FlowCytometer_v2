package application;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import projectTree.ProjectTree;

public class MainAppController {

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
}
