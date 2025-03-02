package com.moigferdsrte.mixin;

import com.moigferdsrte.entity.refine.DiceCubeEntity;
import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MagmaBlock.class)
public class MagmaDiscardDiceMixin {

    @Unique
    protected void onEntityCollision(World world, BlockPos pos) {
        Box box = new Box(pos).expand(1, 0, 1);
        List<DiceCubeEntity> diceCubeEntities = world.getNonSpectatingEntities(DiceCubeEntity.class, box);
        for (DiceCubeEntity dice : diceCubeEntities) {
            dice.killIt(dice.isForEventPlayerGambleUse());
            dice.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.0F, 1.0F);
        }

        List<StickyDiceCubeEntity> stickyDiceCubeEntities = world.getNonSpectatingEntities(StickyDiceCubeEntity.class, box);
        for (StickyDiceCubeEntity dice : stickyDiceCubeEntities) {
            dice.killIt(true);
            dice.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.0F, 1.0F);
        }
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"))
    public void onLand(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (entity instanceof DiceCubeEntity diceCubeEntity) {
            entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.0F, 1.0F);
            diceCubeEntity.killIt(diceCubeEntity.isForEventPlayerGambleUse());
        }
        if (entity instanceof StickyDiceCubeEntity stickyDiceCubeEntity) {
            entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.0F, 1.0F);
            stickyDiceCubeEntity.killIt(true);
        }
    }

    @Inject(method = "scheduledTick", at = @At("TAIL"))
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        onEntityCollision(world, pos);
    }
}
