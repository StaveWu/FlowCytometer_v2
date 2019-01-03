package apitest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {

    @Test
    public void testPathStartWith() {
        Assert.assertTrue(Paths.get(".fcm").getFileName().toString().startsWith("."));
    }

    @Test
    public void testPathIterator() {
        Path p = Paths.get("E:\\01安装包\\.fcm");
        System.out.println(p.getFileName());
        for (Path ele:
                p) {
            System.out.println(ele);
        }
    }
}
