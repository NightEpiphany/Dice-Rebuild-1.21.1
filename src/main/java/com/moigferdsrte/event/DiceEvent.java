package com.moigferdsrte.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class DiceEvent {

    public static final Event<DiceValueCallback> DICE_VALUE_CALLBACK_EVENT = EventFactory.createArrayBacked(DiceValueCallback.class, (listeners) -> (value) -> {
        for (DiceValueCallback listener : listeners) {
            listener.update(value);
        }
    });
}
