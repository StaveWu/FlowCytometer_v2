package application.dashboard.model;

import com.google.gson.Gson;
import javafx.beans.property.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DashboardSetting {

    private String location;
    private Gson gson = new Gson();

    private ObjectProperty<CommunicationDevice> device = new SimpleObjectProperty<>(CommunicationDevice.USB);
    private LongProperty frequency = new SimpleLongProperty(10);
    private ObjectProperty<SampleMode> sampleMode = new SimpleObjectProperty<>(SampleMode.TIME);
    private LongProperty cellNumber = new SimpleLongProperty(10000);
    private IntegerProperty hour = new SimpleIntegerProperty(0);
    private IntegerProperty minute = new SimpleIntegerProperty(1);
    private IntegerProperty second = new SimpleIntegerProperty(0);

    private BooleanProperty valve1 = new SimpleBooleanProperty(false);
    private BooleanProperty valve2 = new SimpleBooleanProperty(false);
    private BooleanProperty valve3 = new SimpleBooleanProperty(false);
    private BooleanProperty valve4 = new SimpleBooleanProperty(false);
    private BooleanProperty valve5 = new SimpleBooleanProperty(false);
    private BooleanProperty valve6 = new SimpleBooleanProperty(false);

    private DoubleProperty supValve1 = new SimpleDoubleProperty(0.);
    private DoubleProperty supValve2 = new SimpleDoubleProperty(0.);

    public DashboardSetting(String location) {
        this.location = location;
        load();

        frequency.addListener((observable, oldValue, newValue) -> dump());
        sampleMode.addListener((observable, oldValue, newValue) -> dump());
        cellNumber.addListener((observable, oldValue, newValue) -> dump());
        hour.addListener((observable, oldValue, newValue) -> dump());
        minute.addListener((observable, oldValue, newValue) -> dump());
        second.addListener((observable, oldValue, newValue) -> dump());
        valve1.addListener((observable, oldValue, newValue) -> dump());
        valve2.addListener((observable, oldValue, newValue) -> dump());
        valve3.addListener((observable, oldValue, newValue) -> dump());
        valve4.addListener((observable, oldValue, newValue) -> dump());
        valve5.addListener((observable, oldValue, newValue) -> dump());
        valve6.addListener((observable, oldValue, newValue) -> dump());
        supValve1.addListener((observable, oldValue, newValue) -> dump());
        supValve2.addListener((observable, oldValue, newValue) -> dump());
    }

    private void load() {
        try (Reader reader = new FileReader(location)) {
            DashboardSetting.JsonObject json = gson.fromJson(reader, DashboardSetting.JsonObject.class);
            setDevice(CommunicationDevice.fromString(json.device));
            setFrequency(json.frequency);
            setSampleMode(SampleMode.fromString(json.sampleMode));
            setCellNumber(json.cellNumber);
            setHour(json.hour);
            setMinute(json.minute);
            setSecond(json.second);
            setValve1(json.valve1);
            setValve2(json.valve2);
            setValve3(json.valve3);
            setValve4(json.valve4);
            setValve5(json.valve5);
            setValve6(json.valve6);
            setSupValve1(json.supValve1);
            setSupValve2(json.supValve2);
        } catch (IOException e) {
            // do nothing
        }
    }

    public void dump() {
        try {
            Files.write(Paths.get(location), gson.toJson(toJsonObject()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getFrequency() {
        return frequency.get();
    }
    public LongProperty frequencyProperty() {
        return frequency;
    }
    public void setFrequency(long frequency) {
        this.frequency.set(frequency);
    }

    public SampleMode getSampleMode() {
        return sampleMode.get();
    }

    public ObjectProperty<SampleMode> sampleModeProperty() {
        return sampleMode;
    }

    public void setSampleMode(SampleMode sampleMode) {
        this.sampleMode.set(sampleMode);
    }

    public long getCellNumber() {
        return cellNumber.get();
    }
    public LongProperty cellNumberProperty() {
        return cellNumber;
    }
    public void setCellNumber(long cellNumber) {
        this.cellNumber.set(cellNumber);
    }

    public int getHour() {
        return hour.get();
    }
    public IntegerProperty hourProperty() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour.set(hour);
    }

    public int getMinute() {
        return minute.get();
    }
    public IntegerProperty minuteProperty() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute.set(minute);
    }

    public int getSecond() {
        return second.get();
    }
    public IntegerProperty secondProperty() {
        return second;
    }
    public void setSecond(int second) {
        this.second.set(second);
    }

    public boolean isValve1() {
        return valve1.get();
    }

    public BooleanProperty valve1Property() {
        return valve1;
    }

    public void setValve1(boolean valve1) {
        this.valve1.set(valve1);
    }

    public boolean isValve2() {
        return valve2.get();
    }

    public BooleanProperty valve2Property() {
        return valve2;
    }

    public void setValve2(boolean valve2) {
        this.valve2.set(valve2);
    }

    public boolean isValve3() {
        return valve3.get();
    }

    public BooleanProperty valve3Property() {
        return valve3;
    }

    public void setValve3(boolean valve3) {
        this.valve3.set(valve3);
    }

    public boolean isValve4() {
        return valve4.get();
    }

    public BooleanProperty valve4Property() {
        return valve4;
    }

    public void setValve4(boolean valve4) {
        this.valve4.set(valve4);
    }

    public boolean isValve5() {
        return valve5.get();
    }

    public BooleanProperty valve5Property() {
        return valve5;
    }

    public void setValve5(boolean valve5) {
        this.valve5.set(valve5);
    }

    public boolean isValve6() {
        return valve6.get();
    }

    public BooleanProperty valve6Property() {
        return valve6;
    }

    public void setValve6(boolean valve6) {
        this.valve6.set(valve6);
    }

    public double getSupValve1() {
        return supValve1.get();
    }

    public DoubleProperty supValve1Property() {
        return supValve1;
    }

    public void setSupValve1(double supValve1) {
        this.supValve1.set(supValve1);
    }

    public double getSupValve2() {
        return supValve2.get();
    }

    public DoubleProperty supValve2Property() {
        return supValve2;
    }

    public void setSupValve2(double supValve2) {
        this.supValve2.set(supValve2);
    }

    public CommunicationDevice getDevice() {
        return device.get();
    }

    public ObjectProperty<CommunicationDevice> deviceProperty() {
        return device;
    }

    public void setDevice(CommunicationDevice device) {
        this.device.set(device);
    }

    public class JsonObject {
        public final String device;
        public final long frequency;
        public final String sampleMode;
        public final long cellNumber;
        public final int hour;
        public final int minute;
        public final int second;
        public final boolean valve1;
        public final boolean valve2;
        public final boolean valve3;
        public final boolean valve4;
        public final boolean valve5;
        public final boolean valve6;
        public final double supValve1;
        public final double supValve2;

        public JsonObject(String device, long frequency, String sampleMode, long cellNumber, int hour,
                          int minute, int second, boolean valve1, boolean valve2,
                          boolean valve3, boolean valve4, boolean valve5, boolean valve6,
                          double supValve1, double supValve2) {
            this.device = device;
            this.frequency = frequency;
            this.sampleMode = sampleMode;
            this.cellNumber = cellNumber;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.valve1 = valve1;
            this.valve2 = valve2;
            this.valve3 = valve3;
            this.valve4 = valve4;
            this.valve5 = valve5;
            this.valve6 = valve6;
            this.supValve1 = supValve1;
            this.supValve2 = supValve2;
        }
    }

    public JsonObject toJsonObject() {
        return new JsonObject(device.get().toString(), frequency.get(), sampleMode.get().toString(),
                cellNumber.get(), hour.get(), minute.get(), second.get(),
                valve1.get(), valve2.get(), valve3.get(), valve4.get(),
                valve5.get(), valve6.get(), supValve1.get(), supValve2.get());
    }

    @Override
    public String toString() {
        return String.format("SamplingSetting[device=%s, frequency=%d, sampleMode=%s, " +
                "cellNumber=%d, hour=%d, minute=%d, second=%d, valve1=%s, valve2=%s, " +
                        "valve3=%s, valve4=%s, valve5=%s, valve6=%s, supValve1=%f, supValve2=%f]",
                device.get(), frequency.get(), sampleMode.get(), cellNumber.get(),
                hour.get(), minute.get(), second.get(),
                valve1.get(), valve2.get(), valve3.get(), valve4.get(),
                valve5.get(), valve6.get(), supValve1.get(), supValve2.get());
    }
}
