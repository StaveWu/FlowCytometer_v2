package application.starter;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;

/**
 * A singleton class that used for save runtime information.
 */
@ThreadSafe
public class FCMRunTimeConfig {

    private volatile static FCMRunTimeConfig instance;

    public static final String PROJECT_CONFIG_FOLDER_NAME = ".fcm";

    private String rootDir;

    private FCMRunTimeConfig() {}

    public static FCMRunTimeConfig getInstance() {
        // double check locking
        if (instance == null) {
            synchronized (FCMRunTimeConfig.class) {
                if (instance == null) {
                    instance = new FCMRunTimeConfig();
                }
            }
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
