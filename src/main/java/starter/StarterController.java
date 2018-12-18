package starter;

import application.MainAppController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.Resource;
import utils.UiUtils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class StarterController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StarterController.class);

    @FXML
    private ListView<ProjectInfo> listView;

    @Autowired
    private ProjectInfoRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repository.save(new ProjectInfo("text1", "D:\\files\\文档\\test_project_tree"));
        repository.save(new ProjectInfo("text2", "D:\\files"));
        repository.save(new ProjectInfo("text3", "D:\\files\\a"));

        List<ProjectInfo> list = new ArrayList<>();
        repository.findAll().forEach(list::add);

        ObservableList<ProjectInfo> data = FXCollections.observableArrayList(list);
        listView.setItems(data);
        listView.setCellFactory(view -> new ProjectListCell(listView));
        listView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // open project by path
                    openProj(observable.getValue().getAbsolutePath());
                    closeSelf();
                });
        listView.getItems().addListener((ListChangeListener<ProjectInfo>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    repository.delete(c.getRemoved().get(0));
                }
                log.info("repository size: " + repository.count());
            }
        });
    }

    private void openProj(String abspath) {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("main_app.fxml"));
            Parent root = loader.load();
            MainAppController controller = loader.getController();
            controller.setRootDir(abspath);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(getTitle(abspath));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "项目打开失败：" + e.getMessage()).showAndWait();
        }
    }

    private String getTitle(String rootDir) {
        Path p = Paths.get(rootDir);
        return String.format("%s [%s] - Flow Cytometer", p.getFileName(), p.toString());
    }

    private void closeSelf() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void createProject(ActionEvent event) {
    }

    @FXML
    protected void importProject(ActionEvent event) {
    }

    @FXML
    protected void openProject(ActionEvent event) {

    }
}
