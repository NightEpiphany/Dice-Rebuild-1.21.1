package com.moigferdsrte.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiceValueIndicator {

    public int value;
    public final BlockPos pos;
    public final BlockState state;
    public final World world;

    public DiceValueIndicator(BlockPos pos, BlockState state, World world) {
        this.pos = pos;
        this.state = state;
        this.world = world;
    }

    @Deprecated
    public void updateValue(BlockPos pos, BlockState state, World world) {
        this.value = DiceDetectorBlock.dicePointCalculation(pos, state, world);
    }
}
