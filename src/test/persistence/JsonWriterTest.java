package persistence;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Code influenced by JsonSerializationDemo: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

class JsonWriterTest {
    private Project project;
    private List<Task> taskList;

    @BeforeEach
    void startUp() {
        project = new Project("testProject");
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyProject() {
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyProject.json");
            writer.open();
            writer.write(project);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyProject.json");
            project = reader.read();
            taskList = project.getTaskList();
            assertEquals("testProject", project.getName());
            assertEquals(0, project.calculateTotalMinutes());
            assertEquals(0, taskList.size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralProject() {
        try {
            Task cpsc210 = new Task("CPSC210", 1, 2, 3);
            Task cpsc121 = new Task("CPSC121", 2, 2, 2);
            Task phil220 = new Task("PHIL220", 3, 3, 3);
            Task dsci100 = new Task("DSCI100", 4, 4, 4);
            Task cpsc213 = new Task("CPSC213", 5, 5, 5);
            Task cpsc221 = new Task("CPSC221", 6, 6, 6);
            cpsc121.recordTime(LocalDate.now().minusDays(1), 5);
            cpsc121.recordTime(LocalDate.now().minusDays(2), 10);
            cpsc121.recordTime(LocalDate.now().minusDays(5), 100);
            phil220.recordTime(LocalDate.now(), 10);
            phil220.recordTime(LocalDate.now().minusDays(1), 25);
            dsci100.recordTime(LocalDate.now(), 15);
            project.addTask(cpsc210);
            project.addTask(cpsc121);
            project.addTask(phil220);
            project.addTask(dsci100);
            project.addTask(cpsc213);
            project.addTask(cpsc221);

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralProject.json");
            writer.open();
            writer.write(project);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralProject.json");
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
            fail("Exception should not have been thrown");
        }
    }
}