package application.projecttree;

import application.event.ChannelDataLoadAction;
import application.event.EventBusFactory;
import application.starter.FCMRunTimeConfig;
import application.utils.Resource;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

public class ProjectTree extends VBox implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProjectTree.class);
    private String rootDir = FCMRunTimeConfig.getInstance().getRootDir();
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private TreeView<TreeItemInfo> treeView;

    public ProjectTree() {
        eventBus.register(this);
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("project_tree.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            log.info("Traverse dir: " + rootDir);
            treeView.setRoot(traverse(rootDir));
            treeView.getRoot().setExpanded(true);
            treeView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node instanceof Text
                            || (node instanceof TreeCell
                            && ((TreeCell) node).getText() != null)) {
                        String path = getAbsolutePath(treeView.getSelectionModel().getSelectedItem());
                        if (new File(path).isDirectory() || !path.endsWith("txt")) {
                            // only load txt file
                            return;
                        }
                        eventBus.post(new ChannelDataLoadAction(path));
                    }
                }
            });

            // start a daemon thread for watching root directory
            Thread watchDirThread = new Thread(() -> {
                try {
                    new WatchDir(Paths.get(rootDir), true).processEvents();
                } catch (IOException e) {
                    UiUtils.getAlert(Alert.AlertType.ERROR, "项目树监控失败",
                             e.getMessage()).showAndWait();
                }
            });
            watchDirThread.setDaemon(true);
            watchDirThread.start();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, "项目树加载失败",
                     e.getMessage()).showAndWait();
        }
    }

    private TreeItem<TreeItemInfo> traverse(String start) throws IOException {
        Path startPath = Paths.get(start);
        TreeItemInfo itemInfo = new TreeItemInfo(startPath.getFileName().toString(),
                TreeItemInfo.FileType.Project);
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
                if (Files.isDirectory(child) && child.getFileName().toString().startsWith(".")) {
                    itemInfo.setType(TreeItemInfo.FileType.ConfigFolder);
                } else {
                    itemInfo.setType(Files.isDirectory(child) ?
                            TreeItemInfo.FileType.Folder : TreeItemInfo.FileType.File);
                }
                TreeItem<TreeItemInfo> childItem = new TreeItem<>(itemInfo,
                        TreeIconManager.getTreeItemIcon(itemInfo));
                item.getChildren().add(childItem);
                traverseHelper(child, childItem);
            }
        }
    }

    @FXML
    protected void newFile() {
        Optional<String> res = new TextInputDialog().showAndWait();
        if (res.isPresent()) {
            String filename = res.get();
            if (!checkFileName(filename)) {
                // pop up an warning dialog
                UiUtils.getAlert(Alert.AlertType.WARNING, "创建失败",
                        "非法文件名！").showAndWait();
                return;
            }
            Path target = Paths.get(getAbsolutePath(getSelectedNearestFolder()), filename);
            try {
                Files.createFile(target);
            } catch (FileAlreadyExistsException e) {
                Optional<ButtonType> choice = UiUtils.getAlert(Alert.AlertType.WARNING, null,
                        "文件名重复，是否要覆盖已有的文件？").showAndWait();
                if (choice.isPresent() && choice.get() == ButtonType.OK) {
                    // overwrite it!
                    try {
                        Files.write(target, "".getBytes(), StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (Exception e1) {
                        UiUtils.getAlert(Alert.AlertType.ERROR, "创建失败",
                                e1.getMessage()).showAndWait();
                    }
                }
            } catch (Exception e2) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "创建失败",
                        e2.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    protected void newFolder() {
        Optional<String> res = new TextInputDialog().showAndWait();
        if (res.isPresent()) {
            String foldername = res.get();
            if (!checkFileName(foldername)) {
                // pop up an warning dialog
                UiUtils.getAlert(Alert.AlertType.WARNING, "创建失败",
                        "非法文件名！").showAndWait();
                return;
            }
            Path target = Paths.get(getAbsolutePath(getSelectedNearestFolder()), foldername);
            try {
                Files.createDirectory(target);
            } catch (Exception e) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "创建失败",
                        e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    protected void remove() {
        TreeItem<TreeItemInfo> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "删除失败",
                    "请先选择要删除的文件！").showAndWait();
            return;
        }
        if (selectedItem  == treeView.getRoot()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "删除失败",
                    "根目录不允许删除！").showAndWait();
            return;
        }
        if (selectedItem.getValue().toString().equals(".fcm")) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "删除失败",
                    "配置文件不允许删除！").showAndWait();
            return;
        }
        Optional<ButtonType> res = UiUtils.getAlert(Alert.AlertType.CONFIRMATION, "删除确认",
                String.format("确定要删除%s吗？", selectedItem.getValue())).showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                delete(getAbsolutePath(selectedItem));
            } catch (Exception e) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "删除失败",
                        e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    protected void rename() {
        TreeItem<TreeItemInfo> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "重命名失败",
                    "请先选择要重命名的文件！").showAndWait();
            return;
        }

        if (selectedItem == treeView.getRoot()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "重命名失败",
                    "根目录暂不支持重命名！").showAndWait();
            return;
        }

        Optional<String> res = new TextInputDialog(selectedItem.getValue().getName()).showAndWait();
        if (res.isPresent()) {
            String newname = res.get();
            if (!checkFileName(newname)) {
                // pop up an warning dialog
                UiUtils.getAlert(Alert.AlertType.WARNING, "重命名失败",
                        "非法文件名！").showAndWait();
                return;
            }

            Path source = Paths.get(getAbsolutePath(selectedItem));
            Path destination = source.resolveSibling(newname);
            try {
                Files.move(source, destination);
                if (destination.toFile().isDirectory()) {
                    // refresh root, or source child item would miss.
                    refreshTreeView();
                }
            } catch (Exception e) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "重命名失败",
                        e.getMessage()).showAndWait();
            }

        }
    }

    @FXML
    protected void moveTo() {
        TreeItem<TreeItemInfo> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "移动文件失败",
                    "请先选择要移动的文件！").showAndWait();
            return;
        }

        File sourceFile = new File(getAbsolutePath(selectedItem));
        if (sourceFile.isDirectory()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "移动文件失败",
                    "暂不支持移动文件夹！").showAndWait();
            return;
        }

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Move to...");
        chooser.setInitialDirectory(new File(rootDir));
        File targetDir = chooser.showDialog(treeView.getScene().getWindow());

        // nothing selected
        if (targetDir == null) {
            return;
        }

        File targetFile = new File(targetDir, selectedItem.getValue().getName());
        if (targetFile.exists()) {
            Optional<ButtonType> choice = UiUtils.getAlert(Alert.AlertType.WARNING, null,
                    "目标文件夹中包含同名文件，是否要覆盖？").showAndWait();
            if (choice.isPresent() && choice.get() == ButtonType.OK) {
                try {
                    Files.move(sourceFile.toPath(),
                            targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtils.getAlert(Alert.AlertType.ERROR, "移动失败",
                            e.getMessage()).showAndWait();
                }
            }
        } else {
            try {
                Files.move(sourceFile.toPath(),
                        targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                UiUtils.getAlert(Alert.AlertType.ERROR, "移动失败",
                        e.getMessage()).showAndWait();
            }
        }
    }

    private void refreshTreeView() throws IOException {
        treeView.setRoot(traverse(rootDir));
        treeView.getRoot().setExpanded(true);
    }

    private String getAbsolutePath(TreeItem<TreeItemInfo> item) {
        LinkedList<String> pathList = new LinkedList<>();
        pathList.add(item.getValue().getName());

        TreeItem<TreeItemInfo> cur = item;
        while (cur.getParent() != null) {
            cur = cur.getParent();
            pathList.add(cur.getValue().getName());
        }
        pathList.removeLast(); // remove root since it has been included in rootDir.
        Collections.reverse(pathList);
        String res = String.join(File.separator, pathList);
        return rootDir + File.separator + res;
    }

    private boolean checkFileName(String name) {
        // check file name is valid or not
        if (name.trim().equals("")) {
            return false;
        }
        try {
            Paths.get(name);
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }

    protected void appendTreeItem(TreeItem<TreeItemInfo> parent, String filename, TreeItemInfo.FileType type) {
        TreeItemInfo info = new TreeItemInfo(filename, type);
        TreeItem<TreeItemInfo> newItem = new TreeItem<>(info, TreeIconManager.getTreeItemIcon(info));
        parent.getChildren().add(newItem);
    }

    private TreeItem<TreeItemInfo> getSelectedNearestFolder() {
        // select the first item by default
        TreeItem<TreeItemInfo> selectedItem = treeView.getSelectionModel().getSelectedItem();
        // if no item selected, return root, or the nearest folder else.
        return selectedItem == null ? treeView.getRoot() :
                (selectedItem.getValue().getType() == TreeItemInfo.FileType.File ?
                        selectedItem.getParent() : selectedItem);
    }

    private void delete(String filename) throws IOException {
        MoreFiles.deleteRecursively(Paths.get(filename), RecursiveDeleteOption.ALLOW_INSECURE);
    }

    private void removeTreeItem(TreeItem item) {
        item.getParent().getChildren().remove(item);
    }

    private TreeItem<TreeItemInfo> getTreeItemByPath(String pathname) {
        String pnStr = pathname.replace(rootDir, "");
        Path pn = Paths.get(pnStr);
        TreeItem<TreeItemInfo> res = treeView.getRoot();
        for (Path ele:
                pn) {
            for (TreeItem<TreeItemInfo> item:
                    res.getChildren()) {
                if (ele.toString().equals(item.getValue().getName())) {
                    res = item;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * An inner class, use to watch any change of specific directory
     */
    private class WatchDir {

        private final WatchService watcher;
        private final Map<WatchKey,Path> keys;
        private final boolean recursive;
        private boolean trace = false;

        @SuppressWarnings("unchecked")
        <T> WatchEvent<T> cast(WatchEvent<?> event) {
            return (WatchEvent<T>)event;
        }

        /**
         * Register the given directory with the WatchService
         */
        private void register(Path dir) throws IOException {
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            if (trace) {
                Path prev = keys.get(key);
                if (prev == null) {
                    System.out.format("register: %s\n", dir);
                } else {
                    if (!dir.equals(prev)) {
                        System.out.format("update: %s -> %s\n", prev, dir);
                    }
                }
            }
            keys.put(key, dir);
        }

        /**
         * Register the given directory, and all its sub-directories, with the
         * WatchService.
         */
        private void registerAll(final Path start) throws IOException {
            // register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException
                {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        /**
         * Creates a WatchService and registers the given directory
         */
        WatchDir(Path dir, boolean recursive) throws IOException {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey,Path>();
            this.recursive = recursive;

            if (recursive) {
                log.info(String.format("Scanning %s ...", dir));
                registerAll(dir);
                log.info("Scan Done!");
            } else {
                register(dir);
            }

            // enable trace after initial registration
            this.trace = true;
        }

        /**
         * Process all events for keys queued to the watcher
         */
        void processEvents() {
            for (;;) {

                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    log.info(String.format("%s: %s", event.kind().name(), child));

                    // update treeView
                    if (kind == ENTRY_CREATE) {
                        TreeItem<TreeItemInfo> parentItem = getTreeItemByPath(child.getParent().toString());
                        appendTreeItem(parentItem, child.getFileName().toString(), Files.isDirectory(child) ?
                                TreeItemInfo.FileType.Folder : TreeItemInfo.FileType.File);
                    } else if (kind == ENTRY_DELETE) {
                        removeTreeItem(getTreeItemByPath(child.toString()));
                    }

                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                            }
                        } catch (IOException x) {
                            // ignore to keep sample readable
                        }
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

}
