package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public class NightUtils {

    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime EVENING_START = LocalTime.of(18, 0);

    private NightUtils() {
    }

    // - Дневной сон (14:30–15:20)
    // - Утреннее досыпание (05:30–10:00)
    // - Вечерний сон (19:00–22:00)
    public static boolean isNightSession(SleepingSession session) {
        LocalTime startTime = session.getFallAsleep().toLocalTime();

        boolean eveningFallAsleep = !startTime.isBefore(EVENING_START);
        if (!eveningFallAsleep) {
            return false;
        }

        return !coveredNightDates(session).isEmpty();
    }

    public static Set<LocalDate> coveredNightDates(SleepingSession session) {
        LocalDateTime start = session.getFallAsleep();
        LocalDateTime end = session.getWakeUp();

        LocalDate from = start.toLocalDate();
        LocalDate to = end.toLocalDate().plusDays(1);

        return from.datesUntil(to)
                .filter(d -> {
                    LocalDateTime nightStart = d.atStartOfDay();
                    LocalDateTime nightEnd = d.atTime(NIGHT_END);
                    return start.isBefore(nightEnd) && end.isAfter(nightStart);
                })
                .collect(Collectors.toSet());
    }
}