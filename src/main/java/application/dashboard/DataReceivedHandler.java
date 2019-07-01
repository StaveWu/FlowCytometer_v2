package application.dashboard;

import application.channel.sampling.SamplingPoint;

import java.util.List;

public interface DataReceivedHandler {
    void onDataReceived(List<SamplingPoint> points);
}
