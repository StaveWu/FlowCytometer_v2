package application.channel;

import com.google.gson.Gson;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChannelSetting {

    private IntegerProperty lookback = new SimpleIntegerProperty(50);

    private String location;
    private Gson gson = new Gson();

    public ChannelSetting(String location) {
        this.location = location;
        load();

        lookback.addListener((observable, oldValue, newValue) -> {
            dump();
        });
    }

    public void load() {
        try (Reader reader = new FileReader(location)) {
            ChannelSetting.JsonObject json = gson.fromJson(reader, ChannelSetting.JsonObject.class);
            setLookback(json.lookback);
        } catch (IOException e) {
            // do nothing
        }
    }

    public synchronized int getLookback() {
        return lookback.get();
    }

    public IntegerProperty lookbackProperty() {
        return lookback;
    }

    public synchronized void setLookback(int lookback) {
        this.lookback.set(lookback);
    }

    public void dump() {
        try {
            Files.write(Paths.get(location), gson.toJson(toJsonObject()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject toJsonObject() {
        return new JsonObject(lookback.get());
    }

    public class JsonObject {
        public final int lookback;

        public JsonObject(int lookback) {
            this.lookback = lookback;
        }
    }
}
