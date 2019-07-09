package application.chart.gate;

import javafx.scene.chart.Axis;

import java.util.List;

public interface GatableChart<X, Y> {

    void setGate(Gate<X, Y> gate);

    void removeGate();

    Gate<X, Y> getGate();

    void addData(KVData data);

    void clearAllData();

    void replotChartData();

    List<KVData> getGatedData();

    boolean isGated(KVData data);

    void setAxisCandidateNames(List<String> names);

    void addGateLifeCycleListener(GateLifeCycleListener listener);

    List<KVData> getKVData();

    Axis<X> getXAxis();

    Axis<Y> getYAxis();
}
