package com.moigferdsrte.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiceValueIndicator {

    public int value = 0;

    public DiceValueIndicator(DiceDetectorBlock block, int value){
        this.value = value;
    }

    public void updateValue(BlockPos pos, BlockState state, World world){
        this.value = DiceDetectorBlock.dicePointCalculation(pos, state, world);
    }
}
