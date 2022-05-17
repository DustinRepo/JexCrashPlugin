package me.dustin.crash.event;

import me.dustin.events.core.Event;
import net.minecraft.util.Identifier;

public class EventPlaySound extends Event {

    private final Identifier identifier;

    public EventPlaySound(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
