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
                        updateMessage(formatSeconds(i.intValue()));
                    }
                }
                return null;
            }

            private String formatSeconds(int timeInSeconds) {
                int hours = timeInSeconds / 3600;
                int secondsLeft = timeInSeconds - hours * 3600;
                int minutes = secondsLeft / 60;
                int seconds = secondsLeft - minutes * 60;

                String formattedTime = "";
                if (hours < 10)
                    formattedTime += "0";
                formattedTime += hours + ":";

                if (minutes < 10)
                    formattedTime += "0";
                formattedTime += minutes + ":";

                if (seconds < 10)
                    formattedTime += "0";
                formattedTime += seconds ;

                return formattedTime;
            }
        };
    }
}
