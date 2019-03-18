package application.event;

public class ChannelChangedEvent {

    private int numChannels;

    public ChannelChangedEvent(int numChannels) {
        this.numChannels = numChannels;
    }

    public int getNumChannels() {
        return numChannels;
    }
}
