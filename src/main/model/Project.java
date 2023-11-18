package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a project, a project can hold multiple tasks
public class Project implements Writable {
    private String name;
    private List<Task> tasks;

    // EFFECTS: constructs a project with a name and an empty list of tasks
    public Project(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: Appends a task to the list of tasks,
    // only if the task has a unique name
    public boolean addTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().equals(task.getName())) {
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

    public String getName() {
        return name;
    }

    public List<Task> getTaskList() {
        return tasks;
    }

    // REQUIRES: index <= tasks.size()
    // EFFECTS: Returns a task from the given index
    public Task getTaskFromIndex(int index) {
        return tasks.get(index);
    }

    public Task getTaskFromString(String query) {
        Task curTask = null;
        for (int i = 0; i < tasks.size(); i++) {
            curTask = tasks.get(i);
            if (query.equals(curTask.getName())) {
                return curTask;
            }
        }
        return curTask;
    }

    // EFFECTS: Creates a string of all tasks and their total minutes
    public String generateAllTaskMinutes() {
        if (tasks.size() == 0) {
            return "No tasks to display";
        }
        String result = "";
        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            result += "(" + curTask.getName() + ") " + curTask.getTotalMinutes() + " min ";
        }
        return result;
    }

    public List<String> tasksToStringList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            result.add(tasks.get(i).getName());
        }
        return result;
    }

    public Task determineMostWorkedOnTask() {
        if (tasks.size() == 0) {
            return null;
        }
        Task mostWorkedOnTask = tasks.get(0);
        for (Task task : tasks) {
            if (task.getTotalMinutes() > mostWorkedOnTask.getTotalMinutes()) {
                mostWorkedOnTask = task;
            }
        }
        return mostWorkedOnTask;
    }

    // EFFECTS: Generates and returns a json object that represents the project
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("project_name", name);
        json.put("tasks", tasksToJson());
        return json;
    }

    // EFFECTS: Iterates each task in tasks and generates a JSON array
    private JSONArray tasksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Task t : tasks) {
            jsonArray.put(t.toJson());
        }

        return jsonArray;
    }
}
