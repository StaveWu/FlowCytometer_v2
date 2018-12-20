package apitest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class PathTest {

    @Test
    public void testPathStartWith() {
        Assert.assertTrue(Paths.get(".fcm").getFileName().toString().startsWith("."));
    }
}
