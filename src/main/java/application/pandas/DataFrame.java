package application.pandas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DataFrame implements Iterable<List<Double>> {

    private List<String> headers = new ArrayList<>();
    /**
     * save along columns
     */
    private List<List<Double>> values = new ArrayList<>();

    public DataFrame() {}

    public DataFrame(List<DataSeries> dataList) {
        for (DataSeries cd :
                dataList) {
            this.headers.add(cd.getName());
            this.values.add(cd.getData());
        }
    }

    public int[] shape() {
        int[] res = new int[2];
        if (values.isEmpty()) {
            return res;
        }
        res[0] = values.get(0).size();
        res[1] = values.size();
        return res;
    }

    public List<Double> rowAt(int i) {
        if (i < 0 || i > shape()[0]) {
            throw new NoSuchElementException();
        }
        List<Double> row = new ArrayList<>();

        for (int j = 0; j < shape()[1]; j++) {
            row.add(values.get(j).get(i));
        }
        return row;
    }

    public void addRow(List<Double> row) {
        if (row.size() != values.size()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < values.size(); i++) {
            values.get(i).add(row.get(i));
        }
    }

    public DataSeries of(String name) {
        int i = headers.indexOf(name);
        if (i < 0) {
            throw new NoSuchElementException();
        }
        return new DataSeries(name, values.get(i));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void dump(String path) throws IOException {
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), Charset.forName("utf-8"))) {
            writer.write(String.join("\t", headers));
            for (List<Double> row:
                 this) {
                writer.newLine();
                writer.write(String.join("\t", row.stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList())));
            }
        }
    }

    public static DataFrame load(String path) throws IOException {
        DataFrame res = new DataFrame();
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    String[] headers = line.split("\t");
                    res.setHeaders(Arrays.asList(headers));
                } else {
                    List<Double> data = Arrays.stream(line.split("\t"))
                            .map(Double::parseDouble)
                            .collect(Collectors.toList());
                    res.addRow(data);
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return String.format("DataFrame[headers=%s, values=%s]", headers, values);
    }

    @Override
    public Iterator<List<Double>> iterator() {
        return new DataRowIterator();
    }

    private class DataRowIterator implements Iterator<List<Double>> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < shape()[0];
        }

        @Override
        public List<Double> next() {
            List<Double> res = rowAt(cursor);
            cursor++;
            return res;
        }
    }
}
