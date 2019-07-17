package application.event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StopSamplingEvent {

    private String timeStamp;

    public StopSamplingEvent() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        timeStamp = dateFormat.format(date);
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
