package application.dashboard.device;

public class SimulationCommDevice implements ICommDevice {

    private CommDeviceEventAdapter handler;
    private volatile boolean canRead = false;
    private boolean isConnected = false;

    public SimulationCommDevice() {
        Thread dataCreator = new Thread(() -> {
            while (true) {
                if (canRead) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("reading");
                    handler.dataReceived();
                }
            }
        });
        dataCreator.setDaemon(true);
        dataCreator.start();
    }

    @Override
    public void connect() throws Exception {
        isConnected = true;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public byte[] read() throws Exception {
        canRead = true;
        return new byte[0];
    }

    @Override
    public void write(byte[] data) throws Exception {
        String dataStr = new String(data);
        if (dataStr.startsWith("StopSampling")) {
            canRead = false;
        }
    }

    @Override
    public void disconnect() throws Exception {
        isConnected = false;
    }

    @Override
    public void setDataReceivedHandler(CommDeviceEventAdapter handler) {
        this.handler = handler;
    }
}
