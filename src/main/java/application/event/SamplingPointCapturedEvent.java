package application.event;

import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
public class SamplingPointCapturedEvent {

    private List<List<Double>> samplingPoint;

    public SamplingPointCapturedEvent(List<List<Double>> samplingPoint) {
        this.samplingPoint = samplingPoint;
    }

    public List<List<Double>> getSamplingPoint() {
        return samplingPoint;
    }
}
