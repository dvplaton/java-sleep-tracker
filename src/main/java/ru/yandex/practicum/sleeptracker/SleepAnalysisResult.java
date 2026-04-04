package ru.yandex.practicum.sleeptracker;

public class SleepAnalysisResult {
    private final String description;
    private final String value;

    public SleepAnalysisResult(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description + ": " + value;
    }
}