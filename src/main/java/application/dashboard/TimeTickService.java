package application.dashboard;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.math.BigDecimal;

public class TimeTickService extends Service<Void> {

    private int timeLimit;

    public TimeTickService(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final BigDecimal step = new BigDecimal("" + 0.01); // should construct by string
                BigDecimal tl = new BigDecimal("" + timeLimit + ".00");
                for (BigDecimal i = tl; i.doubleValue() >= 0; i = i.subtract(step)) {
                    if (isCancelled()) {
                        break;
                    }
                    Thread.sleep(step.multiply(new BigDecimal("" + 1000)).intValue());
                    updateProgress(tl.subtract(i).doubleValue(), tl.doubleValue());

                    if (i.toString().endsWith(".00")) {
                        // convert to hh:mm:ss
                        updateMessage(TimeLimit.formatSeconds(i.intValue()).toString());
                    }
                }
                return null;
            }
        };
    }
}
