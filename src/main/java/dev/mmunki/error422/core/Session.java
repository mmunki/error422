package dev.mmunki.error422.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Session {
    public static final int TICKS_PER_SECOND = 20;

    private final long seed;
    private final int durationTicks;
    private final Random random;
    private int ticks;
    private int nextEventTick;
    private int eventIndex;
    private boolean finaleEmitted;

    public Session(long seed, int durationSeconds) {
        this.seed = seed;
        this.durationTicks = Math.max(TICKS_PER_SECOND, durationSeconds * TICKS_PER_SECOND);
        this.random = new Random(seed);
        this.nextEventTick = intervalTicks(Phase.CALM);
    }

    public long seed() {
        return seed;
    }

    public int durationTicks() {
        return durationTicks;
    }

    public int ticks() {
        return ticks;
    }

    public int eventIndex() {
        return eventIndex;
    }

    public void tick() {
        if (ticks < durationTicks) {
            ticks++;
        }
    }

    public double progress() {
        return durationTicks <= 0 ? 1.0D : (double) ticks / (double) durationTicks;
    }

    public Phase phase() {
        return Phase.at(progress());
    }

    public boolean finished() {
        return ticks >= durationTicks;
    }

    public List<Anomaly> pollEvents(int limit) {
        List<Anomaly> events = new ArrayList<>();
        if (finished() || limit <= 0) {
            return events;
        }
        if (!finaleEmitted && phase() == Phase.FINAL) {
            events.add(Anomaly.FINALE);
            finaleEmitted = true;
        }
        while (events.size() < limit && ticks >= nextEventTick) {
            events.add(Anomaly.pick(random, phase()));
            eventIndex++;
            nextEventTick = ticks + intervalTicks(phase());
        }
        return events;
    }

    public int intervalTicks(Phase phase) {
        int base = switch (phase) {
            case CALM -> 400;
            case UNEASY -> 240;
            case CORRUPT -> 140;
            case FINAL -> 60;
            case ENDED -> 400;
        };
        double scale = (double) durationTicks / (double) (45 * 60 * TICKS_PER_SECOND);
        return Math.max(TICKS_PER_SECOND, (int) Math.round(base * scale));
    }
}
