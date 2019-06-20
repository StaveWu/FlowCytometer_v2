package application.event;

import java.util.List;

/**
 * channel -> worksheet, ask worksheet to add a new cell feature
 */
public class SpectraPeakCapturedEvent {
    private List<Double> cellFeatures;

    public SpectraPeakCapturedEvent(List<Double> cellFeatures) {
        this.cellFeatures = cellFeatures;
    }

    public List<Double> getCellFeatures() {
        return cellFeatures;
    }
}
