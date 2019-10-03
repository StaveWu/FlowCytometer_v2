package application.worksheet;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class EventsCountService extends Service<Void> {

    private AtomicInteger currentCount = new AtomicInteger(0);

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (! isCancelled()) {
                    updateMessage("Events Count: " + currentCount.get());
                }
                return null;
            }
        };
    }

    public void setCurrentCount(int x) {
        currentCount.set(x);
    }
}
