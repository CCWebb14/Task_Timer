package ui;

public interface TimerEventListener {
    void updateClock(String clock, int secondsCompleted, Boolean timerComplete);
}
