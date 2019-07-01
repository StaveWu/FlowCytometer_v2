package application.event;

import application.channel.model.ChannelMeta;

import java.util.List;

/**
 * channel -> dashboard, ask dashboard to follow channel number changing.
 */
public class ChannelChangedEvent {

    private List<ChannelMeta> channelMetas;

    public ChannelChangedEvent(List<ChannelMeta> channelMetas) {
        this.channelMetas = channelMetas;
    }

    public List<ChannelMeta> getChannelMetas() {
        return channelMetas;
    }
}
