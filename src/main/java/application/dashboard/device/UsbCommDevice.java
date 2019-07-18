package application.dashboard.device;

import javax.usb.*;
import javax.usb.event.UsbPipeListener;
import java.util.List;

public class UsbCommDevice implements ICommDevice {

    private UsbInterface iface;
    private UsbPipe sendPipe;
    private UsbPipe receivedPipe;
    public static final short VENDOR_ID = (short) 0x0483;
    public static final short PRODUCT_ID = (short) 0x5740;
    private UsbPipeListener listener;

    public UsbCommDevice() {}

    @SuppressWarnings("unchecked")
    private UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device :
                (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
                return device;
            }
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) {
                    return device;
                }
            }
        }
        return null;
    }

    @Override
    public void connect() throws Exception {
        disconnect();
        UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_ID, PRODUCT_ID);
        if (device == null) {
            throw new NullPointerException("device is not find");
        }
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        if (configuration == null) {
            throw new UsbException("UsbConfiguration is null");
        }

        iface = configuration.getUsbInterface((byte) 1);
        iface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });

        UsbEndpoint sendUsbEndpoint, receivedUsbEndpoint;
        sendUsbEndpoint = (UsbEndpoint)iface.getUsbEndpoints().get(0);
        if (!sendUsbEndpoint.getUsbEndpointDescriptor().toString().contains("OUT")) {
            receivedUsbEndpoint = sendUsbEndpoint;
            sendUsbEndpoint = (UsbEndpoint)iface.getUsbEndpoints().get(1);
        } else {
            receivedUsbEndpoint = (UsbEndpoint)iface.getUsbEndpoints().get(1);
        }
        sendPipe = sendUsbEndpoint.getUsbPipe();
        sendPipe.open();
        receivedPipe = receivedUsbEndpoint.getUsbPipe();
        receivedPipe.addUsbPipeListener(listener);
        receivedPipe.open();
    }

    @Override
    public boolean isConnected() {
        return checkUsbInterface() && checkPipe(receivedPipe) && checkPipe(sendPipe);
    }

    private boolean checkUsbInterface() {
        return iface != null && iface.isActive() && iface.isClaimed();
    }
    private boolean checkPipe(UsbPipe pipe) {
        return pipe != null && pipe.isOpen() && pipe.isActive();
    }

    /**
     * Async read.
     * @return a byte[] data will be returned. The returned data may be empty since it was an async method.
     * Please use event handler to access data instead.
     * @throws Exception
     */
    @Override
    public byte[] read() throws Exception {
        if (!isConnected()) {
            throw new UsbException("usb is not connected");
        }

        byte[] data = new byte[32000];
        receivedPipe.asyncSubmit(data);
        return data;
    }

    @Override
    public void write(byte[] data) throws Exception {
        if (!isConnected()) {
            throw new UsbException("usb is not connected");
        }
        sendPipe.asyncSubmit(data);
    }

    @Override
    public void disconnect() throws Exception {
        if (receivedPipe != null) {
            receivedPipe.close();
            receivedPipe = null;
        }
        if (sendPipe != null) {
            sendPipe.close();
            sendPipe = null;
        }
        if (iface != null) {
            iface.release();
            iface = null;
        }
    }

    @Override
    public void setDataReceivedHandler(CommDeviceEventAdapter handler) {
        this.listener = handler;
    }
}
