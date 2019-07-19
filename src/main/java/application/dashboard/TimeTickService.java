package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.math.BigDecimal;

public class TimeTickService extends Service<Void> {

    private int timeLimit;
    private int currentTimeLimit;

    public TimeTickService(int timeLimit) {
        this.timeLimit = timeLimit;
        this.currentTimeLimit = timeLimit;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (currentTimeLimit >= 0) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(timeLimit - currentTimeLimit, timeLimit);
                    updateMessage(TimeLimit.formatSeconds(currentTimeLimit).toString());
                    Thread.sleep(1000);
                    currentTimeLimit--;
                }
                return null;
            }
        };
    }
}
