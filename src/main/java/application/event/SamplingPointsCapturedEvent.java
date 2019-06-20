package application.event;

import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
public class SamplingPointsCapturedEvent {

    private List<List<Double>> samplingPoints;

    public SamplingPointsCapturedEvent(List<List<Double>> samplingPoints) {
        this.samplingPoints = samplingPoints;
    }

    public List<List<Double>> getSamplingPoints() {
        return samplingPoints;
    }
}
