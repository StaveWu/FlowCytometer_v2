package application.dashboard.device;

import org.springframework.lang.NonNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to decode or encode data by convention.
 */
public class CommDataParser {

    public static byte[] encode(String cmd) {
        return cmd.getBytes();
    }

    /**
     * We specify the data to be arranged in 1H, 1L, 2H, 2L, 1H, 1L, 2H, 2L, ...
     * 1 and 2 indicate the id of channel.
     *
     * The method will decode data to voltage signal based on some plus and multiply rules.
     * For more details about these rules, please refer to device board docs.
     * @param data the data received from device.
     * @return the list
     */
    public static List<List<Double>> decode(@NonNull byte[] data, int numChannels) {
        List<List<Double>> res = new ArrayList<>();
        for (int i = 0; i < numChannels; i++) {
            res.add(new ArrayList<>());
        }

        for (int i = 0; i < data.length - 2 * numChannels; i += 2 * numChannels) {
            for (int j = 0; j < numChannels; j++) {
                // assign 2 bytes to hold channel data to transfer short type.
                byte[] bytes = new byte[2];
                bytes[0] = data[i + 2 * j];
                bytes[1] = data[i + 2 * j + 1];
                // A method "a << 8 | b" to transfer short is not a good idea
                // since it would make a mistake in some situation, i.e. a = 60 and b = -128
                int ch = ByteBuffer.wrap(bytes).getShort();
                res.get(j).add(toVoltageSignal(ch));
            }
        }
        return res;
    }

    private static double toVoltageSignal(int d) {
        return ((double) d) * 4.993 / (0.4138 * 65535.);
    }
}
