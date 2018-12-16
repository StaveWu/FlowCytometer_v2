package starter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;
import utils.Resource;

import java.io.IOException;

@Component
public class ProjectListCell extends ListCell<ProjectInfo> {

    @FXML
    private Label nameLabel;

    @FXML
    private Label pathLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private Hyperlink closeHyperlink;

    private FXMLLoader mloader;
    private ListView<ProjectInfo> listView;

    public ProjectListCell() {}

    public ProjectListCell(ListView<ProjectInfo> listView) {
        this.listView = listView;
    }

    @Override
    protected void updateItem(ProjectInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (empty | item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mloader == null) {
                mloader = new FXMLLoader(Resource.getFXML("project_list_cell.fxml"));
                mloader.setController(this);

                try {
                    mloader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            nameLabel.setText(item.getName());
            pathLabel.setText(item.getAbsolutePath());
            closeHyperlink.setOnMouseClicked(e -> {
                listView.getItems().remove(item);
                listView.refresh();
            });

            setText(null);
            setGraphic(gridPane);
        }
    }

}
