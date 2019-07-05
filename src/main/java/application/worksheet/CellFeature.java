package application.worksheet;

import application.chart.gate.KVData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CellFeature implements KVData {

    private Map<String, Float> feature;

    public CellFeature(Map<String, Float> feature) {
        this.feature = feature;
    }

    @Override
    public List<String> getNames() {
        return new ArrayList<>(feature.keySet());
    }

    @Override
    public Float getValueByName(String name) {
        return feature.get(name);
    }
}
