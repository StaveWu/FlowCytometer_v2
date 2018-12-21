package apitest;

import org.junit.Test;

import java.time.Clock;
import java.time.Duration;

public class ClockTest {

    @Test
    public void testClock() {
        Clock clockDefaultZone = Clock.systemDefaultZone();
        Clock clocktick = Clock.tick(clockDefaultZone, Duration.ofSeconds(30));

        System.out.println("Clock Default Zone: " + clockDefaultZone.instant());
        System.out.println("Clock tick: " + clocktick.instant());
    }
}
