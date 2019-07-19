package apitest;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TimerServiceApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TimerService service = new TimerService();
        AtomicInteger count = new AtomicInteger(0);
        service.setCount(count.get());
        service.setPeriod(Duration.seconds(1));
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("Called : " + t.getSource().getValue()
                        + " time(s)");
                count.set((int) t.getSource().getValue());
            }
        });
        service.start();
    }

    public static void main(String[] args) {
        launch();
    }

    private static class TimerService extends ScheduledService<Integer> {
        private IntegerProperty count = new SimpleIntegerProperty();

        public final void setCount(Integer value) {
            count.set(value);
        }

        public final Integer getCount() {
            return count.get();
        }

        public final IntegerProperty countProperty() {
            return count;
        }

        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                protected Integer call() {
                    //Adds 1 to the count
                    count.set(getCount() + 1);
                    return getCount();
                }
            };
        }
    }
}