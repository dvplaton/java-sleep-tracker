package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MinDurationFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long min = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .min()
                .orElse(0);
        return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", String.valueOf(min));
    }
}