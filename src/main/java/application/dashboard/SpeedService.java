package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class SpeedService extends Service<Void> {

    private AtomicInteger currentSpeed = new AtomicInteger(0);

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    if (isCancelled()) {
                        break;
                    }
                    int currSpeed = currentSpeed.getAndSet(0);
                    updateMessage(currSpeed + " events/s");
                    Thread.sleep(1000);
                }
                return null;
            }
        };
    }

    public void speedUp() {
        currentSpeed.getAndIncrement();
    }

}
