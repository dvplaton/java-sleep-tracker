package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AvgDurationFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avg = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0.0);
        return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", String.format("%.1f", avg));
    }
}