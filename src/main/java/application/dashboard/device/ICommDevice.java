package application.dashboard.device;

public interface ICommDevice {

    void connect() throws Exception;

    boolean isConnected();

    byte[] read() throws Exception;

    void write(byte[] data) throws Exception;

    void disconnect() throws Exception;

    void setDataReceivedHandler(CommDeviceEventAdapter handler);

}
