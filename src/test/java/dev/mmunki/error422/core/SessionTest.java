package dev.mmunki.error422.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {
    @Test
    void durationIsConvertedToTicks() {
        Session session = new Session(1L, 45 * 60);
        assertEquals(45 * 60 * 20, session.durationTicks());
        assertFalse(session.finished());
    }

    @Test
    void phaseBoundariesMatchTheDocumentedSchedule() {
        assertEquals(Phase.CALM, Phase.at(0.0D));
        assertEquals(Phase.CALM, Phase.at(0.249D));
        assertEquals(Phase.UNEASY, Phase.at(0.25D));
        assertEquals(Phase.UNEASY, Phase.at(0.549D));
        assertEquals(Phase.CORRUPT, Phase.at(0.55D));
        assertEquals(Phase.CORRUPT, Phase.at(0.849D));
        assertEquals(Phase.FINAL, Phase.at(0.85D));
        assertEquals(Phase.FINAL, Phase.at(0.999D));
        assertEquals(Phase.ENDED, Phase.at(1.0D));
    }

    @Test
    void sameSeedProducesTheSameEventSchedule() {
        List<Anomaly> first = collect(123456789L, 6000);
        List<Anomaly> second = collect(123456789L, 6000);
        assertEquals(first, second);
    }

    @Test
    void differentSeedsProduceDifferentSchedules() {
        List<Anomaly> first = collect(1L, 6000);
        List<Anomaly> second = collect(2L, 6000);
        assertFalse(first.equals(second));
    }

    @Test
    void finaleIsEmittedExactlyOnce() {
        Session session = new Session(99L, 100);
        int finaleCount = 0;
        while (!session.finished()) {
            session.tick();
            for (Anomaly anomaly : session.pollEvents(4)) {
                if (anomaly == Anomaly.FINALE) {
                    finaleCount++;
                }
            }
        }
        assertEquals(1, finaleCount);
    }

    @Test
    void shortSessionsScaleTheEventIntervalDown() {
        Session shortSession = new Session(5L, 60);
        Session longSession = new Session(5L, 45 * 60);
        assertTrue(shortSession.intervalTicks(Phase.CALM) < longSession.intervalTicks(Phase.CALM));
        assertTrue(shortSession.intervalTicks(Phase.CALM) >= Session.TICKS_PER_SECOND);
    }

    @Test
    void finishedSessionEmitsNoMoreEvents() {
        Session session = new Session(7L, 1);
        for (int tick = 0; tick < session.durationTicks(); tick++) {
            session.tick();
            session.pollEvents(4);
        }
        assertTrue(session.finished());
        assertTrue(session.pollEvents(4).isEmpty());
    }

    private static List<Anomaly> collect(long seed, int ticks) {
        Session session = new Session(seed, 45 * 60);
        List<Anomaly> events = new ArrayList<>();
        for (int tick = 0; tick < ticks; tick++) {
            session.tick();
            events.addAll(session.pollEvents(4));
        }
        return events;
    }
}
