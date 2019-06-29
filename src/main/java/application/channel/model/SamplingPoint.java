package application.channel.model;

import org.springframework.lang.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SamplingPoint {

    private List<String> channelIds;
    private List<Float> coords;

    public static final int COORD_BYTES_LEN = 4;

    public SamplingPoint(@NonNull List<String> channelIds, @NonNull List<Float> coords) {
        if (channelIds.size() != coords.size()) {
            throw new IllegalArgumentException(String.format("Expect the same size of " +
                    "channelIds and coords but get %d channelIds and %d coords",
                    channelIds.size(), coords.size()));
        }
        this.channelIds = channelIds;
        this.coords = coords;
    }

    public List<Float> getCoords() {
        return coords;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public static SamplingPoint fromBytes(List<String> channelIds, byte[] bytes) {
        List<Float> coords = new ArrayList<>();
        for (int i = 0; i < bytes.length / COORD_BYTES_LEN; i++) {
            byte[] coordBytes = new byte[COORD_BYTES_LEN];
            for (int j = 0; j < COORD_BYTES_LEN; j++) {
                coordBytes[j] = bytes[COORD_BYTES_LEN * i + j];
            }
            Float coord = ByteBuffer.wrap(coordBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            coords.add(coord);
        }
        return new SamplingPoint(channelIds, coords);
    }

    @Override
    public String toString() {
        return String.format("SamplingPoint[channelIds=%s, coords=%s]", channelIds, coords);
    }
}
