package ru.astemir.skillsbuster.manager.keybind;


import java.util.function.Predicate;

public enum BindClickType {
    ON_CLICK((bind)->bind.isClicked()),
    ON_DOUBLE_CLICK((bind)->bind.isDoubleClicked()),
    ON_PRESS((bind)->bind.isDown()),
    ON_RELEASE((bind)->bind.isReleased());

    private Predicate<SBKeyBind> predicate;

    BindClickType(Predicate<SBKeyBind> predicate) {
        this.predicate = predicate;
    }

    public Predicate<SBKeyBind> getPredicate() {
        return predicate;
    }
}
