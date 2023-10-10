package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testProject {
    private Project testProject;
    private Task testTask1;
    private Task testTask1Dup;
    private Task testTask2;
    private List<Task> taskList;
    private LocalDate testDate1;
    private LocalDate testDate2;


    @BeforeEach
    void startUp() {
        testProject = new Project("testProject");
        testTask1 = new Task("Task1");
        testTask1Dup = new Task("Task1");
        testTask2 = new Task("Task2");
        testDate1 = LocalDate.of(2000, 01, 01);
        testDate2 = LocalDate.of(2000, 01, 02);
        testTask1.recordTime(testDate1, 25);
        testTask2.recordTime(testDate2, 50);
    }

    @Test
    void testAddTask() {
        assertTrue(testProject.addTask(testTask1));
        taskList = testProject.getTaskList();
        assertEquals(1, taskList.size());
        assertEquals(testTask1, taskList.get(0));
    }

    @Test
    void testAddTaskSameName() {
        assertTrue(testProject.addTask(testTask1));
        assertFalse(testProject.addTask(testTask1Dup));
        taskList = testProject.getTaskList();
        assertEquals(1, taskList.size());
        assertEquals(testTask1, taskList.get(0));
    }

    @Test
    void testAddTaskMultiple() {
        testProject.addTask(testTask1);
        testProject.addTask(testTask2);
        taskList = testProject.getTaskList();
        assertEquals(2, taskList.size());
        assertEquals(testTask1, taskList.get(0));
        assertEquals(testTask2, taskList.get(1));
    }

    @Test
    void testCalculateTotalMinutes() {
        testProject.addTask(testTask1);
        testProject.addTask(testTask2);
        assertEquals(75, testProject.calculateTotalMinutes());
        testTask2.recordTime(testDate2, 125);
        assertEquals(200, testProject.calculateTotalMinutes());
    }

}
