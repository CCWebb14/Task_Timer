package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestTimerSession {
    TimerSession testTimerSession;

    @BeforeEach
    void startUp() {
        testTimerSession = new TimerSession(30);
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
    void testGetSecondsRemainingAndCompleted() throws InterruptedException {
        assertEquals(0, testTimerSession.getSecondsRemaining());
        assertEquals(0, testTimerSession.getSecondsCompleted());
        testTimerSession.startTimer();
        assertEquals(0, testTimerSession.getSecondsRemaining());
        assertEquals(0, testTimerSession.getSecondsCompleted());
        Thread.sleep(2500);
        assertEquals(58, testTimerSession.getSecondsRemaining());
        assertEquals(2, testTimerSession.getSecondsCompleted());
    }

    @Test
    void testCalculateCompletedMinutes() throws InterruptedException {
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
    }

    @Test
    void testAwaitTimer() throws InterruptedException {
        testTimerSession.startTimer();
        Thread.sleep(1200);
        // 29 min, 59 sec
        testTimerSession.fastForwardTimer(29, 58);
        // 1 second remaining
        assertFalse(testTimerSession.isTimerComplete());
        testTimerSession.awaitTimer();
        assertTrue(testTimerSession.isTimerComplete());
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
