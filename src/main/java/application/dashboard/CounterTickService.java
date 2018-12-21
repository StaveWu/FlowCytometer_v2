package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CounterTickService extends Service<Void> {

    private int countLimit;

    public CounterTickService(int countLimit) {
        this.countLimit = countLimit;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // do count
                return null;
            }
        };
    }
}
