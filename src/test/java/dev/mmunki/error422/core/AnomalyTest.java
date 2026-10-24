package dev.mmunki.error422.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnomalyTest {
    @Test
    void everyPhaseHasAValidPool() {
        for (Phase phase : Phase.values()) {
            List<Anomaly> pool = Anomaly.pool(phase);
            if (phase == Phase.ENDED) {
                assertTrue(pool.isEmpty());
            } else {
                assertFalse(pool.isEmpty());
                for (Anomaly anomaly : pool) {
                    assertTrue(Anomaly.pool(phase).contains(anomaly));
                }
            }
        }
    }

    @Test
    void theFinaleIsNotPartOfAnyRandomPool() {
        for (Phase phase : Phase.values()) {
            assertFalse(Anomaly.pool(phase).contains(Anomaly.FINALE));
        }
    }

    @Test
    void pickNeverReturnsAnAnomalyOutsideThePool() {
        Random random = new Random(422L);
        for (Phase phase : List.of(Phase.CALM, Phase.UNEASY, Phase.CORRUPT, Phase.FINAL)) {
            for (int index = 0; index < 200; index++) {
                assertTrue(Anomaly.pool(phase).contains(Anomaly.pick(random, phase)));
            }
        }
    }
}
