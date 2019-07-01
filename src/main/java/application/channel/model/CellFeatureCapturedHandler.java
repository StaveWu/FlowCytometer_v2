package application.channel.model;

import application.event.CellFeatureCapturedEvent;

public interface CellFeatureCapturedHandler {
    void cellFeatureCaptured(CellFeatureCapturedEvent event);
}
