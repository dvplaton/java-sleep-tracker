package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "0");
        }

        LocalDateTime firstStart = sessions.getFirst().getFallAsleep();
        LocalDateTime lastEnd = sessions.getLast().getWakeUp();

        // Определяем первую ночь для подсчёта
        LocalDate firstNight = firstStart.toLocalTime().isBefore(LocalTime.NOON)
                ? firstStart.toLocalDate()
                : firstStart.toLocalDate().plusDays(1);
        // Определяем последнюю ночь для подсчёта
        LocalDate lastNight;
        // Если окончание ровно в полночь, ночь этого дня ещё не наступала
        if (lastEnd.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            lastNight = lastEnd.toLocalDate().minusDays(1);
        } else {
            lastNight = lastEnd.toLocalDate();
        }

        if (firstNight.isAfter(lastNight)) {
            return new SleepAnalysisResult("Количество бессонных ночей", "0");
        }

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        // Собираем все ночи (даты), покрытые хотя бы одной сессией
        Set<LocalDate> coveredNights = sessions.stream()
                .flatMap(s -> coveredNightDates(s).stream())
                .filter(d -> !d.isBefore(firstNight) && !d.isAfter(lastNight))
                .collect(Collectors.toSet());

        long sleeplessCount = totalNights - coveredNights.size();

        return new SleepAnalysisResult("Количество бессонных ночей", String.valueOf(sleeplessCount));
    }

    private Set<LocalDate> coveredNightDates(SleepingSession session) {
        LocalDateTime start = session.getFallAsleep();
        LocalDateTime end = session.getWakeUp();

        LocalDate from = start.toLocalDate();
        LocalDate to = end.toLocalDate().plusDays(1);

        return from.datesUntil(to).filter(d -> {
            LocalDateTime nightStart = d.atStartOfDay();
            LocalDateTime nightEnd = d.atTime(6, 0);
            return start.isBefore(nightEnd) && end.isAfter(nightStart);
        }).collect(Collectors.toSet());
    }
}