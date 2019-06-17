package apitest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestStream {

    @Test
    public void testStream() {
        List<Integer> emptyList = new ArrayList<>();
        List<Integer> res = emptyList.stream().map(ele -> ele + 1).collect(Collectors.toList());
        System.out.println(res);
    }
}
