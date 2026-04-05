package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private final List<Function<List<SleepingSession>, SleepAnalysisResult>> analysisFunctions;

    public SleepTrackerApp() {
        this.analysisFunctions = List.of(
                new TotalSessionsFunction(),
                new MinDurationFunction(),
                new MaxDurationFunction(),
                new AvgDurationFunction(),
                new BadQualityCountFunction(),
                new SleeplessNightsFunction(),
                new ChronoTypeFunction()
        );
    }

    public List<Function<List<SleepingSession>, SleepAnalysisResult>> getAnalysisFunctions() {
        return analysisFunctions;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу>");
            return;
        }

        String filePath = args[0];

        try {
            List<SleepingSession> sessions = SleepFileReader.readSessions(filePath);
            SleepTrackerApp app = new SleepTrackerApp();

            System.out.println("=== Анализ сна ===");
            System.out.println();

            app.getAnalysisFunctions().stream()
                    .map(f -> f.apply(sessions))
                    .forEach(result -> System.out.println(result.toString()));

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}