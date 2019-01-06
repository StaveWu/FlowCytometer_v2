package application.chart;

import application.chart.gate.Gate;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface Gatable {

    boolean isActive();

    boolean isLocated();

    void setRunningPoint(double x, double y);

    void addPoint(double x, double y);

    void setGate(Gate gate);

    void removeGate();

    <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> handler);
}
