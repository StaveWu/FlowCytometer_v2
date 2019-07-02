package application.event;

/**
 * projecttree -> channel, ask channel to load data
 */
public class ChannelDataOpenAction {

    private String channelDataPath;

    public ChannelDataOpenAction(String channelDataPath) {
        this.channelDataPath = channelDataPath;
    }

    public String getChannelDataPath() {
        return channelDataPath;
    }
}
