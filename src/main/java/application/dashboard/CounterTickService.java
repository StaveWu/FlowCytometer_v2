package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterTickService extends Service<Void> {

    private int countLimit;
    private AtomicInteger currentCount = new AtomicInteger(0);

    public CounterTickService(int countLimit) {
        this.countLimit = countLimit;
        currentCount.set(countLimit);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                // do count
                while (currentCount.get() > 0) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(countLimit - currentCount.get(), countLimit);
                    updateMessage("" + currentCount);
                }
                return null;
            }
        };
    }

    public void countDown() {
        currentCount.decrementAndGet();
    }

}
