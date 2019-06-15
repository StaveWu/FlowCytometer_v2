package application.dashboard.device;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

public class CommDataParserTest {

    @Test
    public void decode() {
        byte[] b = {63, -103, -102, -102};
        byte[] b1 = {-65, -103, -103, -102};
//        byte[] b2 = {65, 33, -103, -102};
        byte[] b2 = {-102, -103, 33, 65};
//        int c = 0;
//        c = c | (b[0] & 0xff) << 8 | b[1];

//        System.out.println(c);
        System.out.println(ByteBuffer.wrap(b).getFloat());
        System.out.println(ByteBuffer.wrap(b1).getFloat());
        System.out.println(ByteBuffer.wrap(b2).order(ByteOrder.LITTLE_ENDIAN).getFloat());
    }
}