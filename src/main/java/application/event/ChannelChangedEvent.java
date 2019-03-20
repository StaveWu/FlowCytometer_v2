package application.event;

/**
 * channel -> dashboard, ask dashboard to follow channel number changing.
 */
public class ChannelChangedEvent {

    private int numChannels;

    public ChannelChangedEvent(int numChannels) {
        this.numChannels = numChannels;
    }

    public int getNumChannels() {
        return numChannels;
    }
}
