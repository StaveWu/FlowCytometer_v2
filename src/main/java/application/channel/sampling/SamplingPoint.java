package application.channel.sampling;

import org.springframework.lang.NonNull;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to represent data when sampling, it's thread safe.
 */
@ThreadSafe
public class SamplingPoint {

    /**
     * Unmodifiable channel ids.
     */
    private final List<String> channelIds;

    /**
     * Unmodifiable coordinates.
     *
     * Several coordinates construct a sampling point,
     * each coordinate corresponding to the specific channel.
     */
    private final List<Float> coords;

    /**
     * Since the basic type of coordinate is Float, its bytes should be 4.
     */
    public static final int COORD_BYTES_LEN = 4;

    public SamplingPoint(@NonNull List<String> channelIds, @NonNull List<Float> coords) {
        // assign as unmodifiable lists
        this.channelIds = Arrays.asList(channelIds.toArray(new String[0]));
        this.coords = Arrays.asList(coords.toArray(new Float[0]));
        checkValid();
    }

    private void checkValid() {
        // this class only valid when channelIds size equals to coords size.
        if (this.channelIds.size() != this.coords.size()) {
            throw new IllegalArgumentException(String.format("Expect the same size of " +
                            "channelIds and coords but get %d channelIds and %d coords",
                    this.channelIds.size(), this.coords.size()));
        }
    }

    public List<Float> getCoords() {
        return new ArrayList<>(coords);
    }

    public Float coordOf(int i) {
        return coords.get(i);
    }

    public int size() {
        checkValid();
        return coords.size();
    }

    public List<String> getChannelIds() {
        return new ArrayList<>(channelIds);
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
