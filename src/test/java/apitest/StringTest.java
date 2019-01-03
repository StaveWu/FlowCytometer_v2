package apitest;

import org.junit.Test;

public class StringTest {

    @Test
    public void testContains() {
        String a = "aaabbc";
        String b = "c";
        System.out.println(a.contains(b));
    }
}
