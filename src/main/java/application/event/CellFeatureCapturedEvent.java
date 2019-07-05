package application.event;

import java.util.Map;

/**
 * channel -> worksheet, ask worksheet to add cell feature.
 */
public class CellFeatureCapturedEvent {

    private Map<String, Float> cellFeature;

    public CellFeatureCapturedEvent(Map<String, Float> cellFeature) {
        this.cellFeature = cellFeature;
    }

    public Map<String, Float> getCellFeature() {
        return cellFeature;
    }
}
