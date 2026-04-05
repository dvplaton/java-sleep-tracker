package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SleepFileReader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static List<SleepingSession> readSessions(String filePath) throws IOException {
        return Files.lines(Path.of(filePath))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(SleepFileReader::parseLine)
                .collect(Collectors.toList());
    }

    public static SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        LocalDateTime fallAsleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime wakeUp = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
        return new SleepingSession(fallAsleep, wakeUp, quality);
    }
}