package ru.yandex.practicum.sleeptracker;

public enum ChronoType {
    OWL("Сова"), LARK("Жаворонок"), PIGEON("Голубь");

    private final String displayName;

    ChronoType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}