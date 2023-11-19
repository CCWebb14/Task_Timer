package model;

import ui.TimerEventListener;

import javax.swing.Timer;
import java.util.concurrent.CountDownLatch;

// Represents a TimerSession, a TimerSession holds a java swing timer
// A timer is set with a given amount of minutes and performs an operation every second.
// The object is also given a latch to notify the main thread when it is complete
public class TimerSession {
    private final int timerDurationMinutes;
    private final CountDownLatch latch;

    private int minutesRemaining;
    private int secondsRemaining;
    private int secondsCompleted;
    private Timer timer;
    private Boolean timerCancelled;
    private Boolean timerComplete;
    private TimerEventListener listener;

    // EFFECTS: constructs a timerSession with the given duration,
    // Instantiates a new latch and initializes the timerCancelled and timerComplete boolean values
    // Instantiates a timer that triggers a second countdown every 1s
    public TimerSession(int minutes) {
        this.timerDurationMinutes = minutes;
        this.minutesRemaining = minutes;
        this.secondsRemaining = 0;
        this.secondsCompleted = 0;
        this.timerCancelled = false;
        this.timerComplete = false;
        this.latch = new CountDownLatch(1);
        // Triggers event every 1s if the timer is running
        // Once complete, the latch is counted down in order to notify main thread
        this.timer = new Timer(1000, e -> {
            secondsCompleted++;
            if (secondsRemaining >= 1) {
                if (--secondsRemaining <= 0 && minutesRemaining <= 0) {
                    timer.stop();
                    timerComplete = true;
                    this.latch.countDown();
                }
            } else {
                minutesRemaining--;
                secondsRemaining = 59;
            }
            if (listener != null) {
                updateListener();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: Sets listener to the given listener and triggers update clock on listener
    public void setListener(TimerEventListener listener) {
        this.listener = listener;
        listener.updateClock(minutesRemaining + ":0" + secondsRemaining, secondsCompleted, timerComplete);
    }

    // MODIFIES: this
    // EFFECTS: removes current listener
    public void removeListener() {
        this.listener = null;
    }

    // EFFECTS: updates the clock on the listener object
    public void updateListener() {
        String midClock =  ((secondsRemaining >= 10) ? ":" : ":0");
        listener.updateClock(minutesRemaining + midClock + secondsRemaining, secondsCompleted, timerComplete);
    }

    // MODIFIES: this
    // EFFECTS: Starts 1s timer
    public void startTimer() {
        timer.start();
    }

    // MODIFIES: this
    // EFFECTS: Stops 1s timer
    public void pauseTimer() {
        timer.stop();
    }

    // REQUIRES: Timer has been started
    // EFFECTS: Awaits the countdown of the latch
    // Throws an InterruptedException if the thread is interrupted while waiting
    public void awaitTimer() throws InterruptedException {
        latch.await();
    }

    // MODIFIES: this
    // EFFECTS: Stops the timer and sets timer cancelled to true.
    // The Latch is also counted down to notify main thread.
    public void cancelTimer() {
        timer.stop();
        timerCancelled = true;
        latch.countDown();
    }

    // For testing purposes
    // REQUIRES: this.minutes >= minutes, this.seconds >= seconds
    // MODIFIES: this
    // EFFECTS: Decreases minutes and seconds by given amounts
    public void fastForwardTimer(int minutes, int seconds) {
        minutesRemaining -= minutes;
        secondsRemaining -= seconds;
    }

    // EFFECTS: Subtracts minutesRemaining from timerDurationMinutes and returns the value
    // If there are seconds remaining, subtract 1 from returned value (incomplete minute)
    public int calculateCompletedMinutes() {
        if (secondsRemaining > 0) {
            return (timerDurationMinutes - minutesRemaining - 1);
        }
        return (timerDurationMinutes - minutesRemaining);
    }

    public boolean isTimerComplete() {
        return timerComplete;
    }

    public boolean isTimerCancelled() {
        return timerCancelled;
    }

    // EFFECTS: Returns a boolean value on the state of the timer
    // Returns True if the timer is running, false otherwise
    public boolean isTimerRunning() {
        return timer.isRunning();
    }

    public int getMinutesRemaining() {
        return minutesRemaining;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }
}
