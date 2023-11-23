package ui;

import model.TimerSession;

// Represents a TimerSession UI component that utilizes a listener
public class TimerSessionSubject extends TimerSession {
    private TimerEventListener listener;

    // TimerSessionSubject constructor, calls super class constructor
    public TimerSessionSubject(int minutes) {
        super(minutes);
    }

    // EFFECTS: Overridden tick method that updates the listener if it is not null
    @Override
    public void tick() {
        super.tick();
        if (listener != null) {
            updateListener();
        }
    }

    // MODIFIES: this
    // EFFECTS: Sets listener to the given listener and immediately triggers update clock on listener
    public void setListener(TimerEventListener listener) {
        this.listener = listener;
        updateListener();
    }

    // MODIFIES: this
    // EFFECTS: removes current listener
    public void removeListener() {
        this.listener = null;
    }

    // EFFECTS: updates the clock on the listener object
    public void updateListener() {
        String midClock =  ((getSecondsRemaining() >= 10) ? ":" : ":0");
        listener.updateClock(getMinutesRemaining() + midClock + getSecondsRemaining(), getSecondsCompleted(),
                isTimerComplete());
    }

}
