package ui;

import model.Project;
import model.Task;
import model.TimerSession;

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
    Project school;
    Scanner keyboard;
    int intInput;
    TimerSession currentTimer;
    CountDownLatch latch;
    // Create a new project (Ex. School)

    // Create a new task (Ex. CPSC210)

    // Add the task to the project

    // Start task

    // Start study timer

    // Start break timer

    public TimerApp() {
        runApp();
    }

    private void runApp() {
        init();
        System.out.println("Welcome to Task Timer");
//        menuOptions();

        int page = 0;

        while (true) {
            List<Task> tasks = school.getTaskList();
            String result = "";

            for (int i = (5 * page); i < tasks.size() && i < (5 * (page + 1)); i++) {
                int j = i % 5;
                result += "[" + (j + 1) + "]" + " " + tasks.get(i).getName() + " ";
            }

            result += "[6] More Options";

            // More options should cycle back to first page

            System.out.println(result);

            intInput = keyboard.nextInt();
            int taskNum;
            Task curTask;

            //
//            oneMinute.startWorkTimer();

            if (intInput == 6) {
                page += 1;
            } else {
                taskNum = (intInput + (page * 5) - 1);
                System.out.println(taskNum);
                curTask = tasks.get(taskNum);
                System.out.println(curTask.getName());

                latch = new CountDownLatch(1);
                // TODO: Pass keyboard event listener to Timer Session
                currentTimer = new TimerSession(curTask.getWorkDurationMinutes(), latch);
                currentTimer.startTimer();

                Thread timerControllerThread = new Thread(() -> timerController());
                timerControllerThread.start();

                // Suspending main thread while timer is running
                // TODO: Pass the latch and timer to another ui method with run()
                // This void method should run in parrallel to this one and scan for input to cancel the timer
                // it will cancel timer and countdown latch
                System.out.println("Enter q to cancel the timer");
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Timer is complete! Enter any number to continue.");

                try {
                    timerControllerThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                // TODO: Check output of timer, cancelled or completed?
                // TODO: increment task with output, partial completion or completion
//                System.out.println("Timer is complete!");
            }
        }
    }

    private void menuOptions() {
        intInput = keyboard.nextInt();
        System.out.println("[1] List Tasks [2] Add New Task [3] Delete a task");
        if (intInput == 1) {
            // TODO: List tasks
        } else if (intInput == 2) {
            // TODO: Create a new task menu
        } else if (intInput == 3) {
            // TODO: Delete a task menu
        } else {
            System.out.println("Invalid Input. Please enter a number 1-3.");
        }
    }

    private void timerController() {
        System.out.println("TimerControllerThread Started");
        int intInput;

        while (true) {
            intInput = keyboard.nextInt();

            if (currentTimer.isTimerComplete() || currentTimer.isTimerCancelled()) {
                System.out.println("broke");
                break;
            } else {
                if (intInput == 1) {
                    if (currentTimer.isTimerRunning()) {
                        currentTimer.pauseTimer();
                        System.out.println("Timer paused");
                    } else {
                        currentTimer.startTimer();
                        System.out.println("Timer resumed");
                    }
                } else if (intInput == 2) {
                    currentTimer.cancelTimer();
                    System.out.println("timer cancelled");
                } else {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                }
            }
        }

        System.out.println("TimerControllerThread Completed");
    }

    private void init() {
        cpsc210 = new Task("CPSC210", 1, 1, 1);
        cpsc121 = new Task("CPSC121", 2, 2, 2);
        phil220 = new Task("PHIL220", 3, 3, 3);
        dsci100 = new Task("DSCI100", 4, 4, 4);
        cpsc213 = new Task("CPSC213", 5, 5, 5);
        cpsc221 = new Task("CPSC221", 6, 6, 6);
        school = new Project("School");
        school.addTask(cpsc210);
        school.addTask(cpsc121);
        school.addTask(phil220);
        school.addTask(dsci100);
        school.addTask(cpsc213);
        school.addTask(cpsc221);
        keyboard = new Scanner(System.in);
    }
}
