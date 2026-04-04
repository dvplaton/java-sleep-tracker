package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    // Вспомогательные методы

    private SleepingSession session(String fallAsleep, String wakeUp, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(fallAsleep.replace(" ", "T")),
                LocalDateTime.parse(wakeUp.replace(" ", "T")),
                quality
        );
    }

    private SleepingSession session(String fallAsleep, String wakeUp) {
        return session(fallAsleep, wakeUp, SleepQuality.NORMAL);
    }

    // TotalSessionsFunction

    @Test
    void totalSessions_multipleSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00", SleepQuality.GOOD),
                session("2025-10-02 23:00", "2025-10-03 08:00", SleepQuality.NORMAL),
                session("2025-10-03 14:30", "2025-10-03 15:20", SleepQuality.NORMAL)
        );
        SleepAnalysisResult result = new TotalSessionsFunction().apply(sessions);
        assertEquals("3", result.getValue());
    }

    @Test
    void totalSessions_empty() {
        SleepAnalysisResult result = new TotalSessionsFunction().apply(Collections.emptyList());
        assertEquals("0", result.getValue());
    }

    // MinDurationFunction

    @Test
    void minDuration_basic() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00"),  // 585 min
                session("2025-10-03 14:30", "2025-10-03 15:20")   // 50 min
        );
        SleepAnalysisResult result = new MinDurationFunction().apply(sessions);
        assertEquals("50", result.getValue());
    }

    @Test
    void minDuration_singleSession() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 23:00", "2025-10-02 07:00")  // 480 min
        );
        SleepAnalysisResult result = new MinDurationFunction().apply(sessions);
        assertEquals("480", result.getValue());
    }

    // MaxDurationFunction

    @Test
    void maxDuration_basic() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00"),  // 585 min
                session("2025-10-03 14:30", "2025-10-03 15:20")   // 50 min
        );
        SleepAnalysisResult result = new MaxDurationFunction().apply(sessions);
        assertEquals("585", result.getValue());
    }

    @Test
    void maxDuration_allSameDuration() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:00", "2025-10-02 06:00"),  // 480
                session("2025-10-02 22:00", "2025-10-03 06:00")   // 480
        );
        SleepAnalysisResult result = new MaxDurationFunction().apply(sessions);
        assertEquals("480", result.getValue());
    }

    // AvgDurationFunction

    @Test
    void avgDuration_basic() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:00", "2025-10-02 06:00"),  // 480
                session("2025-10-02 22:00", "2025-10-03 08:00")   // 600
        );
        SleepAnalysisResult result = new AvgDurationFunction().apply(sessions);
        assertEquals("540.0", result.getValue());
    }

    @Test
    void avgDuration_singleSession() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 23:00", "2025-10-02 07:30")  // 510
        );
        SleepAnalysisResult result = new AvgDurationFunction().apply(sessions);
        assertEquals("510.0", result.getValue());
    }

    // BadQualityCountFunction

    @Test
    void badQuality_someBAD() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00", SleepQuality.GOOD),
                session("2025-10-02 23:00", "2025-10-03 08:00", SleepQuality.BAD),
                session("2025-10-03 23:30", "2025-10-04 06:20", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new BadQualityCountFunction().apply(sessions);
        assertEquals("2", result.getValue());
    }

    @Test
    void badQuality_noneBAD() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00", SleepQuality.GOOD),
                session("2025-10-02 23:00", "2025-10-03 08:00", SleepQuality.NORMAL)
        );
        SleepAnalysisResult result = new BadQualityCountFunction().apply(sessions);
        assertEquals("0", result.getValue());
    }

    // SleeplessNightsFunction

    @Test
    void sleeplessNights_noSleeplessNights() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00"),
                session("2025-10-02 23:00", "2025-10-03 08:00"),
                session("2025-10-03 23:30", "2025-10-04 06:20")
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().apply(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_oneSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 22:15", "2025-10-02 08:00"),  // покрывает ночь
                session("2025-10-03 07:00", "2025-10-03 11:00"),  // дневной, не покрывает ночь
                session("2025-10-03 23:30", "2025-10-04 06:20")   // покрывает ночь
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().apply(sessions);
        assertEquals("1", result.getValue());
    }

    @Test
    void sleeplessNights_sessionEndsBefore6AM_stillCoversNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-02 02:00", "2025-10-02 05:00")  // покрывает ночь
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().apply(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_sessionStartsAfterNoon_firstNightIsNext() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 14:00", "2025-10-01 15:00"),  // дневной
                session("2025-10-02 23:00", "2025-10-03 07:00")   // покрывает ночь
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().apply(sessions);
        assertEquals("1", result.getValue());
    }

    @Test
    void sleeplessNights_empty() {
        SleepAnalysisResult result = new SleeplessNightsFunction().apply(Collections.emptyList());
        assertEquals("0", result.getValue());
    }

    // ChronoTypeFunction

    @Test
    void chronoType_owl() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 23:30", "2025-10-02 09:30"),
                session("2025-10-02 23:15", "2025-10-03 10:00"),
                session("2025-10-03 23:45", "2025-10-04 09:15")
        );
        SleepAnalysisResult result = new ChronoTypeFunction().apply(sessions);
        assertEquals("Сова", result.getValue());
    }

    @Test
    void chronoType_lark() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 21:00", "2025-10-02 05:30"),
                session("2025-10-02 21:30", "2025-10-03 06:00"),
                session("2025-10-03 20:45", "2025-10-04 06:30")
        );
        SleepAnalysisResult result = new ChronoTypeFunction().apply(sessions);
        assertEquals("Жаворонок", result.getValue());
    }

    @Test
    void chronoType_pigeon_byDefault() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 23:30", "2025-10-02 09:30"),  // сова
                session("2025-10-02 21:00", "2025-10-03 06:00")   // жаворонок
        );
        SleepAnalysisResult result = new ChronoTypeFunction().apply(sessions);
        assertEquals("Голубь", result.getValue());
    }

    @Test
    void chronoType_daytimeSessions_ignored() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01 14:00", "2025-10-01 15:00"),  // дневной
                session("2025-10-01 23:30", "2025-10-02 09:30"),  // сова
                session("2025-10-02 13:00", "2025-10-02 14:00")   // дневной
        );
        SleepAnalysisResult result = new ChronoTypeFunction().apply(sessions);
        assertEquals("Сова", result.getValue());
    }

    @Test
    void chronoType_empty_returnsPigeon() {
        SleepAnalysisResult result = new ChronoTypeFunction().apply(Collections.emptyList());
        assertEquals("Голубь", result.getValue());
    }

    // Тест парсинга файла

    @Test
    void parseLine_correctParsing() {
        SleepingSession s = SleepFileReader.parseLine("01.10.25 22:15;02.10.25 08:00;GOOD");
        assertEquals(SleepQuality.GOOD, s.getQuality());
        assertEquals(585, s.getDurationMinutes());
    }
}