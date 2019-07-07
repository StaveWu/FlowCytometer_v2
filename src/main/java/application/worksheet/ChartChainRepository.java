package application.worksheet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChartChainRepository {

    private static final Logger log = LoggerFactory.getLogger(ChartChainRepository.class);


    private Gson gson = new Gson();
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    private void checkLocation() {
        if (location == null) {
            throw new RuntimeException("chart chain repository location has not been set");
        }
    }

    public void saveAll(List<ChartChain> chains) throws IOException {
        checkLocation();
        Files.write(Paths.get(location), gson.toJson(chains).getBytes());
    }

    public List<ChartChain> findAll() {
        List<ChartChain> chains = new ArrayList<>();
        try (Reader reader = new FileReader(location)) {
            chains = gson.fromJson(reader, new TypeToken<List<ChartChain>>(){}.getType());
        } catch (IOException e) {
            log.info("never mind for io error occurring when load all chart chain");
        }
        return chains;
    }
}
