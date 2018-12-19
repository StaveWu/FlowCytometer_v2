package application.projectTree;

import javafx.scene.Node;
import utils.Resource;

public class TreeIconManager {

    public static Node getTreeItemIcon(TreeItemInfo info) {
        if (info == null) {
            return null;
        }
        switch (info.getType()) {
            case File:
                return Resource.getIcon("file.png");
            case Folder:
                return Resource.getIcon("folder.png");
            case Porject:
                return Resource.getIcon("projectfolder.png");

                default:
                    return null;
        }
    }
}
