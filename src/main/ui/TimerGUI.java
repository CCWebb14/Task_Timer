package ui;

import model.Project;
import model.Task;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

// Code influenced by the links below:
// https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
// https://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
// https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html

// Clock icon taken from https://icons8.com

public class TimerGUI extends JFrame {
    private static final String JSON_STORE = "./data/project.json";
    private static final String IMAGE_PATH = "icons8-clock-100.png";
    private static final String J_FRAME_NAME = "Task Timer";
    private static final String MAIN_MENU_TITLE = "Main Menu";
    private static final String STATISTICS_TITLE = "Statistics";
    private static final String CREATE_TASK_PANEL_TITLE = "Create Task";
    private static final String SELECT_TASK_PANEL_TITLE = "Select Task";
    private static final String TIMER_PANEL_TITLE = "Timer";
    private static final int extraWindowWidth = 100;
    private static final Dimension FIELD_DIMENSION = new Dimension(75, 30);

    private Project project;
    static JFrame frame;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JPanel mainMenuCard;
    private JPanel statisticsCard;
    private JPanel createTaskCard;
    private JPanel selectTaskCard;
    private ClockComponent timerCard;
    private Task curTask;
    private ImageIcon icon;

    private JPanel workPanel;
    private JPanel breakPanel;
    private JPanel longBreakPanel;

    public TimerGUI() {
        SwingUtilities.invokeLater(() -> {
            runApp();
        });
    }

    private void runApp() {
        init();
        frame = new JFrame();
        setDefaultTitle();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentToPane(frame);
        frame.pack();
        frame.setSize(500, 250);
        frame.setVisible(true);
    }

    private void setDefaultTitle() {
        frame.setTitle(J_FRAME_NAME);
    }

    // MODIFIES: this
    // EFFECTS: initializes project, jsonWriter and jsonReader
    private void init() {
        project = new Project("School");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        icon = createImageIcon(IMAGE_PATH, "timer icon");
    }

