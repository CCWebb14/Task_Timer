package ui;

import model.EventLog;
import model.Event;
import model.Project;
import model.Task;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

// Code influenced by the links below:
// https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
// https://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
// https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
// https://docs.oracle.com/javase/8/docs/api/java/awt/event/WindowListener.html#windowClosing-java.awt.event.WindowEvent-

// Clock icon taken from https://icons8.com

// Task timer gui application
public class TimerGUI extends JFrame {
    private static final String JSON_STORE = "./data/project.json";
    private static final String IMAGE_PATH = "icons8-clock-100.png";
    private static final String J_FRAME_NAME = "Task Timer";
    private static final String MAIN_MENU_TITLE = "Main Menu";
    private static final String STATISTICS_TITLE = "Statistics";
    private static final String CREATE_TASK_PANEL_TITLE = "Create Task";
    private static final String SELECT_TASK_PANEL_TITLE = "Select Task";
    private static final String TIMER_PANEL_TITLE = "Timer";
    private static final Dimension FIELD_DIMENSION = new Dimension(75, 30);

    private Project project;
    private JFrame frame;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JPanel mainMenuCard;
    private JPanel statisticsCard;
    private JPanel createTaskCard;
    private JPanel selectTaskCard;
    private JPanel editTaskPanel;
    private ClockComponent timerCard;


    protected Task curTask;
    private ImageIcon icon;

    // EFFECTS: constructs the TimerGUI and runs app
    public TimerGUI() {
        SwingUtilities.invokeLater(this::runApp);
    }

