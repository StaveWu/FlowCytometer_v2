package application.dashboard.device;

import javax.usb.*;
import javax.usb.event.UsbPipeListener;
import java.util.List;

public class UsbCommDevice implements ICommDevice {

    private UsbInterface iface;
    private UsbPipe outPipe;
    private UsbPipe inPipe;
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
        UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_ID, PRODUCT_ID);
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        if (configuration == null) {
            throw new UsbException("UsbConfiguration is null");
        }

        if (iface != null) { // release iface before re-obtain it.
            iface.release();
        }
        iface = configuration.getUsbInterface((byte) 1);
        iface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });

        UsbEndpoint outEndpoint = iface.getUsbEndpoint(UsbConst.ENDPOINT_DIRECTION_OUT);
        if (outEndpoint == null) {
            throw new UsbException("SendEndpoint is null");
        }
        outPipe = outEndpoint.getUsbPipe();
        outPipe.addUsbPipeListener(listener);
        outPipe.open();

        UsbEndpoint inEndpoint = iface.getUsbEndpoint(UsbConst.ENDPOINT_DIRECTION_IN);
        if (inEndpoint == null) {
            throw new UsbException("ReceiveEndpoint is null");
        }
        inPipe = inEndpoint.getUsbPipe();
        inPipe.open();
    }

    @Override
    public boolean isConnected() {
        return checkUsbInterface() && checkPipe(inPipe) && checkPipe(outPipe);
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
            throw new UsbNotOpenException();
        }

        byte[] data = new byte[1024];
        inPipe.asyncSubmit(data);
        return data;
    }

    @Override
    public void write(byte[] data) throws Exception {
        if (!isConnected()) {
            throw new UsbNotOpenException();
        }
        outPipe.asyncSubmit(data);
    }

    @Override
    public void disconnect() throws Exception {
        if (inPipe != null) {
            inPipe.close();
            inPipe = null;
        }
        if (outPipe != null) {
            outPipe.close();
            outPipe = null;
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
