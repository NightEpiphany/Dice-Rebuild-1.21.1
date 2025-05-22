package com.moigferdsrte;

import com.moigferdsrte.entity.extension.GamblerEntity;
import com.moigferdsrte.entity.refine.RefineRegistry;
import com.moigferdsrte.network.DiceNetwork;
import com.moigferdsrte.particle.DiceCubeParticleFactory;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entrance implements ModInitializer {
	public static final String MOD_ID = "dice";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		FabricDefaultAttributeRegistry.register(RefineRegistry.GAMBLER, GamblerEntity.createGamblerAttributes());
		DiceCubeParticleFactory.init();
		RefineRegistry.init();
		DiceNetwork.init();
		LOGGER.info("Hello Fabric world!");
	}
}