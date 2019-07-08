package application.dashboard;

import application.channel.sampling.SamplingPoint;
import application.dashboard.device.CommDeviceEventAdapter;
import application.dashboard.device.ICommDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CircuitBoard {

    private static final Logger log = LoggerFactory.getLogger(CircuitBoard.class);

    private ICommDevice commDevice;
    private DataReceivedHandler handler;
    private boolean isOnSampling;
    private List<String> channelIds;

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
        this.channelIds = channelIds;
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

    public boolean isOnSampling() {
        return isOnSampling;
    }

    public void setCommDevice(ICommDevice device) {
        commDevice = device;
        commDevice.setDataReceivedHandler(new CommDeviceEventAdapter() {
            @Override
            public void dataEventOccurred(UsbPipeDataEvent event) {
                byte[] data = event.getData();
                System.out.println(Arrays.toString(data));
                List<SamplingPoint> decoded = decode(data, channelIds);
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

            @Override
            public void dataReceived() {
                List<SamplingPoint> samplingPoints = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    List<Float> coords = new ArrayList<>();
                    for (int j = 0; j < channelIds.size(); j++) {
                        coords.add((float) Math.random());
                    }
                    samplingPoints.add(new SamplingPoint(channelIds, coords));
                }
//                System.out.println(samplingPoints);
                handler.onDataReceived(samplingPoints);
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

    public static List<SamplingPoint> decode(byte[] data, List<String> channelIds) {
        List<SamplingPoint> res = new ArrayList<>();
        final int numChannels = channelIds.size();
        for (int i = 0; i < data.length / (SamplingPoint.COORD_BYTES_LEN * numChannels); i++) {
            byte[] bytes = new byte[SamplingPoint.COORD_BYTES_LEN * numChannels];
            for (int j = 0; j < bytes.length; j++) {
                bytes[j] = data[SamplingPoint.COORD_BYTES_LEN * numChannels * i + j];
            }
            res.add(SamplingPoint.fromBytes(channelIds, bytes));
        }
        return res;
    }

}
