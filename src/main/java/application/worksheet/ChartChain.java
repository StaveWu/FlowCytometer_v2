package application.worksheet;

import java.util.ArrayList;
import java.util.List;

public class ChartChain {

    List<String> ids = new ArrayList<>();

    public void add(String chartId) {
        ids.add(chartId);
    }

    public List<String> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return ids.toString();
    }
}
