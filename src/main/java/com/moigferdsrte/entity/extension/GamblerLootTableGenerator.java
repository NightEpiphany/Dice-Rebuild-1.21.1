package com.moigferdsrte.entity.extension;

import com.moigferdsrte.entity.refine.RefineRegistry;
import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;

public class GamblerLootTableGenerator extends EntityLootTableGenerator {
    public GamblerLootTableGenerator() {
        super(FeatureFlags.FEATURE_MANAGER.getFeatureSet());
    }

    @Override
    public void generate() {
        this.register(RefineRegistry.GAMBLER, LootTable.builder()
                .pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(
                                        ItemEntry.builder(Items.EMERALD)
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 3.0F)))
                                                .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
                                )
                )
        );
    }
}
