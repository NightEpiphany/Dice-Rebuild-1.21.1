package com.moigferdsrte;

import com.moigferdsrte.client.config.DiceCubeConfig;
import com.moigferdsrte.client.config.FabricPlatformHelper;
import com.moigferdsrte.client.model.CarriedDiceCubeEntityModel;
import com.moigferdsrte.client.renderer.GamblerEntityRenderer;
import com.moigferdsrte.client.model.DiceCubeEntityModel;
import com.moigferdsrte.client.renderer.DiceCubeEntityRenderer;
import com.moigferdsrte.init.RefineRegistry;
import com.moigferdsrte.client.renderer.StickyDiceCubeEntityRenderer;
import com.moigferdsrte.particle.DiceCubeParticleFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientEntrance implements ClientModInitializer {

    public static final EntityModelLayer GAMBLER_LAYER = new EntityModelLayer(Identifier.of(Entrance.MOD_ID, "gambler"), "main");

    private static DiceCubeConfig CONFIG;
    public static DiceCubeConfig config() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }
    private static DiceCubeConfig loadConfig() {
        return DiceCubeConfig.load(FabricPlatformHelper.INSTANCE.getConfigDirectory().resolve("dice-config.json").toFile());
    }

    @Override
    public void onInitializeClient() {
        DiceCubeParticleFactory.init();
        EntityModelLayerRegistry.registerModelLayer(DiceCubeEntityModel.LAYER, DiceCubeEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.DICE, DiceCubeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DiceCubeEntityModel.STICKY_LAYER, DiceCubeEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.STICKY_DICE, StickyDiceCubeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GAMBLER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.GAMBLER, GamblerEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CarriedDiceCubeEntityModel.LAYER, CarriedDiceCubeEntityModel::getTexturedModelData);
        ParticleFactoryRegistry.getInstance().register(RefineRegistry.DICE_PARTICLE, DiceCubeParticleFactory.DiceFactory::new);
        //ClientTickEvents.END_CLIENT_TICK.register(config().getKeyBinding());
    }
}
