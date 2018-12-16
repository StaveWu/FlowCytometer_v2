package projectTree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import starter.FCMConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class MyTreeView extends TreeView {

    public MyTreeView() {
        super();
        try {
            TreeItem<TreeItemInfo> root = traverse(FCMConfig.getWorkspaceDir());
            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TreeItem<TreeItemInfo> traverse(String start) throws IOException {
        Path startPath = Paths.get(start);
        TreeItemInfo itemInfo = new TreeItemInfo(startPath.getFileName().toString(),
                TreeItemInfo.FileType.Porject);
        TreeItem<TreeItemInfo> res = new TreeItem<>(itemInfo, TreeIconManager.getTreeItemIcon(itemInfo));
        traverseHelper(startPath, res);
        return res;
    }

    private void traverseHelper(Path path, TreeItem<TreeItemInfo> item) throws IOException {
        if (Files.isDirectory(path)) {
            for (Path child: Files.list(path).collect(Collectors.toList())
            ) {
                TreeItemInfo itemInfo = new TreeItemInfo();
                itemInfo.setName(child.getFileName().toString());
                itemInfo.setType(Files.isDirectory(child) ?
                        TreeItemInfo.FileType.Folder : TreeItemInfo.FileType.File);

                TreeItem<TreeItemInfo> childItem = new TreeItem<>(itemInfo,
                        TreeIconManager.getTreeItemIcon(itemInfo));
                item.getChildren().add(childItem);
                traverseHelper(child, childItem);
            }
        }
    }

}
