package application.channel.model;

import application.starter.FCMRunTimeConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChannelModelRepository {

    private Gson gson = new Gson();
    private String location = FCMRunTimeConfig.getInstance()
            .getProjectConfigFolder() + File.separator + "channels.json";

    private static final Logger log = LoggerFactory.getLogger(ChannelModel.class);

    // channel models cache
    private List<ChannelModel> models = new ArrayList<>();

    public ChannelModelRepository() {}

    public void setLocation(String location) {
        this.location = location;
    }

    private void checkLocation() {
        if (location == null) {
            throw new RuntimeException("channel model repository location has not been set");
        }
    }
    /**
     * Expose models caches to outside, so we do not need
     * to care repository update, it will update automatically.
     * @return
     */
    public List<ChannelModel> findAll() {
        checkLocation();
        try (Reader reader = new FileReader(location)) {
            List<ChannelModel.JsonObject> jsonModels = gson.fromJson(reader,
                    new TypeToken<List<ChannelModel.JsonObject>>(){}.getType());
            jsonModels.stream().map(ChannelModel::fromJsonObject).forEach(models::add);
        } catch (IOException e) {
            log.info("Channel models loading failed: " + e.getMessage());
        }
        return models;
    }

    public void addModel(ChannelModel model) {
        checkLocation();
        models.add(model);
    }

    public void removeModel(ChannelModel model) {
        checkLocation();
        models.remove(model);
    }

    /**
     * This is a bit different from spring repository interface,
     * the entities(ChannelModels in here) have been cached, so
     * we just dump them to disk without passing models parameter
     * to this method again.
     * @throws IOException
     */
    public void saveAll() throws IOException {
        checkLocation();
        List<ChannelModel.JsonObject> json_models = models.stream()
                .map(ChannelModel::toJsonObject)
                .collect(Collectors.toList());
        Files.write(Paths.get(location), gson.toJson(json_models).getBytes());
    }

}
