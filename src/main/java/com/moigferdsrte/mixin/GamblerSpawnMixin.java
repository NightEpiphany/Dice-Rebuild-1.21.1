package com.moigferdsrte.mixin;

import com.moigferdsrte.entity.refine.RefineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(WoodlandMansionGenerator.Piece.class)
public class GamblerSpawnMixin {

	@Inject(method = "handleMetadata", at = @At("HEAD"), cancellable = true)
	private void data(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo ci){
	 if (!metadata.startsWith("Chest")){
			List<MobEntity> list = new ArrayList<>();
			switch (metadata) {
				case "Mage":
					list.add(EntityType.EVOKER.create(world.toServerWorld()));
					if (world.getRandom().nextBoolean()) {
						list.add(EntityType.ILLUSIONER.create(world.toServerWorld()));
						list.add(RefineRegistry.GAMBLER.create(world.toServerWorld()));
					}
					break;
				case "Warrior":
					list.add(EntityType.VINDICATOR.create(world.toServerWorld()));
					break;
				case "Group of Allays":
					int i = world.getRandom().nextInt(3) + 1;

					for (int j = 0; j < i; j++) {
						list.add(EntityType.ALLAY.create(world.toServerWorld()));
					}
					break;
				default:
					return;
			}
			for (MobEntity mobEntity : list) {
				if (mobEntity != null) {
					mobEntity.setPersistent();
					mobEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
					mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, null);
					world.spawnEntityAndPassengers(mobEntity);
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}ci.cancel();
	 }
	}
}