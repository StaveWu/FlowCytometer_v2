package application.dashboard.model;

import application.dashboard.TimeLimit;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeLimitTest {

    @Test
    public void totalSeconds() {
        TimeLimit timeLimit = new TimeLimit(1, 1, 1);
        assertEquals(3661, timeLimit.totalSeconds());
    }

    @Test
    public void formatSeconds() {
        TimeLimit timeLimit = TimeLimit.formatSeconds(3661);
        assertEquals(1, timeLimit.hours);
        assertEquals(1, timeLimit.minutes);
        assertEquals(1, timeLimit.seconds);
    }

    @Test
    public void toStringTest() {
        assertEquals("01:01:01", TimeLimit.formatSeconds(3661).toString());
    }
}