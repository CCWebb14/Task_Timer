package ui;

// TimerEventListener interface, required for classes that need to observe TimerSessionSubject
public interface TimerEventListener {
    void updateClock(String clock, int secondsCompleted, Boolean timerComplete);
}
