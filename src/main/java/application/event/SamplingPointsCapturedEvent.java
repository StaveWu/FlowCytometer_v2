package application.event;

import application.channel.sampling.SamplingPoint;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
@ThreadSafe
public class SamplingPointsCapturedEvent {

    private final List<SamplingPoint> samplingPoints;

    public SamplingPointsCapturedEvent(List<SamplingPoint> samplingPoints) {
        this.samplingPoints = new ArrayList<>(samplingPoints);
    }

    public List<SamplingPoint> getSamplingPoints() {
        return new ArrayList<>(samplingPoints);
    }
}
