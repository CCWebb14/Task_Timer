# Project Timer

This project will feature a **timer** that employs the **Pomodoro Technique**. 

## Purpose

It can be hard to remain focused throughout the day when you have to allocate a large amount of time towards a specific project or schoolwork. This application serves to provide timers that can break up that time into intervals of focused working/studying and short breaks. As these timers are completed they will be recorded and you will be able to visualize time spent. 

I am interested in this project as I have used similar applications before, but I have found that they do not let you thoroughly visualize and analyze time spent.

## What is the Pomodoro Technique?

The Pomodoro Technique was developed by Francesco Cirillo and it is a time management method. Briefly put, this technique involves dedicating **25 minutes of concentrated and focused work**, followed by a **short 5 minute break**. After 4 cycles have been completed, you should then take a **long break, 20-30 minutes**.

[Read more here](https://en.wikipedia.org/wiki/Pomodoro_Technique).

## User Stories

### Phase I:
- As a user, I want to be able to name and create a new task (ex. CPSC210) and add it to a project (ex. School)
- As a user, I want to be able to start, pause and complete a 25 minute timer, followed by a 5 minute break for a selected task. If 4 cycles have been completed, I want the option to start a long break.
- As a user I want to be able to abruptly end a work timer and still have time recorded
- As a user, I want to be able to modify the default timer durations. For example 25 min -> 40 min of focused work, and 5 min -> 10 min for breaks.
- As a user, I want to be able to view a list of all of my current tasks and time spent on each

### Phase II:
- As a user, when I select the quit option from the main menu, I want the option to save the data associated to my project (tasks, recorded time) to a file.
- As a user, when I start the application, I want to be given the option to load a project and it's associated data from a file.

### Phase III: \(Instructions for grader\)
- When you launch the application you will land on the main menu tab.
  - The visual component is located here \(clock image\)
  - Here you can load and save data from the data directory using buttons.
  - You can also quit the application.
- You can create and add new tasks to the project on the "Create Task" tab.
  - Enter a unique name and click the submit button.
- In order to view all tasks within the project, select the "Select Task" tab.
  - There is a dropdown here that will allow you to click and select any task within the project.
  - You will have to press the "Select" button in order to select the item from the dropdown.
  - Once a task is selected a panel will appear under it that will allow you to edit the timers durations.
- If you have a task selected, you will be able to click the "Timer" tab
  - Here you can interact with the timer for the associated task.
  - Any time here will be logged to the associated task if it is cancelled or if the timer is completed.
  - NOTE: time will only be logged during work sessions.
- The "Statistics" tab is always available to view data on the project and the selected task.

### Phase IV:
#### Task 2:
```
Thu Nov 23 18:36:48 PST 2023
Created new task (CPSC121)

Thu Nov 23 18:36:48 PST 2023
Added task (CPSC121) to project

Thu Nov 23 18:36:52 PST 2023
Created new task (CPSC210)

Thu Nov 23 18:36:52 PST 2023
Added task (CPSC210) to project

Thu Nov 23 18:37:06 PST 2023
Edited timer durations in task(CPSC121) work duration: 1 break duration: 2 long break duration: 3

Thu Nov 23 18:38:09 PST 2023
Logged 1 minutes in task(CPSC121) mapped to 2023-11-23


Process finished with exit code 0
```
#### Task 3:
**Refactoring**

I could improve my design by separating the TimerGUI class into multiple components. Currently, the entire GUI runs off
of two classes (TimerGUI and ClockComponent). The TimerGUI utilizes a JTabbedPane to hold 5 cards (Main Menu,
Statistics, Create Task, Select Task, Timer). I could have refactored each of the cards (JPanels) into new classes
that TimerGUI would call. I could have sorted these cards (classes) into a directory named Cards within the ui package.
Additionally, I could create an eventLogPrinter class that would abstract away the logic of printing events from the 
TimerGUI class.

Another thing I could refactor is the TimerSession class. I believe it might have been a better implementation if 
the TimerSession only held the seconds, minutes, timerComplete fields and logic on the model side while the ui
package would instantiate the swing timer. Currently, the TimerSession class holds the swing timer which
I think is counterintuitive for a model.