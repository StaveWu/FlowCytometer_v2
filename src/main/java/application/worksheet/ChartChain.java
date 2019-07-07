package application.worksheet;

import java.util.ArrayList;
import java.util.List;

public class ChartChain {

    List<Integer> ids = new ArrayList<>();

    public void add(int chartId) {
        ids.add(chartId);
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return ids.toString();
    }
}
