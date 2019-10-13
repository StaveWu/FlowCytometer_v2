package application.channel.featurecapturing;

public class WaveCapturedEvent {

    private String channelId;

    public WaveCapturedEvent(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
}
