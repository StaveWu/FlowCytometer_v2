package application.event;

import java.util.List;

/**
 * channel -> worksheet, ask worksheet to add a new cell feature
 */
public class SpectraPeakCapturedEvent {
    private List<Double> cellFeature;

    public SpectraPeakCapturedEvent(List<Double> cellFeature) {
        this.cellFeature = cellFeature;
    }

    public List<Double> getCellFeature() {
        return cellFeature;
    }
}
