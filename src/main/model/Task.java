package model;

import java.time.LocalDate;
import java.util.Dictionary;
import java.util.Hashtable;

// Represents a task, a task holds timer options (work duration and break duration)
// A task also holds daily history of total minutes completed
public class Task {
    private String name;
    private int workDurationMinutes;
    private int workDurationMilliseconds;
    private int breakDurationMinutes;
    private int breakDurationMilliseconds;
    private int longBreakDurationMinutes;
    private int longBreakDurationMilliseconds;
    private int totalMinutes;
    private Dictionary<LocalDate, Integer> historyDict;

    private static final int minutesToMillisecondsFactor = 60000;

    // Default constructor
    public Task(String name) {
        this.name = name;
        this.workDurationMinutes = 25;
        this.workDurationMilliseconds = workDurationMinutes * minutesToMillisecondsFactor;
        this.breakDurationMinutes = 5;
        this.breakDurationMilliseconds = breakDurationMinutes * minutesToMillisecondsFactor;
        this.longBreakDurationMinutes = 15;
        this.longBreakDurationMilliseconds = longBreakDurationMinutes * minutesToMillisecondsFactor;
        this.totalMinutes = 0;
        this.historyDict = new Hashtable<>();
    }

    // Constructor that specifies work and break duration
    public Task(String name, int workDurationMinutes, int breakDurationMinutes, int longBreakDurationMinutes) {
        this.name = name;
        this.workDurationMinutes = workDurationMinutes;
        this.workDurationMilliseconds = workDurationMinutes * minutesToMillisecondsFactor;
        this.breakDurationMinutes = breakDurationMinutes;
        this.breakDurationMilliseconds = breakDurationMinutes * minutesToMillisecondsFactor;
        this.longBreakDurationMinutes = longBreakDurationMinutes;
        this.longBreakDurationMilliseconds = longBreakDurationMinutes * minutesToMillisecondsFactor;
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
        this.workDurationMilliseconds = this.workDurationMinutes * minutesToMillisecondsFactor;
    }

    public void setBreakDurationMinutes(int minutes) {
        this.breakDurationMinutes = minutes;
        this.breakDurationMilliseconds = this.breakDurationMinutes * minutesToMillisecondsFactor;
    }

    public void setLongBreakDurationMinutes(int minutes) {
        this.longBreakDurationMinutes = minutes;
        this.longBreakDurationMilliseconds = this.longBreakDurationMilliseconds * minutesToMillisecondsFactor;
    }

    public Dictionary<LocalDate, Integer> getHistoryDict() {
        return historyDict;
    }

    public int getWorkDurationMinutes() {
        return this.workDurationMinutes;
    }

    public int getWorkDurationMilliseconds() {
        return this.workDurationMilliseconds;
    }

    public int getBreakDurationMinutes() {
        return this.breakDurationMinutes;
    }

    public int getBreakDurationMilliseconds() {
        return this.breakDurationMilliseconds;
    }

    public int getLongBreakDurationMinutes() {
        return this.longBreakDurationMinutes;
    }

    public int getTotalMinutes() {
        return this.totalMinutes;
    }
}
