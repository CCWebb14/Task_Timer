package ui;

import model.Task;
import model.TimerSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class ClockComponent extends JPanel implements TimerEventListener {
    private static final String START_LABEL = "Start";
    private static final String PAUSE_LABEL = "Pause";
    private static final String CANCEL_LABEL = "Cancel";

    private JLabel label;
    private JButton timerButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private TimerSession timerSession;
    private Task curTask;

    private int completedWorkTimers;
    private String curState;

    public ClockComponent() {
        initializeAndRenderLabel();
    }

    public void decomposeComponent() {
        if (timerSession != null) {
            timerSession.cancelTimer();
            timerSession.removeListener();
        }
        resetState();
        this.removeAll();
        initializeAndRenderLabel();
    }

    private void initializeAndRenderLabel() {
        label = new JLabel("No task currently selected");
        add(label);
    }

    private void resetState() {
        completedWorkTimers = 0;
        curState = "Work";
    }

    public void renderClockComponent(Task curTask) {
        this.curTask = curTask;
        this.curState = "Work";
        this.completedWorkTimers = 0;
        this.removeAll();
        label = new JLabel();
        cancelButton = new JButton();
        timerButton = new JButton();
        progressBar = new JProgressBar(0, 0, 0);
        progressBar.setStringPainted(true);
        renderBasedOnState();
        centerComponents();
        add(label);
        add(cancelButton);
        add(timerButton);
        add(progressBar);
    }

    private void centerComponents() {
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void renderBasedOnState() {
        if (curState.equals("Work")) {
            renderWork();
        } else if (curState.equals("Break")) {
            renderBreak();
        } else {
            renderLongBreak();
        }
    }

    private void renderWork() {
        int workDurationMinutes = curTask.getWorkDurationMinutes();
        createTimerSession(workDurationMinutes);
        label.setText("Work for " + workDurationMinutes + " minute(s)");
        cancelButton.setText(CANCEL_LABEL);
        removeActionListeners(cancelButton);
        cancelButton.addActionListener((e) -> {
            timerSession.cancelTimer();
            curTask.recordTime(LocalDate.now(), timerSession.calculateCompletedMinutes());
            resetState();
            renderBasedOnState();
        });
        renderStartButton();
    }

    private void renderBreak() {
        int breakDurationMinutes = curTask.getBreakDurationMinutes();
        createTimerSession(breakDurationMinutes);
        label.setText("Take a break for " + breakDurationMinutes + " minute(s)");
        renderCancelButtonNoLog();
        renderStartButton();
    }

    private void renderLongBreak() {
        int longBreakDurationMinutes = curTask.getLongBreakDurationMinutes();
        createTimerSession(longBreakDurationMinutes);
        label.setText("Take a long break for " + longBreakDurationMinutes + " minute(s)");
        renderCancelButtonNoLog();
        renderStartButton();
    }

    private void createTimerSession(int requestedMinutes) {
        timerSession = new TimerSession(requestedMinutes);
        timerSession.setListener(this);
        progressBar.setMaximum(60 * requestedMinutes);
    }

    private void renderCancelButtonNoLog() {
        removeActionListeners(cancelButton);
        cancelButton.addActionListener((e) -> {
            timerSession.cancelTimer();
            resetState();
            renderBasedOnState();
        });
    }

    private void calculateAndDetermineState() {
        if (curState.equals("Work")) {
            recordMinutesChangeToBreak();
        } else {
            curState = "Work";
        }
    }

    private void recordMinutesChangeToBreak() {
        int completedMinutes = timerSession.calculateCompletedMinutes();
        curTask.recordTime(LocalDate.now(), completedMinutes);
        completedWorkTimers++;

        if (completedWorkTimers >= 3) {
            completedWorkTimers = 0;
            curState = "LongBreak";
        } else {
            curState = "Break";
        }
    }

    private void renderStartButton() {
        removeActionListeners(timerButton);
        timerButton.setText(START_LABEL);
        timerButton.addActionListener((e) -> {
            timerSession.startTimer();
            renderPauseButton();
        });
    }

    private void renderPauseButton() {
        removeActionListeners(timerButton);
        timerButton.setText(PAUSE_LABEL);
        timerButton.addActionListener((e) -> {
            timerSession.pauseTimer();
            renderStartButton();
        });
    }

    public void triggerReRender() {
        // TODO: rerender components of timerGUI when timer is cancelled or completed
        // Updates values on breakdown and so forth
    }


    @Override
    public void updateClock(String curClock, int secondsCompleted, Boolean timerComplete) {
        progressBar.setString(curClock);
        progressBar.setValue(secondsCompleted);
        if (timerComplete) {
            calculateAndDetermineState();
            renderBasedOnState();
        }
    }

    public void removeActionListeners(JButton button) {
        ActionListener[] listeners = button.getActionListeners();
        if (listeners != null) {
            for (ActionListener listener : listeners) {
                button.removeActionListener(listener);
            }
        }
    }
}
