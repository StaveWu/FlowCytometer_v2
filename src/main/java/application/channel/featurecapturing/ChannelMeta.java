package application.channel.featurecapturing;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChannelMeta {

    private StringProperty id;
    private StringProperty name;
    private DoubleProperty voltage;
    private DoubleProperty threshold;
    private StringProperty peakPolicy;

    public final StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty("PMT1");
        }
        return id;
    }

    public final StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty("");
        }
        return name;
    }

    public final DoubleProperty voltageProperty() {
        if (voltage == null) {
            voltage = new SimpleDoubleProperty();
        }
        return voltage;
    }

    public final DoubleProperty thresholdProperty() {
        if (threshold == null) {
            threshold = new SimpleDoubleProperty();
        }
        return threshold;
    }

    public final StringProperty peakPolicyProperty() {
        if (peakPolicy == null) {
            peakPolicy = new SimpleStringProperty("Area");
        }
        return peakPolicy;
    }


    public String getId() {
        return idProperty().get();
    }
    public void setId(String id) {
        idProperty().set(id);
    }

    public double getThreshold() {
        return thresholdProperty().get();
    }
    public void setThreshold(double threshold) {
        thresholdProperty().set(threshold);
    }

    public double getVoltage() {
        return voltageProperty().get();
    }
    public void setVoltage(double voltage) {
        voltageProperty().set(voltage);
    }

    public String getName() {
        return nameProperty().get();
    }
    public void setName(String name) {
        nameProperty().set(name);
    }

    public String getPeakPolicy() {
        return peakPolicyProperty().get();
    }
    public void setPeakPolicy(String peakPolicy) {
        peakPolicyProperty().set(peakPolicy);
    }

    public String getNameWithPolicy() {
        return getName() + "-" + getPeakPolicy().charAt(0);
    }

    public class JsonObject {
        public final String id;
        public final String name;
        public final double voltage;
        public final double threshold;
        public final String peakPolicy;

        public JsonObject(String id, String name, double voltage, double threshold, String peakPolicy) {
            this.id = id;
            this.name = name;
            this.voltage = voltage;
            this.threshold = threshold;
            this.peakPolicy = peakPolicy;
        }
    }

    public JsonObject toJsonObject() {
        return new JsonObject(idProperty().get(),
                nameProperty().get(),
                voltageProperty().get(),
                thresholdProperty().get(),
                peakPolicyProperty().get());
    }

    public static ChannelMeta fromJsonObject(JsonObject json) {
        ChannelMeta model = new ChannelMeta();
        model.setId(json.id);
        model.setName(json.name);
        model.setPeakPolicy(json.peakPolicy);
        model.setThreshold(json.threshold);
        model.setVoltage(json.voltage);
        return model;
    }

    @Override
    public String toString() {
        return String.format("{id: %s, name: %s, voltage: %f, threshold: %f, peakPolicy: %s}",
                getId(), getName(), getVoltage(), getThreshold(), getPeakPolicy());
    }
}
