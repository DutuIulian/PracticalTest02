package ro.pub.cs.systems.eim.practicaltest02;

public class AlarmInfo {
    private int hour;
    private int minute;
    private boolean time_expired;

    public AlarmInfo(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.time_expired = false;
    }

    public boolean isTimeExpired() {
        return time_expired;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setTimeExpired(boolean time_expired) {
        this.time_expired = time_expired;
    }
}
