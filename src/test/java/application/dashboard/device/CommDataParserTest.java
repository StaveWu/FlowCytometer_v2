package application.dashboard.device;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class CommDataParserTest {

    @Test
    public void decode() {
        byte[] b = {60, -128};
        System.out.println(ByteBuffer.wrap(b).getShort());
    }
}