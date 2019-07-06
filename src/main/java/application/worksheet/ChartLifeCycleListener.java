package application.worksheet;

public interface ChartLifeCycleListener {

    void afterCreate();

    void afterRemove();

    void propertyChanged();
}
