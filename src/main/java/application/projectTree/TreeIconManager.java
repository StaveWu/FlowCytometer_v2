package application.projectTree;

import javafx.scene.Node;
import application.utils.Resource;

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
            case Project:
                return Resource.getIcon("projectfolder.png");
            case ConfigFolder:
                return Resource.getIcon("configfolder.png");
            default:
                return null;
        }
    }
}
