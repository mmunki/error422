package tw.mmunki.error422.core;

public enum Phase {
    CALM,
    UNEASY,
    CORRUPT,
    FINAL,
    ENDED;

    public static Phase at(double progress) {
        if (progress >= 1.0D) {
            return ENDED;
        }
        if (progress < 0.25D) {
            return CALM;
        }
        if (progress < 0.55D) {
            return UNEASY;
        }
        if (progress < 0.85D) {
            return CORRUPT;
        }
        return FINAL;
    }
}
