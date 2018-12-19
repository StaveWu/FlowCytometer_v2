package application.channel;

import application.channel.Converter.DoublePropertyConverter;
import application.channel.Converter.IntegerPropertyConverter;
import application.channel.Converter.StringPropertyConverter;
import javafx.beans.property.*;

import javax.persistence.*;

@Entity
public class ChannelInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Convert(converter = IntegerPropertyConverter.class)
    private IntegerProperty channelId = new SimpleIntegerProperty(1);

    @Column
    @Convert(converter = StringPropertyConverter.class)
    private StringProperty channelName = new SimpleStringProperty("");

    @Column
    @Convert(converter = DoublePropertyConverter.class)
    private DoubleProperty voltage = new SimpleDoubleProperty();

    @Column
    @Convert(converter = DoublePropertyConverter.class)
    private DoubleProperty threshold = new SimpleDoubleProperty();

    @Column
    @Convert(converter = StringPropertyConverter.class)
    private StringProperty peakPolicy = new SimpleStringProperty("Area");

    public ChannelInfo() {}

    public ChannelInfo(int cid, String cname, double cvol, double cthre, String cpolicy) {
        channelId.setValue(cid);
        channelName.setValue(cname);
        voltage.setValue(cvol);
        threshold.setValue(cthre);
        peakPolicy.setValue(cpolicy);
    }


    public int getChannelId() {
        return channelId.get();
    }
    public void setChannelId(int channelId) {
        this.channelId.set(channelId);
    }

    public double getThreshold() {
        return threshold.get();
    }
    public void setThreshold(int threshold) {
        this.threshold.set(threshold);
    }

    public double getVoltage() {
        return voltage.get();
    }
    public void setVoltage(int voltage) {
        this.voltage.set(voltage);
    }

    public String getChannelName() {
        return channelName.get();
    }
    public void setChannelName(String channelName) {
        this.channelName.set(channelName);
    }

    public String getPeakPolicy() {
        return peakPolicy.get();
    }
    public void setPeakPolicy(String peakPolicy) {
        this.peakPolicy.set(peakPolicy);
    }

    @Override
    public String toString() {
        return String.format("[id=%d, channelId=%d, name=%s, voltage=%f, threshold=%f, peakPolicy=%s]",
                id, getChannelId(), getChannelName(), getVoltage(), getThreshold(), getPeakPolicy());
    }

    public IntegerProperty channelIdProperty() {
        return channelId;
    }
    public StringProperty channelNameProperty() {
        return channelName;
    }
    public DoubleProperty voltageProperty() {
        return voltage;
    }
    public DoubleProperty thresholdProperty() {
        return threshold;
    }
    public StringProperty peakPolicyProperty() {
        return peakPolicy;
    }

    public Long getId() {
        return id;
    }
}
