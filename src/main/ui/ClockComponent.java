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
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    private JLabel label;
    private JPanel utilityPanel;
    private JButton timerButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private TimerSession timerSession;
    private Task curTask;

    private int completedWorkTimers;
    private String curState;

    private TimerGUI listener;

    public ClockComponent(TimerGUI listener) {
        this.listener = listener;
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
        utilityPanel = new JPanel();
        timerButton = new JButton();
        cancelButton = new JButton();
        utilityPanel.add(timerButton);
        utilityPanel.add(cancelButton);
        progressBar = new JProgressBar(0, 0, 0);
        progressBar.setStringPainted(true);
        renderBasedOnState();
        setDimensionsAndAlignment();
        add(label);
        add(utilityPanel);
        add(progressBar);
    }

    private void setDimensionsAndAlignment() {
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setPreferredSize(BUTTON_SIZE);
        timerButton.setPreferredSize(BUTTON_SIZE);
        utilityPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        utilityPanel.setMaximumSize(new Dimension(300, 50));
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
            recordMinutes();
            triggerListenerReRender();
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
        recordMinutes();
        completedWorkTimers++;

        if (completedWorkTimers >= 3) {
            completedWorkTimers = 0;
            curState = "LongBreak";
        } else {
            curState = "Break";
        }
    }

    private void recordMinutes() {
        int completedMinutes = timerSession.calculateCompletedMinutes();
        curTask.recordTime(LocalDate.now(), completedMinutes);
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


    public void triggerListenerReRender() {
        // TODO: rerender components of timerGUI when timer is cancelled or completed
        listener.renderStatisticsCard();
    }


    @Override
    public void updateClock(String curClock, int secondsCompleted, Boolean timerComplete) {
        progressBar.setString(curClock);
        progressBar.setValue(secondsCompleted);
        if (timerComplete) {
            calculateAndDetermineState();
            triggerListenerReRender();
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
