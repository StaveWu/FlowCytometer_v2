package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterTickService extends Service<Void> {

    private int countLimit;
    private AtomicInteger currentCount = new AtomicInteger(0);
    private boolean stop = false;

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
                while (!stop) {
                    if (isCancelled()) {
                        break;
                    }
                    int curcnt = currentCount.get();
                    updateProgress(countLimit - curcnt, countLimit);
                    updateMessage("" + curcnt);
                    if (curcnt <= 0) {
                        stop = true;
                    }
                }
                return null;
            }
        };
    }

    public void countDown() {
        currentCount.getAndDecrement();
    }

}
