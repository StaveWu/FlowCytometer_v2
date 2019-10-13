package application.channel.featurecapturing;

import java.util.ArrayList;
import java.util.List;

public class RecordData {

    private boolean isOnRecording = false;
    private List<Float> data = new ArrayList<>();

    private PendingValue pendingValue = new PendingValue();

    public void add(Float d) {
        if (isOnRecording) {
            data.add(d);
        } else {
            data.clear();
            pendingValue.set(d);
        }
    }

    public List<Float> getData() {
        return data;
    }

    public void stopRecording() {
        isOnRecording = false;
    }

    public void startRecording() {
        isOnRecording = true;
        if (pendingValue.isExisting()) {
            data.add(pendingValue.get());
            pendingValue.clear();
        }
    }
}
