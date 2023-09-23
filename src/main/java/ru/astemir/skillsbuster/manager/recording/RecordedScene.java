package ru.astemir.skillsbuster.manager.recording;

import ru.astemir.skillsbuster.manager.NamedEntry;

public class RecordedScene implements NamedEntry {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
