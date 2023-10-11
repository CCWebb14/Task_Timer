package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

public class TestTimerSession {
    TimerSession testTimerSession;
    CountDownLatch testLatch;

    @BeforeEach
    void startUp() {
        testLatch = new CountDownLatch(1);
        testTimerSession = new TimerSession(30, testLatch);
    }

    @Test
    void testStartTimer() {
        assertFalse(testTimerSession.isTimerRunning());
        testTimerSession.startTimer();
        assertTrue(testTimerSession.isTimerRunning());
        assertFalse(testTimerSession.isTimerComplete());
    }

    @Test
    void testPauseTimer() {
        testTimerSession.startTimer();
        testTimerSession.pauseTimer();
        assertFalse(testTimerSession.isTimerRunning());
        assertFalse(testTimerSession.isTimerComplete());
        testTimerSession.startTimer();
        assertTrue(testTimerSession.isTimerRunning());
        assertFalse(testTimerSession.isTimerComplete());
    }

    @Test
    void testGetMinutesRemaining() {
        assertEquals(30, testTimerSession.getMinutesRemaining());
        testTimerSession.fastForwardTimer(29, 0);
        assertEquals(1, testTimerSession.getMinutesRemaining());
        testTimerSession.fastForwardTimer(1, 0);
        assertEquals(0, testTimerSession.getMinutesRemaining());
    }

    @Test
    void testGetSecondsRemaining() throws InterruptedException {
        assertEquals(0, testTimerSession.getSecondsRemaining());
        testTimerSession.startTimer();
        assertEquals(0, testTimerSession.getSecondsRemaining());
        Thread.sleep(1500);
        assertEquals(59, testTimerSession.getSecondsRemaining());
    }

    @Test
    void testCalculateCompletedMinutes() throws InterruptedException {
        testTimerSession.calculateCompletedMinutes();
        assertEquals(0, testTimerSession.calculateCompletedMinutes());
        testTimerSession.startTimer();
        Thread.sleep(2000);
        assertEquals(0, testTimerSession.calculateCompletedMinutes());
        testTimerSession.fastForwardTimer(29, 0);
        assertEquals(29, testTimerSession.calculateCompletedMinutes());
    }

    @Test
    void testTimerComplete() throws InterruptedException {
        // 30 min, 0 sec
        assertFalse(testTimerSession.isTimerComplete());
        assertEquals(1, testLatch.getCount());
        testTimerSession.fastForwardTimer(28, 0);
        testTimerSession.startTimer();
        Thread.sleep(1100);
        // 1 min, ~59 sec
        assertFalse(testTimerSession.isTimerComplete());
        testTimerSession.fastForwardTimer(1, 0);
        // 0 min, ~59 sec
        assertFalse(testTimerSession.isTimerComplete());
        testTimerSession.fastForwardTimer(0, 58);
        // 0 min, ~1 sec
        assertFalse(testTimerSession.isTimerComplete());
        Thread.sleep(1100);
        // 0 min, 0 sec
        assertTrue(testTimerSession.isTimerComplete());
        assertEquals(0, testLatch.getCount());
    }


    @Test
    void testCancelTimer() {
        testTimerSession.startTimer();
        testTimerSession.cancelTimer();
        assertFalse(testTimerSession.isTimerRunning());
        assertFalse(testTimerSession.isTimerRunning());
        assertTrue(testTimerSession.isTimerCancelled());
    }

}
