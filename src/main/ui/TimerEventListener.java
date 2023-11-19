package ui;

// TimerEventListener interface, required for classes that need data from a TimerSession
public interface TimerEventListener {
    void updateClock(String clock, int secondsCompleted, Boolean timerComplete);
}
