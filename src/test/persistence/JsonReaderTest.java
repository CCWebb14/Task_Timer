package persistence;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Code influenced by JsonSerializationDemo: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

class JsonReaderTest {
    Project project;
    List<Task> taskList;

    @BeforeEach
    void startUp() {
        project = new Project("testProject");
    }

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            project = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyProject() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyProject.json");
        try {
            project = reader.read();
            taskList = project.getTaskList();
            assertEquals("testProject", project.getName());
            assertEquals(0, project.calculateTotalMinutes());
            assertEquals(0, taskList.size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkProject() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralProject.json");
        try {
            project = reader.read();
            assertEquals("testProject", project.getName());
            taskList = project.getTaskList();
            assertEquals(6, taskList.size());

            Task firstTask = taskList.get(0);
            assertEquals("CPSC210", firstTask.getName());
            assertEquals(1, firstTask.getWorkDurationMinutes());
            assertEquals(2, firstTask.getBreakDurationMinutes());
            assertEquals(3, firstTask.getLongBreakDurationMinutes());
            assertEquals(0, firstTask.getTotalMinutes());

            Task secondTask = taskList.get(1);
            assertEquals(115, secondTask.getTotalMinutes());
            int minutesLoggedYesterday = secondTask.getHistoryMap().get(LocalDate.now().minusDays(1));
            assertEquals(5, minutesLoggedYesterday);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}