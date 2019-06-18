package application.dashboard;

import application.dashboard.device.CommDataParser;
import application.dashboard.device.CommDeviceEventAdapter;
import application.dashboard.device.ICommDevice;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
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
                List<List<Double>> decoded = CommDataParser.decode(data, numChannels);
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

}
