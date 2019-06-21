package application.dashboard;

import application.dashboard.device.CommDeviceEventAdapter;
import application.dashboard.device.ICommDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CircuitBoard {

    private static final Logger log = LoggerFactory.getLogger(CircuitBoard.class);

    private ICommDevice commDevice;
    private DataReceivedHandler handler;
    private int numChannels = 0;
    private boolean isOnSampling = true;

    private void checkCommDevice() {
        if (commDevice == null) {
            throw new RuntimeException("communication device has not been set");
        }
    }

    public void connect() throws Exception {
        commDevice.connect();
    }

    public boolean isConnected() {
        return commDevice.isConnected();
    }

    public void resetSystem() throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("ResetSystem");
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void startSampling(List<String> channelIds) throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("StartSampling", channelIds.toArray());
        isOnSampling = true;
        numChannels = channelIds.size();
        commDevice.write(msg.getBytes());
        log.info(msg);
        commDevice.read();
    }

    public void stopSampling() throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("StopSampling");
        isOnSampling = false;
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void setVoltage(String channelId, String voltage) throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("SetVoltage", channelId, voltage);
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void setFrequency(String frequency) throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("SetFrequency", frequency);
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void setValve(String valveId, boolean enabled) throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("SetValve", valveId, enabled ? 0 : 1);
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void setSupValve(String supValveId, String rate) throws Exception {
        checkCommDevice();
        String msg = getCommandMessage("SetSupValve", supValveId, rate);
        commDevice.write(msg.getBytes());
        log.info(msg);
    }

    public void setCommDevice(ICommDevice device) {
        commDevice = device;
        commDevice.setDataReceivedHandler(new CommDeviceEventAdapter() {
            @Override
            public void dataEventOccurred(UsbPipeDataEvent event) {
                byte[] data = event.getData();
                System.out.println(Arrays.toString(data));
                List<List<Double>> decoded = decode(data, numChannels);
                System.out.println(decoded.size());
                System.out.println(decoded);
                handler.onDataReceived(decoded);
                if (isOnSampling) {
                    try {
                        commDevice.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void errorEventOccurred(UsbPipeErrorEvent event) {
                event.getUsbException().printStackTrace();
            }
        });
    }

    public void setDataReceivedHandler(DataReceivedHandler handler) {
        this.handler = handler;
    }

    public static String getCommandMessage(String cmd) {
        return cmd + ":";
    }

    public static String getCommandMessage(String cmd, Object...args) {
        StringBuilder res = new StringBuilder(cmd);
        res.append(":");
        for (Object arg :
                args) {
            res.append(arg);
            res.append("_");
        }
        res.delete(res.length() - 1, res.length());
        return res.toString();
    }

    private static final int BYTES_PER_CHANNEL = 4;
    public static List<List<Double>> decode(byte[] data, int numChannels) {
        List<List<Double>> res = new ArrayList<>();
        for (int i = 0; i < data.length / (BYTES_PER_CHANNEL * numChannels); i++) {
            List<Double> rows = new ArrayList<>();
            for (int j = 0; j < numChannels; j++) {
                // assign 4 bytes to hold channel data to transfer short type.
                byte[] bytes = new byte[BYTES_PER_CHANNEL];
                for (int k = 0; k < BYTES_PER_CHANNEL; k++) {
                    bytes[k] = data[i * numChannels * BYTES_PER_CHANNEL + BYTES_PER_CHANNEL * j + k];
                }
                // A method "a << 8 | b" to transfer short is not a good idea
                // since it would make a mistake in some situation, i.e. a = 60 and b = -128
                float ch = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                rows.add((double) ch);
            }
            res.add(rows);
        }
        return res;
    }

}
