package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronoTypeFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {

        List<SleepingSession> nightSessions = sessions.stream().filter(NightUtils::isNightSession).toList();

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", ChronoType.PIGEON.getDisplayName());
        }

        Map<ChronoType, Long> counts = nightSessions.stream()
                .map(this::classifyNight)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long owlCount = counts.getOrDefault(ChronoType.OWL, 0L);
        long larkCount = counts.getOrDefault(ChronoType.LARK, 0L);
        long pigeonCount = counts.getOrDefault(ChronoType.PIGEON, 0L);

        ChronoType result;
        if (owlCount > larkCount && owlCount > pigeonCount) {
            result = ChronoType.OWL;
        } else if (larkCount > owlCount && larkCount > pigeonCount) {
            result = ChronoType.LARK;
        } else {
            result = ChronoType.PIGEON;
        }

        return new SleepAnalysisResult("Хронотип пользователя", result.getDisplayName());
    }

    private ChronoType classifyNight(SleepingSession session) {
        LocalTime sleepTime = session.getFallAsleep().toLocalTime();
        LocalTime wakeTime = session.getWakeUp().toLocalTime();

        // Сова
        boolean isOwl = sleepTime.isAfter(LocalTime.of(23, 0)) && wakeTime.isAfter(LocalTime.of(9, 0));

        // Жаворонок
        boolean isLark = sleepTime.isBefore(LocalTime.of(22, 0)) && wakeTime.isBefore(LocalTime.of(7, 0));

        if (isOwl) return ChronoType.OWL;
        if (isLark) return ChronoType.LARK;
        return ChronoType.PIGEON;
    }
}