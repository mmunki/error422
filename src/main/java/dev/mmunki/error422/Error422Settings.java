package dev.mmunki.error422;

public final class Error422Settings {
    public boolean enabled = true;
    public int sessionMinutes = 45;
    public int intensity = 2;
    public boolean clientAutoStart = true;
    public boolean corruptHud = true;
    public boolean corruptTitleScreen = true;
    public boolean playStatic = true;
    public boolean allowWorldChanges = false;
    public boolean allowInventoryScramble = false;
    public boolean allowDamage = false;
    public boolean disableInCreative = true;
    public long seed = 0L;

    public Error422Settings normalized() {
        Error422Settings copy = new Error422Settings();
        copy.enabled = enabled;
        copy.sessionMinutes = clamp(sessionMinutes, 1, 240);
        copy.intensity = clamp(intensity, 1, 3);
        copy.clientAutoStart = clientAutoStart;
        copy.corruptHud = corruptHud;
        copy.corruptTitleScreen = corruptTitleScreen;
        copy.playStatic = playStatic;
        copy.allowWorldChanges = allowWorldChanges;
        copy.allowInventoryScramble = allowInventoryScramble;
        copy.allowDamage = allowDamage;
        copy.disableInCreative = disableInCreative;
        copy.seed = seed;
        return copy;
    }

    public int sessionSeconds() {
        return clamp(sessionMinutes, 1, 240) * 60;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
