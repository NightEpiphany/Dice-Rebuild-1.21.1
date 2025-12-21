package com.moigferdsrte.event;

import com.moigferdsrte.block.DiceValueIndicator;

@FunctionalInterface
public interface DiceValueCallback {
    void update(DiceValueIndicator indicator);
}
