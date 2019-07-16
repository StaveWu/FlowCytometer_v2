package apitest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsListTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("h");
        list.add("h");
        list.add("h");
        List<String> unmodifiableList = Arrays.asList(list.toArray(new String[0]));
        System.out.println(unmodifiableList);
    }
}
