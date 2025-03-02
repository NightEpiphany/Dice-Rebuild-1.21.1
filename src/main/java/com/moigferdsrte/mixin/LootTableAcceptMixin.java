package com.moigferdsrte.mixin;

import com.moigferdsrte.entity.refine.RefineRegistry;
import net.minecraft.data.server.loottable.vanilla.VanillaChestLootTableGenerator;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(VanillaChestLootTableGenerator.class)
public class LootTableAcceptMixin {
    @Inject(method = "accept", at = @At("HEAD"))
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer, CallbackInfo ci) {
        lootTableBiConsumer.accept(LootTables.WOODLAND_MANSION_CHEST, this.createExtra());
    }

    @Unique
    public LootTable.Builder createExtra() {
        return LootTable.builder()
                .pool(
                        LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                .with(ItemEntry.builder(RefineRegistry.GRAY_DICE).weight(25))
                );
    }
}
