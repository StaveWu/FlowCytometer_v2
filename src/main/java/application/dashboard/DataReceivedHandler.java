package application.dashboard;

import java.util.List;

public interface DataReceivedHandler {
    void onDataReceived(List<List<Double>> data);
}
