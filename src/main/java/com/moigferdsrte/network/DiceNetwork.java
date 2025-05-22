package com.moigferdsrte.network;

import com.moigferdsrte.network.payload.GambleCompetePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class DiceNetwork {

    public static void init() {

        PayloadTypeRegistry.playC2S().register(GambleCompetePayload.ID, GambleCompetePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GambleCompetePayload.ID, GambleCompetePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GambleCompetePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = (ServerWorld) player.getWorld();
            byte playerPoint = payload.game().entrySet().iterator().next().getKey();
            byte competePoint = payload.game().get(playerPoint);

            if (player.isCreative()) {
                return;
            }

            if (competePoint < playerPoint) {player.addExperience(20);}
        });
    }

}
