package dev.mmunki.error422;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Error422SettingsTest {
    @Test
    void defaultsAreSafe() {
        Error422Settings settings = new Error422Settings();
        assertTrue(settings.enabled);
        assertTrue(settings.corruptHud);
        assertTrue(settings.corruptTitleScreen);
        assertTrue(settings.playStatic);
        assertFalse(settings.allowWorldChanges);
        assertFalse(settings.allowInventoryScramble);
        assertFalse(settings.allowDamage);
        assertEquals(45, settings.sessionMinutes);
        assertEquals(2, settings.intensity);
    }

    @Test
    void normalizationClampsNumericFields() {
        Error422Settings settings = new Error422Settings();
        settings.sessionMinutes = 9999;
        settings.intensity = -4;
        Error422Settings normalized = settings.normalized();
        assertEquals(240, normalized.sessionMinutes);
        assertEquals(1, normalized.intensity);
        assertEquals(240 * 60, normalized.sessionSeconds());
    }

    @Test
    void normalizationKeepsTheFlags() {
        Error422Settings settings = new Error422Settings();
        settings.enabled = false;
        settings.allowWorldChanges = true;
        settings.allowDamage = true;
        Error422Settings normalized = settings.normalized();
        assertFalse(normalized.enabled);
        assertTrue(normalized.allowWorldChanges);
        assertTrue(normalized.allowDamage);
    }

    @Test
    void clampWorksAtTheEdges() {
        assertEquals(1, Error422Settings.clamp(0, 1, 3));
        assertEquals(1, Error422Settings.clamp(1, 1, 3));
        assertEquals(3, Error422Settings.clamp(3, 1, 3));
        assertEquals(3, Error422Settings.clamp(4, 1, 3));
    }
}
