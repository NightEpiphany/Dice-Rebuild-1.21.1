package com.moigferdsrte.config;

import com.moigferdsrte.entity.extension.CarriedDiceCubeEntityModel;
import com.moigferdsrte.entity.extension.GamblerEntityRenderer;
import com.moigferdsrte.entity.refine.DiceCubeEntityModel;
import com.moigferdsrte.entity.refine.DiceCubeEntityRenderer;
import com.moigferdsrte.entity.refine.RefineRegistry;
import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeEntityRenderer;
import com.moigferdsrte.particle.DiceCubeParticleFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.IllagerEntityModel;

public class TestClient implements ClientModInitializer {

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
        EntityModelLayerRegistry.registerModelLayer(DiceCubeEntityModel.LAYER, DiceCubeEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.DICE, DiceCubeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DiceCubeEntityModel.STICKY_LAYER, DiceCubeEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.STICKY_DICE, StickyDiceCubeEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(RefineRegistry.GAMBLER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RefineRegistry.GAMBLER, GamblerEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CarriedDiceCubeEntityModel.LAYER, CarriedDiceCubeEntityModel::getTexturedModelData);
        ParticleFactoryRegistry.getInstance().register(RefineRegistry.DICE_PARTICLE, DiceCubeParticleFactory.DiceFactory::new);
        ClientTickEvents.END_CLIENT_TICK.register(config().getKeyBinding());
    }
}
