package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Dictionary;

import static org.junit.jupiter.api.Assertions.*;

public class TestTask {
    private Task testTask;
    private LocalDate testDate1;
    private LocalDate testDate2;
    private Dictionary<LocalDate, Integer> testDict;

    @BeforeEach
    private void runBefore() {
        testTask = new Task("testTask", 1, 1, 1);
        testDate1 = LocalDate.of(2000, 01, 01);
        testDate2 = LocalDate.of(2000, 01, 02);
    }

    @Test
    void recordTimeFirstEntry() {
        assertEquals(0, testTask.getHistoryDict().size());
        testTask.recordTime(testDate1, 1);
        testDict = testTask.getHistoryDict();
        assertEquals(1, testDict.size());
        assertEquals(1, testDict.get(testDate1));
    }

    @Test
    void recordTimeMultipleEntries() {
        testTask.recordTime(testDate1, 1);
        testTask.recordTime(testDate2, 5);
        testDict = testTask.getHistoryDict();
        assertEquals(2, testDict.size());
        assertEquals(6, testTask.getTotalMinutes());
        assertEquals(1, testDict.get(testDate1));
        assertEquals(5, testDict.get(testDate2));

    }

    @Test
    void recordTimeMultipleEntriesSameDay() {
        testTask.recordTime(testDate1, 1);
        testTask.recordTime(testDate2, 5);
        testTask.recordTime(testDate2, 10);
        testTask.recordTime(testDate2, 14);
        testDict = testTask.getHistoryDict();
        assertEquals(2, testDict.size());
        assertEquals(30, testTask.getTotalMinutes());
        assertEquals(1, testDict.get(testDate1));
        assertEquals(29, testDict.get(testDate2));
    }
}
