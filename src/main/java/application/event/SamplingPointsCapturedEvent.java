package application.event;

import application.channel.model.SamplingPoint;

import java.util.List;

/**
 * dashboard -> channel, ask channel to add a new sample point
 */
public class SamplingPointsCapturedEvent {

    private List<SamplingPoint> samplingPoints;

    public SamplingPointsCapturedEvent(List<SamplingPoint> samplingPoints) {
        this.samplingPoints = samplingPoints;
    }

    public List<SamplingPoint> getSamplingPoints() {
        return samplingPoints;
    }
}
