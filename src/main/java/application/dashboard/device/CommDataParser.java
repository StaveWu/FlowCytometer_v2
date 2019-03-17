package application.dashboard.device;

import org.springframework.lang.NonNull;

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

        for (int i = 0; i < data.length; i += 2 * numChannels) {
            for (int j = 0; j < numChannels; j++) {
                int ch = data[i + 2 * j] << 8 | data[i + 2 * j + 1];
                res.get(j).add(toVoltageSignal(ch));
            }
        }
        return res;
    }

    private static double toVoltageSignal(int d) {
        return ((double) d) * 4.993 / 0.4138 * 65535.;
    }
}
