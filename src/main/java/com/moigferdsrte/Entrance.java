package com.moigferdsrte;

import com.moigferdsrte.block.DiceDetectorBlock;
import com.moigferdsrte.entity.GamblerEntity;
import com.moigferdsrte.event.DiceEvent;
import com.moigferdsrte.init.RefineRegistry;
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
		DiceEvent.DICE_VALUE_CALLBACK_EVENT.register(event -> {
		  	event.value = DiceDetectorBlock.dicePointCalculation(event.pos, event.state, event.world);
		});
		FabricDefaultAttributeRegistry.register(RefineRegistry.GAMBLER, GamblerEntity.createGamblerAttributes());
		DiceCubeParticleFactory.init();
		RefineRegistry.init();
		DiceNetwork.init();
		LOGGER.info("Hello Fabric world!");
	}
}