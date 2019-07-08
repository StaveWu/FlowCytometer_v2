package application.event;

/**
 * projecttree -> channel, ask channel to load data
 */
public class ChannelDataLoadAction {

    private String channelDataPath;

    public ChannelDataLoadAction(String channelDataPath) {
        this.channelDataPath = channelDataPath;
    }

    public String getChannelDataPath() {
        return channelDataPath;
    }
}
