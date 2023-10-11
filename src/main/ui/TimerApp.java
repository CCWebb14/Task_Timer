package ui;

import model.Project;
import model.Task;
import model.TimerSession;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

// Project timer application
public class TimerApp {
    Task cpsc210;
    Task cpsc121;
    Task phil220;
    Task dsci100;
    Task cpsc213;
    Task cpsc221;
    Project project;
    Scanner keyboard;
    int intInput;
    int taskNum;
    TimerSession currentTimer;
    CountDownLatch latch;

    public TimerApp() {
        runApp();
    }

    // EFFECTS: Displays menu's and processes user input
    private void runApp() {
        init();
        System.out.println("Welcome to Task Timer");
        mainMenu();
        System.out.println("Application Closing");
    }

    private void init() {
        cpsc210 = new Task("CPSC210", 1, 1, 1);
        cpsc121 = new Task("CPSC121", 2, 2, 2);
        phil220 = new Task("PHIL220", 3, 3, 3);
        dsci100 = new Task("DSCI100", 4, 4, 4);
        cpsc213 = new Task("CPSC213", 5, 5, 5);
        cpsc221 = new Task("CPSC221", 6, 6, 6);
        project = new Project("School");
        cpsc121.recordTime(LocalDate.now(), 5);
        phil220.recordTime(LocalDate.now(), 10);
        dsci100.recordTime(LocalDate.now(), 15);
        project.addTask(cpsc210);
        project.addTask(cpsc121);
        project.addTask(phil220);
        project.addTask(dsci100);
        project.addTask(cpsc213);
        project.addTask(cpsc221);
        keyboard = new Scanner(System.in);
    }

    // MODIFIES: this
    // EFFECTS: Display's main menu and processes user input
    // Can call listTasksMenu and createTaskMenu
    private void mainMenu() {
        while (true) {
            System.out.println("[1] Timer [2] Task Breakdown [3] Add New Task [4] Quit");
            intInput = keyboard.nextInt();
            if (intInput == 1) {
                listTasksMenu();
            } else if (intInput == 2) {
                System.out.println(project.generateAllTaskMinutes());
            } else if (intInput == 3) {
                createTaskMenu();
            } else if (intInput == 4) {
                break;
            } else {
                System.out.println("Invalid Input. Please enter a number 1-4.");
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
            intInput = keyboard.nextInt();
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
                    System.out.println("Invalid selection. Please try again.");
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
            intInput = keyboard.nextInt();
            if (intInput == 1) {
                runTimer(curTask);
            } else if (intInput == 2) {
                editTask(curTask);
            } else if (intInput == 3) {
                return;
            } else {
                System.out.println("Invalid Input. Please enter a number 1-3.");
            }
        }
    }

    // EFFECTS: SOUT the durations of the given task
    private void listDurations(Task curTask) {
        System.out.println(curTask.getName() + " Work Duration: " + curTask.getWorkDurationMinutes()
                + " Break Duration: " + curTask.getBreakDurationMinutes()
                + " Long Break Duration: " + curTask.getLongBreakDurationMinutes());
    }


    // MODIFIES: this
    // EFFECTS: Takes in 3 user inputs and re-assigns task durations
    private void editTask(Task curTask) {
        try {
            keyboard.nextLine();
            System.out.println("Enter a new work duration (minutes):");
            int intInputWork = keyboard.nextInt();
            curTask.setWorkDurationMinutes(intInput);
            System.out.println("Enter a new short break duration (minutes):");
            int intInputBreak = keyboard.nextInt();
            System.out.println("Enter a new long break duration (minutes):");
            int intInputLongBreak = keyboard.nextInt();
            curTask.setWorkDurationMinutes(intInputWork);
            curTask.setBreakDurationMinutes(intInputBreak);
            curTask.setLongBreakDurationMinutes(intInputLongBreak);
            System.out.println("Successfully changed timer durations");
        } catch (InputMismatchException e) {
            keyboard.nextLine();
            System.out.println("Incorrect input, cancelling edit duration.");
        }

    }

    // MODIFIES: this
    // EFFECTS: Handles the state of the timer (work, break, long break)
    // Creates 2 threads (Timer thread and timerController thread)
    // The countdown latch controls both
    private void runTimer(Task curTask) {
        int completedWorkTimers = 0;
        String curState = "Work";
        while (true) {
            latch = new CountDownLatch(1);
            currentTimer = setTimer(curTask, curState, latch);
            Thread timerControllerThread = new Thread(() -> timerController());
            timerControllerThread.start();
            handleThreads(latch, timerControllerThread, curState);
            completedWorkTimers = incrementCompletedWorkTimers(curState, completedWorkTimers);
            curState = manageState(currentTimer, curTask, curState, completedWorkTimers);
            if (currentTimer.isTimerCancelled()) {
                return;
            }
        }
    }

    // EFFECTS: Creates a timer session based on the given task and state
    private TimerSession setTimer(Task curTask, String curState, CountDownLatch latch) {
        TimerSession currentTimer;
        if (curState == "Work") {
            currentTimer = new TimerSession(curTask.getWorkDurationMinutes(), latch);
        } else if (curState == "Short Break") {
            currentTimer = new TimerSession(curTask.getBreakDurationMinutes(), latch);
        } else {
            currentTimer = new TimerSession(curTask.getLongBreakDurationMinutes(), latch);
        }
        System.out.println(curState + " timer Started");
        currentTimer.startTimer();
        return currentTimer;
    }

    // EFFECTS: Awaits the completion of the latch and the completion of timerController thread
    // SOUT by manageOutputTimer inbetween thread completion
    private void handleThreads(CountDownLatch latch, Thread timerControllerThread, String curState) {
        try {
            latch.await();
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
                        + " timer Complete! " + completedMinutes
                        + " minutes will be recorded. Enter any number to continue");
            } else {
                System.out.println(curState
                        + " timer cancelled. " + completedMinutes
                        + " minutes will be recorded. Enter any number to continue");
            }
        } else {
            if (currentTimer.isTimerComplete()) {
                System.out.println(curState
                        + " timer Complete! Enter any number to continue");
            } else {
                System.out.println(curState
                        + "timer cancelled. Enter any number to continue");
            }
        }

    }

    // EFFECTS: Modifies the current state
    // Work -> Short Break
    // 3X Work -> Long Break
    // Break -> Work
    private String manageState(TimerSession currentTimer, Task curTask, String curState, int completedWorkTimers) {
        if (curState == "Work") {
            curTask.recordTime(LocalDate.now(), currentTimer.calculateCompletedMinutes());
        }
        if (curState == "Work" && completedWorkTimers >= 3) {
            return "Long Break";
        } else if (curState == "Work") {
            return "Short Break";
        } else {
            return "Work";
        }
    }

    // EFFECTS: Increments the completedWorkTimers variable if a work session has been completed
    // If a long break has just been completed, completedWorkTimers will be set to zero
    private int incrementCompletedWorkTimers(String curState, int completedWorkTimers) {
        if (curState == "Work") {
            return completedWorkTimers++;
        } else if (curState == "Long Break") {
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
            intInput = keyboard.nextInt();
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
                    System.out.println("Invalid input. Please enter 1 or 2.");
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
