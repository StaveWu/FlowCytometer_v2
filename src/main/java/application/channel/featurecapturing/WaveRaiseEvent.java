package application.channel.featurecapturing;

public class WaveRaiseEvent {

    private String channelId;

    public WaveRaiseEvent(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
}
