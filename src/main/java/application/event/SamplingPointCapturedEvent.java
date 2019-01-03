package application.event;

import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
public class SamplingPointCapturedEvent {

    private List<Double> samplingPoint;

    public SamplingPointCapturedEvent(List<Double> samplingPoint) {
        this.samplingPoint = samplingPoint;
    }

    public List<Double> getSamplingPoint() {
        return samplingPoint;
    }
}
