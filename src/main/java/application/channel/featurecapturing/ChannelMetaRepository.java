package application.channel.featurecapturing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChannelMetaRepository {

    private Gson gson = new Gson();
    private String location;

    public ChannelMetaRepository() {}

    public void setLocation(String location) {
        this.location = location;
    }

    private void checkLocation() {
        if (location == null) {
            throw new RuntimeException("channel featurecapturing repository location has not been set");
        }
    }
    /**
     * Expose models caches to outside, so we do not need
     * to care repository update, it will update automatically.
     * @return
     */
    public List<ChannelMeta> findAll() {
        checkLocation();
        List<ChannelMeta> models = new ArrayList<>();
        try (Reader reader = new FileReader(location)) {
            List<ChannelMeta.JsonObject> jsonModels = gson.fromJson(reader,
                    new TypeToken<List<ChannelMeta.JsonObject>>(){}.getType());
            jsonModels.stream().map(ChannelMeta::fromJsonObject).forEach(models::add);
        } catch (IOException e) {
            // do nothing
        }
        return models;
    }

    /**
     * This is a bit different from spring repository interface,
     * the entities(ChannelModels in here) have been cached, so
     * we just dump them to disk without passing models parameter
     * to this method again.
     * @throws IOException
     */
    public void saveAll(List<ChannelMeta> models) throws IOException {
        checkLocation();
        List<ChannelMeta.JsonObject> jsonModels = models.stream()
                .map(ChannelMeta::toJsonObject)
                .collect(Collectors.toList());
        Files.write(Paths.get(location), gson.toJson(jsonModels).getBytes());
    }

}
