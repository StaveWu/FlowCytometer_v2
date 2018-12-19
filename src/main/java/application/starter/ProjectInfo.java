package application.starter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProjectInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String absolutePath;

    public ProjectInfo() {}

    public ProjectInfo(String name, String absolutePath) {
        this.name = name;
        this.absolutePath = absolutePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, absolutePath);
    }
}
