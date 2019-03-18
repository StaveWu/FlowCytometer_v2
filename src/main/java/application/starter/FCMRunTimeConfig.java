package application.starter;

import java.io.File;

public class FCMRunTimeConfig {

    private static FCMRunTimeConfig instance = null;

    public static final String PROJECT_CONFIG_FOLDER_NAME = ".fcm";

    private String rootDir;

    private FCMRunTimeConfig() {}

    public static FCMRunTimeConfig getInstance() {
        if (instance == null) {
            instance = new FCMRunTimeConfig();
        }
        return instance;
    }

    public String getProjectConfigFolder() {
        return rootDir == null ? null : (rootDir + File.separator + PROJECT_CONFIG_FOLDER_NAME);
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
