package model;

import java.time.LocalDate;
import java.util.Dictionary;
import java.util.Hashtable;

// Represents a task, a task holds timer options (work duration and break duration)
// A task also holds daily history of total minutes completed
public class Task {
    private String name;
    private int workDurationMinutes;
    private int breakDurationMinutes;
    private int longBreakDurationMinutes;
    private int totalMinutes;
    private Dictionary<LocalDate, Integer> historyDict;

    // Default constructor
    public Task(String name) {
        this.name = name;
        this.workDurationMinutes = 25;
        this.breakDurationMinutes = 5;
        this.longBreakDurationMinutes = 15;
        this.totalMinutes = 0;
        this.historyDict = new Hashtable<>();
    }

    // Constructor that specifies work and break duration
    public Task(String name, int workDurationMinutes, int breakDurationMinutes, int longBreakDurationMinutes) {
        this.name = name;
        this.workDurationMinutes = workDurationMinutes;
        this.breakDurationMinutes = breakDurationMinutes;
        this.longBreakDurationMinutes = longBreakDurationMinutes;
        this.totalMinutes = 0;
        this.historyDict = new Hashtable<>();
    }

    // REQUIRES: minutesCompleted >= 0
    // MODIFIES:this
    // EFFECTS: Adds minutesCompleted from a timer to the total minutes field.
    // Also adds the minutesCompleted to its associated date in the history dictionary.
    public void recordTime(LocalDate date, int minutesCompleted) {
        totalMinutes += minutesCompleted;
        Integer dailyMinutes = historyDict.get(date);
        if (dailyMinutes == null) {
            dailyMinutes = minutesCompleted;
        } else {
            dailyMinutes += minutesCompleted;
        }
        historyDict.put(date, dailyMinutes);
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

    public Dictionary<LocalDate, Integer> getHistoryDict() {
        return historyDict;
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
}
