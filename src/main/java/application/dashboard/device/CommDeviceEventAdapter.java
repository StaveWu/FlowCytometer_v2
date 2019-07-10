package application.dashboard.device;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

/**
 * this class aims to perform an adapter for comm, usb...'s event handler.
 */
public class CommDeviceEventAdapter implements UsbPipeListener, SimulationListener {

    @Override
    public void errorEventOccurred(UsbPipeErrorEvent event) {

    }

    @Override
    public void dataEventOccurred(UsbPipeDataEvent event) {

    }

    @Override
    public void dataReceived() {

    }
}
