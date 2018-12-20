package application.channel;

public class ChannelInfoPojo {

    private int channelId;
    private String channelName;
    private double voltage;
    private double threshold;
    private String peakPolicy;

    public ChannelInfoPojo() {}

    public ChannelInfoPojo(int channelId, String channelName, double voltage, double threshold, String peakPolicy) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.voltage = voltage;
        this.threshold = threshold;
        this.peakPolicy = peakPolicy;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getPeakPolicy() {
        return peakPolicy;
    }

    public void setPeakPolicy(String peakPolicy) {
        this.peakPolicy = peakPolicy;
    }

    @Override
    public String toString() {
        return "ChannelInfoPojo{" +
                "channelId=" + channelId +
                ", channelName='" + channelName + '\'' +
                ", voltage=" + voltage +
                ", threshold=" + threshold +
                ", peakPolicy='" + peakPolicy + '\'' +
                '}';
    }
}
