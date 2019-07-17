package application.event;

import application.channel.sampling.SamplingPoint;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
@ThreadSafe
public class SamplingPointsCapturedEvent {

    private final List<SamplingPoint> samplingPoints;

    public SamplingPointsCapturedEvent(List<SamplingPoint> samplingPoints) {
        // unmodifiable list
        this.samplingPoints = Arrays.asList(samplingPoints.toArray(new SamplingPoint[0]));
    }

    public List<SamplingPoint> getSamplingPoints() {
        return samplingPoints;
    }
}
