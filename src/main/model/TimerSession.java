package model;

import javax.swing.Timer;
import java.util.concurrent.CountDownLatch;

// Represents a TimerSession, a TimerSession holds a java swing timer
// A timer is set with a given amount of minutes and performs an operation every second.
// The object is also given a latch to notify the main thread when it is complete
public class TimerSession {
    private int timerDurationMinutes;
    private int minutesRemaining;
    private int secondsRemaining;
    private Timer timer;
    private CountDownLatch latch;
    private Boolean timerComplete;
    private Boolean timerCancelled;

    public TimerSession(int minutes, CountDownLatch latch) {
        this.timerDurationMinutes = minutes;
        this.minutesRemaining = minutes;
        this.secondsRemaining = 0;
        this.timerComplete = false;
        this.timerCancelled = false;
        this.latch = latch;

        // Triggers event every 1s
        // if timer is running
        // Once complete, the latch is counted down in order to notify main thread
        this.timer = new Timer(1000, e -> {
            if (secondsRemaining > 0) {
                secondsRemaining--;
            } else if (minutesRemaining > 0) {
                minutesRemaining--;
                secondsRemaining = 59;
            } else {
                timer.stop();
                timerComplete = true;
                this.latch.countDown();
            }
        });
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

    // EFFECTS: Determines if timer is complete and returns true if it is
    public boolean isTimerComplete() {
        // For testing purposes
        if (minutesRemaining <= 0 && secondsRemaining <= 0) {
            timerComplete = true;
        }
        return timerComplete;
    }

    public boolean isTimerRunning() {
        return timer.isRunning();
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



}
