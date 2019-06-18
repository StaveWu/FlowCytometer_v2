package application.pandas;

import java.util.ArrayList;
import java.util.List;

public class DataSeries {

    private String name;
    private List<Double> data;

    public DataSeries() {
        this("");
    }

    public DataSeries(String name) {
        this(name, new ArrayList<>());
    }

    public DataSeries(String name, List<Double> data) {
        this.name = name;
        this.data = data;
    }

    public void addAll(List<Double> elements) {
        data.addAll(elements);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }
}
