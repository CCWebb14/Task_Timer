package model;

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


    // EFFECTS: constructs a timerSession with the given duration,
    // Instantiates a new latch and initializes the timerCancelled and timerComplete boolean values
    // Instantiates a timer that triggers tick every 1000ms
    public TimerSession(int minutes) {
        this.timerDurationMinutes = minutes;
        this.minutesRemaining = minutes;
        this.secondsRemaining = 0;
        this.secondsCompleted = 0;
        this.timerCancelled = false;
        this.timerComplete = false;
        this.latch = new CountDownLatch(1);
        this.timer = new Timer(1000, e -> {
            tick();
        });
    }

    // MODIFIES: this
    // EFFECTS: increments secondsCompleted, and reduces secondsRemaining if >=1
    // Otherwise reduces minutesRemaining and sets secondsRemaining to 59
    // Once complete, the timer is marked complete and the latch is counted down in order to notify awaiting threads
    public void tick() {
        secondsCompleted++;
        if (secondsRemaining >= 1) {
            secondsRemaining--;
            if (secondsRemaining <= 0 && minutesRemaining <= 0) {
                timer.stop();
                timerComplete = true;
                this.latch.countDown();
            }
        } else {
            minutesRemaining--;
            secondsRemaining = 59;
        }
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

    // EFFECTS: Returns a boolean value on the state of the timer
    // Returns True if the timer is running, false otherwise
    public boolean isTimerRunning() {
        return timer.isRunning();
    }

    public boolean isTimerComplete() {
        return timerComplete;
    }

    public boolean isTimerCancelled() {
        return timerCancelled;
    }

    public int getMinutesRemaining() {
        return minutesRemaining;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public int getSecondsCompleted() {
        return secondsCompleted;
    }
}
