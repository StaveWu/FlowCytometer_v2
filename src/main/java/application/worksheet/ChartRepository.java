package application.worksheet;

import application.chart.ChartWrapper;
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
public class ChartRepository {

    private Gson gson = new Gson();
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    private void checkLocation() {
        if (location == null) {
            throw new RuntimeException("chart repository location has not been set");
        }
    }

    public void saveAll(List<ChartWrapper> charts) throws IOException {
        checkLocation();
        List<ChartWrapper.JsonObject> jsonCharts = charts.stream()
                .map(ChartWrapper::toJsonObject)
                .collect(Collectors.toList());
        Files.write(Paths.get(location), gson.toJson(jsonCharts).getBytes());
    }

    public List<ChartWrapper> findAll() {
        checkLocation();
        List<ChartWrapper> charts = new ArrayList<>();
        try (Reader reader = new FileReader(location)) {
            List<ChartWrapper.JsonObject> jsonCharts = gson.fromJson(reader,
                    new TypeToken<List<ChartWrapper.JsonObject>>(){}.getType());
            jsonCharts.stream().map(ChartWrapper::fromJsonObject).forEach(charts::add);
        } catch (IOException e) {
            // do nothing
        }
        return charts;
    }
}
