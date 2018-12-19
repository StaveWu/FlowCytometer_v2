package application.starter;

import application.mainpage.MainAppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.Resource;
import utils.UiUtils;

import java.io.File;
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
        reloadListItems();
    }

    private void reloadListItems() {
        // reload items from database
        listView.getItems().clear();
        List<ProjectInfo> list = new ArrayList<>();
        repository.findAll().forEach(list::add);

        ObservableList<ProjectInfo> data = FXCollections.observableArrayList(list);
        listView.setItems(data);
        listView.setCellFactory(view -> {
            ProjectListCell cell = new ProjectListCell(this);
            cell.setOnMouseClicked(event -> {
                if (cell.getItem() != null) {
                    openProj(cell.getItem().getAbsolutePath());
                    StarterController.this.hide();
                }
            });
            return cell;
        });
    }

    private void openProj(String abspath) {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("main_app.fxml"));
            Parent root = loader.load();
            MainAppController controller = loader.getController();
            controller.setRootDir(abspath);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(getTitle(abspath));
            stage.setScene(new Scene(root));
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

    public void hide() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.hide();
    }

    public void show() {
        reloadListItems();
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.show();
    }

    @FXML
    protected void createProject() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File directory = directoryChooser.showDialog(listView.getScene().getWindow());
        if (directory != null) {
            String abspath = directory.getAbsolutePath();
            log.info("project is created on " + abspath);
            repository.save(new ProjectInfo(
                    Paths.get(abspath).getFileName().toString(), abspath));
            openProj(abspath);
            hide();
        }
    }

    @FXML
    protected void importProject() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File directory = directoryChooser.showDialog(listView.getScene().getWindow());
        if (directory != null) {
            String abspath = directory.getAbsolutePath();
            log.info("project at " + abspath + " is imported.");
            repository.save(new ProjectInfo(
                    Paths.get(abspath).getFileName().toString(), abspath));
            openProj(abspath);
            hide();
        }
    }

    /**
     * guarantee items and database to be updated together.
     */
    public void removeListItem(ProjectInfo item) {
        repository.delete(item);
        listView.getItems().remove(item);
        listView.refresh();
    }

}
