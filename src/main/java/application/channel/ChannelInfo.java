package application.channel;

import javafx.beans.property.*;

public class ChannelInfo {

    private IntegerProperty channelId = new SimpleIntegerProperty(1);
    private StringProperty channelName = new SimpleStringProperty("");
    private DoubleProperty voltage = new SimpleDoubleProperty();
    private DoubleProperty threshold = new SimpleDoubleProperty();
    private StringProperty peakPolicy = new SimpleStringProperty("Area");

    public ChannelInfo() {}

    public ChannelInfo(int cid, String cname, double cvol, double cthre, String cpolicy) {
        channelId.setValue(cid);
        channelName.setValue(cname);
        voltage.setValue(cvol);
        threshold.setValue(cthre);
        peakPolicy.setValue(cpolicy);
    }

    public int getChannelId() {
        return channelId.get();
    }
    public void setChannelId(int channelId) {
        this.channelId.set(channelId);
    }

    public double getThreshold() {
        return threshold.get();
    }
    public void setThreshold(double threshold) {
        this.threshold.set(threshold);
    }

    public double getVoltage() {
        return voltage.get();
    }
    public void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }

    public String getChannelName() {
        return channelName.get();
    }
    public void setChannelName(String channelName) {
        this.channelName.set(channelName);
    }

    public String getPeakPolicy() {
        return peakPolicy.get();
    }
    public void setPeakPolicy(String peakPolicy) {
        this.peakPolicy.set(peakPolicy);
    }

    @Override
    public String toString() {
        return String.format("[channelId=%d, name=%s, voltage=%f, threshold=%f, peakPolicy=%s]",
                getChannelId(), getChannelName(), getVoltage(), getThreshold(), getPeakPolicy());
    }

    public IntegerProperty channelIdProperty() {
        return channelId;
    }
    public StringProperty channelNameProperty() {
        return channelName;
    }
    public DoubleProperty voltageProperty() {
        return voltage;
    }
    public DoubleProperty thresholdProperty() {
        return threshold;
    }
    public StringProperty peakPolicyProperty() {
        return peakPolicy;
    }

}
