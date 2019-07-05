package application.chart.gate;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface GatableChart {

    boolean isActive();

    boolean isLocated();

    void setRunningPoint(double x, double y);

    void addPoint(double x, double y);

    void setGate(Gate gate);

    void removeGate();

    void addData(KVData data);

    boolean isGated(KVData data);

    <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> handler);
}