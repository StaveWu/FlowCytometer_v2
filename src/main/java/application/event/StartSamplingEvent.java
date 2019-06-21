package application.event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StartSamplingEvent {

    private String timeStamp;

    public StartSamplingEvent() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        timeStamp = dateFormat.format(date);
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