    // MODIFIES: this
    // EFFECTS: creates and displays the main JFrame, sets
    private void runApp() {
        init();
        frame = new JFrame();
        setDefaultTitle();
        addComponentsToFrame();
        frame.pack();
        frame.setSize(500, 250);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                processExit();
            }
        });
    }

    // EFFECTS: Calls print log and exits with status 0
    private void processExit() {
        printLog();
        System.exit(0);
    }

    // EFFECTS: Collects the event log and prints each event to the console
    private void printLog() {
        EventLog eventLog = EventLog.getInstance();
        for (Event event : eventLog) {
            System.out.println(event.toString() + "\n");
        }
    }

    // MODIFIES: this
    // EFFECTS: Sets title of main JFrame to given string
    private void setDefaultTitle() {
        frame.setTitle(J_FRAME_NAME);
    }

    // MODIFIES: this
    // EFFECTS: initializes project, jsonWriter, jsonReader and icon
    private void init() {
        project = new Project("School");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        icon = createImageIcon();
    }

    // EFFECTS: Returns an ImageIcon if the path is valid,
    // Otherwise returns null
    private ImageIcon createImageIcon() {
        java.net.URL imgURL = getClass().getResource(IMAGE_PATH);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "timer icon");
        } else {
            return null;
        }
    }

    // MODIFIES: this
    // EFFECTS: Adds cards to associated tabs, adds the tabbed pane to the main JFrame
    // Sets layouts of cards
    private void addComponentsToFrame() {
        JTabbedPane tabbedPane = new JTabbedPane();
        initializeCards();
        tabbedPane.addTab(MAIN_MENU_TITLE, mainMenuCard);
        tabbedPane.addTab(STATISTICS_TITLE, statisticsCard);
        statisticsCard.setLayout(new BoxLayout(statisticsCard, BoxLayout.Y_AXIS));
        tabbedPane.addTab(CREATE_TASK_PANEL_TITLE, createTaskCard);
        createTaskCard.setLayout(new BoxLayout(createTaskCard, BoxLayout.Y_AXIS));
        createTaskCard.add(Box.createVerticalStrut(500));
        tabbedPane.addTab(SELECT_TASK_PANEL_TITLE, selectTaskCard);
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));
        tabbedPane.addTab(TIMER_PANEL_TITLE, timerCard);
        frame.add(tabbedPane);
    }

    // EFFECTS: Calls the initialization method for all cards to be added to tabs
    private void initializeCards() {
        initializeMainMenuCard();
        initializeStatisticsCard();
        initializeCreateTaskCard();
        initializeSelectTaskCard();
        initializeTimerCard();
    }

    // MODIFIES: this
    // EFFECTS: Initializes the main menu card and calls its render method
    private void initializeMainMenuCard() {
        mainMenuCard = new JPanel();
        renderMainMenuCard();
    }

    // REQUIRES: mainMenuCard has been initialized
    // MODIFIES: this
    // EFFECTS: Initializes and adds all components and action listeners to the main menu card
    private void renderMainMenuCard() {
        mainMenuCard.removeAll();
        JLabel timerIcon = new JLabel(icon);
        JLabel message = new JLabel("");
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener((e) -> {
            message.setText(loadProject());
            loadEffects();
        });
        JButton saveDataButton = new JButton("Save Data");
        saveDataButton.addActionListener((e) -> message.setText(saveProject()));
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener((e) -> processExit());
        message.setForeground(Color.blue);
        mainMenuCard.add(timerIcon);
        mainMenuCard.add(loadDataButton);
        mainMenuCard.add(saveDataButton);
        mainMenuCard.add(quitButton);
        mainMenuCard.add(message);
    }

    // MODIFIES: this
    // EFFECTS: triggers re-renders of desired components when state is loaded
    private void loadEffects() {
        curTask = null;
        setDefaultTitle();
        renderStatisticsCard();
        renderCreateTaskCard();
        editTaskPanel.removeAll();
        renderSelectTaskCard();
        timerCard.decomposeComponent();
    }

    // MODIFIES: this
    // EFFECTS: Initializes the main menu card and calls its render method
    private void initializeStatisticsCard() {
        statisticsCard = new JPanel();
        renderStatisticsCard();
    }

    // REQUIRES: statisticsCard has been initialized
    // MODIFIES: this
    // EFFECTS: Initializes and adds all components to the statistics card
    protected void renderStatisticsCard() {
        statisticsCard.removeAll();
        JLabel projectLabel = new JLabel("Project statistics:");
        JLabel totalMinutesLabel = new JLabel("Total timed work (All Time): "
                + project.calculateTotalMinutes() + " minutes");
        Task mostWorkedOnTask = project.determineMostWorkedOnTask();
        JLabel emptyLabel = new JLabel(" ");
        JLabel mostWorkedOnTaskLabel = createTaskDurationLabel(mostWorkedOnTask);
        JLabel currentTaskLabel = new JLabel("Current task statistics: "
                + ((curTask == null) ? "no task selected" : curTask.getName()));
        statisticsCard.add(projectLabel);
        statisticsCard.add(totalMinutesLabel);
        statisticsCard.add(mostWorkedOnTaskLabel);
        statisticsCard.add(emptyLabel);
        statisticsCard.add(currentTaskLabel);
        if (curTask != null) {
            JLabel totalTaskMinutesLabel = new JLabel("Total timed work (All time): "
                    + curTask.getTotalMinutes() + " minutes");
            statisticsCard.add(totalTaskMinutesLabel);
            Integer totalTaskMinutesToday = curTask.getHistoryMap().get(LocalDate.now());
            JLabel totalTaskMinutesTodayLabel = new JLabel("Total timed work (Today): "
                    + ((totalTaskMinutesToday == null) ? "0" : totalTaskMinutesToday) + " minutes");
            statisticsCard.add(totalTaskMinutesTodayLabel);
        }
    }

    // EFFECTS: Generates a JLabel for the given task with statistics
    private JLabel createTaskDurationLabel(Task task) {
        String base = "Most worked on task: ";
        JLabel mostWorkedOnTaskLabel = new JLabel();
        if (task == null) {
            mostWorkedOnTaskLabel.setText(base + "no tasks to display");
            return mostWorkedOnTaskLabel;
        }
        mostWorkedOnTaskLabel.setText(base + task.getName() + " (" + task.getTotalMinutes() + " minutes)");
        return mostWorkedOnTaskLabel;
    }

    // MODIFIES: this
    // EFFECTS: Initializes the create task card and calls its render method
    private void initializeCreateTaskCard() {
        createTaskCard = new JPanel();
        renderCreateTaskCard();
    }

    // REQUIRES: createTaskCard has been initialized
    // MODIFIES: this
    // EFFECTS: Initializes and adds all components to the create task card
    private void renderCreateTaskCard() {
        createTaskCard.removeAll();
        JPanel subPanelTop = new JPanel();
        JLabel nameLabel = new JLabel("Unique name:");
        JTextField nameField = new JTextField();
        JButton submitButton = new JButton("Submit");
        subPanelTop.add(nameLabel);
        subPanelTop.add(nameField);
        subPanelTop.add(submitButton);
        JPanel subPanelBottom = new JPanel();
        JLabel message = new JLabel(" ");
        subPanelBottom.add(message);
        message.setForeground(Color.blue);
        submitButton.addActionListener((e) -> {
            boolean response = project.addTask(new Task(nameField.getText()));
            message.setText(response ? "Task successfully created" : "Could not create task");
            renderSelectTaskCard();
        });
        nameField.setPreferredSize(FIELD_DIMENSION);
        createTaskCard.add(subPanelTop);
        createTaskCard.add(subPanelBottom);
    }


    // MODIFIES: this
    // EFFECTS: loads the project from a file, returns response string
    private String loadProject() {
        try {
            project = jsonReader.read();
            return ("Loaded project " + project.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            return ("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: saves the project to a file, returns response string
    private String saveProject() {
        try {
            jsonWriter.open();
            jsonWriter.write(project);
            jsonWriter.close();
            return ("Saved project " + project.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            return ("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: Initializes the select task card and calls its render method
    private void initializeSelectTaskCard() {
        selectTaskCard = new JPanel();
        editTaskPanel = new JPanel();
        editTaskPanel.setLayout(new GridLayout(4, 2));
        renderSelectTaskCard();
    }

    // REQUIRES: select task card has been initialized
    // MODIFIES: this
    // EFFECTS: Initializes and adds all components and action listeners to the select task card
    private void renderSelectTaskCard() {
        selectTaskCard.removeAll();
        JPanel topPanel = new JPanel();
        List<String> curTasks = project.tasksToStringList();
        JComboBox<String> comboBox = new JComboBox<>(curTasks.toArray(new String[0]));
        comboBox.setMaximumRowCount(5);
        JScrollPane scrollPane = new JScrollPane(comboBox);
        selectTaskCard.add(scrollPane);
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener((e) -> selectTaskEffects((String) comboBox.getSelectedItem()));
        topPanel.add(selectButton);
        selectTaskCard.add(topPanel);
        selectTaskCard.add(editTaskPanel);
    }

    // MODIFIES: this
    // EFFECTS: Triggers re-renders and selects curTask based off of given string
    private void selectTaskEffects(String taskString) {
        timerCard.decomposeComponent();
        curTask = project.getTaskFromString(taskString);
        if (curTask != null) {
            frame.setTitle(J_FRAME_NAME + ": " + curTask.getName());
            timerCard.renderClockComponent();
            renderStatisticsCard();
            renderEditTask();
            selectTaskCard.revalidate();
        }
    }

    // MODIFIES: this
    // EFFECTS: Initializes and adds all components and action listeners to the edit task card
    private void renderEditTask() {
        editTaskPanel.removeAll();
        JLabel workDurationLabel = new JLabel("Work Duration:");
        JTextField workDurationField = new JTextField(Integer.toString(curTask.getWorkDurationMinutes()));
        JLabel breakDurationLabel = new JLabel("Break Duration:");
        JTextField breakDurationField = new JTextField(Integer.toString(curTask.getBreakDurationMinutes()));
        JLabel longBreakDurationLabel = new JLabel("Long Break Duration:");
        JTextField longBreakDurationField = new JTextField(Integer.toString(curTask.getLongBreakDurationMinutes()));
        JButton editButton = new JButton("Edit Task");
        editButton.addActionListener((e) -> {
            curTask.setDurations(Integer.parseInt(workDurationField.getText()),
                    Integer.parseInt(breakDurationField.getText()), Integer.parseInt(longBreakDurationField.getText()));
            timerCard.renderClockComponent();
        });
        workDurationField.setPreferredSize(FIELD_DIMENSION);
        breakDurationField.setPreferredSize(FIELD_DIMENSION);
        longBreakDurationField.setPreferredSize(FIELD_DIMENSION);
        editTaskPanel.add(workDurationLabel);
        editTaskPanel.add(workDurationField);
        editTaskPanel.add(breakDurationLabel);
        editTaskPanel.add(breakDurationField);
        editTaskPanel.add(longBreakDurationLabel);
        editTaskPanel.add(longBreakDurationField);
        editTaskPanel.add(editButton);
    }

    // MODIFIES: this
    // EFFECTS: Calls the constructor of clock component with this
    private void initializeTimerCard() {
        timerCard = new ClockComponent(this);
    }
}