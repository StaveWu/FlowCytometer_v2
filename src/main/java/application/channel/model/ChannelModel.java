package application.channel.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelModel {

    private StringProperty id;
    private StringProperty name;
    private DoubleProperty voltage;
    private DoubleProperty threshold;
    private StringProperty peakPolicy;
    private ListProperty<Double> dataProperty;

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

    public final ListProperty<Double> dataProperty() {
        if (dataProperty == null) {
            dataProperty = new SimpleListProperty<>();
        }
        return dataProperty;
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

    public List<Double> getData() {
        return new ArrayList<>(dataProperty.get());
    }
    public void setData(List<Double> data) {
        dataProperty.set(FXCollections.observableArrayList(data));
    }

    public class JsonObject {
        public final String id;
        public final String name;
        public final double voltage;
        public final double threshold;
        public final String peakPolicy;
        public final List<Double> data;

        public JsonObject(String id, String name, double voltage, double threshold, String peakPolicy, List<Double> data) {
            this.id = id;
            this.name = name;
            this.voltage = voltage;
            this.threshold = threshold;
            this.peakPolicy = peakPolicy;
            this.data = data;
        }
    }

    public JsonObject toJsonObject() {
        return new JsonObject(idProperty().get(),
                nameProperty().get(),
                voltageProperty().get(),
                thresholdProperty().get(),
                peakPolicyProperty().get(),
                dataProperty().get());
    }

    public static ChannelModel fromJsonObject(JsonObject json) {
        ChannelModel model = new ChannelModel();
        model.setId(json.id);
        model.setName(json.name);
        model.setPeakPolicy(json.peakPolicy);
        model.setThreshold(json.threshold);
        model.setVoltage(json.voltage);
        model.setData(json.data);
        return model;
    }

    @Override
    public String toString() {
        return String.format("{id: %s, name: %s, voltage: %f, threshold: %f, peakPolicy: %s}",
                getId(), getName(), getVoltage(), getThreshold(), getPeakPolicy());
    }
}
