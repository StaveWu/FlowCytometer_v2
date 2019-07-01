package application.dashboard.device;

import java.util.Arrays;

public enum CommunicationDevice {
    SERIAL("串口"),
    USB("USB");

    private String deviceName;

    CommunicationDevice(String name) {
        deviceName = name;
    }

    public static CommunicationDevice fromString(String deviceName) {
        return Arrays.stream(CommunicationDevice.values())
                .filter(cd -> cd.deviceName.equals(deviceName))
                .findFirst()
                .orElse(CommunicationDevice.USB);
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
