package application.worksheet;

import application.chart.WrappedChart;
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

    public void saveAll(List<WrappedChart> charts) throws IOException {
        checkLocation();
        List<WrappedChart.JsonObject> jsonCharts = charts.stream()
                .map(WrappedChart::toJsonObject)
                .collect(Collectors.toList());
        Files.write(Paths.get(location), gson.toJson(jsonCharts).getBytes());
    }

    public List<WrappedChart> findAll() {
        checkLocation();
        List<WrappedChart> charts = new ArrayList<>();
        try (Reader reader = new FileReader(location)) {
            List<WrappedChart.JsonObject> jsonCharts = gson.fromJson(reader,
                    new TypeToken<List<WrappedChart.JsonObject>>(){}.getType());
            jsonCharts.stream().map(WrappedChart::fromJsonObject).forEach(charts::add);
        } catch (IOException e) {
            // do nothing
        }
        return charts;
    }
}
