package ui;

import model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;

// Represents the clock component card, the clock component implements the logic
// to control the TimerSession object
// This component is bidirectional with the timerGUI in order to trigger re-renders
public class ClockComponent extends JPanel implements TimerEventListener {
    private static final String START_LABEL = "Start";
    private static final String PAUSE_LABEL = "Pause";
    private static final String CANCEL_LABEL = "Cancel";
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);
    private final TimerGUI mainPanel;

    private JLabel label;
    private JPanel utilityPanel;
    private JButton timerButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private TimerSessionSubject timerSession;

    private int completedWorkTimers;
    private String curState;

    // EFFECTS: Constructs the ClockComponent
    public ClockComponent(TimerGUI mainPanel) {
        this.mainPanel = mainPanel;
        initializeAndRenderLabel();
    }

    // MODIFIES: this
    // EFFECTS: Initializes the label with text
    private void initializeAndRenderLabel() {
        label = new JLabel("No task currently selected");
        add(label);
    }

    // MODIFIES: this
    // EFFECTS: cancels the timer and removes this as listener
    // removes all components and resets state
    public void decomposeComponent() {
        if (timerSession != null) {
            timerSession.cancelTimer();
            timerSession.removeListener();
        }
        resetState();
        this.removeAll();
        initializeAndRenderLabel();
    }

    // MODIFIES: this
    // EFFECTS: resets completedWorkTimers and curState
    private void resetState() {
        completedWorkTimers = 0;
        curState = "Work";
    }

    // MODIFIES: this
    // EFFECTS: Initializes and adds all components to the clock component
    public void renderClockComponent() {
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
        add(label);
        add(utilityPanel);
        add(progressBar);
        setDimensionsAndAlignment();
    }

    // EFFECTS: Calls a render method based on curState
    private void renderBasedOnState() {
        if (curState.equals("Work")) {
            renderWork();
        } else if (curState.equals("Break")) {
            renderBreak();
        } else {
            renderLongBreak();
        }
    }

    // EFFECTS: sets the alignment of all components
    private void setDimensionsAndAlignment() {
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setPreferredSize(BUTTON_SIZE);
        timerButton.setPreferredSize(BUTTON_SIZE);
        utilityPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        utilityPanel.setMaximumSize(new Dimension(300, 50));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // MODIFIES: this
    // EFFECTS: alters components and action listeners based on work state
    private void renderWork() {
        int workDurationMinutes = mainPanel.curTask.getWorkDurationMinutes();
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

    // MODIFIES: this
    // EFFECTS: alters components and action listeners based on break state
    private void renderBreak() {
        int breakDurationMinutes = mainPanel.curTask.getBreakDurationMinutes();
        createTimerSession(breakDurationMinutes);
        label.setText("Take a break for " + breakDurationMinutes + " minute(s)");
        renderCancelButtonNoLog();
        renderStartButton();
    }

    // MODIFIES: this
    // EFFECTS: alters components and action listeners based on long break state
    private void renderLongBreak() {
        int longBreakDurationMinutes = mainPanel.curTask.getLongBreakDurationMinutes();
        createTimerSession(longBreakDurationMinutes);
        label.setText("Take a long break for " + longBreakDurationMinutes + " minute(s)");
        renderCancelButtonNoLog();
        renderStartButton();
    }

    // MODIFIES: this
    // EFFECTS: Initializes a new timer session and sets this as a listener
    private void createTimerSession(int requestedMinutes) {
        timerSession = new TimerSessionSubject(requestedMinutes);
        timerSession.setListener(this);
        progressBar.setMaximum(60 * requestedMinutes);
    }

    // MODIFIES: this
    // EFFECTS: renders the cancel buttons action listeners with no recording
    private void renderCancelButtonNoLog() {
        removeActionListeners(cancelButton);
        cancelButton.addActionListener((e) -> {
            timerSession.cancelTimer();
            resetState();
            renderBasedOnState();
        });
    }

    // MODIFIES: this
    // EFFECTS: If the current state is work, call recordMinutesChangeToBreak
    private void calculateAndDetermineState() {
        if (curState.equals("Work")) {
            recordMinutesChangeToBreak();
        } else {
            curState = "Work";
        }
    }

    // MODIFIES: this
    // EFFECTS: records minutes completed and increments completedWork timers
    // Alters current state based on completed timers
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

    // EFFECTS: Triggers recording of completed minutes from the timer session
    private void recordMinutes() {
        int completedMinutes = timerSession.calculateCompletedMinutes();
        mainPanel.curTask.recordTime(LocalDate.now(), completedMinutes);
    }

    // MODIFIES: this
    // EFFECTS: converts the timer button to a start button
    private void renderStartButton() {
        removeActionListeners(timerButton);
        timerButton.setText(START_LABEL);
        timerButton.addActionListener((e) -> {
            timerSession.startTimer();
            renderPauseButton();
        });
    }

    // MODIFIES: this
    // EFFECTS: converts the timer button to a pause button
    private void renderPauseButton() {
        removeActionListeners(timerButton);
        timerButton.setText(PAUSE_LABEL);
        timerButton.addActionListener((e) -> {
            timerSession.pauseTimer();
            renderStartButton();
        });
    }

    // EFFECTS: triggers re-render of listener
    private void triggerListenerReRender() {
        mainPanel.renderStatisticsCard();
    }

    // MODIFIES: this
    // EFFECTS: utilized by TimerSession to update the progress bar on tick and trigger
    // re-renders when necessary
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

    // EFFECTS: removes all action listeners from given button
    private void removeActionListeners(JButton button) {
        ActionListener[] listeners = button.getActionListeners();
        if (listeners != null) {
            for (ActionListener listener : listeners) {
                button.removeActionListener(listener);
            }
        }
    }
}
