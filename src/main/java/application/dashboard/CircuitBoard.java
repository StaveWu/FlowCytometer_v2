package application.dashboard;

import application.dashboard.device.CommDeviceEventAdapter;
import application.dashboard.device.ICommDevice;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class CircuitBoard {

    private ICommDevice commDevice;
    private DataReceivedHandler handler;
    private int numChannels = 0;
    private boolean isOnSampling = false;

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
        commDevice.write(getCommandMessage("ResetSystem").getBytes());
    }

    public void startSampling(List<String> channelIds) throws Exception {
        checkCommDevice();
        isOnSampling = true;
        numChannels = channelIds.size();
        commDevice.write(getCommandMessage("StartSampling", channelIds).getBytes());
    }

    public void stopSampling() throws Exception {
        checkCommDevice();
        isOnSampling = false;
        commDevice.write(getCommandMessage("StopSampling").getBytes());
        commDevice.read();
    }

    public void setVoltage(String channelId, double voltage) throws Exception {
        checkCommDevice();
        commDevice.write(getCommandMessage("SetVoltage", channelId, voltage).getBytes());
    }

    public void setFrequency(long frequency) throws Exception {
        checkCommDevice();
        commDevice.write(getCommandMessage("SetFrequency", frequency).getBytes());
    }

    public void setValve(String valveId, boolean enabled) throws Exception {
        checkCommDevice();
        commDevice.write(getCommandMessage("SetValve", valveId, enabled ? 1 : 0).getBytes());
    }

    public void setSupValve(String supValveId, double rate) throws Exception {
        checkCommDevice();
        commDevice.write(getCommandMessage("SetSupValve", supValveId, rate).getBytes());
    }

    public void setCommDevice(ICommDevice device) {
        commDevice = device;
        commDevice.setDataReceivedHandler(new CommDeviceEventAdapter() {
            @Override
            public void dataEventOccurred(UsbPipeDataEvent event) {
                byte[] data = event.getData();
                List<List<Double>> decoded = decode(data, numChannels);
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
    private static List<List<Double>> decode(byte[] data, int numChannels) {
        List<List<Double>> res = new ArrayList<>();
        for (int i = 0; i < data.length - BYTES_PER_CHANNEL * numChannels;
             i += BYTES_PER_CHANNEL * numChannels) {
            List<Double> rows = new ArrayList<>();
            for (int j = 0; j < numChannels; j++) {
                // assign 4 bytes to hold channel data to transfer short type.
                byte[] bytes = new byte[BYTES_PER_CHANNEL];
                for (int k = 0; k < BYTES_PER_CHANNEL; k++) {
                    bytes[k] = data[i + BYTES_PER_CHANNEL * j + k];
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
