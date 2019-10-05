package application.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilsTest {

    @Test
    public void getMean() {
        double[] data = new double[] {1.0, 2.0, 3.0, 4.0};
        System.out.println(MathUtils.getMean(data));
        assertEquals(2.5f, MathUtils.getMean(data));
    }
}