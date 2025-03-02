package com.moigferdsrte.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

}
