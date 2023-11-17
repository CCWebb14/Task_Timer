package ui;

import model.Project;
import model.Task;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

// Code influenced by the links below:
// https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
// https://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html

// Clock icon taken from https://icons8.com

public class TimerGUI extends JFrame {
    private static final String JSON_STORE = "./data/project.json";
    private static final String MAIN_MENU_TITLE = "Main Menu";
    private static final String PROJECT_BREAKDOWN_TITLE = "Project Breakdown";
    private static final String CREATE_TASK_PANEL_TITLE = "Create Task";
    private static final String SELECT_TASK_PANEL_TITLE = "Select Task";
    private static final String TASK_BREAKDOWN_TITLE = "Task Breakdown";
    private static final String TIMER_PANEL_TITLE = "Timer";
    private static final int extraWindowWidth = 100;
    private static final Dimension FIELD_DIMENSION = new Dimension(75, 30);
    private static final ImageIcon icon = new ImageIcon("/icons8-clock-100.png");

    private Project project;
    static JFrame frame;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JPanel mainMenuCard;
    private JPanel projectBreakdownCard;
    private JPanel createTaskCard;
    private JPanel selectTaskCard;
    private JPanel taskBreakdownCard;
    private ClockComponent timerCard;
    private Task curTask;

    public TimerGUI() {
        SwingUtilities.invokeLater(() -> {
            runApp();
        });
    }

    private void runApp() {
        init();
        frame = new JFrame("Task Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentToPane(frame);
        frame.pack();
        frame.setSize(500, 250);
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes project, jsonWriter and jsonReader
    private void init() {
        project = new Project("School");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    private void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        initializeCards();
        tabbedPane.addTab(MAIN_MENU_TITLE, mainMenuCard);
        tabbedPane.addTab(CREATE_TASK_PANEL_TITLE, createTaskCard);
        tabbedPane.addTab(SELECT_TASK_PANEL_TITLE, selectTaskCard);
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));
        tabbedPane.addTab(TIMER_PANEL_TITLE, timerCard);
        pane.add(tabbedPane);
    }

    private void initializeCards() {
        initializeMainMenuCard();
        initializeCreateTaskCard();
        initializeSelectTaskCard();
        initializeTimerCard();
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
        JLabel message = new JLabel("");
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener((e) -> {
            message.setText(loadProject());
            curTask = null;
            renderSelectTaskCard();
            timerCard.decomposeComponent();
        });
        JButton saveDataButton = new JButton("Save Data");
        saveDataButton.addActionListener((e) -> {
            message.setText(saveProject());
        });
        message.setForeground(Color.blue);
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
        //        JButton submitButton = new JButton("Submit");
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
                timerCard.renderClockComponent(curTask);
                message.setText("Current task: " + curTask.getName());
            }
        });
        if (curTask == null) {
            message.setText("No task currently selected.");
        } else {
            message.setText("Current task: " + curTask.getName());
        }
        selectTaskCard.add(selectButton);
        selectTaskCard.add(message);
    }

    private void initializeTimerCard() {
        timerCard = new ClockComponent();
    }
}