package com.moigferdsrte.client.config;

import com.moigferdsrte.Entrance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.file.Path;
import java.util.ServiceLoader;

@Environment(EnvType.CLIENT)
public interface IPlatformHelper {
    public static final IPlatformHelper INSTANCE = load(IPlatformHelper.class);

    Path getConfigDirectory();

    Path getGameDirectory();

    boolean isModLoaded(String modid);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Entrance.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
