package application.channel.featurecapturing;

import javafx.beans.property.*;

public class ChannelMeta {

    private StringProperty id;
    private StringProperty name;
    private DoubleProperty voltage;
    private DoubleProperty threshold;
    private StringProperty peakPolicy;
    private BooleanProperty eventTrigger;
    private DoubleProperty background;

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

    public final BooleanProperty eventTriggerProperty() {
        if (eventTrigger == null) {
            eventTrigger = new SimpleBooleanProperty(true);
        }
        return eventTrigger;
    }

    public final DoubleProperty backgroundProperty() {
        if (background == null) {
            background = new SimpleDoubleProperty(0.0);
        }
        return background;
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

    public boolean getEventTrigger() {
        return eventTriggerProperty().get();
    }
    public void setEventTrigger(boolean trigger) {
        eventTriggerProperty().set(trigger);
    }

    public double getBackground() {
        return backgroundProperty().get();
    }
    public void setBackground(double background) {
        backgroundProperty().set(background);
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
        public final boolean eventTrigger;
        public final double background;

        public JsonObject(String id, String name, double voltage, double threshold, String peakPolicy,
                          boolean eventTrigger, double background) {
            this.id = id;
            this.name = name;
            this.voltage = voltage;
            this.threshold = threshold;
            this.peakPolicy = peakPolicy;
            this.eventTrigger = eventTrigger;
            this.background = background;
        }
    }

    public JsonObject toJsonObject() {
        return new JsonObject(idProperty().get(),
                nameProperty().get(),
                voltageProperty().get(),
                thresholdProperty().get(),
                peakPolicyProperty().get(),
                eventTriggerProperty().get(),
                backgroundProperty().get());
    }

    public static ChannelMeta fromJsonObject(JsonObject json) {
        ChannelMeta model = new ChannelMeta();
        model.setId(json.id);
        model.setName(json.name);
        model.setPeakPolicy(json.peakPolicy);
        model.setThreshold(json.threshold);
        model.setVoltage(json.voltage);
        model.setEventTrigger(json.eventTrigger);
        model.setBackground(json.background);
        return model;
    }

    @Override
    public String toString() {
        return String.format("ChannelMeta[id: %s, " +
                        "name: %s, " +
                        "voltage: %f, " +
                        "threshold: %f, " +
                        "peakPolicy: %s, " +
                        "eventTrigger: %s, " +
                        "backgound: %f]",
                getId(),
                getName(),
                getVoltage(),
                getThreshold(),
                getPeakPolicy(),
                getEventTrigger(),
                getBackground());
    }
}
