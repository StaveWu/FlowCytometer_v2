package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class EventCountService extends Service<Void> {

    private AtomicInteger totalCount = new AtomicInteger(0);

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    int currCount = totalCount.get();
                    updateMessage("" + currCount);
                }
                return null;
            }
        };
    }

    public void count() {
        totalCount.getAndIncrement();
    }
}
