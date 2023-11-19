package ui;

import model.Project;
import model.Task;
import persistence.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// Code influenced by JsonSerializationDemo: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
// Code influenced by TellerApp: https://github.students.cs.ubc.ca/CPSC210/TellerApp

// task timer command line application
public class TimerCLI {
    private static final String JSON_STORE = "./data/project.json";
    private Project project;
    private Scanner keyboard;
    private int intInput;
    private int taskNum;
    private TimerSession currentTimer;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: constructs the TimerCLI
    // Runs app
    public TimerCLI() {
        runApp();
    }

    // EFFECTS: Displays menu's and processes user input
    private void runApp() {
        init();
        System.out.println("Welcome to Task Timer");
        mainMenu();
        System.out.println("Application Closing");
    }

    // MODIFIES: this
    // EFFECTS: initializes project and scanner
    private void init() {
        project = new Project("School");
        keyboard = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    // MODIFIES: this
    // EFFECTS: Display's main menu and processes user input
    // Can call listTasksMenu and createTaskMenu
    private void mainMenu() {
        while (true) {
            System.out.println("[1] Timer [2] Task breakdown [3] Add new Task [4] Load data from file [5] Quit");
            intInput = handleIntInput();
            if (intInput == 1) {
                listTasksMenu();
            } else if (intInput == 2) {
                System.out.println(project.generateAllTaskMinutes());
            } else if (intInput == 3) {
                createTaskMenu();
            } else if (intInput == 4) {
                loadProject();
            } else if (intInput == 5) {
                saveProjectQuery();
                break;
            } else {
                System.out.println("Invalid number. Please enter a number 1-4.");
            }
        }
    }

    // EFFECTS: Asks the user if they would like to save the data to a file
    private void saveProjectQuery() {
        System.out.println("Would you like to save the project to a file? [1] Yes [2] No");
        intInput = handleIntInput();
        if (intInput == 1) {
            saveProject();
        } else if (intInput == 2) {
            System.out.println("Continuing without saving...");
        } else {
            System.out.println("Invalid number. Please enter a number 1-2.");
        }
    }

    // EFFECTS: saves the project to a file
    private void saveProject() {
        try {
            jsonWriter.open();
            jsonWriter.write(project);
            jsonWriter.close();
            System.out.println("Saved project " + project.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads project from a file
    private void loadProject() {
        try {
            project = jsonReader.read();
            System.out.println("Loaded project " + project.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: Requests user input, if it is not an int clear scanner and retry
    private int handleIntInput() {
        while (true) {
            try {
                return keyboard.nextInt();
            } catch (InputMismatchException e) {
                keyboard.nextLine();
                System.out.println("Please enter a number");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Takes in user input and creates a task with entered string
    // Adds the task to the project
    private void createTaskMenu() {
        keyboard.nextLine();
        System.out.println("Enter your task name.");
        String input = keyboard.nextLine();
        Task newTask = new Task(input);
        project.addTask(newTask);
        System.out.println("Successfully added the task: " + input);
    }

    // MODIFIES: this
    // EFFECTS: Displays a page of 5 available tasks and processes user input
    // If the user asks for more tasks, the page will be incremented and the display will be re-rendered
    // Assigns taskNum when a task is selected and calls taskMenu
    private void listTasksMenu() {
        int page = 0;
        String result = "";
        List<Task> tasks = project.getTaskList();
        while (true) {
            result = createListTasksMenuOptions(tasks, page);
            System.out.println(result);
            intInput = handleIntInput();
            if (intInput == 7) {
                return;
            }
            if (intInput == 6) {
                page = handleMoreOptions(tasks, page);
            } else {
                taskNum = (intInput + (page * 5) - 1);
                if (tasks.size() - 1 >= taskNum) {
                    taskMenu(taskNum);
                } else {
                    System.out.println("Invalid number. Please try again.");
                }
            }
        }
    }

    // EFFECTS: Creates task list string of 5 current options with the given page
    private String createListTasksMenuOptions(List<Task> tasks, int page) {
        String result = "";
        for (int i = (5 * page); i < tasks.size() && i < (5 * (page + 1)); i++) {
            int j = i % 5;
            result += "[" + (j + 1) + "]" + " " + tasks.get(i).getName() + " ";
        }
        result += "[6] More Tasks ";
        result += "[7] Main Menu ";
        return result;
    }

    // EFFECTS: Increments the page variable if there are more options on the next page
    // Otherwise returns 0 (Returns to first page)
    private int handleMoreOptions(List<Task> tasks, int page) {
        page += 1;
        if ((page * 5) > tasks.size()) {
            page = 0;
        }
        return page;
    }

    // MODIFIES: this
    // EFFECTS: Displays the menu for the given task
    // Processes user input
    // Can call runTimer and editTask on the given task
    private void taskMenu(int taskNum) {
        Task curTask = project.getTaskFromIndex(taskNum);
        while (true) {
            listDurations(curTask);
            System.out.println("[1] Start Timer [2] Edit Timer Durations [3] Back");
            intInput = handleIntInput();
            if (intInput == 1) {
                runTimer(curTask);
            } else if (intInput == 2) {
                editTask(curTask);
            } else if (intInput == 3) {
                return;
            } else {
                System.out.println("Invalid number. Please enter a number 1-3.");
            }
        }
    }

    // EFFECTS: SOUT the durations of the given task
    private void listDurations(Task curTask) {
        System.out.println(curTask.getName() + " Work duration: " + curTask.getWorkDurationMinutes()
                + " Break duration: " + curTask.getBreakDurationMinutes()
                + " Long break Duration: " + curTask.getLongBreakDurationMinutes());
    }

    // MODIFIES: this
    // EFFECTS: Takes in 3 user inputs and re-assigns task durations
    private void editTask(Task curTask) {
        keyboard.nextLine();
        System.out.println("Enter a new work duration (minutes):");
        int intInputWork = handleIntInput();
        curTask.setWorkDurationMinutes(intInput);
        System.out.println("Enter a new short break duration (minutes):");
        int intInputBreak = handleIntInput();
        System.out.println("Enter a new long break duration (minutes):");
        int intInputLongBreak = handleIntInput();
        curTask.setWorkDurationMinutes(intInputWork);
        curTask.setBreakDurationMinutes(intInputBreak);
        curTask.setLongBreakDurationMinutes(intInputLongBreak);
        System.out.println("Successfully changed timer durations");
    }

    // MODIFIES: this
    // EFFECTS: Handles the state of the timer (work, break, long break)
    // Creates 2 threads (Timer thread and timerController thread)
    private void runTimer(Task curTask) {
        int completedWorkTimers = 0;
        String curState = "Work";
        while (true) {
            currentTimer = setTimer(curTask, curState);
            Thread timerControllerThread = new Thread(() -> timerController());
            timerControllerThread.start();
            handleThreads(timerControllerThread, curState);
            completedWorkTimers = incrementCompletedWorkTimers(curState, completedWorkTimers);
            curState = manageState(currentTimer, curTask, curState, completedWorkTimers);
            if (currentTimer.isTimerCancelled()) {
                return;
            }
        }
    }

    // EFFECTS: Creates a timer session based on the given task and state
    private TimerSession setTimer(Task curTask, String curState) {
        TimerSession currentTimer;
        if (curState == "Work") {
            currentTimer = new TimerSession(curTask.getWorkDurationMinutes());
        } else if (curState == "Short break") {
            currentTimer = new TimerSession(curTask.getBreakDurationMinutes());
        } else {
            currentTimer = new TimerSession(curTask.getLongBreakDurationMinutes());
        }
        System.out.println(curState + " timer started");
        currentTimer.startTimer();
        return currentTimer;
    }

    // EFFECTS: Awaits the completion of the timer and the completion of timerController thread
    // SOUT by manageOutputTimer inbetween thread completion
    private void handleThreads(Thread timerControllerThread, String curState) {
        try {
            currentTimer.awaitTimer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        manageOutputPostTimer(currentTimer, curState);
        try {
            timerControllerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // EFFECTS: Creates SOUT after the main thread has waited for timer completion
    // Displays how many minutes will be recorded if it was a work timer
    private void manageOutputPostTimer(TimerSession currentTimer, String curState) {
        if (curState == "Work") {
            int completedMinutes = currentTimer.calculateCompletedMinutes();
            if (currentTimer.isTimerComplete()) {
                System.out.println(curState
                        + " timer complete! " + completedMinutes
                        + " minutes will be recorded. Enter any number to continue");
            } else {
                System.out.println(curState
                        + " timer cancelled. " + completedMinutes
                        + " minutes will be recorded. Enter any number to continue");
            }
        } else {
            if (currentTimer.isTimerComplete()) {
                System.out.println(curState
                        + " timer complete! Enter any number to continue");
            } else {
                System.out.println(curState
                        + " timer cancelled. Enter any number to continue");
            }
        }
    }

    // EFFECTS: Modifies the current state
    // Work -> Short break
    // 3X Work -> Long break
    // Break -> Work
    private String manageState(TimerSession currentTimer, Task curTask, String curState, int completedWorkTimers) {
        if (curState == "Work") {
            curTask.recordTime(LocalDate.now(), currentTimer.calculateCompletedMinutes());
        }
        if (curState == "Work" && completedWorkTimers >= 3) {
            return "Long break";
        } else if (curState == "Work") {
            return "Short break";
        } else {
            return "Work";
        }
    }

    // EFFECTS: Increments the completedWorkTimers variable if a work session has been completed
    // If a long break has just been completed, completedWorkTimers will be set to zero
    private int incrementCompletedWorkTimers(String curState, int completedWorkTimers) {
        if (curState == "Work") {
            return ++completedWorkTimers;
        } else if (curState == "Long break") {
            return 0;
        } else {
            return completedWorkTimers;
        }
    }

    // MODIFIES: this
    // EFFECTS: Takes in user input to modify the timer state
    private void timerController() {
        System.out.println("[1] Pause/Start Timer [2] Cancel Timer");
        int intInput;
        while (true) {
            intInput = handleIntInput();
            if (currentTimer.isTimerComplete() || currentTimer.isTimerCancelled()) {
                break;
            } else {
                if (intInput == 1) {
                    if (currentTimer.isTimerRunning()) {
                        currentTimer.pauseTimer();
                        System.out.println("Timer paused " + displayRemainingTime(currentTimer));
                    } else {
                        currentTimer.startTimer();
                        System.out.println("Timer resumed");
                    }
                } else if (intInput == 2) {
                    currentTimer.cancelTimer();
                } else {
                    System.out.println("Invalid number. Please enter 1 or 2.");
                }
            }
        }
    }

    // EFFECTS: Returns a string of remaining time
    private String displayRemainingTime(TimerSession currentTimer) {
        int secondsRemaining = currentTimer.getSecondsRemaining();
        int minutesRemaining = currentTimer.getMinutesRemaining();
        if (secondsRemaining < 10) {
            return minutesRemaining + ":" + "0" + secondsRemaining;
        }
        return minutesRemaining + ":" + secondsRemaining;
    }
}