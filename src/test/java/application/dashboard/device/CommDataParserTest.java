package application.dashboard.device;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class CommDataParserTest {

    @Test
    public void decode() {
        byte[] b = {60, -128};
        int c = 0;
        c = c | (b[0] & 0xff) << 8 | b[1];

        System.out.println(c); // Output: -128
        System.out.println(ByteBuffer.wrap(b).getShort()); // Output: 15488
    }
}