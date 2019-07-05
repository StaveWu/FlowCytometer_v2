package application.chart.gate;

import java.util.List;

public interface KVData {

    List<String> getNames();

    Float getValueByName(String name);
}
