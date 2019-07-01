package application.channel.featurecapturing;

import application.event.CellFeatureCapturedEvent;

public interface CellFeatureCapturedHandler {
    void cellFeatureCaptured(CellFeatureCapturedEvent event);
}
