package application.event;

import java.util.List;

/**
 * channel -> worksheet, ask worksheet to add cell feature.
 */
public class CellFeatureCapturedEvent {

    private List<Float> cellFeature;

    public CellFeatureCapturedEvent(List<Float> cellFeature) {
        this.cellFeature = cellFeature;
    }

    public List<Float> getCellFeature() {
        return cellFeature;
    }
}
