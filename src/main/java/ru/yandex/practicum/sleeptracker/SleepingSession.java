package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.Duration;

public class SleepingSession {
    private final LocalDateTime fallAsleep;
    private final LocalDateTime wakeUp;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime fallAsleep, LocalDateTime wakeUp, SleepQuality quality) {
        this.fallAsleep = fallAsleep;
        this.wakeUp = wakeUp;
        this.quality = quality;
    }

    public LocalDateTime getFallAsleep() {
        return fallAsleep;
    }

    public LocalDateTime getWakeUp() {
        return wakeUp;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationMinutes() {
        return Duration.between(fallAsleep, wakeUp).toMinutes();
    }
}