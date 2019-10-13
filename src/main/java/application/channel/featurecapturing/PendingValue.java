package application.channel.featurecapturing;

class PendingValue {

    private Float value = null;

    public PendingValue() {
    }

    void clear() {
        value = null;
    }

    boolean isExisting() {
        return value != null;
    }

    void set(Float d) {
        value = d;
    }

    public Float get() {
        return value;
    }
}
