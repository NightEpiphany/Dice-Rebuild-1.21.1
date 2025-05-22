package com.moigferdsrte.entity.refine;

import com.moigferdsrte.Entrance;
import com.moigferdsrte.block.DiceDetectorBlock;
import com.moigferdsrte.entity.extension.GamblerEntity;
import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeEntity;
import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeItem;
import com.moigferdsrte.particle.DiceCubeParticle;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.GameRules;

public class RefineRegistry {

    public static final ParticleType<DiceCubeParticle> DICE_PARTICLE = FabricParticleTypes.complex(
            false,
            DiceCubeParticle::createCodec,
            DiceCubeParticle::createPacketCodec
    );

    public static final GameRules.Key<GameRules.BooleanRule> DICE_PRIVACY =
            GameRuleRegistry.register("dicePrivacy", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.IntRule> MAXIMUM_GAMBLE_TIME =
            GameRuleRegistry.register("maximumGambleTime", GameRules.Category.MISC, GameRuleFactory.createIntRule(20));

    public static final GameRules.Key<GameRules.IntRule> EVENT_USE_DICE_DECAY_TIME =
            GameRuleRegistry.register("eventUseDiceDecayTime", GameRules.Category.MISC, GameRuleFactory.createIntRule(4800));

    public static final GameRules.Key<GameRules.IntRule> GAMBLER_DICE_VISIBLE_DISTANCE =
            GameRuleRegistry.register("gamblerDiceVisibleDistance", GameRules.Category.MISC, GameRuleFactory.createIntRule(10));

    public static final EntityModelLayer GAMBLER_LAYER = new EntityModelLayer(Identifier.of(Entrance.MOD_ID, "gambler"), "main");

    public static final SoundEvent DICE_ROLLING = Registry.register(
            Registries.SOUND_EVENT,
            Identifier.of(Entrance.MOD_ID, "dice_rolling"),
            SoundEvent.of(Identifier.of(Entrance.MOD_ID, "dice_rolling"))
    );

    public static final TagKey<Item> DICES = TagKey.of(RegistryKeys.ITEM, Identifier.of(Entrance.MOD_ID, "dice"));

    public static final TagKey<Item> STANDARD_DICES = TagKey.of(RegistryKeys.ITEM, Identifier.of(Entrance.MOD_ID, "standard_dice"));

    public static final EntityType<GamblerEntity> GAMBLER =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Entrance.MOD_ID, "gambler"),
                    EntityType.Builder.create(GamblerEntity::new, SpawnGroup.MONSTER)
                            .spawnableFarFromPlayer()
                            .dimensions(0.6F, 1.95F)
                            .passengerAttachments(2.0F)
                            .vehicleAttachment(-0.6F)
                            .maxTrackingRange(8)
                            .build(Entrance.MOD_ID + "gambler"));

    public static final EntityType<DiceCubeEntity> DICE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Entrance.MOD_ID, "dice_entity"),
                    EntityType.Builder.<DiceCubeEntity>create(DiceCubeEntity::new, SpawnGroup.MISC)
                            .dimensions(0.3125f, 0.3125f).build(Entrance.MOD_ID + "dice_entity"));

    public static final EntityType<StickyDiceCubeEntity> STICKY_DICE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Entrance.MOD_ID, "sticky_dice_entity"),
                    EntityType.Builder.<StickyDiceCubeEntity>create(StickyDiceCubeEntity::new, SpawnGroup.MISC)
                            .dimensions(0.3125f, 0.3125f).build(Entrance.MOD_ID + "sticky_dice_entity"));

    public static final Block DICE_DETECTOR = Registry.register(Registries.BLOCK,
            RegistryKey.of(Registries.BLOCK.getKey(),
                    Identifier.of(Entrance.MOD_ID, "dice_detector")),
            new DiceDetectorBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.STONE_GRAY)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(3.0F)
                            .requiresTool()
                            .sounds(BlockSoundGroup.STONE)
                            .solidBlock(Blocks::never)
            ));

    public static final Item GAMBLER_SPAWN_EGG = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "gambler_spawn_egg")),
                    new SpawnEggItem(GAMBLER, 3503979, 15834675, new Item.Settings())
                    );

    public static final Item SLIME_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "slime_d6")),
            new StickyDiceCubeItem(true, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item HONEY_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "honey_d6")),
            new StickyDiceCubeItem(false, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item WHITE_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "white_d6")),
            new DiceCubeItem(DyeColor.WHITE, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item BLACK_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "black_d6")),
            new DiceCubeItem(DyeColor.BLACK, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item BLUE_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "blue_d6")),
            new DiceCubeItem(DyeColor.BLUE, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item BROWN_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "brown_d6")),
            new DiceCubeItem(DyeColor.BROWN, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item CYAN_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "cyan_d6")),
            new DiceCubeItem(DyeColor.CYAN, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item GRAY_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "gray_d6")),
            new DiceCubeItem(DyeColor.GRAY, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item GREEN_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "green_d6")),
            new DiceCubeItem(DyeColor.GREEN, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item LIGHT_BLUE_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "light_blue_d6")),
            new DiceCubeItem(DyeColor.LIGHT_BLUE, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item LIGHT_GRAY_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "light_gray_d6")),
            new DiceCubeItem(DyeColor.LIGHT_GRAY, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item LIME_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "lime_d6")),
            new DiceCubeItem(DyeColor.LIME, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item MAGENTA_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "magenta_d6")),
            new DiceCubeItem(DyeColor.MAGENTA, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item ORANGE_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "orange_d6")),
            new DiceCubeItem(DyeColor.ORANGE, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item PINK_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "pink_d6")),
            new DiceCubeItem(DyeColor.PINK, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item PURPLE_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "purple_d6")),
            new DiceCubeItem(DyeColor.PURPLE, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item RED_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "red_d6")),
            new DiceCubeItem(DyeColor.RED, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item YELLOW_DICE = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "yellow_d6")),
            new DiceCubeItem(DyeColor.YELLOW, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item DICE_DETECTOR_ITEM = Registry.register(Registries.ITEM,
            RegistryKey.of(Registries.ITEM.getKey(),
                    Identifier.of(Entrance.MOD_ID, "dice_detector")),
            new BlockItem(DICE_DETECTOR, new Item.Settings()));

    private static ItemStack icon() {
        return new ItemStack(YELLOW_DICE);
    }

    private static void toolGroup(FabricItemGroupEntries entries) {
        entries.add(WHITE_DICE);
        entries.add(BLACK_DICE);
        entries.add(BLUE_DICE);
        entries.add(BROWN_DICE);
        entries.add(CYAN_DICE);
        entries.add(GRAY_DICE);
        entries.add(GREEN_DICE);
        entries.add(LIGHT_BLUE_DICE);
        entries.add(LIGHT_GRAY_DICE);
        entries.add(LIME_DICE);
        entries.add(MAGENTA_DICE);
        entries.add(ORANGE_DICE);
        entries.add(PINK_DICE);
        entries.add(PURPLE_DICE);
        entries.add(RED_DICE);
        entries.add(YELLOW_DICE);
    }

    private static void redstoneGroup(FabricItemGroupEntries entries){
        entries.add(SLIME_DICE);
        entries.add(HONEY_DICE);
        entries.add(DICE_DETECTOR_ITEM);
    }

    private static void spawnEggGroup(FabricItemGroupEntries entries) {
        entries.add(GAMBLER_SPAWN_EGG);
    }

    public static void init() {

        Registry.register(Registries.ITEM_GROUP, Identifier.of(Entrance.MOD_ID, "dice_tab"),
                ItemGroup.create(null, -1).displayName(Text.translatable("itemGroup.dice_tab"))
                        .icon(RefineRegistry::icon)
                        .entries(
                                ((displayContext, entries) -> {
                                    entries.add(WHITE_DICE);
                                    entries.add(BLACK_DICE);
                                    entries.add(BLUE_DICE);
                                    entries.add(BROWN_DICE);
                                    entries.add(CYAN_DICE);
                                    entries.add(GRAY_DICE);
                                    entries.add(GREEN_DICE);
                                    entries.add(LIGHT_BLUE_DICE);
                                    entries.add(LIGHT_GRAY_DICE);
                                    entries.add(LIME_DICE);
                                    entries.add(MAGENTA_DICE);
                                    entries.add(ORANGE_DICE);
                                    entries.add(PINK_DICE);
                                    entries.add(PURPLE_DICE);
                                    entries.add(RED_DICE);
                                    entries.add(YELLOW_DICE);
                                    entries.add(SLIME_DICE);
                                    entries.add(HONEY_DICE);
                                    entries.add(DICE_DETECTOR_ITEM);
                                    entries.add(GAMBLER_SPAWN_EGG);
                                })
                        ).build()
                );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(RefineRegistry::toolGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(RefineRegistry::spawnEggGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(RefineRegistry::redstoneGroup);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Entrance.MOD_ID, "dice_particle"), DICE_PARTICLE);
    }
}
