package application.channel.model;

import application.starter.FCMRunTimeConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelModelRepository {

    private Gson gson = new Gson();
    private String modelsPath = FCMRunTimeConfig.getInstance()
            .getProjectConfigFolder() + File.separator + "channels.json";

    private static final Logger log = LoggerFactory.getLogger(ChannelModel.class);

    /**
     * Singleton
     */
    private static ChannelModelRepository instance;
    private ChannelModelRepository() {}
    public static ChannelModelRepository getInstance() {
        if (instance == null) {
            instance = new ChannelModelRepository();
        }
        return instance;
    }

    public List<ChannelModel> findAll() {
        List<ChannelModel> models = new ArrayList<>();
        try (Reader reader = new FileReader(modelsPath)) {
            List<ChannelModel.JsonObject> jsonModels = gson.fromJson(reader,
                    new TypeToken<List<ChannelModel.JsonObject>>(){}.getType());
            jsonModels.stream().map(ChannelModel::fromJsonObject).forEach(models::add);
        } catch (IOException e) {
            log.info("Channel models loading failed: " + e.getMessage());
        }
        return models;
    }

    public void saveAll(List<ChannelModel> models) throws IOException {
        List<ChannelModel.JsonObject> json_models = models.stream()
                .map(ChannelModel::toJsonObject)
                .collect(Collectors.toList());
        Files.write(Paths.get(modelsPath), gson.toJson(json_models).getBytes());
    }

}
