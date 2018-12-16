package projectTree;

public class TreeItemInfo {

    public enum FileType {
        File,
        Folder,
        Porject
    }

    private String name;
    private FileType type;

    public TreeItemInfo() {}

    public TreeItemInfo(String name, FileType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
