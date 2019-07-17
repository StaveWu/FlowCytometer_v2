package application.starter;

import application.mainpage.MainAppController;
import application.utils.Resource;
import application.utils.UiUtils;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class StarterController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StarterController.class);

    private static final FCMRunTimeConfig globalConfig = FCMRunTimeConfig.getInstance();

    @FXML
    private ListView<ProjectInfo> listView;

    @Autowired
    private ProjectInfoRepository repository;

    @Autowired
    private ConfigurableApplicationContext context;

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
                    globalConfig.setRootDir(cell.getItem().getAbsolutePath());
                    openProj();
                    StarterController.this.hide();
                }
            });
            return cell;
        });
    }

    private void openProj() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.getFXML("main_app.fxml"));
            loader.setController(context.getBean(MainAppController.class));
            Parent root = loader.load();
            MainAppController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle(getTitle(globalConfig.getRootDir()));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "项目打开失败",
                    e.getMessage()).showAndWait();
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
            globalConfig.setRootDir(abspath);
            // create a config folder for this project
            try {
                log.info("creating config folder: " + globalConfig.getProjectConfigFolder());
                Files.createDirectory(Paths.get(globalConfig.getProjectConfigFolder()));
            } catch (Exception e) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "创建项目失败",
                        e.getMessage());
                return;
            }
            log.info("project is created on " + abspath);
            repository.save(new ProjectInfo(
                    Paths.get(abspath).getFileName().toString(), abspath));
            openProj();
            hide();
        }
    }

    private boolean checkProjectConfigFolder(String folderpath) {
        return Files.exists(Paths.get(folderpath));
    }

    @FXML
    protected void importProject() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File directory = directoryChooser.showDialog(listView.getScene().getWindow());
        if (directory != null) {
            String abspath = directory.getAbsolutePath();
            globalConfig.setRootDir(abspath);

            if (!checkProjectConfigFolder(globalConfig.getProjectConfigFolder())) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "导入失败",
                        "所导入的文件夹不是项目文件夹！");
                return;
            }
            log.info("project at " + abspath + " is imported.");
            repository.save(new ProjectInfo(
                    Paths.get(abspath).getFileName().toString(), abspath));
            openProj();
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
