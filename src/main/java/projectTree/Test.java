package projectTree;

import javafx.scene.control.TreeItem;
import sun.reflect.generics.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws IOException {
        Path start = Paths.get("D:\\softwares\\controlsfx\\controlsfx-8.40.14");
        Files.list(start).forEach(System.out::println);
        new Test().deleteRecursively("D:\\files");
    }

    public TreeItem<String> traverse(String start) throws IOException {
        Path startPath = Paths.get(start);
        TreeItem<String> res = new TreeItem<>(startPath.getFileName().toString());
        traverseHelper(startPath, res);
        return res;
    }

    private void traverseHelper(Path path, TreeItem<String> item) throws IOException {
        if (Files.isDirectory(path)) {
            for (Path child: Files.list(path).collect(Collectors.toList())
                 ) {
                TreeItem<String> childItem = new TreeItem<>(child.getFileName().toString());
                item.getChildren().add(childItem);
                traverseHelper(child, childItem);
            }
        }
    }

    private void deleteRecursively(String filename) throws IOException {
        Files.walkFileTree(Paths.get(filename), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("delete: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("now delete: " + dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