    private void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        initializePanels();
        tabbedPane.addTab(MAIN_MENU_TITLE, mainMenuCard);
        tabbedPane.addTab(STATISTICS_TITLE, statisticsCard);
        tabbedPane.addTab(CREATE_TASK_PANEL_TITLE, createTaskCard);
        tabbedPane.addTab(SELECT_TASK_PANEL_TITLE, selectTaskCard);
        statisticsCard.setLayout(new BoxLayout(statisticsCard, BoxLayout.Y_AXIS));
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));
        tabbedPane.addTab(TIMER_PANEL_TITLE, timerCard);
        pane.add(tabbedPane);
    }

    private void initializePanels() {
        initializeMainMenuCard();
        initializeStatisticsCard();
        initializeCreateTaskCard();
        initializeSelectTaskCard();
        initializeTimerCard();
        initializeEditTaskPanels();
    }

    private void initializeEditTaskPanels() {
        workPanel = new JPanel();
        breakPanel = new JPanel();
        longBreakPanel = new JPanel();
    }

    private void initializeStatisticsCard() {
        statisticsCard = new JPanel();
        renderStatisticsCard();
    }

    protected void renderStatisticsCard() {
        statisticsCard.removeAll();
        JLabel projectLabel = new JLabel("Project statistics:");
        JLabel totalMinutesLabel = new JLabel("Total timed work (All Time): " + project.calculateTotalMinutes() + " minutes");
        Task mostWorkedOnTask = project.determineMostWorkedOnTask();
        JLabel emptyLabel = new JLabel(" ");
        JLabel mostWorkedOnTaskLabel = createMostWorkedOnTaskLabel(mostWorkedOnTask);
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

    private JLabel createMostWorkedOnTaskLabel(Task mostWorkedOnTask) {
        String base = "Most worked on task: ";
        JLabel mostWorkedOnTaskLabel = new JLabel();
        if (mostWorkedOnTask == null) {
            mostWorkedOnTaskLabel.setText(base + "no tasks to display");
            return mostWorkedOnTaskLabel;
        }
        mostWorkedOnTaskLabel.setText(base + mostWorkedOnTask.getName() + " (" + mostWorkedOnTask.getTotalMinutes() + " minutes)");
        return mostWorkedOnTaskLabel;
    }

    private void initializeMainMenuCard() {
        mainMenuCard = new JPanel() {
            //Make the panel wider than it really needs, so
            //the window's wide enough for the tabs to stay
            //in one row.
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += extraWindowWidth;
                return size;
            }
        };
        renderMainMenuCard();
    }

    private void renderMainMenuCard() {
        mainMenuCard.removeAll();
        JLabel timerIcon = new JLabel(icon);
        JLabel message = new JLabel("");
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener((e) -> {
            message.setText(loadProject());
            setDefaultTitle();
            curTask = null;
            renderSelectTaskCard();
            renderStatisticsCard();
            timerCard.decomposeComponent();
        });
        JButton saveDataButton = new JButton("Save Data");
        saveDataButton.addActionListener((e) -> {
            message.setText(saveProject());
        });
        message.setForeground(Color.blue);
        mainMenuCard.add(timerIcon);
        mainMenuCard.add(loadDataButton);
        mainMenuCard.add(saveDataButton);
        mainMenuCard.add(new JButton("Quit"));
        mainMenuCard.add(message);
    }

    private void initializeCreateTaskCard() {
        createTaskCard = new JPanel();
        renderCreateTaskCard();
    }

    private void renderCreateTaskCard() {
        JLabel workDurationLabel = new JLabel("Work Duration:");
        JTextField workDurationField = new JTextField("25");
        JLabel breakDurationLabel = new JLabel("Break Duration:");
        JTextField breakDurationField = new JTextField("5");
        JLabel longBreakDurationLabel = new JLabel("Long Break Duration:");
        JTextField longBreakDurationField = new JTextField("15");
        JLabel nameLabel = new JLabel("Unique name:");
        JTextField nameField = new JTextField();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener((e) -> {
            project.addTask(new Task(nameField.getText(), Integer.parseInt(workDurationField.getText()),
                    Integer.parseInt(breakDurationField.getText()),
                    Integer.parseInt(longBreakDurationField.getText())));
            renderSelectTaskCard();
        });
        workDurationField.setPreferredSize(FIELD_DIMENSION);
        breakDurationField.setPreferredSize(FIELD_DIMENSION);
        longBreakDurationField.setPreferredSize(FIELD_DIMENSION);
        nameField.setPreferredSize(FIELD_DIMENSION);
        createTaskCard.add(workDurationLabel);
        createTaskCard.add(workDurationField);
        createTaskCard.add(breakDurationLabel);
        createTaskCard.add(breakDurationField);
        createTaskCard.add(longBreakDurationLabel);
        createTaskCard.add(longBreakDurationField);
        createTaskCard.add(nameLabel);
        createTaskCard.add(nameField);
        createTaskCard.add(submitButton);
    }

    private String loadProject() {
        try {
            project = jsonReader.read();
            return ("Loaded project " + project.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            return ("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: saves the project to a file
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

    private void initializeSelectTaskCard() {
        selectTaskCard = new JPanel();
        renderSelectTaskCard();
    }

    private void renderSelectTaskCard() {
        selectTaskCard.removeAll();
        List<String> curTasks = project.tasksToStringList();
        JComboBox<String> comboBox = new JComboBox<>(curTasks.toArray(new String[0]));
        comboBox.setMaximumRowCount(5);
        JScrollPane scrollPane = new JScrollPane(comboBox);
        selectTaskCard.add(scrollPane);
        JLabel message = new JLabel();
        message.setForeground(Color.blue);
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener((e) -> {
            timerCard.decomposeComponent();
            curTask = project.getTaskFromString((String) comboBox.getSelectedItem());
            if (curTask != null) {
                frame.setTitle(J_FRAME_NAME + ": " + curTask.getName());
                timerCard.renderClockComponent(curTask);
                renderSelectTaskCard();
                renderStatisticsCard();
            }
        });
        if (curTask == null) {
            message.setText("No task currently selected.");
        } else {
            message.setText("Current task: " + curTask.getName());
        }
        selectTaskCard.add(selectButton);
        selectTaskCard.add(message);
        if (curTask != null) {
            renderEditTask();
        }
    }

    private void renderEditTask() {
        JPanel workPanel = new JPanel();
        JPanel breakPanel = new JPanel();
        JPanel longBreakPanel = new JPanel();
        JLabel workDurationLabel = new JLabel("Work Duration:");
        JTextField workDurationField = new JTextField(Integer.toString(curTask.getWorkDurationMinutes()));
        JLabel breakDurationLabel = new JLabel("Break Duration:");
        JTextField breakDurationField = new JTextField(Integer.toString(curTask.getBreakDurationMinutes()));
        JLabel longBreakDurationLabel = new JLabel("Long Break Duration:");
        JTextField longBreakDurationField = new JTextField(Integer.toString(curTask.getLongBreakDurationMinutes()));
        JButton editButton = new JButton("Edit Task");
        editButton.addActionListener((e) -> {
            curTask.setWorkDurationMinutes(Integer.parseInt(workDurationField.getText()));
            curTask.setBreakDurationMinutes(Integer.parseInt(breakDurationField.getText()));
            curTask.setLongBreakDurationMinutes(Integer.parseInt(longBreakDurationField.getText()));
        });
        workDurationField.setPreferredSize(FIELD_DIMENSION);
        breakDurationField.setPreferredSize(FIELD_DIMENSION);
        longBreakDurationField.setPreferredSize(FIELD_DIMENSION);
        workPanel.add(workDurationLabel);
        workPanel.add(workDurationField);
        breakPanel.add(breakDurationLabel);
        breakPanel.add(breakDurationField);
        longBreakPanel.add(longBreakDurationLabel);
        longBreakPanel.add(longBreakDurationField);
        longBreakPanel.add(editButton);
        selectTaskCard.add(workPanel);
        selectTaskCard.add(breakPanel);
        selectTaskCard.add(longBreakPanel);
    }

    private void initializeTimerCard() {
        timerCard = new ClockComponent(this);
    }

    // EFFECTS: Returns an ImageIcon if the path is valid,
    // Otherwise returns null
    private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            return null;
        }
    }
}