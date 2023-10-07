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
    // EFFECTS: Appends a task to the list of tasks
    public void addTask(Task task) {
        this.tasks.add(task);
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
