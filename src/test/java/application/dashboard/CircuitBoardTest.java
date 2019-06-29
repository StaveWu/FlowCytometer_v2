package application.dashboard;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CircuitBoardTest {

    @Test
    public void getCommandMessage() {
        assertEquals("ResetSystem:", CircuitBoard.getCommandMessage("ResetSystem"));
        assertEquals("StartSampling:PMT1_PMT3",
                CircuitBoard.getCommandMessage("StartSampling", "PMT1", "PMT3"));
        assertEquals("SetVoltage:PMT1_1.23",
                CircuitBoard.getCommandMessage("SetVoltage", "PMT1", 1.23));
        assertEquals("SetValve:0",
                CircuitBoard.getCommandMessage("SetValve", 0));
        assertEquals("SetFrequency:100000",
                CircuitBoard.getCommandMessage("SetFrequency", 100000));
    }

    @Test
    public void testDecode() {
        byte[] data = new byte[]{0, 0, 32, 65, 0, 0, 32, 65, 0, 0, 32, 65, 0, 0, 32, 65};
        System.out.println(CircuitBoard.decode(data, Arrays.asList("PMT")));
    }

}