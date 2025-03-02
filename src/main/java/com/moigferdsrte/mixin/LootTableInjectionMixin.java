package com.moigferdsrte.mixin;

import com.moigferdsrte.entity.extension.GamblerLootTableGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.data.server.loottable.vanilla.*;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(VanillaLootTableProviders.class)
public class LootTableInjectionMixin {
    @Inject(method = "createVanillaProvider", at = @At("HEAD"), cancellable = true)
    private static void provider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, CallbackInfoReturnable<LootTableProvider> cir){
        cir.setReturnValue( new LootTableProvider(
                output,
                LootTables.getAll(),
                List.of(
                        new LootTableProvider.LootTypeGenerator(VanillaFishingLootTableGenerator::new, LootContextTypes.FISHING),
                        new LootTableProvider.LootTypeGenerator(VanillaChestLootTableGenerator::new, LootContextTypes.CHEST),
                        new LootTableProvider.LootTypeGenerator(VanillaEntityLootTableGenerator::new, LootContextTypes.ENTITY),
                        new LootTableProvider.LootTypeGenerator(VanillaEquipmentLootTableGenerator::new, LootContextTypes.EQUIPMENT),
                        new LootTableProvider.LootTypeGenerator(VanillaBlockLootTableGenerator::new, LootContextTypes.BLOCK),
                        new LootTableProvider.LootTypeGenerator(VanillaBarterLootTableGenerator::new, LootContextTypes.BARTER),
                        new LootTableProvider.LootTypeGenerator(VanillaGiftLootTableGenerator::new, LootContextTypes.GIFT),
                        new LootTableProvider.LootTypeGenerator(VanillaArchaeologyLootTableGenerator::new, LootContextTypes.ARCHAEOLOGY),
                        new LootTableProvider.LootTypeGenerator(VanillaShearingLootTableGenerator::new, LootContextTypes.SHEARING),
                        new LootTableProvider.LootTypeGenerator(GamblerLootTableGenerator::new, LootContextTypes.ENTITY)
                ),
                registryLookupFuture
        ));
    }
}
