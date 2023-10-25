package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Represents a task, a task holds timer options (work duration and break duration)
// A task also holds daily history of total minutes completed
public class Task implements Writable {
    private String name;
    private int workDurationMinutes;
    private int breakDurationMinutes;
    private int longBreakDurationMinutes;
    private int totalMinutes;
    private Map<LocalDate, Integer> historyMap;

    // EFFECTS: Constructs a task with a given name, a map that holds dates:minutes
    // and default durations of work and break minutes
    public Task(String name) {
        this.name = name;
        this.workDurationMinutes = 25;
        this.breakDurationMinutes = 5;
        this.longBreakDurationMinutes = 15;
        this.totalMinutes = 0;
        this.historyMap = new HashMap<>();
    }

    // EFFECTS: Constructs a task with a given name and work/break durations,
    // and a map that holds dates:minutes
    public Task(String name, int workDurationMinutes, int breakDurationMinutes, int longBreakDurationMinutes) {
        this.name = name;
        this.workDurationMinutes = workDurationMinutes;
        this.breakDurationMinutes = breakDurationMinutes;
        this.longBreakDurationMinutes = longBreakDurationMinutes;
        this.totalMinutes = 0;
        this.historyMap = new HashMap<>();
    }

    // REQUIRES: minutesCompleted >= 0
    // MODIFIES:this
    // EFFECTS: Adds minutesCompleted from a timer to the total minutes field.
    // Also adds the minutesCompleted to its associated date in the history dictionary.
    public void recordTime(LocalDate date, int minutesCompleted) {
        totalMinutes += minutesCompleted;
        Integer dailyMinutes = historyMap.get(date);
        if (dailyMinutes == null) {
            dailyMinutes = minutesCompleted;
        } else {
            dailyMinutes += minutesCompleted;
        }
        historyMap.put(date, dailyMinutes);
    }


    public void setWorkDurationMinutes(int minutes) {
        this.workDurationMinutes = minutes;
    }

    public void setBreakDurationMinutes(int minutes) {
        this.breakDurationMinutes = minutes;
    }

    public void setLongBreakDurationMinutes(int minutes) {
        this.longBreakDurationMinutes = minutes;
    }

    public Map<LocalDate, Integer> getHistoryMap() {
        return historyMap;
    }

    public String getName() {
        return this.name;
    }

    public int getWorkDurationMinutes() {
        return this.workDurationMinutes;
    }

    public int getBreakDurationMinutes() {
        return this.breakDurationMinutes;
    }

    public int getLongBreakDurationMinutes() {
        return this.longBreakDurationMinutes;
    }

    public int getTotalMinutes() {
        return this.totalMinutes;
    }

    // EFFECTS: Generates and returns a json object that represents the task
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("task_name", name);
        json.put("workDurationMinutes", workDurationMinutes);
        json.put("breakDurationMinutes", breakDurationMinutes);
        json.put("longBreakDurationMinutes", longBreakDurationMinutes);
        json.put("totalMinutes", totalMinutes);
        json.put("historyMap", historyMapToJson());

        return json;
    }

    // EFFECTS: Iterates through every key value pair in historyMap and adds them to a JSONArray
    private JSONArray historyMapToJson() {
        JSONArray jsonArray = new JSONArray();
        JSONObject pair;

        for (LocalDate key : historyMap.keySet()) {
            pair = new JSONObject();
            pair.put("date", key.toString());
            pair.put("minutes_completed", historyMap.get(key));
            jsonArray.put(pair);
        }
        return jsonArray;
    }
}
