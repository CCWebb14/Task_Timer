package persistence;

import model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.json.*;

// Code influenced by JsonSerializationDemo: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a reader that reads in a project from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads project from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Project read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseProject(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses project from JSON object and returns it
    private Project parseProject(JSONObject jsonObject) {
        String name = jsonObject.getString("project_name");
        Project project = new Project(name);
        addTasks(project, jsonObject);
        return project;
    }

    // MODIFIES: project
    // EFFECTS: parses tasks from JSON object and adds them to project
    private void addTasks(Project project, JSONObject jsonObject) {
        JSONArray jsonTasksArray = jsonObject.getJSONArray("tasks");
        for (Object json : jsonTasksArray) {
            JSONObject nextTask = (JSONObject) json;
            addTask(project, nextTask);
        }
    }

    // MODIFIES: project
    // EFFECTS: parses task from JSON object and adds it to project
    private void addTask(Project project, JSONObject jsonObject) {
        String name = jsonObject.getString("task_name");
        int workDurationMinutes = jsonObject.getInt("workDurationMinutes");
        int breakDurationMinutes = jsonObject.getInt("breakDurationMinutes");
        int longBreakDurationMinutes = jsonObject.getInt("longBreakDurationMinutes");
        JSONArray jsonHistoryMapArray = jsonObject.getJSONArray("historyMap");
        Task task = new Task(name, workDurationMinutes, breakDurationMinutes, longBreakDurationMinutes);
        for (Object json : jsonHistoryMapArray) {
            JSONObject nextDay = (JSONObject) json;
            recordHistoryMap(task, nextDay);
        }
        project.addTask(task);
    }

    // MODIFIES: task
    // EFFECTS: Takes dates and minutes completed from the jsonObject and records it in the given task
    private void recordHistoryMap(Task task, JSONObject jsonObject) {
        LocalDate date = LocalDate.parse(jsonObject.getString("date"));
        int minutesCompleted = jsonObject.getInt("minutes_completed");
        task.recordTime(date, minutesCompleted);
    }
}
