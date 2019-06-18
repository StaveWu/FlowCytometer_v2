package application.dashboard.model;

/**
 * This class serve as a value object.
 */
public class TimeLimit {

    public final int hours;
    public final int minutes;
    public final int seconds;

    public TimeLimit(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int totalSeconds() {
        return hours * 60 * 60 + minutes * 60 + seconds;
    }

    public static TimeLimit formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        return new TimeLimit(hours, minutes, seconds);
    }

    @Override
    public String toString() {
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
}
