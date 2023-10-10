package model;

import java.util.ArrayList;
import java.util.List;

// Represents a project, a project can hold multiple tasks
public class Project {
    private String name;
    private List<Task> tasks;

    public Project(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: Appends a task to the list of tasks,
    // only if the task has a unique name
    public boolean addTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName() == task.getName()) {
                return false;
            }
        }
        this.tasks.add(task);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: Iterates through the task list and calculates
    // the total minutes across all tasks.
    public int calculateTotalMinutes() {
        int totalMinutes = 0;
        for (int i = 0; i < tasks.size(); i++) {
            totalMinutes += tasks.get(i).getTotalMinutes();
        }
        return totalMinutes;
    }

    public List<Task> getTaskList() {
        return tasks;
    }


}
