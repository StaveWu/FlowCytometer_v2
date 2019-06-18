package application.channel.model;

import application.pandas.DataFrame;
import application.pandas.DataSeries;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DataFrameTest {

    private DataFrame dataFrame;

    @Before
    public void prepare_dataframe() {
        DataSeries s1 = new DataSeries("hello", new ArrayList<>(Arrays.asList(1.1, 2.2, 3.3)));
        DataSeries s2 = new DataSeries("world", new ArrayList<>(Arrays.asList(4.4, 5.5, 6.6)));
        dataFrame = new DataFrame(Arrays.asList(s1, s2));
    }

    @Test
    public void shape() {
        assertArrayEquals(new int[] {3, 2}, dataFrame.shape());
    }

    @Test
    public void iterator() {
        for (List<Double> row : dataFrame) {
            for (Double ele :
                    row) {
                System.out.print(ele + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void rowAt() {
        List<Double> row = dataFrame.rowAt(0);
        assertArrayEquals(new Double[] {1.1, 4.4}, row.toArray());
    }

    @Test
    public void addRow() {
        dataFrame.addRow(Arrays.asList(7.7, 8.8));
        assertArrayEquals(new Double[] {7.7, 8.8},
                dataFrame.rowAt(dataFrame.shape()[0] - 1).toArray());
    }
}